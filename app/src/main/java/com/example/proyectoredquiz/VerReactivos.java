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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class VerReactivos extends AppCompatActivity {
    Button btn_agregar;
    RecyclerView recyclerView;
    ArrayList<Question> list;
    DatabaseReference databaseReference;
    MyAdapter adapter;
    Button btn_atras;

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
        adapter = new MyAdapter(this, list); //AQUI se agregó algo
        recyclerView.setAdapter(adapter);

        // Inicializa Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Configura la referencia a la colección "preguntas" en Firestore
        CollectionReference collectionRef = db.collection("preguntas");


        collectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            list.clear(); // Limpia la lista antes de agregar nuevos elementos

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Question question = document.toObject(Question.class);
                list.add(question);
            }

            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Manejar errores en caso de que ocurran
        });


        btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(VerReactivos.this, IngresarPregunta.class);
                startActivities(new Intent[]{index});
            }
        });
    }
}