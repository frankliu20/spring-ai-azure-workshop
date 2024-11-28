package com.xkcd.ai;

import org.springframework.ai.autoconfigure.azure.openai.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.util.ClientOptions;
import com.azure.identity.DefaultAzureCredentialBuilder;

@Configuration
@EnableConfigurationProperties({AzureOpenAiConnectionProperties.class})
public class OpenAiConfig {

    // support key-less auth (e.g. mi-based auth)
    // see AzureOpenAiAutoConfiguration
    @Bean
    @ConditionalOnMissingBean
    public TokenCredential AzureTokenCredential() {
        return new DefaultAzureCredentialBuilder().build();
    }

    // log http requests and responses for azure open-ai LLM call
    // https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/openai/azure-ai-openai/TROUBLESHOOTING.md#enable-http-requestresponse-logging
    @Bean
    public OpenAIClientBuilder openAIClientWithTokenCredential(AzureOpenAiConnectionProperties connectionProperties,
                                                               TokenCredential tokenCredential) {
        Assert.notNull(tokenCredential, "TokenCredential must not be null");
        Assert.hasText(connectionProperties.getEndpoint(), "Endpoint must not be empty");
        return new OpenAIClientBuilder().endpoint(connectionProperties.getEndpoint())
                .credential(tokenCredential)
                .clientOptions(new ClientOptions().setApplicationId("spring-ai"))
                .httpLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY));
    }

    // add simple logger to the default advisors
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }


}