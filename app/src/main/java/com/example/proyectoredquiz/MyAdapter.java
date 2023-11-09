package com.example.proyectoredquiz;

import static com.google.firebase.database.DatabaseKt.getSnapshots;

import android.app.Activity;
import android.content.Context;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Context context;
    ArrayList<Question> list;

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Question question = list.get(position);
        holder.pregunta.setText(question.getPregunta());
        holder.categoria.setText(question.getCategoria());

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
