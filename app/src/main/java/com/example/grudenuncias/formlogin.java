package com.example.grudenuncias;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class formlogin extends AppCompatActivity {

    private TextView tela_cadastro;
    private EditText editEmail,editSenha;
    private Button bt_login;
    private ProgressBar progressBar;

    String[] mensagens ={ "Preencha os Dados","Login Realizado com Sucesso"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formlogin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        iniciar_componentes ();

        tela_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mudar_p_cadastro = new Intent(formlogin.this, formcadastro.class);
                startActivity(mudar_p_cadastro);
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v,mensagens[0],Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else {
                    autenticarUsuario();
                }
            }
        });

    }

    protected void onStart(){
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null){
            telaPrincipal();
        }
    }

    private void autenticarUsuario(){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            telaPrincipal();
                        }
                    },1000);
                }
            }
        });
    }


    private void telaPrincipal(){
        Intent intent = new Intent(formlogin.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void iniciar_componentes(){
        tela_cadastro = findViewById(R.id.text_telaCadastro);
        editEmail = findViewById(R.id.loginusuario);
        editSenha = findViewById(R.id.loginsenha);
        bt_login = findViewById(R.id.botaoEntrar);
        progressBar = findViewById(R.id.barrradeprogresso);

    }



}