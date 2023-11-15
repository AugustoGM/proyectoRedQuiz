package com.example.proyectoredquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuUserActivity extends AppCompatActivity {

    Button btn_exit, btn_perfil, btn_jugar, btn_puntaje, btn_avatar;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    private TextView nombreUsuario, vidas, mensajeTiempo;
    private String idUser;
    private TextClock textClock;
    private CountDownTimer countDownTimer;
    private int tiempoRestante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_user);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

        mensajeTiempo = findViewById(R.id.mensaje);
        btn_exit = findViewById(R.id.btn_cerrar);
        btn_perfil = findViewById(R.id.btn_perfil);
        btn_avatar = findViewById(R.id.btn_avatar);
        btn_jugar = findViewById(R.id.btn_jugar);
        btn_puntaje = findViewById(R.id.btn_puntaje);
        nombreUsuario = findViewById(R.id.nombreU);
        vidas = findViewById(R.id.vidasUsuario);

        textClock = findViewById(R.id.clock);
        tiempoRestante = 15000;

        mensajeVidas();

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
                nombreUsuario.setText(documentSnapshot.getString("nombre"));
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

        // ACCEDER AL AVATAR
        btn_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(MenuUserActivity.this, miavatar.class);
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
                    Intent index = new Intent(MenuUserActivity.this, PreguntaActivity.class);
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

        // COUNT
        countDownTimer = new CountDownTimer(tiempoRestante, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestante = (int) millisUntilFinished;

                // Actualizar el tiempo en el TextClock
                if (vidas.getText().toString().equals("5 vidas")) {
                    // Mostrar "00:00:00" si vidas es igual a 5
                    textClock.setFormat24Hour("00");
                } else {
                    // Mostrar solo los segundos restantes
                    textClock.setFormat24Hour("ss");
                    textClock.setText(String.valueOf(tiempoRestante / 1000));
                }
            }

            @Override
            public void onFinish() {
                // Regenerar una vida y actualizar en la base de datos
                regenerarVida();
                // Reiniciar el contador
                tiempoRestante = 15000;
                countDownTimer.start();
            }
        };

        // Iniciar el contador
        countDownTimer.start();
    }

    private void mensajeVidas() {
        DocumentReference documentReference = fStore.collection("users").document(idUser);

        documentReference.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                // Manejar el error, si es necesario
                Log.e("ERROR", "Error al escuchar cambios en el documento", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                int vidasActuales = documentSnapshot.getLong("vidas").intValue();

                // Verificar si el número de vidas es menor que 5 antes de regenerar
                if (vidasActuales == 5) {
                    mensajeTiempo.setText("¡Vidas Completas!");
                } else {
                    mensajeTiempo.setText("Tiempo de regeneración para la próxima vida:");
                }
            }
        });
    }


    // Método para regenerar una vida y actualizar en la base de datos
    private void regenerarVida() {
        // Obtener las vidas actuales del usuario
        DocumentReference documentReference = fStore.collection("users").document(idUser);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    int vidasActuales = document.getLong("vidas").intValue();

                    // Verificar si el número de vidas es menor que 5 antes de regenerar
                    if (vidasActuales < 5) {
                        // Incrementar el número de vidas y actualizar en la base de datos
                        documentReference.update("vidas", vidasActuales + 1);
                    } else {
                        // Detener el contador si el número de vidas es igual a 5
                        countDownTimer.cancel();
                    }
                }
            } else {
                Log.d("TAG", "Error obteniendo documento: ", task.getException());
            }
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
