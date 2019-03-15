package com.hp.letscolor.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.hp.letscolor.resource.I18nResource;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import static com.hp.letscolor.handler.ColoringPagesIntentHandler.SHOULD_PICK_CATEGORY;

public class NoIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.intentName("AMAZON.NoIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        Locale locale = Locale.forLanguageTag(handlerInput.getRequestEnvelope().getRequest().getLocale());
        handlerInput.getAttributesManager().setSessionAttributes(Collections.singletonMap(SHOULD_PICK_CATEGORY, false));

        return handlerInput.getResponseBuilder()
                .withSpeech(I18nResource.getString("no_message", locale))
                .withShouldEndSession(false)
                .build();
    }
}
