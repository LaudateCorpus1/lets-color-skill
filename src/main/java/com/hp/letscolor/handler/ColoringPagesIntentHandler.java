package com.hp.letscolor.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.interfaces.connections.SendRequestDirective;
import com.amazon.ask.request.Predicates;
import com.amazon.ask.response.ResponseBuilder;
import com.hp.letscolor.resource.ColoringPagesResource;
import com.hp.letscolor.resource.I18nResource;
import com.hp.letscolor.util.UrlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.hp.letscolor.resource.SkillConnectionsConstants.CONTEXT;
import static com.hp.letscolor.resource.SkillConnectionsConstants.DESCRIPTION;
import static com.hp.letscolor.resource.SkillConnectionsConstants.IMAGE_TYPE;
import static com.hp.letscolor.resource.SkillConnectionsConstants.PRINT_IMAGE_REQUEST;
import static com.hp.letscolor.resource.SkillConnectionsConstants.PRINT_PDF_REQUEST;
import static com.hp.letscolor.resource.SkillConnectionsConstants.PRINT_WEB_PAGE_REQUEST;
import static com.hp.letscolor.resource.SkillConnectionsConstants.PROVIDER_ID;
import static com.hp.letscolor.resource.SkillConnectionsConstants.TITLE;
import static com.hp.letscolor.resource.SkillConnectionsConstants.TYPE;
import static com.hp.letscolor.resource.SkillConnectionsConstants.URL;
import static com.hp.letscolor.resource.SkillConnectionsConstants.VERSION;

public class ColoringPagesIntentHandler implements RequestHandler {

    private static Logger logger = LogManager.getLogger(ColoringPagesIntentHandler.class);

    static final String SHOULD_PICK_CATEGORY = "should_pick_category";
    static final String COLORING_PAGES_TOKEN = "ColoringPages";
    public static final String PRINT_NAME = "Print";
    private static final String COLORING_PAGE_TYPE = "COLORING_PAGE_TYPE";

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.intentName("ColoringPagesIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        Request request = handlerInput.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;
        Intent intent = intentRequest.getIntent();
        Locale locale = Locale.forLanguageTag(intentRequest.getLocale());

        Map<String, Slot> slots = intent.getSlots();
        Slot coloringPageType = slots.get(COLORING_PAGE_TYPE);

        if (coloringPageType != null && coloringPageType.getResolutions() != null
                && coloringPageType.getResolutions().toString().contains("ER_SUCCESS_MATCH")) {
            return handleCategory(handlerInput, locale, coloringPageType);
        }
        return askToSortCategory(handlerInput, locale);
    }

    private static Optional<Response> handleCategory(HandlerInput handlerInput, Locale locale, Slot coloringPageType) {
        String coloringPageId = getSlotId(coloringPageType);
        ColoringPagesResource resource = ColoringPagesResource.valueOf(coloringPageId);
        return sendToHPPrinter(handlerInput.getResponseBuilder(), locale, resource);
    }


    private Optional<Response> askToSortCategory(HandlerInput handlerInput, Locale locale) {
        handlerInput.getAttributesManager().setSessionAttributes(Collections.singletonMap(SHOULD_PICK_CATEGORY, true));

        String speechText = I18nResource.getString("ask_type", locale);
        String speechTextRepromt = I18nResource.getString("ask_type_reprompt", locale);
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(speechTextRepromt)
                .withShouldEndSession(false)
                .build();
    }

    private static String getSlotId(Slot slot) {
        return slot.getResolutions()
                .getResolutionsPerAuthority()
                .get(0)
                .getValues()
                .get(0)
                .getValue()
                .getId();
    }

    static Optional<Response> sendToHPPrinter(ResponseBuilder responseBuilder, Locale locale, ColoringPagesResource resource) {
        String url = UrlUtils.pickUrl(resource.urls());
        String name = UrlUtils.getNameFromUrl(url);
        String resourceName = resource.capitalizeName();

        String speechText = String.format(I18nResource.getString("sent_message", locale), resourceName);
        String cardTitle = I18nResource.getString("title_card", locale);
        String cardText = I18nResource.getString("sent_message_card", locale);

        return responseBuilder
                .withSpeech(speechText)
                .withSimpleCard(cardTitle, cardText)
                .addDirective(hpPrinterDirective(name, url, resource, locale))
                .build();
    }

    private static SendRequestDirective hpPrinterDirective(String name, String url, ColoringPagesResource resource, Locale locale) {
        String extension = UrlUtils.getExtensionFromUrl(url);
        Map<String, Object> payload = generatePayload(extension, name, url, resource, locale);

        return SendRequestDirective.builder()
                .withName(PRINT_NAME)
                .withToken(COLORING_PAGES_TOKEN)
                .withPayload(payload)
                .build();
    }

    private static Map<String, Object> generatePayload(String extension, String name, String url,
                                                       ColoringPagesResource resource, Locale locale) {
        String requestType = getRequestType(extension);
        Map<String, Object> payload = new HashMap<>();
        payload.put(TYPE, requestType);
        payload.put(VERSION, "1");
        payload.put(TITLE, name);
        payload.put(DESCRIPTION, String.format(I18nResource.getString("category_of", locale), resource.name()));
        payload.put(URL, url);
        if (requestType.equals(PRINT_IMAGE_REQUEST)) {
            payload.put(IMAGE_TYPE, extension.toUpperCase());
        }
        String providerId = System.getenv(PROVIDER_ID);

        if (providerId != null) {
            Map<String, Object> context = new HashMap<>();
            context.put(PROVIDER_ID, providerId);
            payload.put(CONTEXT, context);
        }
        return payload;
    }

    private static String getRequestType(String extension) {
        switch (extension.toUpperCase()) {
            case "PDF":
                return PRINT_PDF_REQUEST;
            case "WebPage":
                return PRINT_WEB_PAGE_REQUEST;
            case "JPG":
            case "GIF":
            case "PNG":
            case "TIF":
                return PRINT_IMAGE_REQUEST;
            default:
                return PRINT_PDF_REQUEST;
        }
    }
}
