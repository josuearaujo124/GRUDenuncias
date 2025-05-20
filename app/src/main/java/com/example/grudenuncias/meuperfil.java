package com.example.grudenuncias;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class meuperfil extends Fragment {

    private TextView nomeUsuario, emailUsuario;
    private Button bt_deslogar;
    private FirebaseFirestore db;
    private String usuarioID;

    public meuperfil() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meuperfil, container, false);

        // Inicializar componentes corretamente dentro do onCreateView()
        nomeUsuario = view.findViewById(R.id.nomeUsuario);
        emailUsuario = view.findViewById(R.id.emailUsuario);
        bt_deslogar = view.findViewById(R.id.bt_deslogar);

        db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bt_deslogar.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent telaLogin = new Intent(getActivity(), formlogin.class);
            startActivity(telaLogin);
            requireActivity().finish(); // 🔹 Finaliza a Activity corretamente
        });

        carregarDadosUsuario();

        return view;
    }

    // Método para buscar os dados do usuário no Firestore
    private void carregarDadosUsuario() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);

        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Erro ao carregar perfil!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                nomeUsuario.setText(documentSnapshot.getString("nome"));
                emailUsuario.setText(email);
            }
        });
    }
}