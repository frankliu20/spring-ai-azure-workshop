# Spring AI Workshop for Azure

## Prerequisites

Before you begin, make sure to set the following environment variables.

```shell
export SPRING_AI_AZURE_OPENAI_API_KEY=<INSERT KEY HERE>
export SPRING_AI_AZURE_OPENAI_ENDPOINT=<INSERT ENDPOINT URL HERE>
```

### Create Azure AI Deployments

The configuration assumes you have already created deployments of the following names in the Azure OpenAI Studio

* Deployment Name: `gpt-4o` with the model `gpt-4o`
* Deployment Name: `text-embedding-ada-002` with the model `text-embedding-ada-002`

NOTE: Spring configuration properties currently use the property-name `model` instead of `deployment-name` so don't get confused.  Spring AI will be renaming the property in the future to avoid confusion.  See [here](https://docs.spring.io/spring-ai/reference/api/clients/azure-openai-chat.html#_deployment_name) for more information.

For the chat, the configuration in `application.properties` should contain the following

`spring.ai.azure.openai.chat.options.model=gpt-4o`

The default value for the embedding model is `text-embedding-ada-002`, but if you wanted to change it you would set the configuration in `application.properties` as shown below.

`spring.ai.azure.openai.embedding.options.model=text-embedding-ada-002`

## Prepare role 
Add `Cognitive Services OpenAI User` Role to you or your application

## Workshop Overview

The workshop consists of six examples, each with a dedicated `README` file.

All six workshop examples are organized into individual Java packages within this project. In each package, you'll find a Spring @RestController class that serves as the entry point for showcasing the discussed functionality.

To interact with the @RestController, you will be using the `http` utility as a user-friendly alternative to `curl`.

Detailed instructions and exercises for each example can be found in their respective README files:

* 1-README-tell-me-a-joke.md 
* 2-README-prompt-templating.md 
* 3-README-prompt-roles.md 
* 4-README-output-parser.md 
* 5-README-stuff-prompt.md 
* 6-README-retrieval-augmented-generation.md

These guides will walk you through the workshop exercises.