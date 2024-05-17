package com.esteban.socialmedia.FormularioRegistro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.esteban.socialmedia.InicioApp.InicioApp;
import com.esteban.socialmedia.R;
import com.esteban.socialmedia.loading.LoadingDialog;
import com.esteban.socialmedia.models.User;
import com.esteban.socialmedia.providers.AuthProvider;
import com.esteban.socialmedia.providers.UserProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CompletarInformacion extends AppCompatActivity {
    EditText username;
    EditText telefono;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    AppCompatButton confirmar;
    LoadingDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar_informacion);

        username = findViewById(R.id.usuario);
        telefono = findViewById(R.id.telefonoCompletarInformacion);
        confirmar = findViewById(R.id.confirmar);

        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        progressDialog = new LoadingDialog(this);
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmar();
            }
        });
    }

    private void confirmar() {
        String usuario = username.getText().toString();
        String telefonoP = telefono.getText().toString();
        if (!usuario.isEmpty()) {
            updateUser(usuario, telefonoP);
        } else {
            Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser(final String username, final String telefono) {
        String id = mAuthProvider.getUid();
        User user = new User();
        user.setUsername(username);
        user.setId(id);
        user.setTelefono(telefono);
        user.setTimestamp(new Date().getTime());
        progressDialog.show();
        mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(CompletarInformacion.this, InicioApp.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CompletarInformacion.this, "No se pudo almacenar el usuario en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}