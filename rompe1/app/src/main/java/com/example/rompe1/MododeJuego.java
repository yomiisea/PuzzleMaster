package com.example.rompe1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MododeJuego extends AppCompatActivity {
    Button btPartImg, btPartNum,btVersus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mododejuego);  // Esto enlaza el layout inicio.xml
        btPartImg=findViewById(R.id.btPartImg);
        btPartNum=findViewById(R.id.btPartNum);
        btVersus=findViewById(R.id.btVersus);
        btPartImg.setOnClickListener(v -> {
            Intent intent = new Intent(MododeJuego.this, MainActivity.class);
            startActivity(intent);
        });
        btPartNum.setOnClickListener(v -> {
            Intent intent = new Intent(MododeJuego.this, ModoNum.class);
            startActivity(intent);
        });
        btVersus.setOnClickListener(v -> {
            Intent intent = new Intent(MododeJuego.this, Versus.class);
            startActivity(intent);
        });
    }
}
