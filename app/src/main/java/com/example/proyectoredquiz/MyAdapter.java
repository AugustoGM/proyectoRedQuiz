package com.example.proyectoredquiz;

import static com.google.firebase.database.DatabaseKt.getSnapshots;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.logging.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Context context;
    ArrayList<Question> list;
    private FirebaseFirestore mfirestore;
    DocumentSnapshot documentSnapshot;

    public MyAdapter(Context context, ArrayList<Question> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.questionentry,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Question question = list.get(position);
        holder.pregunta.setText(question.getPregunta());
        holder.categoria.setText(question.getCategoria());

        holder.btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoConfirmacion(position);
            }
        });
    }

    // Método para mostrar el cuadro de diálogo de confirmación
    private void mostrarDialogoConfirmacion(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Eliminar pregunta");
        builder.setMessage("¿Está seguro de que desea eliminar esta pregunta?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Eliminar la pregunta de Firestore
                eliminarPreguntaFirestore(position);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // No hacer nada si el usuario elige no eliminar
            }
        });

        builder.show();
    }

    // Método para eliminar la pregunta de Firestore
    private void eliminarPreguntaFirestore(final int position) {
        Question pregunta = list.get(position);

        // Obtén el DocumentSnapshot asociado a la pregunta
        DocumentSnapshot documentSnapshot = pregunta.getDocumentSnapshot();

        if (documentSnapshot != null) {
            // Obtén el ID del documento (identificador único)
            String documentId = documentSnapshot.getId();

            mFirestore.collection("preguntas")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Eliminación exitosa, actualiza la lista y notifica al adaptador
                            list.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Pregunta eliminada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error al eliminar la pregunta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(context, "Error: DocumentSnapshot es nulo", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView pregunta, categoria;
        ImageView btn_eliminar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pregunta = itemView.findViewById(R.id.textPregunta);
            categoria = itemView.findViewById(R.id.textcategoria);

            btn_eliminar = itemView.findViewById(R.id.btn_delete);
        }
    }
}
