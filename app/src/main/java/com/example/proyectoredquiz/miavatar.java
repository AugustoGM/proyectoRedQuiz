package com.example.proyectoredquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class miavatar extends AppCompatActivity {

    Button btn_volver, btn_editar;
    TextView puntaje;
    private FirebaseFirestore mfirestore;
    FirebaseAuth mAuth;
    private String idUser, generoUsuario;
    private int puntajeUsuario, prendaSuperior, prendaInferior;
    ImageView avatar, superiorH, inferiorH, zapatosH, superiorM, inferiorM, zapatosM;

    private boolean recompensa1Desbloqueada = false;
    private boolean recompensa2Desbloqueada = false;
    private boolean recompensa3Desbloqueada = false;
    private boolean recompensa4Desbloqueada = false;
    private boolean recompensa5Desbloqueada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miavatar);
        mAuth = FirebaseAuth.getInstance();

        idUser = mAuth.getCurrentUser().getUid();
        btn_volver = findViewById(R.id.btn_volverA);
        btn_editar = findViewById(R.id.btn_editarA);
        puntaje = findViewById(R.id.punt);
        avatar = findViewById(R.id.avatar);

        superiorH = findViewById(R.id.supH);
        inferiorH = findViewById(R.id.infH);
        zapatosH = findViewById(R.id.zapH);

        superiorM = findViewById(R.id.supM);
        inferiorM = findViewById(R.id.infM);
        zapatosM = findViewById(R.id.zapM);

        //FIRESTORE
        mfirestore = FirebaseFirestore.getInstance();

        // OBTENER PUNTAJE DEL USUARIO
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
                    Log.e("EtiquetaDeRegistro", "Valor del género del usuario: " + generoUsuario);
                    if ("Masculino".equals(generoUsuario)) {
                        avatar.setImageResource(R.drawable.hombre);
                        superiorH.setVisibility(View.VISIBLE);
                        inferiorH.setVisibility(View.VISIBLE);
                        zapatosH.setVisibility(View.VISIBLE);
                        superiorM.setVisibility(View.GONE);
                        inferiorM.setVisibility(View.GONE);
                        zapatosM.setVisibility(View.GONE);
                        obtenerPrendas(idUser, generoUsuario);
                    } else if ("Femenino".equals(generoUsuario)) {
                        avatar.setImageResource(R.drawable.mujer);
                        superiorH.setVisibility(View.GONE);
                        inferiorH.setVisibility(View.GONE);
                        zapatosH.setVisibility(View.GONE);
                        superiorM.setVisibility(View.VISIBLE);
                        inferiorM.setVisibility(View.VISIBLE);
                        zapatosM.setVisibility(View.VISIBLE);
                        obtenerPrendas(idUser, generoUsuario);
                    }
                    puntajeUsuario = documentSnapshot.getLong("puntaje").intValue();
                    puntaje.setText(String.valueOf(puntajeUsuario));
                    //vidas.setText("X " + String.valueOf(VIDAS));
                }
            }
        });

        // VOLVER A INICIO
        btn_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(miavatar.this, MenuUserActivity.class);
                startActivities(new Intent[]{index});
            }
        });

        // VOLVER EDITAR
        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(miavatar.this, EditarAvatar.class);
                startActivities(new Intent[]{index});
            }
        });

        obtenerPuntajeUsuarioDesdeFirestore(idUser);
    }

    private void obtenerPrendas(String userId, String genero) {
        // Obtener la referencia al documento del usuario en Firestore
        String usuarioDocumentPath = "rqUsers/" + userId;

        // Realizar la consulta para obtener el documento del usuario
        mfirestore.document(usuarioDocumentPath)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // El documento del usuario existe, obtener el puntaje
                            Long prendaI = documentSnapshot.getLong("prendaI");
                            Long prendaS = documentSnapshot.getLong("prendaS");

                            if (puntaje != null) {
                                // Actualizar la variable puntajeUsuario y realizar las acciones necesarias
                                prendaSuperior = prendaS.intValue();
                                prendaInferior = prendaI.intValue();
                                // Realizar otras acciones según el puntaje, como verificar y desbloquear recompensas
                                asignarPrendas(genero, prendaS, prendaI);
                            }
                        } else {
                            // El documento del usuario no existe
                            Toast.makeText(miavatar.this, "Documento de usuario no encontrado en Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al obtener el documento del usuario
                        Toast.makeText(miavatar.this, "Error al obtener las prendas desde Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void asignarPrendas(String genero, Long prendaS, Long prendaI) {
        if ("Masculino".equals(genero)){
            if (prendaS == 1){
                superiorH.setImageResource(R.drawable.basicahombreb);
            } else if (prendaS == 2){
                superiorH.setImageResource(R.drawable.basicahombren);
            } else if (prendaS == 3) {
                superiorH.setImageResource(R.drawable.basicahombrev);
            } else {
                superiorH.setImageResource(R.drawable.basicahombrecr);
            }

            if (prendaI == 1){
                inferiorH.setImageResource(R.drawable.pantalonhombrea);
            } else if (prendaI == 2) {
                inferiorH.setImageResource(R.drawable.pantalonhombreac);
            } else {
                inferiorH.setImageResource(R.drawable.pantalonhombreg);
            }
        }
        if ("Femenino".equals(genero)){
            if (prendaS == 1){
                superiorM.setImageResource(R.drawable.basicamujerb);
            } else if (prendaS == 2){
                superiorM.setImageResource(R.drawable.basicamujeraz);
            } else if (prendaS == 3) {
                superiorM.setImageResource(R.drawable.basicamujerr);
            } else {
                superiorM.setImageResource(R.drawable.basicamujercr);
            }

            if (prendaI == 1){
                inferiorM.setImageResource(R.drawable.pantalonmujera);
            } else if (prendaI == 2) {
                inferiorM.setImageResource(R.drawable.pantalonmujerc);
            } else {
                inferiorM.setImageResource(R.drawable.pantalonmujerg);
            }
        }
    }

    private void obtenerPuntajeUsuarioDesdeFirestore(String userId) {
        // Obtener la referencia al documento del usuario en Firestore
        String usuarioDocumentPath = "rqUsers/" + userId;

        // Realizar la consulta para obtener el documento del usuario
        mfirestore.document(usuarioDocumentPath)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // El documento del usuario existe, obtener el puntaje
                            Long puntaje = documentSnapshot.getLong("puntaje");

                            if (puntaje != null) {
                                // Actualizar la variable puntajeUsuario y realizar las acciones necesarias
                                puntajeUsuario = puntaje.intValue();
                                // Realizar otras acciones según el puntaje, como verificar y desbloquear recompensas
                                verificarDesbloqueoRecompensas();
                            }
                        } else {
                            // El documento del usuario no existe
                            Toast.makeText(miavatar.this, "Documento de usuario no encontrado en Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al obtener el documento del usuario
                        Toast.makeText(miavatar.this, "Error al obtener el puntaje desde Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarDesbloqueoRecompensas() {
        // Verificar y desbloquear recompensas según el puntaje del usuario
        if (puntajeUsuario >= 50 && !recompensa1Desbloqueada) {
            // Desbloquear recompensa 1
            recompensa1Desbloqueada = true;
            // Actualizar en Firestore la recompensa desbloqueada
            actualizarRecompensaEnFirestore("recompensa1", true);
        }

        if (puntajeUsuario >= 100 && !recompensa2Desbloqueada) {
            // Desbloquear recompensa 2
            recompensa2Desbloqueada = true;
            // Actualizar en Firestore la recompensa desbloqueada
            actualizarRecompensaEnFirestore("recompensa2", true);
        }

        if (puntajeUsuario >= 150 && !recompensa3Desbloqueada) {
            // Desbloquear recompensa 3
            recompensa3Desbloqueada = true;
            // Actualizar en Firestore la recompensa desbloqueada
            actualizarRecompensaEnFirestore("recompensa3", true);
        }

        if (puntajeUsuario >= 200 && !recompensa4Desbloqueada) {
            // Desbloquear recompensa 4
            recompensa4Desbloqueada = true;
            // Actualizar en Firestore la recompensa desbloqueada
            actualizarRecompensaEnFirestore("recompensa4", true);
        }
        if (puntajeUsuario >= 350 && !recompensa5Desbloqueada) {
            // Desbloquear recompensa 4
            recompensa5Desbloqueada = true;
            // Actualizar en Firestore la recompensa desbloqueada
            actualizarRecompensaEnFirestore("recompensa5", true);
        }
    }

    private void actualizarRecompensaEnFirestore(String nombreRecompensa, boolean desbloqueada) {
        // Obtener la referencia al documento del usuario en Firestore
        String userId = mAuth.getCurrentUser().getUid(); // Obtener el ID del usuario actual
        String recompensaDocumentPath = "rqRecompensas/" + userId;

        // Crear un mapa para actualizar el campo de la recompensa en Firestore
        Map<String, Object> actualizacionRecompensa = new HashMap<>();
        actualizacionRecompensa.put(nombreRecompensa, desbloqueada);

        // Actualizar el documento en Firestore
        mfirestore.collection("rqRecompensas").document(userId)
                .set(actualizacionRecompensa, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La actualización se realizó con éxito
                        //Toast.makeText(miavatar.this, "Recompensa actualizada en Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // La actualización falló
                        Toast.makeText(miavatar.this, "Error al actualizar la recompensa en Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}