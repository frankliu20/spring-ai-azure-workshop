package com.xkcd.ai.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.chat.client.ChatClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@RestController
public class SimpleAiController {

	private final ChatClient chatClient;
	@Autowired
	public SimpleAiController(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@GetMapping("/ai/simple")
	public Map<String, String> generation(
			@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
		return Map.of("generation", chatClient.prompt().user(message).call().chatResponse().getResult().getOutput().getContent());
	}

	@GetMapping("/ai/simple/image")
	public Map<String, String> chatwithimage() throws MalformedURLException {
		URL url = new URL("https://docs.spring.io/spring-ai/reference/_images/multimodal.test.png");
		String response =  chatClient.prompt()
				.user(u -> u.text("Explain what do you see on this picture?").media(MimeTypeUtils.IMAGE_PNG, url))
				.call()
				.chatResponse()
				.getResult()
				.getOutput()
				.getContent();
		return Map.of("generation", response);
	}

}
