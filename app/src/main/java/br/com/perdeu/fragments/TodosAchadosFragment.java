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
import br.com.perdeu.ReportPerdaActivity;
import br.com.perdeu.TodosAchadosList;
import br.com.perdeu.model.Achado;

/**
 * Created by rhau on 6/15/18.
 */

@SuppressLint("ValidFragment")
public class TodosAchadosFragment extends android.app.Fragment {

    View viewTodosAchados;
    private ListView listViewTodosAchados;
    private List<Achado> achados = new ArrayList<>();
    private DatabaseReference db;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        viewTodosAchados = inflater.inflate(R.layout.fragment_todos_achados, container, false);
        listViewTodosAchados = viewTodosAchados.findViewById(R.id.listViewTodosAchados);
        listViewTodosAchados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Achado a = achados.get(i);
            doIntentReport(a);
            }
        });
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Carregando dados...");
        dialog.show();
        return viewTodosAchados;
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
                achados.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot collection : ds.getChildren()) {
                        if (collection.getKey().equals("achados")) {
                            for (DataSnapshot registroAchado : collection.getChildren()) {
                                Achado a = (Achado) registroAchado.getValue(Achado.class);
                                if ( !a.getFacebookId().equals(uid) )
                                    achados.add(a);
                            }
                        }
                    }

                }
                if (getActivity() != null) {
                    TodosAchadosList adapter = new TodosAchadosList(getActivity(), achados);
                    listViewTodosAchados.setAdapter(adapter);
                }
                dialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Busca cancelada.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static final String ID = "idAchado";
    public static final String NOME = "nomeAchado";
    public static final String CATEGORIA = "categoriaAchado";
    public static final String QUANTIDADE = "quantidadeAchado";
    public static final String LOCAL_ENCONTRADO = "localEncontradoAchado";
    public static final String LOCAL_ATUAL = "localAtualAchado";
    public static final String DESCRICAO = "descricaoAchado";
    public static final String FACEBOOK_ID = "facebookId";
    public static final String IMAGE_URL = "imageUrl";

    private void doIntentReport(Achado a) {
        Intent intent = new Intent(getActivity(), ReportPerdaActivity.class);
        intent.putExtra(ID, a.getId_item());
        intent.putExtra(NOME, a.getNome_item());
        intent.putExtra(CATEGORIA, a.getCategoria_item().getTipo_item());
        intent.putExtra(QUANTIDADE, a.getQuantidade_item());
        intent.putExtra(LOCAL_ENCONTRADO, a.getLocalEncontrado_item());
        intent.putExtra(LOCAL_ATUAL, a.getLocalAtual_item());
        intent.putExtra(DESCRICAO, a.getDescricao_item());
        intent.putExtra(FACEBOOK_ID, a.getFacebookId());
        intent.putExtra(IMAGE_URL, a.getImageUrl());
        startActivity(intent);
    }
    
}