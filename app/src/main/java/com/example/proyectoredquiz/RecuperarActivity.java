package com.example.proyectoredquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarActivity extends AppCompatActivity {

    Button btn_volver, btn_restaurar;
    EditText email;
    ProgressBar cargando;
    FirebaseAuth mAuth;
    String strEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        mAuth = FirebaseAuth.getInstance();

        btn_restaurar = findViewById(R.id.restaurarR);
        btn_volver = findViewById(R.id.volverR);
        email = findViewById(R.id.correoRecuperar);
        cargando = findViewById(R.id.progressRec);

        btn_restaurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strEmail = email.getText().toString().trim();
                if (!TextUtils.isEmpty(strEmail)) {
                    ResetPassword();
                } else {
                    email.setError("Ingrese su correo");
                }
                //Intent iregistro = new Intent(RecuperarActivity.this, MainActivity.class);
                //startActivities(new Intent[]{iregistro});
            }
        });

        btn_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iregistro = new Intent(RecuperarActivity.this, MainActivity.class);
                startActivities(new Intent[]{iregistro});
            }
        });
    }

    private void ResetPassword() {
        cargando.setVisibility(View.VISIBLE);
        btn_restaurar.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RecuperarActivity.this, "Link para restaurar contraseña enviado correctamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RecuperarActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RecuperarActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                cargando.setVisibility(View.INVISIBLE);
                btn_restaurar.setVisibility(View.VISIBLE);
            }
        });
    }
}