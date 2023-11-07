package com.example.proyectoredquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class IngresarPregunta extends AppCompatActivity {

    private Spinner spinnerCategory;
    private ArrayAdapter<CharSequence> categoyAdapter;
    Button btn_add, btn_back;
    EditText question, rate, correct, incorrect1, incorrect2, incorrect3;
    private FirebaseFirestore mfirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_pregunta);

        //FIRESTORE
        mfirestore = FirebaseFirestore.getInstance();

        // SPINNER
        spinnerCategory = findViewById(R.id.categoria);
        categoyAdapter = ArrayAdapter.createFromResource(this, R.array.categrias_array, android.R.layout.simple_spinner_item);
        categoyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoyAdapter);

        //INSTANCIAS
        btn_back = findViewById(R.id.btn_back);
        btn_add = findViewById(R.id.btn_agregar);
        question = findViewById(R.id.pregunta);
        rate = findViewById(R.id.rating);
        correct = findViewById(R.id.correcta);
        incorrect1 = findViewById(R.id.incorrecta1);
        incorrect2 = findViewById(R.id.incorrecta2);
        incorrect3 = findViewById(R.id.incorrecta3);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent index = new Intent(IngresarPregunta.this, VerReactivos.class);
                startActivities(new Intent[]{index});
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCategory = (String) categoyAdapter.getItem(i);
                Toast.makeText(IngresarPregunta.this, "Categoría: " + selectedCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // AGREGAR
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String preguntai = question.getText().toString();
                String ratingi = rate.getText().toString();
                String correctai = correct.getText().toString();
                String incorrecta1i = incorrect1.getText().toString();
                String incorrecta2i = incorrect2.getText().toString();
                String incorrecta3i = incorrect3.getText().toString();
                String categoriai = spinnerCategory.getSelectedItem().toString();

                if (preguntai.isEmpty() && ratingi.isEmpty() && correctai.isEmpty() && incorrecta1i.isEmpty() && incorrecta2i.isEmpty() && incorrecta3i.isEmpty()){
                    Toast.makeText(new IngresarPregunta(), "Ingrese los datos", Toast.LENGTH_SHORT).show();
                }else {
                    postPregunta(preguntai, ratingi, correctai, incorrecta1i, incorrecta2i, incorrecta3i, categoriai);
                }
            }
        });

    }

    private void postPregunta(String preguntai, String ratingi, String correctai, String incorrecta1i, String incorrecta2i, String incorrecta3i, String categoriai) {
        Map<String, Object> map = new HashMap<>();
        map.put("pregunta", preguntai);
        map.put("categoria", categoriai);
        map.put("rating", ratingi);
        map.put("correcta", correctai);
        map.put("incorrecta1", incorrecta1i);
        map.put("incorrecta2", incorrecta2i);
        map.put("incorrecta3", incorrecta3i);

        mfirestore.collection("preguntas").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(new IngresarPregunta(), "Pregunta agregada exitosamente", Toast.LENGTH_SHORT).show();
                finish();
                //startActivity(new Intent(IngresarPregunta.this, ));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(new IngresarPregunta(), "Error al ingresar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}