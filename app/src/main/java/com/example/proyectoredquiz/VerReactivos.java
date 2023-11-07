package com.example.proyectoredquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VerReactivos extends AppCompatActivity {

    Button btn_agregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_reactivos);

        btn_agregar = findViewById(R.id.btn_add);

        btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(VerReactivos.this, IngresarPregunta.class);
                startActivities(new Intent[]{index});
            }
        });
    }
}