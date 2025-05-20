package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.grudenuncias.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import model.Denuncia;

public class DenunciaAdapter extends RecyclerView.Adapter<DenunciaAdapter.ViewHolder> {
    private List<Denuncia> denunciaList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Denuncia denuncia);
    }

    public DenunciaAdapter(List<Denuncia> denunciaList, Context context, OnItemClickListener listener) {
        this.denunciaList = denunciaList;
        this.context = context;
        this.listener = listener;
    }

    public void atualizarLista(List<Denuncia> novasDenuncias) {
        this.denunciaList.clear();
        this.denunciaList.addAll(novasDenuncias);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_denuncia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Denuncia denuncia = denunciaList.get(position);
        holder.textTitle.setText(denuncia.gettitulo());
        holder.textDescricao.setText(denuncia.getDescricao());
        holder.textData.setText(denuncia.getData());

        // Carregar imagem da denúncia com Glide
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("denuncias").document(denuncia.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("imageUrl"); // Obtém a URL do Firestore
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(context)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.placeholder_image)
                                    .error(R.drawable.error_image)
                                    .into(holder.imageDenuncia);
                        } else {
                            holder.imageDenuncia.setImageResource(R.drawable.placeholder_image);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    holder.imageDenuncia.setImageResource(R.drawable.placeholder_image);
                });


        // Definir evento de clique para abrir o fragmento detalhes_denuncias
        holder.itemView.setOnClickListener(v -> listener.onItemClick(denuncia));
    }

    @Override
    public int getItemCount() {
        return denunciaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDescricao, textData;
        ImageView imageDenuncia;

        public ViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textDescricao = itemView.findViewById(R.id.text_descricao);
            textData = itemView.findViewById(R.id.text_date);
            imageDenuncia = itemView.findViewById(R.id.image_denuncia);
        }
    }
}