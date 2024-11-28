package com.xkcd.ai.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.Map;

@RestController
public class FunctionController {

	private final ChatClient chatClient;

	@Autowired
	public FunctionController(ChatModel chatModel) {
		this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();;
	}

	@GetMapping("/ai/function")
	public Map<String, String> generation(@RequestParam(value = "partient", defaultValue = "P004") String partient) {
		ChatResponse response = chatClient.prompt()
				.user(u -> u.text("What's the health status of the patient with id {partient}.")
						.param("partient", partient))
				.functions("retrievePatientHealthStatus")
				.call().chatResponse();
		return Map.of("generation", response.getResult().getOutput().getContent());
	}

}
