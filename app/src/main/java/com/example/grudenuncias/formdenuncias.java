package com.example.grudenuncias;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kotlin.contracts.Returns;
import model.Denuncia;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link formdenuncias#newInstance} factory method to
 * create an instance of this fragment.
 */
public class formdenuncias extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public formdenuncias() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment formdenuncias.
     */
    // TODO: Rename and change types and number of parameters
    public static formdenuncias newInstance(String param1, String param2) {
        formdenuncias fragment = new formdenuncias();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private Uri imageUri;
    private String imageUrl;
    private ImageView imagePreview;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_formdenuncias, container, false);

        // Configurar Spinner
        Spinner spinner = view.findViewById(R.id.spinner_opcoes);
        String[] opcoes = {"Tipo de denúncia", "Estrada", "Energia", "Acidente", "Água"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, opcoes);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        // Configurar botão e ImageView para seleção de imagem
        Button btnSelecionarImagem = view.findViewById(R.id.btn_selecionar_imagem);
        imagePreview = view.findViewById(R.id.imagePreview);

        btnSelecionarImagem.setOnClickListener(v -> selecionarImagem());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        EditText textLocalizacao = view.findViewById(R.id.text_localizacao);
        EditText textDescricao = view.findViewById(R.id.text_Descricao);
        Button botaoDenunciar = view.findViewById(R.id.botaoDenunciar);

            botaoDenunciar.setOnClickListener(v -> {
                if (imageUrl == null || imageUrl.isEmpty()) {
                    Toast.makeText(getContext(), "Aguarde o upload da imagem antes de enviar a denúncia", Toast.LENGTH_SHORT).show();
                    return;
                }
                salvarDenuncia();
                Fragment fragmentoPrincipal = new principal();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragmentoPrincipal)
                        .addToBackStack(null)
                        .commit();

            });
            return view;
        }





    public static String capturarDataAtual() {
        SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return formatoSaida.format(new Date());
    }

    private void selecionarImagem() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
        } else {

            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();

            StorageReference imageRef = storageRef.child("denuncias/" + System.currentTimeMillis() + ".jpg");


            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrl = uri.toString();
                            Log.d("Firebase", "Imagem salva! URL: " + imageUrl);
                            imagePreview.setImageURI(imageUri);



                        }).addOnFailureListener(e -> Log.e("Firebase", "Erro ao obter URL da imagem", e));
                    })
                    .addOnFailureListener(e -> Log.e("Firebase", "Erro ao salvar imagem no Firebase Storage", e));
        }
    }

    private void salvarDenuncia() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String usuarioId = auth.getCurrentUser().getUid();
        String tipoDenuncia = ((Spinner) getView().findViewById(R.id.spinner_opcoes)).getSelectedItem().toString();
        String localizacao = ((EditText) getView().findViewById(R.id.text_localizacao)).getText().toString();
        String descricao = ((EditText) getView().findViewById(R.id.text_Descricao)).getText().toString();
        String dataFormatada = capturarDataAtual();


        if (imageUrl == null || imageUrl.isEmpty()) {
            Toast.makeText(getContext(), "Erro: URL da imagem não gerada", Toast.LENGTH_SHORT).show();
            return;
        }

        Denuncia denuncia = new Denuncia("1", usuarioId, tipoDenuncia, localizacao, descricao, imageUrl, dataFormatada, null, null);

        db.collection("denuncias").add(denuncia)
                .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Denúncia enviada!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Firebase", "Erro ao enviar denúncia", e));
    }
}




