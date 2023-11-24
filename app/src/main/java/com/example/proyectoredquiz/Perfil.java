package com.example.proyectoredquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Perfil extends AppCompatActivity {

    Button btn_inicio, btn_cerrar;
    TextView nombre, correo,fecha, genero;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    private String idUser;
    ImageView fotoPerfil, editarF;
    String generoU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

        btn_inicio = findViewById(R.id.btn_inicioU);
        btn_cerrar = findViewById(R.id.btn_cerrar3);
        nombre = findViewById(R.id.nombreCompleto);
        correo = findViewById(R.id.correoElectronico);
        fecha = findViewById(R.id.fechaNacimiento);
        genero = findViewById(R.id.generoUsuario);
        editarF = findViewById(R.id.eF);
        fotoPerfil = findViewById(R.id.fotoFoto);

        btn_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(Perfil.this, MenuUserActivity.class);
                startActivities(new Intent[]{index});
            }
        });

        DocumentReference documentReference = fStore.collection("rqUsers").document(idUser);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    generoU = document.getString("genero");
                    if (generoU.equals("Masculino")){
                        fotoPerfil.setImageResource(R.drawable.profilemen);
                    } else {
                        fotoPerfil.setImageResource(R.drawable.profilewoman);
                    }
                    String nombreCompleto = document.getString("nombre") + " " + document.getString("apellidos");
                    nombre.setText(nombreCompleto);
                    correo.setText(document.getString("email"));
                    fecha.setText(document.getString("fechaNacimiento"));
                    genero.setText(document.getString("genero"));
                }
            } else {
                // Manejar el error, si es necesario
            }
        });

    }
}