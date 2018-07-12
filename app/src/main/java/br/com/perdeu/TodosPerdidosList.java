package br.com.perdeu;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import br.com.perdeu.model.Perdido;

/**
 * Created by rhau on 6/10/18.
 */

public class TodosPerdidosList extends ArrayAdapter<Perdido> {

    private Activity context;
    private List<Perdido> perdidos;

    public TodosPerdidosList(Activity context, List<Perdido> perdidos) {
        super(context, R.layout.list_itens_layout, perdidos);
        this.context = context;
        this.perdidos = perdidos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_itens_layout, null, true);

        TextView nome = listViewItem.findViewById(R.id.nome_item_list);
        TextView categoria = listViewItem.findViewById(R.id.categoria_item_list);
        TextView local = listViewItem.findViewById(R.id.local_item_list);
        TextView descricao = listViewItem.findViewById(R.id.descricao_item_list);
        ImageView imagem = listViewItem.findViewById(R.id.img_item_list);

        Perdido p = perdidos.get(position);
        if (p.getNome_item() != null) nome.setText(p.getNome_item());
        if (p.getCategoria_item() != null) categoria.setText("Tipo: " + p.getCategoria_item().getTipo_item());
        if (p.getProvavelLocalPerda_item() != null) local.setText("Provável local perda: " + p.getProvavelLocalPerda_item());
        if (p.getDescricao_item() != null) descricao.setText("Descrição: " + p.getDescricao_item());
        if (p.getImageUrl() != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child("itens/" + p.getId_item() + ".jpg");
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(pathReference)
                    .override(300, 300)
                    .centerCrop()
                    .into(imagem);
            imagem.setBackgroundResource(android.R.color.transparent);
        }

        return listViewItem;
    }

}