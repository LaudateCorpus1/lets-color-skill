##Let’s Color Skill

1. **About the skill:**\
Let’s create a skill that has a real purpose. The goal here is to show how to communicate and request a job to 
HP printer skill from another skill. For that our skill will need to have printable content. So, the content that our 
skill will provide will be coloring pages. Let’s call our skill as *“let’s color”*, or known as *requester skill*.\
So our flow will look like:
![alt text](/home/marcelo/Downloads/Sample_Connected_Skill.jpg)
The image is self explanatory, but as you can see, the way our skill will communicate with HP Printer skill is made by 
Amazon Skill Connections. 

2. **Create the skill:**\
For this tutorial, our skill will be written in `Java` language using the [official SDK](https://github.com/alexa/alexa-skills-kit-sdk-for-java) 
that Amazon provides us. The interaction between the user and Alexa should be simple but we’ll have to handle unpredictable situations 
(Not so unpredictable).
    1. **Intent:**\
        Our skill has only one custom Intent which should handle every Coloring Pages interaction. Let’s call it *ColoringPagesIntent*.\
        We also need to include `Amazon.YesIntent` and `Amazon.NoIntent` built in Intents.\
        The `Amazon.FallbackIntent`, `Amazon.StopIntent` and `Amazon.CancelIntent` should be already there after the skill creation.

        So the Intents that we’ll be using and will need to create are:

        * ColoringPagesIntent
        * Amazon.YesIntent
        * Amazon.NoIntent
        * Amazon.FallbackIntent
        * Amazon.StopIntent
        * Amazon.CancelIntent

        The Fallback intent is for when Alexa doesn’t understand clearly what you said. The last two intentions are to close the skill elegantly.

        Let’s focus on our main custom Intent: *ColoringPagesIntent*.\
        We’re going to accept two ways of asking for coloring pages:\
        Specifying the category of coloring pages or just ask for a coloring pages without specifying the category.

        Examples of samples for training our Intent:

        * print `{COLORING_PAGE_TYPE}` coloring page
        * print a coloring page from `{COLORING_PAGE_TYPE}` category
        * print coloring pages
        * print a coloring page
        * coloring page

    2. **Slot:**\
        Now it's time to create a slot, so we can use it in our Custom Intent.
        Let’s call it `COLORING_PAGE_TYPE`. The slot values that we’re going to accept are: `ANIMALS`, `FRUITS`, `ALPHABET`, `NATURE` and `THINGS`.

        Don’t forget to add the slot to the intent. We’re using *COLORING_PAGE_TYPE* as name as well.

    3. **Developing let’s color skill:**\
        After setting up the invocation name, endpoint, intents and slot, we’re ready to implement the code.\
        If you just want to look at the code, you can find it [here](https://github.azc.ext.hp.com/IoT-Voice/lets-color-skill).

    4. **Handling ColoringPagesIntent:**\
        Okay, the first thing we need to have in mind is the fact that user can or cannot specify the kind of category he wants.
        Based on that, if the user does not specify a category, we’ll ask if we can pick a category for him. 
        In other words, we’ll sort the available categories and pick one. **OR** we’ll just get the category he said and then send to the printer.

        Then the interaction should be as follows:

        ![alt text](images/without_category_interaction.jpg)

        **OR**

        ![alt text](images/with_category_interaction.jpg)

        Very simple. 

        Let’s implement our class then.\
        The first method that we need to implement is the `canHandle` so we can tell the SDK that we’re handling all 
        requests coming from *ColoringPagesIntent*.

        ``` 
        public class ColoringPagesIntentHandler implements RequestHandler {
            @Override
            public boolean canHandle(HandlerInput handlerInput) {
                return handlerInput.matches(Predicates.intentName("ColoringPagesIntent"));
            }

            @Override
            public Optional<Response> handle(HandlerInput handlerInput) {
                return Optional.empty();
            }
        }
        ```
        Okay, now we need to implement the `handle` method, which is basically what we are going to do to fulfill the 
        user's intention.\
        In our case, first we need to verify whether the user has provided a category or not before sending the 
        coloring page to HP Printer Skill.

        **Internationalizing:**\
        Since Java allows us to internationalize in a very simple way and the payload that Alexa sends us always come 
        with the locale, why not implement it?\
        We can retrieve user’s locale by getting the locale coming from the payload, which is now converted to 
        `RequestEnvelope` class. This object has pretty much everything that comes from Alexa JSON payload. 
        After, we can get the locale object by calling the `Locale.forLanguageTag` method.
        ```
        Request request = handlerInput.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;
        Intent intent = intentRequest.getIntent();
        Locale locale = Locale.forLanguageTag(intentRequest.getLocale());
        ```
        Now with locale, we are able to internationalize using the `ResourceBundle` class. Let's create a Helper class 
        that will get the corresponding String from a given key.
        This helper will be called `I18nResource` and to get a String, we call `getString` method passing the key and locale:
        `I18nResource.getString("welcome", locale);`

        Getting back to checking category, we can get the slots from the payload in the same way as we got the locale. 
        The intent object has all the slots that we configured for our skill on the Alexa console. It’s a `Map<String, Slot>` 
        where you get the slot through the `get` method.\
        Since we’ve set *COLORING_PAGE_TYPE* as the name of the slot, we just call the `get` method of the Map passing 
        the name of the Slot.\
        Now we only need to check whether the category is null or not.\
        We can also check if Alexa was able to map what the user said to a specific slot. 
        ```
        Slot coloringPageType = slots.get(COLORING_PAGE_TYPE);
        if (coloringPageType != null && coloringPageType.getResolutions() != null
              && coloringPageType.getResolutions().toString().contains("ER_SUCCESS_MATCH")) {
             return handleCategory(handlerInput, locale, coloringPageType);
        }
        return askToSortCategory(handlerInput, locale);
        ```

        So far, the code looks like this:
        ```
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
        ```
        After this validation, we are able to implement each response properly.

        1. #####Implementing `askToSortCategory` method:
            This method corresponds to when user has **not** specified a category.\
            There's no secret in this method, we'll just need to ask the user if he wants us to pick a category for him.

            ```
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
            ```
            Easy enough. The only thing to note here, is that we are setting to the session attributes the flag
            *SHOULD_PICK_CATEGORY* with **true** value. We're doing this so we can known that the user has passed to this
            part of the interaction. 

            Now that we have asked the user, we need to wait for his answer, our question kind of forces
            him to answer "Yes" or "No", but nothing stop the user from saying anything else. That's why we have to have the
            `AMAZON.FallbackIntent`, to handle those undesired answers gracefully. Let's focus on "Yes/No" answers which are 
            the acceptable answers for us.

            1. Let's start with **"Yes"**:\
                So the flow would be as follows:
                ![alt text](images/yes_interaction.jpg)
                So when the user says "yes", Alexa will send us the Intent `AMAZON.YesIntent`.
                We should be ready to handle that by creating a RequestHandler that handles the Yes Intent.
                So, now that we known the user wants us to pick a category, we'll sort/get a category, and then send to the 
                HP Printer Skill.

                In this example, for comprehension sake, we've created a separated handler for YesIntent, the `YesIntentHandler`.
                This Handler is also pretty simple.
                ```
                public class YesIntentHandler implements RequestHandler {

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
                ```
                Note that we're getting the flag *SHOULD_PICK_CATEGORY* from the session attributes, with this value, we known
                the user has passed to the first part of the interaction. If it's `true`, he allowed us to select a category,
                otherwise it means that he said "yes" with no context, for example: he opened the skill and said "yes".
                After we get a random category we can send it to the **HP Printer Skill** which is exactly what the method 
                `sendToHpPrinter` does which we'll check in a minute.

            2. What about **"No**?\
                Here's the flow:
                ![alt text](images/no_interaction.jpg "No interaction")

                If a user says "No", it means he does not want us to pick the category, he prefers to choose a category.
                Alexa will send us `AMAZON.NoIntent`, let's handle it with `NoIntentHandler`.

                ```
                public class NoIntentHandler implements RequestHandler {

                    @Override
                    public boolean canHandle(HandlerInput handlerInput) {
                        return handlerInput.matches(Predicates.intentName("AMAZON.NoIntent"));
                    }

                    @Override
                    public Optional<Response> handle(HandlerInput handlerInput) {
                        Locale locale = Locale.forLanguageTag(handlerInput.getRequestEnvelope().getRequest().getLocale());

                        return handlerInput.getResponseBuilder()
                                .withSpeech(I18nResource.getString("no_message", locale))
                                .withShouldEndSession(false)
                                .build();
                    }
                }
                ```
                Again nothing new in this class, we're just asking the user what category he wants. If the user answer the question
                correctly, it'll trigger the `ColoringPagesIntent` with the category slot populated properly, otherwise it'll
                again trigger the `AMAZON.FallbackIntent`.\
                That's it for `askForCategory` method. Let's move on to `handleCategory` method.

        2. #####Implementing `handleCategory` method and working with `Skill Connections`:
            In this method is where all the "magic" happens. It's where we use [`Skill Connections`](https://developer.amazon.com/docs/custom-skills/skill-connections.html)
             to ask **HP Printer Skill* to print the selected coloring page.
            ```
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
            ```
            No big deal here, just getting the name and url of the given category, then get the text and text card from our 
            message resources and then prepare to return to the user the message.

            But this time we're adding a [directive](https://developer.amazon.com/docs/alexa-voice-service/interaction-model.html#interfaces)
            to the response called `SendRequestDirective` from the [Skill Connections](https://developer.amazon.com/docs/custom-skills/skill-connections.html).
            This directive is responsible to ask Alexa to call HP Printer Skill and send the coloring pages to it.
            Basically the Payload to be sent is pretty tied to the connection Name. Which means that there are different
            attributes for each kind of connection. For sending jobs to be printed to HP Printer Skill, we'll working with
            connection name **"PRINT"**. So the acceptable attributes for this connections are:
            - **@version**
            - **@type**: *PrintImageRequest*, *PrintPDFRequest*, *PrintWebPageRequest*
            - **title**: Title of the document to be printed
            - **description**: description of the document to be printed
            - **url**: url of the document to be printed

            Let's take a look at the `hpPrinterDirective` method and the payload creation:
            ```
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
            ```
            The first thing we create is the payload, which is nothing more than a `Map<String, Object>`. 
            We populate the required attributes with our resource information and after that, we generate the 
            `SendRequestDirective` through the `SendRequestDirectiveBuilder`, specifying that we want to use the name
            connections as PRINT. We set the payload and a token and that's it!
            Our response is completely ready to be sent to Alexa, so it can be forwarded to HP Printer Skill.

            Alexa will ask us if we want to print the document using *HP Printer Skill*, and after we agree to use it,
            if you're linked, the HP Printer Skill will send a card with a link for on-boarding. There we can set our 
            printer email address. But if you're already linked, the document will be sent successfully to the HP Printer
            Skill which will do its job, which is print our coloring page!

            After the job is sent, Alexa will return to our skill a request containing the status of the process.
            If everything goes well, it'll be a success or else an error with description.

            Let's create a class to handle that.

            ```
            @Override
            public class ConnectionsResponseIntentHandler implements RequestHandler {

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
            ```

            We're just checking the status and respond to Alexa accordingly. Note that if you don't want to respond anything
            you can, just returning `Optional.empty()`.