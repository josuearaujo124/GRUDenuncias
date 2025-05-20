package com.example.grudenuncias;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import adapter.DenunciaAdapter;
import model.Denuncia;

public class principal extends Fragment {

    private FirebaseFirestore db;
    private String usuarioID;

    public principal() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_principal, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        FloatingActionButton fab = view.findViewById(R.id.bt_novasdenuncias);
        fab.setOnClickListener(v -> abrirNovoFragment());

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            usuarioID = auth.getCurrentUser().getUid();
        } else {
            Toast.makeText(requireContext(), "Erro: Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            Log.e("Auth", "Usuário não autenticado!");
            return view;
        }

        List<Denuncia> listaDenuncias = new ArrayList<>();
        DenunciaAdapter adapter = new DenunciaAdapter(listaDenuncias, requireContext(), this::abrirDetalhesDenuncia);
        recyclerView.setAdapter(adapter);

        // Buscar e ordenar denúncias do usuário atual pela mais recente
        db.collection("denuncias")
                .whereEqualTo("usuarioId", usuarioID)
                .orderBy("data", Query.Direction.DESCENDING) // Exibir a mais recente primeiro
                .addSnapshotListener((querySnapshot, e) -> {
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

    private void abrirDetalhesDenuncia(Denuncia denuncia) {
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

    private void abrirNovoFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new formdenuncias());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}