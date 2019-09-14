package com.example.image_translator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import android.widget.ImageView;

import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVision;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.Category;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageCaption;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.VisualFeatureTypes;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button testBtn;
    private ImageView dog;

    private String API_KEY = "47b9fd328cfe4004a79f9e3e665e37d7";
    private String API_LINK = "https://image-translator-computer-vision.cognitiveservices.azure.com/";
    private ComputerVisionClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (client == null) {
            client = ComputerVisionManager.authenticate(API_KEY).withEndpoint(API_LINK);
        }
        System.out.println("--------------------got client => " + client);

        testBtn = (Button) findViewById(R.id.button);
        dog = (ImageView) findViewById(R.id.testImage);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Grace is here!!!!!!!!!!!!!");

                new analyzeRemoteImage().execute(client);
                //create bitmap of hardcorded dog image
                /*BitmapDrawable drawable = (BitmapDrawable) dog.getDrawable();
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
                            String[] features = {"Description"};
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

                visionTask.execute(inputStream);*/
            }
        });
    }

    public static void AnalyzeImage(ComputerVisionClient comVisionClient) {
        String remotePath = "https://66.media.tumblr.com/03091c224694fc7b8705b2e8c1b70b9a/tumblr_inline_pjajwfl8xP1s826nn_540.jpg";

        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.DESCRIPTION);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.CATEGORIES);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.FACES);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.ADULT);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.COLOR);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.IMAGE_TYPE);

        try {
            ImageAnalysis analysis = comVisionClient.computerVision().analyzeImage()
                    .withUrl(remotePath)
                    .withVisualFeatures(featuresToExtractFromLocalImage)
                    .execute();
            System.out.println("\nCaptions: ");
            for (ImageCaption caption : analysis.description().captions()) {
                System.out.printf("\'%s\' with confidence %f\n", caption.text(), caption.confidence());
            }
        } catch (Exception e) {
            System.out.println("exception => " + e);
        }
    }

    class analyzeRemoteImage extends AsyncTask<ComputerVisionClient, String, String>{
        @Override
        protected String doInBackground(ComputerVisionClient... computerVisionClients) {
            String remotePath = "https://www.allkpop.com/upload/2019/08/content/060428/1565080101-vu7hr9pclec11.jpg";

            List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.DESCRIPTION);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.CATEGORIES);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.FACES);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.ADULT);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.COLOR);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.IMAGE_TYPE);

            try {
                ImageAnalysis analysis = computerVisionClients[0].computerVision().analyzeImage()
                        .withUrl(remotePath)
                        .withVisualFeatures(featuresToExtractFromLocalImage)
                        .execute();
                System.out.println("\nCaptions: ");
                for (ImageCaption caption : analysis.description().captions()) {
                    System.out.printf("\'%s\' with confidence %f\n", caption.text(), caption.confidence());
                }

                /*System.out.println("\nCategories: ");
                for (Category category : analysis.description().)*/
            } catch (Exception e) {
                System.out.println("exception => " + e);
            }
            return null;
        }
    }
}
