package com.example.image_translator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVision;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.Category;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.FaceDescription;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageCaption;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageTag;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.LandmarksModel;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.VisualFeatureTypes;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button testBtn, openCameraBtn;
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
        openCameraBtn = (Button) findViewById(R.id.openCamera);
        dog = (ImageView) findViewById(R.id.testImage);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Grace is here!!!!!!!!!!!!!");

                new analyzeRemoteImage().execute(client);
            }
        });

        openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        dog.setImageBitmap(bitmap);
        MyBitMap cameraBitmap = new MyBitMap(bitmap, client);
        new analyzeCameraImage(this).execute(cameraBitmap);
    }

    private static class MyBitMap {
        Bitmap myBitmap;
        ComputerVisionClient comVisionClient;

        MyBitMap(Bitmap myBitmap, ComputerVisionClient comVisionClient) {
            this.myBitmap = myBitmap;
            this.comVisionClient = comVisionClient;
        }
    }

    public class analyzeCameraImage extends AsyncTask<MyBitMap, String, String> {

        String englishResult;
        private WeakReference<Context> contextRef;

        public analyzeCameraImage(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(MyBitMap... myBitMaps) {
            List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.DESCRIPTION);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.CATEGORIES);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.FACES);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.ADULT);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.COLOR);
            featuresToExtractFromLocalImage.add(VisualFeatureTypes.IMAGE_TYPE);
            //convert new photo drawable to byte array
            Bitmap cameraBitmap = myBitMaps[0].myBitmap;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] cameraPhotoByteArray = outputStream.toByteArray();

            //call microsoft azure api
            ImageAnalysis analysis = myBitMaps[0].comVisionClient.computerVision().analyzeImageInStream()
                    .withImage(cameraPhotoByteArray)
                    .withVisualFeatures(featuresToExtractFromLocalImage)
                    .execute();

            StringBuffer englishResultBuffer = new StringBuffer();

            System.out.println("\nCaptions: ");
            for (ImageCaption caption : analysis.description().captions()) {
                System.out.printf("\'%s\' with confidence %f\n", caption.text(), caption.confidence());
                englishResultBuffer.append(caption.text());
            }
            englishResult = englishResultBuffer.toString();
            System.out.println("--------------------------");
            System.out.println("english result => " + englishResult);

            System.out.println("\nCategories: ");
            for (Category category : analysis.categories()) {
                System.out.printf("\'%s\' with confidence %f\n", category.name(), category.score());
            }

            System.out.println("\nTags: ");
            for (ImageTag tag : analysis.tags()) {
                System.out.printf("\'%s\' with confidence %f\n", tag.name(), tag.confidence());
            }

            System.out.println("\nFaces: ");
            for (FaceDescription face : analysis.faces()) {
                System.out.printf("\'%s\' of age %d at location (%d, %d), (%d, %d)\n", face.gender(), face.age(),
                        face.faceRectangle().left(), face.faceRectangle().top(),
                        face.faceRectangle().left() + face.faceRectangle().width(),
                        face.faceRectangle().top() + face.faceRectangle().height());
            }

            System.out.println("\nLandmarks: ");
            for (Category category : analysis.categories())
            {
                if (category.detail() != null && category.detail().landmarks() != null)
                {
                    for (LandmarksModel landmark : category.detail().landmarks())
                    {
                        System.out.printf("\'%s\' with confidence %f\n", landmark.name(), landmark.confidence());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("--------------------------");
            System.out.println("on post exe");
            System.out.println("english result => " + englishResult);

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.resultLinearLayout);
            linearLayout.removeAllViews();
            Context myContext = contextRef.get();
            TextView resultText = new TextView(myContext);
            resultText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            resultText.setText(englishResult);
            resultText.setTextColor(Color.parseColor("#486463"));
            resultText.setTextSize(30);
            resultText.setGravity(Gravity.CENTER);
            linearLayout.addView(resultText);
        }
    }

    class analyzeRemoteImage extends AsyncTask<ComputerVisionClient, String, String>{
        @Override
        protected String doInBackground(ComputerVisionClient... computerVisionClients) {
            String remotePath = "https://peopledotcom.files.wordpress.com/2019/08/shawn-camila-2.jpg";

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

                System.out.println("\nCategories: ");
                for (Category category : analysis.categories()) {
                    System.out.printf("\'%s\' with confidence %f\n", category.name(), category.score());
                }
            } catch (Exception e) {
                System.out.println("exception => " + e);
            }
            return null;
        }
    }
}
