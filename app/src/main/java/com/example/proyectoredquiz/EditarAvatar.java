package com.example.proyectoredquiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class EditarAvatar extends AppCompatActivity {

    Button btn_superior, btn_inferior, btn_color1, btn_color2, btn_color3, btn_color4, btn_volver;
    ImageView superior, inferior, avatar, zapatosH, superiorM, inferiorM, zapatosM;
    private int estado = 1;
    private FirebaseFirestore mfirestore;
    FirebaseAuth mAuth;
    private String idUser, generoUsuario;
    private int puntajeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_avatar);
        // AUTH
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        //FIRESTORE
        mfirestore = FirebaseFirestore.getInstance();

        btn_volver = findViewById(R.id.btn_volverA2);
        btn_superior = findViewById(R.id.btn_sup);
        btn_inferior = findViewById(R.id.btn_inf);
        btn_color1 = findViewById(R.id.color1);
        btn_color2 = findViewById(R.id.color2);
        btn_color3 = findViewById(R.id.color3);
        btn_color4 = findViewById(R.id.color4);
        superior = findViewById(R.id.superiorHombre);
        inferior = findViewById(R.id.inferiorHombre);
        avatar = findViewById(R.id.avatar2);
        zapatosH = findViewById(R.id.imageView9);
        superiorM = findViewById(R.id.superiorM);
        inferiorM = findViewById(R.id.inferiorM);
        zapatosM = findViewById(R.id.zapatosM);


        // OBTENER GENERO DEL USUARIO
        DocumentReference documentReference = mfirestore.collection("rqUsers").document(idUser);
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
                    generoUsuario = documentSnapshot.getString("genero");
                    // EDITAR
                    if (estado == 1 && "Masculino".equals(generoUsuario)){
                        btn_color1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                superior.setImageResource(R.drawable.basicahombreb);
                            }
                        });

                        btn_color2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                superior.setImageResource(R.drawable.basicahombren);
                            }
                        });

                        btn_color3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                superior.setImageResource(R.drawable.basicahombrev);
                            }
                        });

                        btn_color4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                superior.setImageResource(R.drawable.basicahombrecr);
                            }
                        });
                    } else if (estado == 1 && "Femenino".equals(generoUsuario)){
                        btn_color1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                superiorM.setImageResource(R.drawable.basicamujerb);
                            }
                        });

                        btn_color2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                superiorM.setImageResource(R.drawable.basicamujeraz);
                            }
                        });

                        btn_color3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                superiorM.setImageResource(R.drawable.basicamujerr);
                            }
                        });

                        btn_color4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                superiorM.setImageResource(R.drawable.basicamujercr);
                            }
                        });
                    }
                    Log.e("EtiquetaDeRegistro", "Valor del género del usuario: " + generoUsuario);
                    if ("Masculino".equals(generoUsuario)) {
                        avatar.setImageResource(R.drawable.hombre);
                        superior.setVisibility(View.VISIBLE);
                        inferior.setVisibility(View.VISIBLE);
                        zapatosH.setVisibility(View.VISIBLE);
                        superiorM.setVisibility(View.GONE);
                        inferiorM.setVisibility(View.GONE);
                        zapatosM.setVisibility(View.GONE);
                    } else if ("Femenino".equals(generoUsuario)) {
                        avatar.setImageResource(R.drawable.mujer);
                        btn_color1.setBackgroundColor(ContextCompat.getColor(EditarAvatar.this, R.color.blancoH));
                        btn_color2.setBackgroundColor(ContextCompat.getColor(EditarAvatar.this, R.color.azulM));
                        btn_color3.setBackgroundColor(ContextCompat.getColor(EditarAvatar.this, R.color.rosaM));
                        btn_color4.setBackgroundColor(ContextCompat.getColor(EditarAvatar.this, R.color.cruzRoja));
                        superior.setVisibility(View.GONE);
                        inferior.setVisibility(View.GONE);
                        zapatosH.setVisibility(View.GONE);
                        superiorM.setVisibility(View.VISIBLE);
                        inferiorM.setVisibility(View.VISIBLE);
                        zapatosM.setVisibility(View.VISIBLE);
                    }

                    puntajeUsuario = documentSnapshot.getLong("puntaje").intValue();
                    //puntaje.setText(String.valueOf(puntajeUsuario));
                    //vidas.setText("X " + String.valueOf(VIDAS));
                }
            }
        });

        // VOLVER
        btn_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(EditarAvatar.this, miavatar.class);
                startActivities(new Intent[]{index});
            }
        });

        //Log.d("DEBUG", "Estado: " + estado + ", Género: " + generoUsuario);


        // SUPERIOR ACTIVADO
        btn_superior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("Masculino".equals(generoUsuario)) {
                    estado = 1;
                    botonActual1();
                }
                if ("Femenino".equals(generoUsuario)){
                    estado = 1;
                    botonActualM1();
                }
            }
        });

        // INFERIOR ACTIVADO
        btn_inferior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("Masculino".equals(generoUsuario)) {
                    estado = 2;
                    botonActual2();
                }
                if ("Femenino".equals(generoUsuario)){
                    estado = 2;
                    botonActualM2();
                }
            }
        });



    }

    // HOMBRE
    private void botonActual1() {
        btn_superior.setBackgroundColor(ContextCompat.getColor(this, R.color.actual));
        btn_superior.setTextColor(ContextCompat.getColor(this, R.color.white));
        btn_inferior.setBackgroundColor(ContextCompat.getColor(this, R.color.pendiente));
        btn_inferior.setTextColor(ContextCompat.getColor(this, R.color.black));

        btn_color1.setBackgroundColor(ContextCompat.getColor(this, R.color.blancoH));
        btn_color2.setBackgroundColor(ContextCompat.getColor(this, R.color.naranjaH));
        btn_color3.setBackgroundColor(ContextCompat.getColor(this, R.color.verdeH));
        btn_color4.setBackgroundColor(ContextCompat.getColor(this, R.color.cruzRoja));
        btn_color4.setVisibility(View.VISIBLE);

        if (estado == 1){
            btn_color1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    superior.setImageResource(R.drawable.basicahombreb);
                }
            });

            btn_color2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    superior.setImageResource(R.drawable.basicahombren);
                }
            });

            btn_color3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    superior.setImageResource(R.drawable.basicahombrev);
                }
            });

            btn_color4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    superior.setImageResource(R.drawable.basicahombrecr);
                }
            });
        }
    }

    private void botonActual2() {
        btn_superior.setBackgroundColor(ContextCompat.getColor(this, R.color.pendiente));
        btn_superior.setTextColor(ContextCompat.getColor(this, R.color.black));
        btn_inferior.setBackgroundColor(ContextCompat.getColor(this, R.color.actual));
        btn_inferior.setTextColor(ContextCompat.getColor(this, R.color.white));

        btn_color1.setBackgroundColor(ContextCompat.getColor(this, R.color.pantalonH1));
        btn_color2.setBackgroundColor(ContextCompat.getColor(this, R.color.pantalonH2));
        btn_color3.setBackgroundColor(ContextCompat.getColor(this, R.color.pantalonH3));
        btn_color4.setBackgroundColor(ContextCompat.getColor(this, R.color.pantalonH4));
        btn_color4.setVisibility(View.GONE);

        if (estado == 2){
            btn_color1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inferior.setImageResource(R.drawable.pantalonhombrea);
                }
            });

            btn_color2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inferior.setImageResource(R.drawable.pantalonhombreac);
                }
            });

            btn_color3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inferior.setImageResource(R.drawable.pantalonhombreg);
                }
            });

            btn_color4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inferior.setImageResource(R.drawable.pantalonhombreg);
                }
            });

        }
    }

    // MUJER
    private void botonActualM1() {
        btn_superior.setBackgroundColor(ContextCompat.getColor(this, R.color.actual));
        btn_superior.setTextColor(ContextCompat.getColor(this, R.color.white));
        btn_inferior.setBackgroundColor(ContextCompat.getColor(this, R.color.pendiente));
        btn_inferior.setTextColor(ContextCompat.getColor(this, R.color.black));

        btn_color1.setBackgroundColor(ContextCompat.getColor(this, R.color.blancoH));
        btn_color2.setBackgroundColor(ContextCompat.getColor(this, R.color.azulM));
        btn_color3.setBackgroundColor(ContextCompat.getColor(this, R.color.rosaM));
        btn_color4.setBackgroundColor(ContextCompat.getColor(this, R.color.cruzRoja));
        btn_color4.setVisibility(View.VISIBLE);

        if (estado == 1){
            btn_color1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    superiorM.setImageResource(R.drawable.basicamujerb);
                }
            });

            btn_color2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    superiorM.setImageResource(R.drawable.basicamujeraz);
                }
            });

            btn_color3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    superiorM.setImageResource(R.drawable.basicamujerr);
                }
            });

            btn_color4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    superiorM.setImageResource(R.drawable.basicamujercr);
                }
            });
        }
    }

    private void botonActualM2() {
        btn_superior.setBackgroundColor(ContextCompat.getColor(this, R.color.pendiente));
        btn_superior.setTextColor(ContextCompat.getColor(this, R.color.black));
        btn_inferior.setBackgroundColor(ContextCompat.getColor(this, R.color.actual));
        btn_inferior.setTextColor(ContextCompat.getColor(this, R.color.white));

        btn_color1.setBackgroundColor(ContextCompat.getColor(this, R.color.pantalonH1));
        btn_color2.setBackgroundColor(ContextCompat.getColor(this, R.color.pantalonH4));
        btn_color3.setBackgroundColor(ContextCompat.getColor(this, R.color.pantalonH3));
        btn_color4.setBackgroundColor(ContextCompat.getColor(this, R.color.pantalonH4));
        btn_color4.setVisibility(View.GONE);

        if (estado == 2){
            btn_color1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inferiorM.setImageResource(R.drawable.pantalonmujera);
                }
            });

            btn_color2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inferiorM.setImageResource(R.drawable.pantalonmujerc);
                }
            });

            btn_color3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inferiorM.setImageResource(R.drawable.pantalonmujerg);
                }
            });

            btn_color4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inferiorM.setImageResource(R.drawable.pantalonmujerc);
                }
            });

        }
    }
}