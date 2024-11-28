package com.xkcd.ai.rag;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {

	private final RagService ragService;
	private final ChatClient chatClient;

	private QuestionAnswerAdvisor questionAnswerAdvisor;

	@Autowired
	public RagController(RagService ragService, ChatModel chatModel) {
		this.ragService = ragService;
		this.chatClient = ChatClient.builder(chatModel)
				.defaultAdvisors(new SimpleLoggerAdvisor())
				.build();
	}

	@GetMapping("/ai/rag")
	public AssistantMessage generate(
			@RequestParam(value = "message", defaultValue = "What bike is good for city commuting?") String message) {
		return ragService.retrieve(message);
	}

	@GetMapping("/ai/rag-with-advisor")
	public Map<String, String> ragWithAdvisor(
			@RequestParam(value = "message", defaultValue = "What bike is good for city commuting?") String message) {
		var search = SearchRequest.defaults().withTopK(3);
		if (questionAnswerAdvisor == null) {
			questionAnswerAdvisor = new QuestionAnswerAdvisor(ragService.getVectorStore(), search);
		}
		ChatResponse response = chatClient.prompt()
				.advisors(new SimpleLoggerAdvisor(), questionAnswerAdvisor)
				.user(message)
				.call().chatResponse();
		return Map.of("generation", response.getResult().getOutput().getContent());
	}

}
