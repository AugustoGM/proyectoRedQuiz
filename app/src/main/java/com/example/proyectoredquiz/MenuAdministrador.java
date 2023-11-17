package com.example.proyectoredquiz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MenuAdministrador extends AppCompatActivity {

    Button btn_ver;
    Button btn_add, btn_cerrar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_administrador);
        mAuth = FirebaseAuth.getInstance();

        btn_add = findViewById(R.id.btn_addPregunta);
        btn_ver = findViewById(R.id.btn_ver);
        btn_cerrar = findViewById(R.id.btn_cerrar2);

        btn_ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(MenuAdministrador.this, VerReactivos.class);
                startActivities(new Intent[]{index});
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(MenuAdministrador.this, IngresarPregunta.class);
                startActivities(new Intent[]{index});
            }
        });

        // CERRAR SESIÓN
        btn_cerrar.setOnClickListener(view -> {
            // Mostrar un cuadro de diálogo de confirmación
            AlertDialog.Builder builder = new AlertDialog.Builder(MenuAdministrador.this);
            builder.setMessage("¿Estás seguro de que deseas cerrar la sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(MenuAdministrador.this, MainActivity.class));
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // No hacer nada y cerrar el cuadro de diálogo
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }
}