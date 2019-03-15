package com.hp.letscolor.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.hp.letscolor.resource.I18nResource;

import java.util.Locale;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class CancelAndStopIntent implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(intentName("AMAZON.StopIntent").or(intentName("AMAZON.CancelIntent")));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        String requestLocale = handlerInput.getRequestEnvelope().getRequest().getLocale();
        Locale locale = Locale.forLanguageTag(requestLocale);
        String speechText = I18nResource.getString("cancel_and_stop", locale);

        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Coloring Pages", speechText)
                .withReprompt(speechText)
                .build();
    }
}
