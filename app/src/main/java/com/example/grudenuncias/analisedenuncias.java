package com.example.grudenuncias;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class analisedenuncias extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String denunciaId;
    private EditText adminResponsavel;
    private Spinner spinnerStatus;
    private Button btnConfirmar, btnCancelar;
    private ImageView imgAnalise;
    private TextView txtStatus, txtResponsavel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analisedenuncias);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Recuperando os componentes da tela
        adminResponsavel = findViewById(R.id.admin_responsavel);
        spinnerStatus = findViewById(R.id.spinner_status);
        btnConfirmar = findViewById(R.id.bt_Feedback);
        btnCancelar = findViewById(R.id.bt_cancelar);
        imgAnalise = findViewById(R.id.img_analise);
        txtStatus = findViewById(R.id.status_atual);
        txtResponsavel = findViewById(R.id.Responsavel_atual);

        // Recuperando dados passados via Intent
        Bundle args = getIntent().getExtras();
        if (args != null) {
            denunciaId = args.getString("denunciaId", "");
            if (denunciaId.isEmpty()) {
                Toast.makeText(this, "Erro: ID da denúncia não encontrado.", Toast.LENGTH_SHORT).show();
                finish();
            }

            TextView tituloTextView = findViewById(R.id.titulo_detalhes);
            tituloTextView.setText(args.getString("titulo", "Título não disponível"));

            TextView dataTextView = findViewById(R.id.data_detalhes);
            dataTextView.setText(args.getString("data", "Data não disponível"));

            TextView descricaoTextView = findViewById(R.id.descricao_detalhes);
            descricaoTextView.setText(args.getString("descricao", "Descrição não disponível"));

            carregarDadosDenuncia();
        }

        // Configurando o Spinner
        String[] statusOptions = {"Concluído", "Em progresso", "Interrompido", "Aguardando"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                Toast.makeText(analisedenuncias.this, "Status selecionado: " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnConfirmar.setOnClickListener(v -> salvarDadosFirebase());
        btnCancelar.setOnClickListener(v -> abrirFragmentPrincipal());
    }

    private void carregarDadosDenuncia() {
        db.collection("denuncias").document(denunciaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("status");
                        String responsavel = documentSnapshot.getString("responsavel");
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        // Define os valores nos TextViews
                        txtStatus.setText(status);
                        txtResponsavel.setText(responsavel);
                        adminResponsavel.setText(responsavel);

                        // Atualiza Spinner para refletir o status atual
                        for (int i = 0; i < spinnerStatus.getCount(); i++) {
                            if (spinnerStatus.getItemAtPosition(i).toString().equals(status)) {
                                spinnerStatus.setSelection(i);
                                break;
                            }
                        }

                        // Carrega imagem
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.placeholder_image)
                                    .error(R.drawable.error_image)
                                    .into(imgAnalise);
                        } else {
                            Glide.with(this).load(R.drawable.placeholder_image).into(imgAnalise);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao buscar dados", e));
    }

    private void salvarDadosFirebase() {
        String responsavel = adminResponsavel.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

        if (responsavel.isEmpty()) {
            Toast.makeText(this, "Preencha o responsável", Toast.LENGTH_SHORT).show();
            return;
        }

        if (denunciaId == null || denunciaId.isEmpty()) {
            Toast.makeText(this, "Erro: ID da denúncia não encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> dadosAtualizados = new HashMap<>();
        dadosAtualizados.put("responsavel", responsavel);
        dadosAtualizados.put("status", status);

        db.collection("denuncias").document(denunciaId)
                .update(dadosAtualizados)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Denúncia atualizada com sucesso!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao atualizar denúncia", Toast.LENGTH_SHORT).show());
    }
    private void abrirFragmentPrincipal() {
        Fragment fragment = new principal(); // Substitua pelo nome correto do seu fragmento principal

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment); // Certifique-se que `frame_layout` é o container de fragments
        transaction.addToBackStack(null);
        transaction.commit();
    }
}