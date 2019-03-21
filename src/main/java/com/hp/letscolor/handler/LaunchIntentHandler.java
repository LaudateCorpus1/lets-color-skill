package com.hp.letscolor.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.hp.letscolor.resource.I18nResource;

import java.util.Locale;
import java.util.Optional;

public class LaunchIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(Predicates.requestType(LaunchRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        Locale locale = Locale.forLanguageTag(input.getRequestEnvelope().getRequest().getLocale());
        String speechText = I18nResource.getString("welcome_message", locale);
        String cardTitle = I18nResource.getString("title_card", locale);
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard(cardTitle, speechText)
                .withReprompt(speechText)
                .withShouldEndSession(false)
                .build();
    }

}
