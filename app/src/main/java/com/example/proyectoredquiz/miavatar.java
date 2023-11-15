package com.example.proyectoredquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class miavatar extends AppCompatActivity {

    Button btn_volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miavatar);

        btn_volver = findViewById(R.id.btn_volverA);

        // VOLVER A INICIO
        btn_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(miavatar.this, MenuUserActivity.class);
                startActivities(new Intent[]{index});
            }
        });
    }
}