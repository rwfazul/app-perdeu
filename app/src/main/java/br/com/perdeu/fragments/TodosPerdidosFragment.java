package br.com.perdeu.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.perdeu.R;
import br.com.perdeu.ReportEncontroActivity;
import br.com.perdeu.TodosPerdidosList;
import br.com.perdeu.model.Perdido;

/**
 * Created by rhau on 6/15/18.
 */

@SuppressLint("ValidFragment")
public class TodosPerdidosFragment extends android.app.Fragment {

    View viewTodosPerdidos;
    private ListView listViewTodosPerdidos;
    private List<Perdido> perdidos = new ArrayList<>();
    private DatabaseReference db;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        viewTodosPerdidos = inflater.inflate(R.layout.fragment_todos_perdidos, container, false);
        listViewTodosPerdidos = viewTodosPerdidos.findViewById(R.id.listViewTodosPerdidos);
        listViewTodosPerdidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Perdido p = perdidos.get(i);
            doIntentEdit(p);
            }
        });
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Carregando dados...");
        dialog.show();
        return viewTodosPerdidos;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uid = currentUser.getProviderData().get(currentUser.getProviderData().size() - 1).getUid();

        db = FirebaseDatabase.getInstance().getReference("users");
        db.keepSynced(true);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                perdidos.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot collection : ds.getChildren()) {
                        if (collection.getKey().equals("perdidos")) {
                            for (DataSnapshot registroPerdido : collection.getChildren()) {
                                Perdido p = (Perdido) registroPerdido.getValue(Perdido.class);
                                if ( !p.getFacebookId().equals(uid) )
                                    perdidos.add(p);
                            }
                        }
                    }
                }
                if (getActivity() != null) {
                    TodosPerdidosList adapter = new TodosPerdidosList(getActivity(), perdidos);
                    listViewTodosPerdidos.setAdapter(adapter);
                }
                dialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Busca cancelada.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static final String ID = "idPerdido";
    public static final String NOME = "nomePerdido";
    public static final String CATEGORIA = "categoriaPerdido";
    public static final String QUANTIDADE = "quantidadePerdido";
    public static final String LOCAL_PROVAVEL_PERDA = "localProvavelPerda";
    public static final String PREFERENCIA_RETIRADA = "preferenciaRetirada";
    public static final String DESCRICAO = "descricaoPerdido";
    public static final String FACEBOOK_ID = "facebookId";
    public static final String IMAGE_URL = "imageUrl";

    private void doIntentEdit(Perdido p) {
        Intent intent = new Intent(getActivity(), ReportEncontroActivity.class);
        intent.putExtra(ID, p.getId_item());
        intent.putExtra(NOME, p.getNome_item());
        intent.putExtra(CATEGORIA, p.getCategoria_item().getTipo_item());
        intent.putExtra(QUANTIDADE, p.getQuantidade_item());
        intent.putExtra(LOCAL_PROVAVEL_PERDA, p.getProvavelLocalPerda_item());
        intent.putExtra(PREFERENCIA_RETIRADA, p.getPreferenciaRetirada_item());
        intent.putExtra(DESCRICAO, p.getDescricao_item());
        intent.putExtra(FACEBOOK_ID, p.getFacebookId());
        intent.putExtra(IMAGE_URL, p.getImageUrl());
        startActivity(intent);
    }

}