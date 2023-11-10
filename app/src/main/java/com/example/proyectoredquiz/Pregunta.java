package com.example.proyectoredquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Pregunta extends AppCompatActivity {

    Button btn_volver, boton1, boton2, boton3, boton4;
    TextView pregunta, categoria, vidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);

        btn_volver = findViewById(R.id.btn_volverJ);
        boton1 = findViewById(R.id.r1);
        boton2 = findViewById(R.id.r2);
        boton3 = findViewById(R.id.r3);
        boton4 = findViewById(R.id.r4);

        pregunta = findViewById(R.id.preguntaJ);
        categoria = findViewById(R.id.categoriaJ);
        vidas = findViewById(R.id.vidasJ);

        btn_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(Pregunta.this, MenuUserActivity.class);
                startActivities(new Intent[]{index});
            }
        });
    }
}