package com.example.proyectoredquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PreguntaActivity extends AppCompatActivity {
    Button btn_volver, boton1, boton2, boton3, boton4;
    TextView pregunta, categoria, vidas, color;
    FirebaseAuth mAuth;
    private String idUser;
    private FirebaseFirestore db;
    private CollectionReference preguntasCollection;
    private String idPreguntaActual;
    private ProgressBar duracion;
    int counter = 0;
    private Handler handler;
    private Timer progressBarTimer;
    private TimerTask progressBarTimerTask;
    private int preguntaActualIndex = 0;
    private List<DocumentSnapshot> preguntasList;
    private  int VIDAS;

    public static class SoundManager {
        private static MediaPlayer mediaPlayerCorrecta;
        private static MediaPlayer mediaPlayerIncorrecta;

        public static void reproducirSonidoCorrecto(Context context) {
            try {
                mediaPlayerCorrecta = MediaPlayer.create(context, R.raw.success);
                mediaPlayerCorrecta.start();
                mediaPlayerCorrecta.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        mediaPlayerCorrecta = null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void reproducirSonidoIncorrecto(Context context) {
            try {
                mediaPlayerIncorrecta = MediaPlayer.create(context, R.raw.negative);
                mediaPlayerIncorrecta.start();
                mediaPlayerIncorrecta.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        mediaPlayerIncorrecta = null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SoundManager.mediaPlayerCorrecta != null) {
            SoundManager.mediaPlayerCorrecta.release();
            SoundManager.mediaPlayerCorrecta = null;
        }
        if (SoundManager.mediaPlayerIncorrecta != null) {
            SoundManager.mediaPlayerIncorrecta.release();
            SoundManager.mediaPlayerIncorrecta = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

        btn_volver = findViewById(R.id.btn_volverJ);
        boton1 = findViewById(R.id.r1);
        boton2 = findViewById(R.id.r2);
        boton3 = findViewById(R.id.r3);
        boton4 = findViewById(R.id.r4);

        pregunta = findViewById(R.id.preguntaJ);
        categoria = findViewById(R.id.categoriaJ);
        color = findViewById(R.id.colorCaregoria);
        vidas = findViewById(R.id.vidasJ);

        duracion = findViewById(R.id.duracion);
        handler = new Handler();

        //FIREBASE
        db = FirebaseFirestore.getInstance();
        preguntasCollection = db.collection("preguntas");

        btn_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoConfirmacion();
            }
        });

        // ASIGNAR RESPUESTAS Y PREGUNTA
        getQuestion();
        iniciarProgreso(); // Pasa la vista adecuada

        // ONCLICK PARA LOS BOTONES
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PreguntaActivity.this, "no se perdió", Toast.LENGTH_SHORT).show();
                detenerProgreso();
                verificarRespuesta(boton1);
            }
        });

        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerProgreso();
                verificarRespuesta(boton2);
            }
        });

        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerProgreso();
                verificarRespuesta(boton3);
            }
        });

        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerProgreso();
                verificarRespuesta(boton4);
            }
        });

        // OBTENER LAS VIDAS DEL USUARIO
        DocumentReference documentReference = db.collection("rqUsers").document(idUser);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Manejar el error, si es necesario
                    Log.e("ERROR", "Error al escuchar cambios en el documento", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Actualizar el valor de las vidas en el TextView
                    VIDAS = documentSnapshot.getLong("vidas").intValue();
                    vidas.setText("X " + String.valueOf(VIDAS));
                }
            }
        });

    }


    // BOTÓN VOLVER
    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Estás por dejar el juego");
        builder.setMessage("¿Seguro de que quieres volver?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onPause();
                // Acciones a realizar si el usuario hace clic en "Sí"
                volver();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Acciones a realizar si el usuario hace clic en "No" o cierra el diálogo
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void volver() {
        Intent index = new Intent(PreguntaActivity.this, MenuUserActivity.class);
        startActivities(new Intent[]{index});
    }

    // BARRA DE PROGRESO
    // BARRA DE PROGRESO
    public void iniciarProgreso() {
        progressBarTimer = new Timer();
        progressBarTimerTask = new TimerTask() {
            @Override
            public void run() {
                counter++;
                duracion.setProgress(counter);

                if (counter == 100) {
                    detenerProgreso();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (VIDAS > 0) {
                                // Si el usuario aún tiene vidas, restar una vida
                                VIDAS = VIDAS - 1;

                                // Actualizar el valor de "vidas" en Firestore
                                DocumentReference userDocumentRef = db.collection("rqUsers").document(idUser);
                                userDocumentRef
                                        .update("vidas", VIDAS)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("DEBUG", "Valor de vidas actualizado correctamente en Firestore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("ERROR", "Error al actualizar el valor de vidas en Firestore", e);
                                            }
                                        });
                            } else {
                                // El usuario ya no tiene vidas, mostrar mensaje
                                mostrarMensajeSinVidas();
                            }
                            preguntaActualIndex ++;
                            cargarSiguientePregunta();
                        }
                    });
                }
            }
        };
        progressBarTimer.schedule(progressBarTimerTask, 0, 100);
    }



    // DETENER BARRA DE PROGRESO
    public void detenerProgreso() {
        if (progressBarTimer != null) {
            progressBarTimer.cancel();
            progressBarTimer = null;
            counter = 0; // Reiniciar el contador a cero
            duracion.setProgress(counter);
        }
    }

    private void getQuestion() {
        resetColoresBotones(); // Restablecer colores de los botones

        // Verificar si hay preguntas disponibles
        if (preguntasList == null || preguntaActualIndex >= preguntasList.size()) {
            // Obtener una nueva lista de preguntas
            preguntasCollection.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    preguntasList = task.getResult().getDocuments();
                    mostrarPreguntaActual();

                    // Incrementar el índice después de mostrar la pregunta actual
                    //preguntaActualIndex++;
                } else {
                    // Manejar el error si es necesario
                }
            });
        } else {
            // Mostrar la pregunta actual
            mostrarPreguntaActual();

            // Incrementar el índice si ya hay preguntas disponibles en la lista
            //preguntaActualIndex++;
        }
    }


    // ASIGNAR VALORES A LOS BOTONES DE FORMA ALEATORIA
    private void mostrarPreguntaActual() {
        // Limpiar acciones previas de los botones
        boton1.setOnClickListener(null);
        boton2.setOnClickListener(null);
        boton3.setOnClickListener(null);
        boton4.setOnClickListener(null);

        DocumentSnapshot document = preguntasList.get(preguntaActualIndex);

        String Pregunta = document.getString("pregunta");
        String Categoria = document.getString("categoria");
        String correcta = document.getString("correcta");
        String incorrecta1 = document.getString("incorrecta1");
        String incorrecta2 = document.getString("incorrecta2");
        String incorrecta3 = document.getString("incorrecta3");
        String puntos = document.getString("puntos");

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            numbers.add(i);
        }

        // Shuffle the list to get random order
        Collections.shuffle(numbers);

        List<String> respuestas = new ArrayList<>();
        for (int j = 0; j < numbers.size(); j++){
            if (numbers.size() > 0 && numbers.get(j) == 1){
                respuestas.add(correcta);
            }
            if (numbers.size() > 0 && numbers.get(j) == 2){
                respuestas.add(incorrecta1);
            }
            if (numbers.size() > 0 && numbers.get(j) == 3){
                respuestas.add(incorrecta2);
            }
            if (numbers.size() > 0 && numbers.get(j) == 4){
                respuestas.add(incorrecta3);
            }
        }

        // Assign each number to a button
        pregunta.setText(Pregunta);

        if (Categoria.equals("Signos Vitales")) {
            color.setBackgroundColor(ContextCompat.getColor(this, R.color.signosVitales));
            color.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.signosVitales));
        } else if (Categoria.equals("Curación")) {
            color.setBackgroundColor(ContextCompat.getColor(this, R.color.curacion));
            color.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.curacion));
        } else if (Categoria.equals("Síntomas")) {
            color.setBackgroundColor(ContextCompat.getColor(this, R.color.sintomas));
            color.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.sintomas));
        } else if (Categoria.equals("Anatomía")) {
            color.setBackgroundColor(ContextCompat.getColor(this, R.color.anatomia));
            color.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.anatomia));
        } else {
            color.setBackgroundColor(ContextCompat.getColor(this, R.color.bonus));
            color.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.bonus));
        }

        categoria.setText(Categoria);
        boton1.setText(String.valueOf(respuestas.get(0)));
        boton2.setText(String.valueOf(respuestas.get(1)));
        boton3.setText(String.valueOf(respuestas.get(2)));
        boton4.setText(String.valueOf(respuestas.get(3)));

        // Restablecer la funcionalidad de los botones
        asignarFuncionalidadBotones();
    }
