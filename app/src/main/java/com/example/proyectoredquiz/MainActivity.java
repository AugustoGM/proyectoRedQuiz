package com.example.proyectoredquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button bnt_regist, btn_login;
    EditText emailI, passwordI;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth= FirebaseAuth.getInstance();

        bnt_regist = findViewById(R.id.btn_registro);
        emailI = findViewById(R.id.correoI);
        passwordI = findViewById(R.id.contrasenaI);
        btn_login = findViewById(R.id.btn_iniciar);

        bnt_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iregistro = new Intent(MainActivity.this, RegistroActivity.class);
                startActivities(new Intent[]{iregistro});
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailUser = emailI.getText().toString().trim();
                String passUser = passwordI.getText().toString().trim();

                // Verifica si las credenciales coinciden con las del administrador
                if (esCuentaDeAdmin(emailUser, passUser)) {
                    // Credenciales de administrador, redirige a la interfaz del administrador
                    finish();
                    startActivity(new Intent(MainActivity.this, MenuAdministrador.class));
                    Toast.makeText(MainActivity.this, "¡Bienvenido Administrador!", Toast.LENGTH_SHORT).show();
                } else {
                    // No son credenciales de administrador, realiza el inicio de sesión normal
                    if (TextUtils.isEmpty(emailUser) && TextUtils.isEmpty(passUser)) {
                        Toast.makeText(MainActivity.this, "Ingrese los datos", Toast.LENGTH_SHORT).show();
                    } else {
                        loginUser(emailUser, passUser);
                    }
                }
            }
        });

    }

    private boolean esCuentaDeAdmin(String email, String contraseña) {
        // Verifica aquí si las credenciales coinciden con las de una cuenta de administrador
        // Puedes almacenar las cuentas de administrador en una base de datos o en algún otro lugar seguro
        // Por ahora, usaremos credenciales "dummy" para demostración.
        return email.equals("admin@gmail.com") && contraseña.equals("admin123#");  //checar si se puden poner mas cuentas de admin
    }

    private void loginUser(String emailUser, String passUser) {
        mAuth.signInWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                    startActivity(new Intent(MainActivity.this, MenuUserActivity.class));
                    Toast.makeText(MainActivity.this, "¡Binvenid@!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivities(new Intent[]{new Intent(MainActivity.this, MenuUserActivity.class)});
            finish();
        }
    }
}