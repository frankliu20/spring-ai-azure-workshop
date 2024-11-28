package com.xkcd.ai.rag;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonMetadataGenerator;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RagService {

    private static final Logger logger = LoggerFactory.getLogger(RagService.class);

    @Value("classpath:/data/bikes.json")
    private Resource bikesResource;

    @Value("classpath:/prompts/system-qa.st")
    private Resource systemBikePrompt;

    private final ChatClient chatClient;

    private final VectorStore vectorStore;

    private boolean dataLoaded = false;

    public RagService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    private boolean hasData(VectorStore vectorStore) {
        SearchRequest searchRequest = SearchRequest.defaults().withTopK(1).withQuery("find a bike");
        return !vectorStore.similaritySearch(searchRequest).isEmpty();
    }

    private boolean dataLoaded() {
        return dataLoaded || hasData(vectorStore);
    }

    public VectorStore getVectorStore() {
        if (dataLoaded()) {
            return vectorStore;
        }

        // Step 1 - Load JSON document as Documents

        logger.info("Loading JSON as Documents");

        var metaGenerator = new JsonMetadataGenerator() {
            @Override
            public Map<String, Object> generate(Map<String, Object> jsonMap) {
                return Map.of("name", jsonMap.getOrDefault("name", ""));
            }
        };

        // JsonReader jsonReader = new JsonReader(bikesResource, "name", "price", "shortDescription", "description");
        JsonReader jsonReader = new JsonReader(bikesResource, metaGenerator, "name", "price", "shortDescription");
        List<Document> documents = jsonReader.get();

        logger.info("Loading JSON as Documents");

        // Step 2 - Create embeddings and save to vector store

        logger.info("Creating Embeddings...");
        vectorStore.add(documents);
        logger.info("Embeddings created.");
        dataLoaded = true;

        return vectorStore;
    }

    private String extract(String json, String key) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        return jsonObject.has(key) ? jsonObject.get(key).getAsString() : null;
	}

    public AssistantMessage retrieve(String message) {

        getVectorStore();

        // Step 3 retrieve related documents to query
        logger.info("Retrieving relevant documents");
        List<Document> similarDocuments = vectorStore.similaritySearch(message);
        logger.info(String.format("Found %s relevant documents.", similarDocuments.size()));

        // Step 4 Embed documents into SystemMessage with the `system-qa.st` prompt
        // template
        Message systemMessage = getSystemMessage(similarDocuments);
        UserMessage userMessage = new UserMessage(message);

        // Step 4 - Ask the AI model

        logger.info("Asking AI model to reply to question.");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        logger.info(prompt.toString());

        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
        logger.info("AI responded.");

        logger.info(chatResponse.getResult().getOutput().getContent());
        return chatResponse.getResult().getOutput();
    }

    private Message getSystemMessage(List<Document> similarDocuments) {

        String documents = similarDocuments.stream().map(entry -> entry.getContent()).collect(Collectors.joining("\n"));
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemBikePrompt);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", documents));
        return systemMessage;
    }

}
