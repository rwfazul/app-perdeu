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

import br.com.perdeu.model.Achado;

/**
 * Created by rhau on 6/10/18.
 */

public class TodosAchadosList extends ArrayAdapter<Achado> {

    private Activity context;
    private List<Achado> achados;

    public TodosAchadosList(Activity context, List<Achado> achados) {
        super(context, R.layout.list_itens_layout, achados);
        this.context = context;
        this.achados = achados;
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

        Achado a = achados.get(position);
        if (a.getNome_item() != null) nome.setText(a.getNome_item());
        if (a.getLocalEncontrado_item() != null) local.setText("Local do encontro: " + a.getLocalEncontrado_item());
        if (a.getCategoria_item() != null) categoria.setText("Tipo: " + a.getCategoria_item().getTipo_item());
        if (a.getDescricao_item() != null) descricao.setText("Descrição: " + a.getDescricao_item());
        if (a.getImageUrl() != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child("itens/" + a.getId_item() + ".jpg");
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