package com.hp.letscolor.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.hp.letscolor.resource.I18nResource;

import java.util.Locale;
import java.util.Optional;

public class FallbackIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.intentName("AMAZON.FallbackIntent"));

    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        String requestLocale = handlerInput.getRequestEnvelope().getRequest().getLocale();
        Locale locale = Locale.forLanguageTag(requestLocale);
        String speechText = I18nResource.getString("fallback_message", locale);

        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Coloring Pages", speechText)
                .withReprompt(speechText)
                .build();
    }
}
