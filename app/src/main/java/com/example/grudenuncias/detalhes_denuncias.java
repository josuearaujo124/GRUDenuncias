package com.example.grudenuncias;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class detalhes_denuncias extends Fragment {
    private TextView titulo, descricao, data, status, responsavel;
    private ImageView imagemDenuncia;
    private FirebaseFirestore db;
    private String denunciaId;

    public detalhes_denuncias() {}

    public static detalhes_denuncias newInstance(String titulo, String responsavel, String data, String status, String descricao, String local, String imageUrl, String denunciaID) {
        detalhes_denuncias fragment = new detalhes_denuncias();
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        args.putString("responsavel", responsavel);
        args.putString("data", data);
        args.putString("status", status);
        args.putString("descricao", descricao);
        args.putString("local", local);
        args.putString("imageUrl", imageUrl);
        args.putString("denunciaID", denunciaID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalhes_denuncias, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar elementos da UI
        titulo = view.findViewById(R.id.titulo_detalhes);
        descricao = view.findViewById(R.id.descricao_detalhes);
        data = view.findViewById(R.id.data_detalhes);
        status = view.findViewById(R.id.status_detalhes);
        responsavel = view.findViewById(R.id.responsavel_detalhes);
        imagemDenuncia = view.findViewById(R.id.img_detalhes);

        // Definir valores dos argumentos
        if (getArguments() != null) {
            titulo.setText(getArguments().getString("titulo", "Título não disponível"));
            descricao.setText(getArguments().getString("descricao", "Sem descrição"));
            data.setText(getArguments().getString("data", "Data desconhecida"));
            status.setText(getArguments().getString("status", "Status indefinido"));
            responsavel.setText(getArguments().getString("responsavel", "Responsável não informado"));
            denunciaId = getArguments().getString("imageUrl", "");

            // Carregar imagem do Firestore
            carregarImagemDenuncia();
        }

        return view;
    }


    private void carregarImagemDenuncia() {
        if (denunciaId != null && !denunciaId.isEmpty()) {
            Glide.with(requireContext())
                    .load(denunciaId)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imagemDenuncia);
        } else {
            Log.e("Firebase", "URL da imagem não encontrada!");
            Glide.with(requireContext()).load(R.drawable.placeholder_image).into(imagemDenuncia);
        }
    }
}
