package com.musicApp.backend.map.OpenAISearch;

import com.openai.client.OpenAIClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;
import com.openai.models.responses.ResponseOutputMessage;
import com.openai.models.responses.Tool;
import com.openai.models.responses.WebSearchTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A simple AI method that suggests artists and genres that sound similar to a user's prefered artist
 */
@Service
public class OpenAISearchConcerts {
     /**
      * A string that holds the AI model
      */
    private final String model;
    /**
     * An OpenAICLient object that holds access to the OpenAI service
     */
    private final OpenAIClient client;


    /**
     * A constructor that gives access to the AI services
     * @param model creates the AI model
     * @param client creates the access to the AI services
     */
    public OpenAISearchConcerts(@Value("${openai.model}") String model,
                                OpenAIClient client) {
        this.model = model;
        this.client = client;
    }

    /**
     * A method that, when given a prompt to look for an artist, it returns a JSON of the genre
     * and the artists related to the genre
     * @param prompt holds the user's request
     * @return a json of the AI's result
     */
    public String searchForConcerts(String prompt) {
        System.out.println("Search Start");

        if (prompt == null || prompt.trim().length() < 10) {
            return "Prompt is too short.";
        }

        Tool webSearch = Tool.ofWebSearch(
                WebSearchTool.builder()
                        .type(WebSearchTool.Type.WEB_SEARCH)
                        .build()
        );

        String request = String.format("""
                You are trying to find live concerts.

                From the user input, extract:
                - genre
                - 3-5 similar artists

                Return ONLY valid JSON.

                Format:
                {
                  "genre":
                  "similar_artists": [artist 1, artist 2]
                }

                User input: %s
                """, prompt);

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(model)
                .input(request)
                .tools(List.of(webSearch))
                .build();

        Response res = client.responses().create(params);

        System.out.println(extractText(res));

        return extractText(res);
    }

    /**
   * This method turns Json into a String
   * @param response is the Json being converted into a string
   * @return a string of the Json
   */
    public static String extractText(Response response) {
        if (response == null || response.output() == null) return null;

        for (ResponseOutputItem item : response.output()) {
            var msgOpt = item.message();
            if (msgOpt.isEmpty()) continue;

            var msg = msgOpt.get();
            if (msg.content() == null) continue;

            for (ResponseOutputMessage.Content c : msg.content()) {
                var outTextOpt = c.outputText();
                if (outTextOpt.isPresent()) {
                    return outTextOpt.get().text();
                }
            }
        }
        return null;
    }
}
