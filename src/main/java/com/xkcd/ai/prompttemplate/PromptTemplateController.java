package com.xkcd.ai.prompttemplate;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PromptTemplateController {

	private final AzureOpenAiChatModel chatModel;

	@Autowired
	public PromptTemplateController(AzureOpenAiChatModel chatModel) {
		this.chatModel = chatModel;
	}

	@Value("classpath:/prompts/joke-prompt.st")
	private Resource jokeResource;


	@GetMapping("/ai/prompt")
	public AssistantMessage completion(@RequestParam(value = "adjective", defaultValue = "funny") String adjective,
			@RequestParam(value = "topic", defaultValue = "cows") String topic,
			@RequestParam(value = "language", defaultValue = "english") String language) {
		PromptTemplate promptTemplate = new PromptTemplate(jokeResource);
		Prompt prompt = promptTemplate.create(Map.of("adjective", adjective, "topic", topic, "language", language));
		return chatModel.call(prompt).getResult().getOutput();
	}

}
