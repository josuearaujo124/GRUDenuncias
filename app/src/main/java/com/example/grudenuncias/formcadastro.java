package com.example.grudenuncias;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class formcadastro extends AppCompatActivity {

    private EditText editNome,editEmail,editSenha;
    private Button botaoCadastrar;
    String usuarioID;
    String[] mensagens = {"preencha todos os dados","Cadastro Realizado com sucesso!"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formcadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        IniciarComponentes();

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = editNome.getText().toString();
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v,mensagens[0],Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else {
                    CadastrarUsuario(v);
                }

            }
        });


    }

    private void CadastrarUsuario(View v){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    salvarDadosUsuario();

                    Snackbar snackbar = Snackbar.make(v,mensagens[1],Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            telaPrincipal();
                        }
                    },1000);
                }
                else {
                    String erro;
                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Digite uma Senha com no mimimo 6 Caracteres";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "esta conta ja foi cadastrada";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "Email invalido";
                    } catch (Exception e){
                        erro = "Erro ao cadastrar Usuario";
                    }

                    Snackbar snackbar = Snackbar.make(v,erro,Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }
            }
        });

    }

    private void salvarDadosUsuario(){
        String nome = editNome.getText().toString();

        FirebaseFirestore bancodDados = FirebaseFirestore.getInstance();

        Map<String,Object> Usuarios = new HashMap<>();
        Usuarios.put("nome",nome);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docDReferencia = bancodDados.collection("Usuarios").document(usuarioID);

        docDReferencia.set(Usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("bancodDados", "Sucesso ao Salvar os Dados!: ");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("bancodDados_erro", "Erro ao Salvar os Dados: " + e.toString());
                    }
                }) ;

    }

    private void IniciarComponentes(){
        editNome = findViewById(R.id.cadastroNome);
        editEmail = findViewById(R.id.cadastroEmail);
        editSenha = findViewById(R.id.cadastroSenha);
        botaoCadastrar = findViewById(R.id.cadastroBotao);
    }
private void telaPrincipal(){
    Intent intent = new Intent(formcadastro.this, MainActivity.class);
    startActivity(intent);
    finish();
}
}