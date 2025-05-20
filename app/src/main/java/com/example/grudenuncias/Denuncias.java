package com.example.grudenuncias;

import static com.example.grudenuncias.formdenuncias.capturarDataAtual;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import adapter.DenunciaAdapter;
import model.Denuncia;

public class Denuncias extends Fragment {

    private FirebaseFirestore db;
    private boolean isAdmin = false;
    private String usuarioID;

    public Denuncias() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_denuncias, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.viewdenucias);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            usuarioID = auth.getCurrentUser().getUid();
            verificarSeEhAdmin(usuarioID);
        } else {
            Toast.makeText(requireContext(), "Erro: Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            Log.e("Auth", "Usuário não autenticado!");
            return view;
        }

        List<Denuncia> listaDenuncias = new ArrayList<>();
        DenunciaAdapter adapter = new DenunciaAdapter(listaDenuncias, requireContext(), this::abrirDetalhesDenuncia);
        recyclerView.setAdapter(adapter);

        db.collection("denuncias").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Erro ao carregar denúncias", e);
                return;
            }

            if (querySnapshot == null || querySnapshot.isEmpty()) {
                Toast.makeText(requireContext(), "Nenhuma denúncia encontrada.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Denuncia> novasDenuncias = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Denuncia denuncia = doc.toObject(Denuncia.class);
                if (denuncia != null) {
                    denuncia.setId(doc.getId());
                    denuncia.setImageUrl(doc.contains("imageUrl") ? doc.getString("imageUrl") : ""); // Correção para evitar null
                    novasDenuncias.add(denuncia);
                }
            }

            if (isAdded()) {
                adapter.atualizarLista(novasDenuncias);
            } else {
                Log.e("DEBUG", "Fragmento não está mais ativo, evitando erro.");
            }
        });

        return view;
    }

    private void verificarSeEhAdmin(String userId) {
        db.collection("Usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("autorizacao")) {
                        String autorizacao = documentSnapshot.getString("autorizacao");
                        isAdmin = "Admin".equalsIgnoreCase(autorizacao);
                        Log.d("AdminCheck", "Usuário tem autorização de admin? " + isAdmin);
                    } else {
                        Log.e("AdminCheck", "Documento não encontrado ou campo 'autorizacao' ausente.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Erro ao verificar autorização", e));
    }

    private void abrirDetalhesDenuncia(Denuncia denuncia) {
        if (isAdmin) {
            Intent intent = new Intent(getActivity(), analisedenuncias.class);
            intent.putExtra("denunciaId", denuncia.getId());
            intent.putExtra("titulo", denuncia.gettitulo());
            intent.putExtra("responsavel", denuncia.getResponsavel());
            intent.putExtra("data", denuncia.getData());
            intent.putExtra("status", denuncia.getStatus());
            intent.putExtra("descricao", denuncia.getDescricao());
            intent.putExtra("localizacao", denuncia.getLocalizacao());
            intent.putExtra("imageUrl", denuncia.getImageUrl());
            startActivity(intent);
        } else {
            Fragment fragment = detalhes_denuncias.newInstance(
                    denuncia.gettitulo(),
                    denuncia.getResponsavel(),
                    denuncia.getData(),
                    denuncia.getStatus(),
                    denuncia.getDescricao(),
                    denuncia.getLocalizacao(),
                    denuncia.getImageUrl(),
                    denuncia.getId()
            );

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }


}
