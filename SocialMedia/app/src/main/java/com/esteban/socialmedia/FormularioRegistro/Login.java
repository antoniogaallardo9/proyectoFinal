package com.esteban.socialmedia.FormularioRegistro;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.esteban.socialmedia.InicioApp.InicioApp;
import com.esteban.socialmedia.R;
import com.esteban.socialmedia.loading.LoadingDialog;
import com.esteban.socialmedia.models.User;
import com.esteban.socialmedia.providers.AuthProvider;
import com.esteban.socialmedia.providers.UserProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;



public class Login extends AppCompatActivity {

    // Elementos de la interfaz
    private EditText campoCorreo;
    private EditText campoContrasena;
    private AppCompatButton iniciar;

    LinearLayout signInButton;

    // Proveedores de autenticación y usuario
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;

   LoadingDialog loadingDialog;


    // Cliente de inicio de sesión de Google
    private GoogleSignInClient googleSignInClient;
    private final int REQUEST_CODE_GOOGLE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialización de elementos de la interfaz y proveedores
        campoCorreo = findViewById(R.id.email);
        campoContrasena = findViewById(R.id.contrasena);
        signInButton = findViewById(R.id.iniciarcongoogle);
        iniciar = findViewById(R.id.iniciarSesion);
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        loadingDialog = new LoadingDialog(this);


        // Configuración de opciones de inicio de sesión de Google
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(Login.this, options);

        // Configuración de listeners para los botones
        signInButton.setOnClickListener(view -> signInGoogle());
        iniciar.setOnClickListener(view -> login());

        // Cierre de sesión de Google al iniciar la actividad
        googleSignInClient.signOut();
    }

    //INICIAR SESION DE UNA VEZ 😁
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.getUserSession() != null){
            Intent intent = new Intent(Login.this, InicioApp.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void signInGoogle() {
        // Inicia la actividad de inicio de sesión de Google
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado de la actividad de inicio de sesión de Google
        if (requestCode == REQUEST_CODE_GOOGLE) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Manejo de excepciones en caso de error al iniciar sesión con Google
                Log.w("ERROR", "Google sign in failed", e);
                toastIncorrecto("No se pudo iniciar sesión con Google");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        loadingDialog.show();
        // Autenticación en Firebase con la cuenta de Google
        mAuthProvider.googleLogin(acct).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                String id = mAuthProvider.getUid();
                checkUserExist(id);
                showToast("Se inició sesión con éxito");
            } else {
                loadingDialog.dismiss();
                // Manejo de excepciones en caso de error en la autenticación con Google
                Log.w("ERROR", "signInWithCredential:failure", task.getException());
                toastIncorrecto("No se pudo iniciar sesión con Google");
            }
        });
    }

    private void checkUserExist(final String id) {
        // Verifica si el usuario ya existe en la base de datos
        mUserProvider.getUser(id).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                loadingDialog.dismiss();
                Intent intent = new Intent(Login.this, InicioApp.class);
                showToast("Se inició sesión con éxito");
                startActivity(intent);
            } else {
                // Si el usuario no existe, crea un nuevo usuario en la base de datos
                String email = mAuthProvider.getEmail();
                User user = new User();
                user.setEmail(email);
                user.setId(id);
                mUserProvider.create(user).addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(Login.this, CompletarInformacion.class);
                        showToast("Registro exitoso");
                        startActivity(intent);
                    } else {
                        // Manejo de excepciones en caso de error al crear el usuario
                        toastIncorrecto("No se pudo almacenar la información del usuario");
                    }
                });
            }
        });
    }

    private void login() {
        // Inicia sesión con correo y contraseña
        String email = campoCorreo.getText().toString().trim();
        String password = campoContrasena.getText().toString().trim();
        //loadingDialog.show();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // Validación de campos vacíos
            toastIncorrecto("Por favor, completa todos los campos");
        } else {
            // Este es el punto donde se produce el error, verifica que 'email' y 'password' no sean nulos o vacíos
            mAuthProvider.login(email, password).addOnCompleteListener(task -> {
                loadingDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(Login.this, InicioApp.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // Manejo de excepciones en caso de error al iniciar sesión
                    toastIncorrecto("El email o la contraseña que ingresaste no son correctos");
                }
            });
        }
    }


    private void showToast(String message) {
        // Muestra un Toast personalizado de éxito
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_toast_ok, findViewById(R.id.ll_custom_toast_ok));
        TextView txtMensaje = view.findViewById(R.id.txtMensajeToast1);
        txtMensaje.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    private void toastIncorrecto(String mensaje) {
        // Muestra un Toast personalizado de error
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_toast_error, findViewById(R.id.ll_custom_toast_error));
        TextView txtMensaje = view.findViewById(R.id.txtMensajeToast2);
        txtMensaje.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}
