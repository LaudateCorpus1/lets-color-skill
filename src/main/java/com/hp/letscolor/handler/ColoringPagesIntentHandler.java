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

public class ColoringPagesIntentHandler implements RequestHandler {

    public static final String PROVIDER_ID = "PROVIDER_ID";
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

        Map<String, Object> sessionAttributes = handlerInput.getAttributesManager().getSessionAttributes();
        Boolean shouldChooseCategory = (Boolean) sessionAttributes.get(SHOULD_PICK_CATEGORY);
        if (shouldChooseCategory == null || !shouldChooseCategory) {
            Slot coloringPageType = slots.get(COLORING_PAGE_TYPE);

            if (coloringPageType != null && coloringPageType.getResolutions() != null
                    && coloringPageType.getResolutions().toString().contains("ER_SUCCESS_MATCH")) {

                String coloringPageId = getSlotId(coloringPageType);

                ColoringPagesResource resource = ColoringPagesResource.valueOf(coloringPageId);
                String url = UrlUtils.pickUrl(resource.urls());
                String name = UrlUtils.getNameFromUrl(url);

                return handlerInput.getResponseBuilder()
                        .addDirective(hpPrinterDirective(name, url, resource, locale))
                        .build();
            }
        }

        handlerInput.getAttributesManager().setSessionAttributes(Collections.singletonMap(SHOULD_PICK_CATEGORY, true));

        String speechText = I18nResource.getString("ask_type", locale);
        String speechTextRepromt = I18nResource.getString("ask_type_reprompt", locale);
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(speechTextRepromt)
                .withShouldEndSession(false)
                .build();
    }

    private String getSlotId(Slot slot) {
        return slot.getResolutions()
                            .getResolutionsPerAuthority()
                            .get(0)
                            .getValues()
                            .get(0)
                            .getValue()
                            .getId();
    }

    static SendRequestDirective hpPrinterDirective(String name, String url, ColoringPagesResource resource, Locale locale) {
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
        payload.put("@type", requestType);
        payload.put("@version", "1");
        payload.put("title", name);
        payload.put("description", String.format(I18nResource.getString("category_of", locale), resource.name()));
        payload.put("url", url);
        if (requestType.equals("PrintImageRequest")) {
            payload.put("imageType", extension.toUpperCase());
        }
        String providerId = System.getenv(PROVIDER_ID);

        if (providerId != null) {
            Map<String, Object> context = new HashMap<>();
            context.put("providerId", providerId);
            payload.put("context", context);
        }
        return payload;
    }

    private static String getRequestType(String extension) {
        switch (extension.toUpperCase()) {
            case "PDF":
                return "PrintPDFRequest";
            case "WebPage":
                return "PrintWebPageRequest";
            case "JPG":
            case "GIF":
            case "PNG":
            case "TIF":
                return "PrintImageRequest";
            default:
                return "PrintPDFRequest";
        }
    }
}
