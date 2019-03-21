package com.hp.letscolor.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.connections.ConnectionsResponse;
import com.amazon.ask.request.Predicates;
import com.hp.letscolor.resource.I18nResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Optional;

import static com.hp.letscolor.handler.ColoringPagesIntentHandler.COLORING_PAGES_TOKEN;

/**
 * Intent Handler for Connections Response
 */
public class ConnectionsResponseIntentHandler implements RequestHandler {

    private Logger logger = LogManager.getLogger(ConnectionsResponseIntentHandler.class);

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.requestType(ConnectionsResponse.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        Request request = handlerInput.getRequestEnvelope().getRequest();
        Locale locale = Locale.forLanguageTag(request.getLocale());
        ConnectionsResponse connectionsResponse = (ConnectionsResponse) request;
        String token = connectionsResponse.getToken();
        if (!token.equals(COLORING_PAGES_TOKEN)) {
            return Optional.empty();
        }
        String speechKeyText = connectionsResponse.getStatus().getCode().equals("200") ? "response_success" : "response_error";
        String speechText = I18nResource.getString(speechKeyText, locale);
        String cardTitle = I18nResource.getString("title_card", locale);
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard(cardTitle, speechText)
                .withShouldEndSession(true)
                .build();
    }
}
