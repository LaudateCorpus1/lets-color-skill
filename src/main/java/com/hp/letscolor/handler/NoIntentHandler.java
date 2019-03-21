package com.hp.letscolor.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.hp.letscolor.resource.I18nResource;

import java.util.Locale;
import java.util.Optional;

public class NoIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.intentName("AMAZON.NoIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        Locale locale = Locale.forLanguageTag(handlerInput.getRequestEnvelope().getRequest().getLocale());

        return handlerInput.getResponseBuilder()
                .withSpeech(I18nResource.getString("ask_no_question", locale))
                .withShouldEndSession(false)
                .build();
    }
}
