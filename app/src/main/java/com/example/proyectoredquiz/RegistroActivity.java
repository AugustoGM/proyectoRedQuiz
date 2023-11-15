package com.example.proyectoredquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    EditText name, lastname, email, password, confPassword, date;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    private Spinner spinnerGender;
    private ArrayAdapter<CharSequence> genderAdapter;

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
        confPassword = findViewById(R.id.confContrasena);
        date = findViewById(R.id.fechaN);
        //curp = findViewById(R.id.curp);
        btn_register = findViewById(R.id.btn_registrar);
        btn_index = findViewById(R.id.btn_inicio);

        //SPINNER
        spinnerGender = findViewById(R.id.genero);
        genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedGender = (String) genderAdapter.getItem(i);
                Toast.makeText(RegistroActivity.this, "Género: " + selectedGender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                String confirmPassUser = confPassword.getText().toString().trim();
                String dateUser = date.getText().toString().trim();
                //String curpUser = curp.getText().toString().trim();
                String selectedGender = spinnerGender.getSelectedItem().toString();

                if (nameUser.isEmpty() || lastnameUser.isEmpty() || emailUser.isEmpty() || passUser.isEmpty() || confirmPassUser.isEmpty() || dateUser.isEmpty()){
                    Toast.makeText(RegistroActivity.this, "Complete los datos", Toast.LENGTH_SHORT).show();
                }else{
                    if (passUser.equals(confirmPassUser)){
                        registerUser(nameUser, lastnameUser, emailUser, passUser, dateUser, selectedGender);
                    } else {
                        Toast.makeText(RegistroActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void registerUser(String nameUser, String lastnameUser, String emailUser, String passUser, String dateUser, String selectedGender) {
        //String selectedGender = spinnerGender.getSelectedItem().toString();

        mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", id);
                    map.put("nombre", nameUser);
                    map.put("apellidos", lastnameUser);
                    map.put("email", emailUser);
                    map.put("password", passUser);
                    map.put("fechaNacimiento", dateUser);
                    //map.put("curp", curpUser);
                    map.put("genero", selectedGender); // Agregar el género al mapa de datos
                    map.put("vidas", 5);
                    map.put("puntaje", 0);

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
                } else {
                    Toast.makeText(RegistroActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}