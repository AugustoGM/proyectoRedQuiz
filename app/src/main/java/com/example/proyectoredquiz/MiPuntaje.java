package com.example.proyectoredquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MiPuntaje extends AppCompatActivity {

    Button btn_volver;
    TextView por, signosPocentaje, curacionPorcentaje, anatomiaPorcentaje, bonusPorcentaje, sintomasPorcentaje, totales, correctos;
    FirebaseAuth mAuth;
    private String idUser;
    private FirebaseFirestore db;
    private CollectionReference conteoCollection;
    ProgressBar porcentaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_puntaje);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

        porcentaje = findViewById(R.id.progressBar);
        por = findViewById(R.id.por);

        btn_volver = findViewById(R.id.btn_volverP);
        signosPocentaje = findViewById(R.id.signosPor);
        curacionPorcentaje = findViewById(R.id.curacionPor);
        anatomiaPorcentaje = findViewById(R.id.anatomiaPor);
        bonusPorcentaje = findViewById(R.id.bonusPor);
        signosPocentaje = findViewById(R.id.signosPor);
        sintomasPorcentaje = findViewById(R.id.sintomasPor);
        totales = findViewById(R.id.reactivosT);
        correctos = findViewById(R.id.reactivosC);

        //FIREBASE
        db = FirebaseFirestore.getInstance();
        conteoCollection = db.collection("rqConteo");

        btn_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(MiPuntaje.this, MenuUserActivity.class);
                startActivities(new Intent[]{index});
            }
        });

        mostrarConteo();
    }

    private void mostrarConteo() {
        DocumentReference conteoDocumentRef = db.collection("rqConteo").document(idUser);
        conteoDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Long conteoC = document.getLong("conteoC");
                        Long conteoT = document.getLong("conteoT");

                        if (conteoC != null && conteoT != null) {
                            correctos.setText(String.valueOf(conteoC));
                            totales.setText(String.valueOf(conteoT));

                            // Calcular el porcentaje de respuestas correctas
                            double porcentajeCorrectas = (conteoC * 100.0) / conteoT;

                            // Configurar la ProgressBar
                            int porcentajeInt = (int) porcentajeCorrectas;
                            porcentaje.setProgress(porcentajeInt);

                            // Mostrar el porcentaje en un TextView
                            por.setText(String.format("%.2f%%", porcentajeCorrectas));
                        } else {
                            Log.e("ERROR", "Los valores de conteoC o conteoT son nulos");
                        }
                    } else {
                        Log.d("DEBUG", "El documento no existe en Firestore");
                    }
                } else {
                    Log.e("ERROR", "Error al obtener el documento en Firestore", task.getException());
                }
            }
        });

        // Llamada al nuevo método para obtener y mostrar el porcentaje de curación
        obtenerPorcentajeCuracion();
        obtenerPorcentajAnatomia();
        obtenerPorcentajeSignos();
        obtenerPorcentajeSintomas();
        obtenerPorcentajeBonus();
    }

    private void obtenerPorcentajeCuracion() {
        DocumentReference curacionDocumentRef = db.collection("rqCuracion").document(idUser);
        curacionDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot curacionDocument = task.getResult();

                    if (curacionDocument.exists()) {
                        // El documento de curación existe, obtén el valor de "acumulado"
                        Long acumulado = curacionDocument.getLong("acumulado");

                        if (acumulado != null) {
                            // Ahora que tenemos el valor de "acumulado", obtenemos el valor de "conteoC"
                            DocumentReference conteoDocumentRef = db.collection("rqConteo").document(idUser);

                            conteoDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot conteoDocument = task.getResult();

                                        if (conteoDocument.exists()) {
                                            // El documento de conteo existe, obtén el valor de "conteoC"
                                            Long conteoC = conteoDocument.getLong("conteoC");

                                            if (conteoC != null) {
                                                // Calcular el porcentaje y mostrarlo en la vista correspondiente
                                                double porcentaje = (acumulado * 100.0) / conteoC;
                                                curacionPorcentaje.setText(String.format("%.2f%%", porcentaje));
                                            } else {
                                                Log.e("ERROR", "El valor de conteoC es nulo");
                                            }
                                        } else {
                                            Log.d("DEBUG", "El documento de conteo no existe en Firestore");
                                        }
                                    } else {
                                        Log.e("ERROR", "Error al obtener el documento de conteo en Firestore", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.e("ERROR", "El valor de acumulado es nulo");
                        }
                    } else {
                        Log.d("DEBUG", "El documento de curación no existe en Firestore");
                    }
                } else {
                    Log.e("ERROR", "Error al obtener el documento de curación en Firestore", task.getException());
                }
            }
        });
    }

    // ANATOMÍA
    private void obtenerPorcentajAnatomia() {
        DocumentReference anatomiaDocumentRef = db.collection("rqAnatomia").document(idUser);
        anatomiaDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot anatomiaDocument = task.getResult();

                    if (anatomiaDocument.exists()) {
                        // El documento de curación existe, obtén el valor de "acumulado"
                        Long acumulado = anatomiaDocument.getLong("acumulado");

                        if (acumulado != null) {
                            // Ahora que tenemos el valor de "acumulado", obtenemos el valor de "conteoC"
                            DocumentReference conteoDocumentRef = db.collection("rqConteo").document(idUser);

                            conteoDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot conteoDocument = task.getResult();

                                        if (conteoDocument.exists()) {
                                            // El documento de conteo existe, obtén el valor de "conteoC"
                                            Long conteoC = conteoDocument.getLong("conteoC");

                                            if (conteoC != null) {
                                                // Calcular el porcentaje y mostrarlo en la vista correspondiente
                                                double porcentaje = (acumulado * 100.0) / conteoC;
                                                anatomiaPorcentaje.setText(String.format("%.2f%%", porcentaje));
                                            } else {
                                                Log.e("ERROR", "El valor de conteoC es nulo");
                                            }
                                        } else {
                                            Log.d("DEBUG", "El documento de conteo no existe en Firestore");
                                        }
                                    } else {
                                        Log.e("ERROR", "Error al obtener el documento de conteo en Firestore", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.e("ERROR", "El valor de acumulado es nulo");
                        }
                    } else {
                        Log.d("DEBUG", "El documento de curación no existe en Firestore");
                    }
                } else {
                    Log.e("ERROR", "Error al obtener el documento de curación en Firestore", task.getException());
                }
            }
        });
    }

    // SIGNOS VITALES
    private void obtenerPorcentajeSignos() {
        DocumentReference anatomiaDocumentRef = db.collection("rqSignosVitales").document(idUser);
        anatomiaDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot anatomiaDocument = task.getResult();

                    if (anatomiaDocument.exists()) {
                        // El documento de curación existe, obtén el valor de "acumulado"
                        Long acumulado = anatomiaDocument.getLong("acumulado");

                        if (acumulado != null) {
                            // Ahora que tenemos el valor de "acumulado", obtenemos el valor de "conteoC"
                            DocumentReference conteoDocumentRef = db.collection("rqConteo").document(idUser);

                            conteoDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot conteoDocument = task.getResult();

                                        if (conteoDocument.exists()) {
                                            // El documento de conteo existe, obtén el valor de "conteoC"
                                            Long conteoC = conteoDocument.getLong("conteoC");

                                            if (conteoC != null) {
                                                // Calcular el porcentaje y mostrarlo en la vista correspondiente
                                                double porcentaje = (acumulado * 100.0) / conteoC;
                                                signosPocentaje.setText(String.format("%.2f%%", porcentaje));
                                            } else {
                                                Log.e("ERROR", "El valor de conteoC es nulo");
                                            }
                                        } else {
                                            Log.d("DEBUG", "El documento de conteo no existe en Firestore");
                                        }
                                    } else {
                                        Log.e("ERROR", "Error al obtener el documento de conteo en Firestore", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.e("ERROR", "El valor de acumulado es nulo");
                        }
                    } else {
                        Log.d("DEBUG", "El documento de curación no existe en Firestore");
                    }
                } else {
                    Log.e("ERROR", "Error al obtener el documento de curación en Firestore", task.getException());
                }
            }
        });
    }

    // SÍNTOMAS
    private void obtenerPorcentajeSintomas() {
        DocumentReference anatomiaDocumentRef = db.collection("rqSintomas").document(idUser);
        anatomiaDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot anatomiaDocument = task.getResult();

                    if (anatomiaDocument.exists()) {
                        // El documento de curación existe, obtén el valor de "acumulado"
                        Long acumulado = anatomiaDocument.getLong("acumulado");

                        if (acumulado != null) {
                            // Ahora que tenemos el valor de "acumulado", obtenemos el valor de "conteoC"
                            DocumentReference conteoDocumentRef = db.collection("rqConteo").document(idUser);

                            conteoDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot conteoDocument = task.getResult();

                                        if (conteoDocument.exists()) {
                                            // El documento de conteo existe, obtén el valor de "conteoC"
                                            Long conteoC = conteoDocument.getLong("conteoC");

                                            if (conteoC != null) {
                                                // Calcular el porcentaje y mostrarlo en la vista correspondiente
                                                double porcentaje = (acumulado * 100.0) / conteoC;
                                                sintomasPorcentaje.setText(String.format("%.2f%%", porcentaje));
                                            } else {
                                                Log.e("ERROR", "El valor de conteoC es nulo");
                                            }
                                        } else {
                                            Log.d("DEBUG", "El documento de conteo no existe en Firestore");
                                        }
                                    } else {
                                        Log.e("ERROR", "Error al obtener el documento de conteo en Firestore", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.e("ERROR", "El valor de acumulado es nulo");
                        }
                    } else {
                        Log.d("DEBUG", "El documento de curación no existe en Firestore");
                    }
                } else {
                    Log.e("ERROR", "Error al obtener el documento de curación en Firestore", task.getException());
                }
            }
        });
    }
    // BONUS
    private void obtenerPorcentajeBonus() {
        DocumentReference anatomiaDocumentRef = db.collection("rqBonus").document(idUser);
        anatomiaDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot anatomiaDocument = task.getResult();

                    if (anatomiaDocument.exists()) {
                        // El documento de curación existe, obtén el valor de "acumulado"
                        Long acumulado = anatomiaDocument.getLong("acumulado");

                        if (acumulado != null) {
                            // Ahora que tenemos el valor de "acumulado", obtenemos el valor de "conteoC"
                            DocumentReference conteoDocumentRef = db.collection("rqConteo").document(idUser);

                            conteoDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot conteoDocument = task.getResult();

                                        if (conteoDocument.exists()) {
                                            // El documento de conteo existe, obtén el valor de "conteoC"
                                            Long conteoC = conteoDocument.getLong("conteoC");

                                            if (conteoC != null) {
                                                // Calcular el porcentaje y mostrarlo en la vista correspondiente
                                                double porcentaje = (acumulado * 100.0) / conteoC;
                                                bonusPorcentaje.setText(String.format("%.2f%%", porcentaje));
                                            } else {
                                                Log.e("ERROR", "El valor de conteoC es nulo");
                                            }
                                        } else {
                                            Log.d("DEBUG", "El documento de conteo no existe en Firestore");
                                        }
                                    } else {
                                        Log.e("ERROR", "Error al obtener el documento de conteo en Firestore", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.e("ERROR", "El valor de acumulado es nulo");
                        }
                    } else {
                        Log.d("DEBUG", "El documento de curación no existe en Firestore");
                    }
                } else {
                    Log.e("ERROR", "Error al obtener el documento de curación en Firestore", task.getException());
                }
            }
        });
    }
}