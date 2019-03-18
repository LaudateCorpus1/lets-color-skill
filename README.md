# lets-color-skill
Sample Connected Skill for Alexa


How to create a skill that requests HP Printer Skill:

Here’s the prerequisites There are two steps that you need to do to create your Skill
 
1 - Create your skill on https://developer.amazon.com
2 - Create your application it can be a web service or a lambda function (in this tutorial, it’ll be a lambda function).


About the Skill:
1 - Go to https://developer.amazon.com/. Create an account in case you don’t have it.
2 - Then go https://developer.amazon.com/alexa/console/ask
3 - Click the “Create Skill” button in the upper right. Enter “HelloWorld” as your skill name. On the next page, select “Custom” and click “Create skill”.




Let’s Color Skill:

1. About the skill
Let’s create a skill that has a real purpose. The goal here is to show how to communicate and request a job to HP printer skill from another skill. For that our skill will need to have printable content. So, the content that our skill will provide will be coloring pages. Let’s call our skill as “let’s color”, or known as “requester skill”.
So our flow will look like:
![alt text](/home/marcelo/Downloads/Sample_Connected_Skill.jpg)
The image is self explanatory, but as you can see, the way our skill will communicate with HP Printer skill is made by Amazon Skill Connections. 

2. Create the skill
For this tutorial, our skill will be written in Java language using the official SDK that Amazon provides us. The interaction between the user and Alexa should be simple but we’ll have to handle unpredictable situations (Not so unpredictable).
    1. #### Intent:
        Our skill has only one custom Intent which should handle every Coloring Pages interaction, let’s call it “ColoringPagesIntent”. 
        We also need to include the Amazon.YesIntent and the Amazon.NoIntent Built In Intents. The Amazon.FallbackIntent, Amazon.StopIntent and Amazon.CancelIntent should be already there after we create the skill.
        
            So the Intents that we’ll be using and need to create are:
            * ColoringPagesIntent
            * Amazon.YesIntent
            * Amazon.NoIntent
            * Amazon.FallbackIntent
            * Amazon.StopIntent
            * Amazon.CancelIntent
            
            The Fallback intent is for when Alexa doesn’t understand clearly what you said. The last two intentions are to close the skill elegantly.
        
            Let’s focus on our main custom Intent: ColoringPagesIntent. We’re going to accept two ways of asking for coloring pages: Specifying the category of coloring pages or just ask for a coloring pages without specifying the category.
            Examples of samples for training our Intent:
            * "print `{COLORING_PAGE_TYPE}` coloring page",
            * "print a coloring page from `{COLORING_PAGE_TYPE}` category”,
            * "print coloring pages",
            * "print a coloring page"
            * "coloring page",

    2. #### Slot:
        Now we need to create a slot so we can use it in our Custom Intent.
        Let’s call it COLORING_PAGE_TYPE. The slot values that we’re going to support are: ANIMALS, FRUITS, ALPHABET, NATURE and THINGS.
    
        Don’t forget to add the slot to the intent. We’re using COLORING_PAGE_TYPE as name as well.

    3. #### Developing let’s color skill:
    
        After setting up the invocation name, endpoint, intents and slot, we’re ready to implement the code. 
        If you just want to look at the code, you can find it here.

    4. #### Handling ColoringPagesIntent:
    
        Okay, the first thing we need to have in mind is the fact that user can or cannot specify the kind of category he wants. Based on that, if the user doesn’t specify a category, we’ll ask if we can pick a category for him. In other words, we’ll sort the available categories and pick one. Or we’ll just get the category he said and then send to the printer.
        
        Then the interaction should be as follows:
        ![alt text](/home/marcelo/Downloads/happy_path_1.jpg)
        AND
        ![alt text](/home/marcelo/Downloads/happy_path_2.jpg)
        
        Very simple. 
        
        Let’s implement our class then. 
        The first method that we need to implement is the “canHandle” so we can tell the SDK that we’re handling all requests coming from ColoringPagesIntent.
        
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
        Okay, now we need to implement the `handle` method, which is basically what we are going to do to fulfill the coming Intent. In our case, send the coloring pages to the HP Printer. But first we need to verify whether the user has provided a category before sending the coloring page.
        
        Since Java allows us to internationalize in a very simple way and the payload that Alexa sends us always come with the locale, why not implement it?
        We can retrieve user’s locale by getting the locale coming from the payload, which is now converted to RequestEnvelope class. This object has pretty much everything that comes from Alexa payload JSON. And the we can get the locale object by calling the forLanguageTag method.
        ```
        Request request = handlerInput.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;
        Intent intent = intentRequest.getIntent();
        Locale locale = Locale.forLanguageTag(intentRequest.getLocale());
        ```
        Now with locale, we are able to internationalize using ResourceBundle class.
        
        Okay, getting back to checking category. We can get the slots from the payload in the same way as we got the locale, the intent object has all the slots that we configured on the skill on the Alexa console. It’s a HashMap<String, Slot> where you get the slot through the get method. Since we’ve set COLORING_PAGE_TYPE as the name, we just call the get method from the HashMap passing the name of the Slot.
        It’s very simple to check if the category was provided. We check if the value is null or not.
        We also can check whether the category is similar to what the user said, in other words, if Alexa could map what the user said to a specific slot. 
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
        
