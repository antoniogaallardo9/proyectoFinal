package com.esteban.socialmedia.FormularioRegistro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esteban.socialmedia.R;
import com.esteban.socialmedia.loading.LoadingDialog;
import com.esteban.socialmedia.models.User;
import com.esteban.socialmedia.providers.AuthProvider;
import com.esteban.socialmedia.providers.UserProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.Date;


public class Registro extends AppCompatActivity {

    TextView textoInicioSesion;
    EditText campoEmail;  // Cambio de nombre a campoEmail
    EditText campoContrasena;
    EditText campoNombre;

    EditText campoTelefono;

    private String email;
    private String nombre;

    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    LoadingDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        campoEmail = findViewById(R.id.email);  // Cambio de nombre a campoEmail
        campoContrasena = findViewById(R.id.contrasena);
        campoTelefono = findViewById(R.id.telefono);
        //textoInicioSesion = findViewById(R.id.irainicio);
        campoNombre = findViewById(R.id.nombre);
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        progressDialog = new LoadingDialog(this);
    }

    public void registrar(View view) {
        nombre = campoNombre.getText().toString();
        email = campoEmail.getText().toString();  // Cambio de nombre a campoEmail
        String password = campoContrasena.getText().toString();
        String telefono = campoTelefono.getText().toString();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(telefono)) {
            toastIncorrecto("Por favor, completa todos los campos");
        } else {
            createAccount(nombre, email, password, telefono);
        }
    }

    private void createAccount(final String username, final String email, final String password, final String telefono) {
        progressDialog.show();
        mAuthProvider.register(email, password).addOnCompleteListener((Activity) this, new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuthProvider.getUid();
                    User user = new User();
                    user.setId(id);
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setTelefono(telefono);
                    user.setTimestamp(new Date().getTime());
                    mUserProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                toastCorrecto("Registro exitoso");
                                Intent intent = new Intent(Registro.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                toastIncorrecto("Error en el registro");
                                Log.e("Registro", "Error en el registro", task.getException());
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    toastIncorrecto("Error en el registro: " + task.getException().getMessage());
                }
            }
        });
    }

    public void navegarAInicioSesion() {  // Cambio de nombre a navegarAInicioSesion
        startActivity(new Intent(this, Login.class));
    }

    public void toastCorrecto(String mensaje) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_toast_ok, (ViewGroup) findViewById(R.id.ll_custom_toast_ok));
        TextView txtMensaje = view.findViewById(R.id.txtMensajeToast1);
        txtMensaje.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public void toastIncorrecto(String mensaje) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_toast_error, (ViewGroup) findViewById(R.id.ll_custom_toast_error));
        TextView txtMensaje = view.findViewById(R.id.txtMensajeToast2);
        txtMensaje.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}
