/*
 * Copyright 2018 HP Development Company, L.P.
 * SPDX-License-Identifier: MIT
 */

package com.hp.letscolor.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import com.hp.letscolor.resource.ColoringPagesResource;
import com.hp.letscolor.resource.I18nResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.hp.letscolor.handler.ColoringPagesIntentHandler.SHOULD_PICK_CATEGORY;

public class YesIntentHandler implements RequestHandler {
    private Logger logger = LogManager.getLogger(YesIntentHandler.class);

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.intentName("AMAZON.YesIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        Locale locale = Locale.forLanguageTag(handlerInput.getRequestEnvelope().getRequest().getLocale());

        Map<String, Object> sessionAttributes = handlerInput.getAttributesManager().getSessionAttributes();
        Boolean shouldChooseCategory = (Boolean) sessionAttributes.get(SHOULD_PICK_CATEGORY);

        if (shouldChooseCategory == null || !shouldChooseCategory) {
            String speechText = I18nResource.getString("yes_with_no_context", locale);
            return handlerInput.getResponseBuilder()
                    .withSpeech(speechText)
                    .withShouldEndSession(true)
                    .build();
        }

        ColoringPagesResource resource = ColoringPagesResource.pickResource();
        return ColoringPagesIntentHandler.sendToHPPrinter(handlerInput.getResponseBuilder(), locale, resource);
    }
}
