package com.example.image_translator;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;


import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private Button testBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testBtn = (Button) findViewById(R.id.button);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Grace is here!!!!!!!!!!!!!");
            }
        });
    }
}
