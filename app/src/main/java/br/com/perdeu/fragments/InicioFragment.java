package br.com.perdeu.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

import br.com.perdeu.MeusAlertasList;
import br.com.perdeu.R;
import br.com.perdeu.model.Alerta;
import br.com.perdeu.model.AlertaDAO;

/**
 * Created by rhau on 6/15/18.
 */

@SuppressLint("ValidFragment")
public class InicioFragment extends android.app.Fragment {

    View viewInicio;
    private ListView listViewMeusAlertas;
    private List<Alerta> alertas = new ArrayList<>();
    private DatabaseReference db;
    private ProgressDialog dialog;
    private Button btnLimparAlertas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        viewInicio = inflater.inflate(R.layout.fragment_inicio, container, false);
        listViewMeusAlertas = viewInicio.findViewById(R.id.listViewMeusAlertas);
        listViewMeusAlertas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Alerta a = alertas.get(i);
                Uri uri = Uri.parse("fb-messenger://user/");
                uri = ContentUris.withAppendedId(uri, Long.parseLong(a.getFacebookIdDest()));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Por favor, instale o Facebook Messenger!", Toast.LENGTH_LONG).show();
                    Uri marketUri = Uri.parse("market://search?q=pname:com.facebook.orca");
                    Intent market = new Intent(Intent.ACTION_VIEW).setData(marketUri);
                    startActivity(market);
                }
            }
        });

        btnLimparAlertas = viewInicio.findViewById(R.id.btn_limpar_alertas);
        btnLimparAlertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertaDAO().deleteAll();
            }
        });

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Carregando dados...");
        dialog.show();
        return viewInicio;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getProviderData().get(currentUser.getProviderData().size() - 1).getUid();

        db = FirebaseDatabase.getInstance().getReference("users/" + uid + "/alertas");
        db.keepSynced(true);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alertas.clear();
                for (DataSnapshot alertaSnapshot : dataSnapshot.getChildren()) {
                    Alerta a = alertaSnapshot.getValue(Alerta.class);
                    alertas.add(a);
                }
                if (getActivity() != null) {
                    MeusAlertasList adapter = new MeusAlertasList(getActivity(), alertas);
                    listViewMeusAlertas.setAdapter(adapter);
                }
                dialog.hide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Busca cancelada.", Toast.LENGTH_LONG).show();
            }
        });
    }

}