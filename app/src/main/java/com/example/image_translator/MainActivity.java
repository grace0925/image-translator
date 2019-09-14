package com.example.image_translator;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;

import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private Button testBtn;
    private ImageView dog;

    private String API_KEY = "ef4a2f50a3344e5a9ecb0d4e637c74cb";
    private String API_LINK = "https://image-translator-computer-vision.cognitiveservices.azure.com/";
    private VisionServiceClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (client == null) {
            client = new VisionServiceRestClient(API_KEY, API_LINK);
        }
        System.out.println("--------------------got client => " + client);

        testBtn = (Button) findViewById(R.id.button);
        dog = (ImageView) findViewById(R.id.testImage);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Grace is here!!!!!!!!!!!!!");

                //create bitmap of hardcorded dog image
                BitmapDrawable drawable = (BitmapDrawable) dog.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                System.out.println("------------bit map => " + bitmap);

                //output stream
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                //compress image to an output stream
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                //put image in an input stream
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                System.out.println("--------------input Stream => " + inputStream);

                AsyncTask<InputStream, String, String> visionTask = new AsyncTask<InputStream, String, String>() {
                    @Override
                    protected String doInBackground(InputStream... inputStreams) {
                        try {
                            System.out.println("----------input streams =>" + inputStreams);
                            String[] features = {"Descriptions"};
                            String[] details = {};

                            AnalysisResult visionResult = client.analyzeImage(inputStream, features, details);

                            Gson gson = new Gson();
                            String result = gson.toJson(visionResult);
                            System.out.println("result => " + result);
                            return result;
                        } catch(VisionServiceException e) {
                            System.out.println("vision service exception");
                            System.out.println("exception => " + e);
                        } catch(IOException e) {
                            System.out.println("IO exception");
                            System.out.println("exception => " + e);
                        }
                        return null;
                    }
                };

                visionTask.execute(inputStream);
            }
        });
    }

    //recognize the image with microsoft azure computer vision api
    /*public void recognizeImage() {
        //disable button while loading
        testBtn.setEnabled(false);

        try {
            new doRecRequest().execute();
        }catch (Exception e) {
            System.out.println("error during recognition => "+ e);
        }
    }

    private String process() throws VisionServiceException, IOException {
        //Use gson to convert JSON string to equivalent java object or other way
        Gson gson = new Gson();

        //create bitmap of hardcorded dog image
        BitmapDrawable drawable = (BitmapDrawable) dog.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        //output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //compress image to an output stream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        //put image in an input stream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        OCR ocr;
        ocr = this.client.recognizeText(inputStream, LanguageCodes.AutoDetect, true);

        String result = gson.toJson(ocr);
        System.out.println("result => "+ result);

        return result;
    }

    private class doRecRequest extends AsyncTask<String, String, String> {
        public doRecRequest() {
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return process();
            } catch (Exception e) {
                System.out.println("Error during do in background => " + e);
            }
            return null;
        }
    }*/
}
