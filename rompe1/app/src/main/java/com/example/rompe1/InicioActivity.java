package com.example.rompe1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InicioActivity extends AppCompatActivity {
    Button btEmpezar,btChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);
        btEmpezar = findViewById(R.id.btEmpezar);
        btChart = findViewById(R.id.btchartt);
        btEmpezar.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, MododeJuego.class);
            startActivity(intent);

        });
        btChart.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, ScoreListActivity.class);
            startActivity(intent);

        });
    }
}
