package com.example.proyectoredquiz;

import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;

public class MenuUserActivity extends AppCompatActivity {

    Button btn_exit;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_user);
        mAuth = FirebaseAuth.getInstance();

        btn_exit = findViewById(R.id.btn_cerrar);

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar un cuadro de diálogo de confirmación
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuUserActivity.this);
                builder.setMessage("¿Estás seguro de que deseas cerrar la sesión?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                finish();
                                startActivity(new Intent(MenuUserActivity.this, MainActivity.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // No hacer nada y cerrar el cuadro de diálogo
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
