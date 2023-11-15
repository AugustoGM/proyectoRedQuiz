package com.example.proyectoredquiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MenuUserActivity extends AppCompatActivity {

    Button btn_exit, btn_perfil, btn_jugar, btn_puntaje;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    private TextView nombreUsuario, vidas;
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_user);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

        btn_exit = findViewById(R.id.btn_cerrar);
        btn_perfil = findViewById(R.id.btn_perfil);
        btn_jugar = findViewById(R.id.btn_jugar);
        btn_puntaje = findViewById(R.id.btn_puntaje);
        nombreUsuario = findViewById(R.id.nombreU);
        vidas = findViewById(R.id.vidasUsuario);

        btn_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(MenuUserActivity.this, Perfil.class);
                startActivities(new Intent[]{index});
            }
        });

        // OBTENER LAS VIDAS DEL USUARIO
        DocumentReference documentReference = fStore.collection("users").document(idUser);
        documentReference.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                // Manejar el error, si es necesario
                Log.e("ERROR", "Error al escuchar cambios en el documento", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Actualizar el valor de las vidas en el TextView
                int vidasDisponibles = documentSnapshot.getLong("vidas").intValue();
                vidas.setText(String.valueOf(vidasDisponibles)+ " vidas");
            }
        });

        // ACCEDER A PUNTAJE
        btn_puntaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(MenuUserActivity.this, MiPuntaje.class);
                startActivities(new Intent[]{index});
            }
        });

        // INICIAR JUEGO
        btn_jugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verificar si el usuario tiene vidas disponibles
                int vidasDisponibles = Integer.parseInt(vidas.getText().toString().replaceAll("\\D+", ""));

                if (vidasDisponibles > 0) {
                    // El usuario tiene vidas, iniciar el juego
                    Intent index = new Intent(MenuUserActivity.this, Pregunta.class);
                    startActivities(new Intent[]{index});
                } else {
                    // El usuario no tiene vidas, mostrar un mensaje
                    mostrarMensajeSinVidas();
                }
            }
        });

        // CERRAR SESIÓN
        btn_exit.setOnClickListener(view -> {
            // Mostrar un cuadro de diálogo de confirmación
            AlertDialog.Builder builder = new AlertDialog.Builder(MenuUserActivity.this);
            builder.setMessage("¿Estás seguro de que deseas cerrar la sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(MenuUserActivity.this, MainActivity.class));
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // No hacer nada y cerrar el cuadro de diálogo
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    // Método para mostrar el mensaje cuando el usuario no tiene vidas
    private void mostrarMensajeSinVidas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuUserActivity.this);
        builder.setTitle("¡Vidas Agotadas!");
        builder.setMessage("Espera un tiempo para que se regeneren nuevamente.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Cerrar el cuadro de diálogo
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
