package com.example.proyectoredquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Pregunta extends AppCompatActivity {

    Button btn_volver, boton1, boton2, boton3, boton4;
    TextView pregunta, categoria, vidas, color;
    private FirebaseFirestore db;
    private CollectionReference preguntasCollection;
    private String idPreguntaActual;
    private ProgressBar duracion;
    int counter = 0;
    private Handler handler;

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
                Intent index = new Intent(Pregunta.this, MenuUserActivity.class);
                startActivities(new Intent[]{index});
            }
        });

        // ASIGNAR RESPUESTAS Y PREGUNTA
        getQuestion();
        iniciarProgreso(); // Pasa la vista adecuada

        // ONCLICK PARA LOS BOTONES
        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarRespuesta(boton1);
            }
        });

        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarRespuesta(boton2);
            }
        });

        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarRespuesta(boton3);
            }
        });

        boton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarRespuesta(boton4);
            }
        });

    }

    // BARRA DE PROGRESO
    public void iniciarProgreso() {
        final Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                counter++;
                duracion.setProgress(counter);

                if (counter == 100)
                    t.cancel();
            }
        };
        t.schedule(tt,0,100);
    }

    // ASIGNAR VALORES A LOS BOTONES DE FORMA ALEATORIA
    private void getQuestion() {
        preguntasCollection.limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()){
                    idPreguntaActual = document.getId();

                    String Pregunta = document.getString("pregunta");
                    String Categoria = document.getString("categoria");
                    String correcta = document.getString("correcta");
                    String incorrecta1 = document.getString("incorrecta1");
                    String incorrecta2 = document.getString("incorrecta2");
                    String incorrecta3 = document.getString("incorrecta3");
                    String rating = document.getString("rating");

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
                    Log.d("TAG", "Valor de Categoria: " + Categoria);

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
                }
            } else {
                Exception exception = task.getException();
                if (exception != null){
                    exception.printStackTrace();
                }
            }
        });
    }

    private void verificarRespuesta(Button boton) {
        String respuestaSeleccionada = boton.getText().toString();

        preguntasCollection.limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Button botonCorrecto = null; // Variable para almacenar el botón correcto

                for (QueryDocumentSnapshot document : task.getResult()) {
                    idPreguntaActual = document.getId();
                    String respuestaCorrecta = document.getString("correcta");

                    if (boton1.getText().toString().equals(respuestaCorrecta)) {
                        botonCorrecto = boton1;
                    } else if (boton2.getText().toString().equals(respuestaCorrecta)) {
                        botonCorrecto = boton2;
                    } else if (boton3.getText().toString().equals(respuestaCorrecta)) {
                        botonCorrecto = boton3;
                    } else if (boton4.getText().toString().equals(respuestaCorrecta)) {
                        botonCorrecto = boton4;
                    }
                }

                // Ahora puedes usar la variable botonCorrecto fuera de la lambda
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String respuestaCorrecta = document.getString("correcta");
                    if (botonCorrecto != null) {
                        if (respuestaSeleccionada.equals(respuestaCorrecta)) {
                            // Respuesta correcta, cambiar color a verde
                            boton.setBackgroundColor(Color.GREEN);
                            // Aquí puedes hacer otras acciones relacionadas con la respuesta correcta
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            cargarSiguientePregunta();
                                        }
                                    },
                                    2000 // 1 segundo de retraso
                            );
                        } else {
                            // Respuesta incorrecta, cambiar color a rojo
                            boton.setBackgroundColor(Color.RED);
                            // Cambiar el color del botón correcto a verde
                            botonCorrecto.setBackgroundColor(Color.GREEN);
                            // Aquí puedes hacer otras acciones relacionadas con la respuesta incorrecta
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            cargarSiguientePregunta();
                                        }
                                    },
                                    2000 // 1 segundo de retraso
                            );
                        }
                }
                }
            }
        });
    }

    private void cargarSiguientePregunta() {
        // Antes de cargar la siguiente pregunta, verifica si hay más preguntas disponibles
        // Puedes hacer esto comparando el número total de preguntas con el número de preguntas ya mostradas o mediante otro criterio de tu elección
        // Si no hay más preguntas, puedes mostrar un mensaje o realizar otra acción
        // En este ejemplo, simplemente se obtiene el total de documentos en la colección "preguntas"
        preguntasCollection.get().addOnCompleteListener(totalTask -> {
            if (totalTask.isSuccessful()) {
                int totalPreguntas = totalTask.getResult().size();
                if (totalPreguntas > 1) {
                    // Si hay más preguntas, carga la siguiente pregunta
                    getQuestion();
                } else {
                    // Si no hay más preguntas, realiza la acción que consideres apropiada (mostrar mensaje, volver a la actividad anterior, etc.)
                    // Por ejemplo, mostrar un mensaje y cerrar la actividad actual
                    Toast.makeText(this, "¡Has respondido todas las preguntas!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                // Maneja el caso en que no se pueda obtener el número total de preguntas
                Toast.makeText(this, "Error al obtener preguntas", Toast.LENGTH_SHORT).show();
                // Puedes realizar otras acciones, como volver a la actividad anterior, por ejemplo
                finish();
            }
        });
    }


}