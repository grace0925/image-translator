package com.example.image_translator;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button testBtn;
    private ImageView dog;

    private final String API_KEY = "e568cca769794e5eb8c18cdf11c01f65";
    private final String API_LINK = "https://image-translator.cognitiveservices.azure.com/";

    private VisionServiceClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (client == null) {
            client = new VisionServiceRestClient(API_KEY, API_LINK);
        }

        testBtn = (Button) findViewById(R.id.button);
        dog = (ImageView) findViewById(R.id.testImage);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Grace is here!!!!!!!!!!!!!");
            }
        });
    }

    //recognize the image with microsoft azure computer vision api
    public void recognizeImage() {
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
    }
}