//}); }

    @Override
    protected void onPause() {
        super.onPause();
        detenerProgreso();
    }

    private void asignarFuncionalidadBotones() {
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerProgreso();
                verificarRespuesta(boton1);
            }
        });

        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerProgreso();
                verificarRespuesta(boton2);
            }
        });

        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerProgreso();
                verificarRespuesta(boton3);
            }
        });

        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerProgreso();
                verificarRespuesta(boton4);
            }
        });

    }

    private void verificarRespuesta(Button boton) {
        String respuestaSeleccionada = boton.getText().toString();

        if (preguntaActualIndex < preguntasList.size()) {
            DocumentSnapshot document = preguntasList.get(preguntaActualIndex);
            String correcta = document.getString("correcta").trim();

            Log.d("DEBUG", "Respuesta seleccionada: " + respuestaSeleccionada);
            Log.d("DEBUG", "Respuesta correcta: " + correcta);

            // Verificar la respuesta seleccionada con la respuesta correcta
            if (respuestaSeleccionada.equals(correcta)) {
                // Respuesta correcta, cambiar color a verde
                boton.setBackgroundColor(Color.GREEN);
                SoundManager.reproducirSonidoCorrecto(this);
            } else {
                // Respuesta incorrecta, cambiar color a rojo
                boton.setBackgroundColor(Color.RED);
                SoundManager.reproducirSonidoIncorrecto(this);

                // Encontrar el botón correcto y cambiar su color a verde
                Button botonCorrecto = encontrarBotonRespuestaCorrecta(correcta);

                if (botonCorrecto != null) {
                    botonCorrecto.setBackgroundColor(Color.GREEN);
                }

                if (VIDAS > 0) {
                    // Si el usuario aún tiene vidas, restar una vida
                    VIDAS = VIDAS - 1;

                    // Actualizar el valor de "vidas" en Firestore
                    DocumentReference userDocumentRef = db.collection("rqUsers").document(idUser);
                    userDocumentRef
                            .update("vidas", VIDAS)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("DEBUG", "Valor de vidas actualizado correctamente en Firestore");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("ERROR", "Error al actualizar el valor de vidas en Firestore", e);
                                }
                            });
                } else {
                    // El usuario ya no tiene vidas, mostrar mensaje
                    mostrarMensajeSinVidas();
                }
            }

            // Desactivar todos los botones después de la respuesta
            boton1.setEnabled(false);
            boton2.setEnabled(false);
            boton3.setEnabled(false);
            boton4.setEnabled(false);

            // Realizar acciones relacionadas con la respuesta (por ejemplo, cargar la siguiente pregunta)
            preguntaActualIndex++;
            new Handler().postDelayed(this::cargarSiguientePregunta, 2000);
        }
    }


    private Button encontrarBotonRespuestaCorrecta(String respuestaCorrecta) {
        // Convertir todas las respuestas a minúsculas para realizar una comparación sin distinción de mayúsculas
        //String respuestaCorrectaLower = respuestaCorrecta.toLowerCase();

        if (respuestaCorrecta.equals(boton1.getText().toString())) {
            return boton1;
        } else if (respuestaCorrecta.equals(boton2.getText().toString())) {
            return boton2;
        } else if (respuestaCorrecta.equals(boton3.getText().toString())) {
            return boton3;
        } else if (respuestaCorrecta.equals(boton4.getText().toString())) {
            return boton4;
        }

        return null; // No se encontró el botón correspondiente
    }



    private void cargarSiguientePregunta() {
        counter = 0;
        // Volver a habilitar todos los botones
        boton1.setEnabled(true);
        boton2.setEnabled(true);
        boton3.setEnabled(true);
        boton4.setEnabled(true);

        // Verificar si hay más preguntas disponibles
        if (preguntaActualIndex < preguntasList.size()) {
            // Si hay más preguntas, cargar la siguiente pregunta
            getQuestion();
            iniciarProgreso();
        } else {
            // Si no hay más preguntas, restablecer el índice a cero
            preguntaActualIndex = 0;
            getQuestion();
            iniciarProgreso();
            // Realizar la acción que consideres apropiada (mostrar mensaje, volver a la actividad anterior, etc.)
            // Por ejemplo, mostrar un mensaje y cerrar la actividad actual
            //Toast.makeText(this, "¡Has respondido todas las preguntas!", Toast.LENGTH_SHORT).show();
            //onPause();
            //finish();
        }
    }

    // Método para mostrar el mensaje cuando el usuario no tiene vidas
    private void mostrarMensajeSinVidas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¡Te has quedado sin vidas! Vuelve a la interfaz principal.")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Acciones a realizar si el usuario hace clic en "Aceptar"
                    volverAMenuPrincipal();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void volverAMenuPrincipal() {
        Intent intent = new Intent(PreguntaActivity.this, MenuUserActivity.class);
        startActivity(intent);
        finish(); // Cierra la actividad actual para que el usuario no pueda volver atrás desde el menú principal
    }

    // COLOR ORIGINAL DE LOS BOTONES
    private void resetColoresBotones() {
        boton1.setBackgroundColor(ContextCompat.getColor(this, R.color.botones));
        boton2.setBackgroundColor(ContextCompat.getColor(this, R.color.botones));
        boton3.setBackgroundColor(ContextCompat.getColor(this, R.color.botones));
        boton4.setBackgroundColor(ContextCompat.getColor(this, R.color.botones));
    }

}