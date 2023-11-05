package com.example.proyectoredquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistroActivity extends AppCompatActivity {

    Button btn_register, btn_index;
    EditText name, lastname, email, password, date, curp;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.nombre);
        lastname = findViewById(R.id.apellidos);
        email = findViewById(R.id.correo);
        password = findViewById(R.id.contrasena);
        date = findViewById(R.id.fechaN);
        curp = findViewById(R.id.curp);
        btn_register = findViewById(R.id.btn_registrar);
        btn_index = findViewById(R.id.btn_inicio);

        btn_index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(RegistroActivity.this, MainActivity.class);
                startActivities(new Intent[]{index});
            }
        });


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameUser = name.getText().toString().trim();
                String lastnameUser = lastname.getText().toString().trim();
                String emailUser = email.getText().toString().trim();
                String passUser = password.getText().toString().trim();
                String dateUser = date.getText().toString().trim();
                String curpUser = curp.getText().toString().trim();

                if (nameUser.isEmpty() && lastnameUser.isEmpty() && emailUser.isEmpty() && passUser.isEmpty() && dateUser.isEmpty() && curpUser.isEmpty()){
                    Toast.makeText(RegistroActivity.this, "Complete los datos", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(nameUser, lastnameUser, emailUser, passUser, dateUser, curpUser);
                }
            }
        });
    }

    private void registerUser(String nameUser, String lastnameUser, String emailUser, String passUser, String dateUser, String curpUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = mAuth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("nombre", nameUser);
                map.put("apellidos", lastnameUser);
                map.put("email", emailUser);
                map.put("password", passUser);
                map.put("fechaNacimiento", dateUser);
                map.put("curp", curpUser);

                mFirestore.collection("users").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        startActivity(new Intent(RegistroActivity.this, MainActivity.class));
                        Toast.makeText(RegistroActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistroActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistroActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}