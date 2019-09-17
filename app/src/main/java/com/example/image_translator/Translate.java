package com.example.image_translator;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

class MyContext {
    Context myContext;
    String string;

    MyContext(Context myContext, String string) {
        this.myContext = myContext;
        this.string = string;
    }
}

public class Translate extends AsyncTask<MyContext, String, String> {

    OkHttpClient translateClient  = new OkHttpClient();
    private static String TRANSLATE_API_KEY = "071718bd389940289e365df91531b395";
//    private static String TRANSLATE_API_LINK = "https://image-translator-translator-text.cognitiveservices.azure.com/sts/v1.0/issuetoken";
    private static String TRANSLATE_API_LINK = "https://api.cognitive.microsofttranslator.com/";
    private String url = TRANSLATE_API_LINK + "/translate?api-version=3.0&to=zh-CN";

    private String translatedStringJSON;
    @Override
    protected String doInBackground(MyContext... myContexts) {
        try {
            translatedStringJSON = Post(myContexts);
        } catch (Exception e) {
            System.out.println("exception => " + e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.resultLinearLayout);

    }

    public String Post(String inputString) throws IOException {
        // Create JSON request body
        String requestBody = String.format("[{\"Text\": \"%s\"}]", inputString);

        System.out.println("---------------------------------");
        System.out.println("input Stirng => " + inputString);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBody);
        Request request = new Request.Builder().url(url).post(body).addHeader("Ocp-Apim-Subscription-Key", TRANSLATE_API_KEY)
                .addHeader("Content-type", "application/json").build();
        Response response = translateClient.newCall(request).execute();

        String responseBody = response.body().string();
        System.out.println("response body => " + responseBody);
        JSONTokener tokener = new JSONTokener(responseBody);

        try {
            JSONArray results = new JSONArray(tokener);

            JSONObject result = results.getJSONObject(0);
            JSONArray translations = result.getJSONArray("translations");
            JSONObject firstTranslation = translations.getJSONObject(0);
            String translation = firstTranslation.getString("text");

            System.out.printf("The translated string is %s\n", translation);
            return translation;
        } catch (Throwable t) {
            t.printStackTrace();

            return null;
        }


//
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        System.out.println("gson to json => " + gson.toJson(json));
//        singleTranslation result = gson.fromJson(gson.toJson(json), singleTranslation.class);
//        System.out.println("result object => "+ result.translationResultsArray[0].translations.text);
//
//        return gson.toJson(json);
    }

    class singleTranslation {
        translationResults[] translationResultsArray;
    }
    class translationResults {
        detectedLanguage detectedLanguage;
        translations translations;
    }
    class detectedLanguage {
        String language;
        float score;

        public detectedLanguage(String language, float score) {
            this.language = language;
            this.score = score;
        }
    }
    class translations {
        String text;
        String to;

        public translations(String text, String to) {
            this.text = text;
            this.to = to;
        }
    }






}
