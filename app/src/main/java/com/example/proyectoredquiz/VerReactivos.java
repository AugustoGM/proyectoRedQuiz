package com.example.proyectoredquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class VerReactivos extends AppCompatActivity implements MyAdapter.OnQuestionDeleteListener {
    Button btn_agregar;
    RecyclerView recyclerView;
    ArrayList<Question> list;
    DatabaseReference databaseReference;
    MyAdapter adapter;
    Button btn_atras;

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(VerReactivos.this, VerReactivos.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_reactivos);

        btn_agregar = findViewById(R.id.btn_add);
        btn_atras = findViewById(R.id.btn_volverAdmin);

        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(VerReactivos.this, MenuAdministrador.class);
                startActivities(new Intent[]{index});
            }
        });

        //RECYCLER VIEW
        recyclerView = findViewById(R.id.recyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference("preguntas");
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(list, VerReactivos.this); //AQUI se agregó algo
        recyclerView.setAdapter(adapter);

        // Inicializa Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Configura la referencia a la colección "preguntas" en Firestore
        CollectionReference collectionRef = db.collection("preguntas");


        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                list = new ArrayList<>();

                // Iterate over the results of the query
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    // Convert the document to an "Ad" object
                    Question question = document.toObject(Question.class);
                    list.add(question);
                }
                //adapter.notifyDataSetChanged();
                // Create an adapter to display the ads in the RecyclerView
                adapter = new MyAdapter(list, VerReactivos.this);
                recyclerView.setAdapter(adapter);
            }
            // Handle error if the query fails
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NotNull Exception e) {
                // Notify user of error
                Toast.makeText(VerReactivos.this, "Error al visualizar los anuncios",
                        Toast.LENGTH_SHORT).show();
            }
        });


        btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(VerReactivos.this, IngresarPregunta.class);
                startActivities(new Intent[]{index});
            }
        });

    }

        @Override
        public void onQuestionDelete(int position){
            Question question = list.get(position);
            String documentId = question.getDocumentSnapshot().getId();

            mFirestore.collection("preguntas").document(documentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update the UI after successful deletion
                            list.remove(position);
                            //notifyDataSetChanged();
                            //Toast.makeText(context, "Pregunta eliminada exitosamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to delete
                            //Toast.makeText(context, "Error al eliminar la pregunta", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


}

