package com.xkcd.ai.rag.config;

import com.azure.core.credential.TokenCredential;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.xkcd.ai.rag.RagService;

import org.springframework.ai.autoconfigure.vectorstore.cosmosdb.CosmosDBVectorStoreProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.CosmosDBVectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.azure.AzureVectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RagConfiguration {

    // used azure Search service as vector store, see AzureVectorStoreAutoConfiguration
    @Primary
    @Bean
    @ConditionalOnMissingBean
    public RagService ragServiceWithAzureVectorStore(ChatClient chatClient, AzureVectorStore vectorStore) {
        return new RagService(chatClient, vectorStore);
    }


    @Bean
    @ConditionalOnMissingBean
    // use CosmosDB as vector store, see CosmosDBVectorStoreAutoConfiguration
    public RagService ragServiceWithCosmosDBVectorStore(ChatClient chatClient, CosmosDBVectorStore vectorStore) {
        return new RagService(chatClient, vectorStore);
    }


    // used SimpleVectorStore as vector store
    @Bean
    @ConditionalOnMissingBean
    public RagService ragServiceWithSimpleVectorStore(ChatClient chatClient, EmbeddingModel embeddingModel) {
        return new RagService(chatClient, new SimpleVectorStore(embeddingModel));
    }


    @Bean
    public CosmosAsyncClient cosmosClientWithCredential(CosmosDBVectorStoreProperties properties,
                                                        TokenCredential tokenCredential) {
        return new CosmosClientBuilder().endpoint(properties.getEndpoint())
                .credential(tokenCredential)
                .userAgentSuffix("SpringAI-CDBNoSQL-VectorStore")
                .gatewayMode()
                .buildAsyncClient();
    }
}