package com.xkcd.ai.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OutputParserController {

	private static final Logger logger = LoggerFactory.getLogger(OutputParserController.class);

	private final ChatClient chatClient;

	private final ChatModel chatModel;

	@Autowired
	public OutputParserController(ChatClient chatClient, ChatModel chatModel) {
		this.chatClient = chatClient;
		this.chatModel = chatModel;
	}

	@GetMapping("/ai/output")
	public ActorsFilms generate(@RequestParam(value = "actor", defaultValue = "Jeff Bridges") String actor) {
		// var outputParser = new BeanOutputParser<>(ActorsFilms.class);

		// String format = outputParser.getFormat();
		// logger.info("format: " + format);
		// String userMessage = """
		// 		Generate the filmography for the actor {actor}.
		// 		{format}
		// 		""";
		// PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("actor", actor, "format", format));
		// Prompt prompt = promptTemplate.create();

		// Generation generation = chatClient.prompt(prompt).call().chatResponse().getResult();
		// return outputParser.parse(generation.getOutput().getContent());


		ActorsFilms actorsFilms = chatClient.prompt()
			.user(u -> u.text("Generate the filmography of 10 movies for {actor}.")
						.param("actor", actor))
			.call()
			.entity(ActorsFilms.class);
		return actorsFilms;
	}

}
