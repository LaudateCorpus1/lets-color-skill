/*
 * Copyright 2019 HP Development Company, L.P.
 * SPDX-License-Identifier: MIT
 */

package com.hp.letscolor.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.SessionResumedRequestHandler;
import com.amazon.ask.model.ConnectionCompleted;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.SessionResumedRequest;
import com.hp.letscolor.resource.I18nResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Optional;

import static com.hp.letscolor.handler.ColoringPagesIntentHandler.COLORING_PAGES_TOKEN;

/**
 * Intent Handler for Connections Response
 */
public class SessionResumedHandler implements SessionResumedRequestHandler {

    private Logger logger = LogManager.getLogger(SessionResumedHandler.class);

    @Override
    public boolean canHandle(HandlerInput handlerInput, SessionResumedRequest sessionResumedRequest) {
        return true;
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, SessionResumedRequest sessionResumedRequest) {
        Locale locale = Locale.forLanguageTag(sessionResumedRequest.getLocale());
        ConnectionCompleted cause = (ConnectionCompleted) sessionResumedRequest.getCause();
        String token = cause.getToken();
        if (!token.equals(COLORING_PAGES_TOKEN)) {
            return Optional.empty();
        }
        String speechKeyText = cause.getStatus().getCode().equals("200") ? "response_success" : "response_error";
        String speechText = I18nResource.getString(speechKeyText, locale);
        String cardTitle = I18nResource.getString("title_card", locale);
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard(cardTitle, speechText)
                .withShouldEndSession(true)
                .build();
    }
}
