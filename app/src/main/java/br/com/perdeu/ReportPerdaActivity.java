package br.com.perdeu;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import br.com.perdeu.fragments.TodosAchadosFragment;
import br.com.perdeu.model.Alerta;
import br.com.perdeu.model.AlertaDAO;

/**
 * Created by rhau on 6/25/18.
 */

public class ReportPerdaActivity extends AppCompatActivity {

    private String idAchado;
    private TextView nomeAchado;
    private TextView categoriaAchado;
    private TextView unidadesAchado;
    private TextView localAchado;
    private TextView localAtualAchado;
    private TextView descricaoAchado;
    private ImageView imagemAchado;
    private String facebookIdAchado;
    private String nomeItem;
    private Button btnRelatoPerda;
    private Button btnAlertaPerda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_perda);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nomeAchado = findViewById(R.id.nome_item_achado);
        categoriaAchado = findViewById(R.id.categoria_item_achado);
        unidadesAchado = findViewById(R.id.unidades_item_achado);
        localAchado = findViewById(R.id.lugar_item_achado);
        localAtualAchado = findViewById(R.id.lugar_atual_item_achado);
        descricaoAchado = findViewById(R.id.descricao_item_achado);
        imagemAchado = findViewById(R.id.img_item_achado);
        btnRelatoPerda = findViewById(R.id.btn_relato_perda);
        btnAlertaPerda = findViewById(R.id.btn_alerta_perda);

        getIntentData();

        btnRelatoPerda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("fb-messenger://user/");
                uri = ContentUris.withAppendedId(uri, Long.parseLong(facebookIdAchado));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ReportPerdaActivity.this, "Por favor, instale o Facebook Messenger!", Toast.LENGTH_LONG).show();
                    Uri marketUri = Uri.parse("market://search?q=pname:com.facebook.orca");
                    Intent market = new Intent(Intent.ACTION_VIEW).setData(marketUri);
                    startActivity(market);
                }
            }
        });

        btnAlertaPerda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alerta a = new Alerta();
                a.setTitulo("Relato de perda");
                a.setConteudo("Você realizou um alerta sobre a perda do item '"
                        + nomeItem + "'. Clique para iniciar o chat com o portador atual do item.");
                a.setFacebookIdDest(facebookIdAchado);
                new AlertaDAO().save(a);
                Alerta send = new Alerta();
                a.setTitulo("Provável dono encontrado");
                a.setConteudo("Alguém está lhe alertando sobre o item '"
                        + nomeItem + "'. Clique para inciar o chat com o provável dono.");
                a.setFacebookIdDest(facebookIdAchado);
                new AlertaDAO().send(a);
                redirectMeusAchados();
            }
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();

        String id = intent.getStringExtra(TodosAchadosFragment.ID);
        String nome = intent.getStringExtra(TodosAchadosFragment.NOME);
        String categoria = intent.getStringExtra(TodosAchadosFragment.CATEGORIA);
        String quantidade = intent.getStringExtra(TodosAchadosFragment.QUANTIDADE);
        String localEncontrado = intent.getStringExtra(TodosAchadosFragment.LOCAL_ENCONTRADO);
        String localAtual = intent.getStringExtra(TodosAchadosFragment.LOCAL_ATUAL);
        String descricao = intent.getStringExtra(TodosAchadosFragment.DESCRICAO);
        String facebookIdItem = intent.getStringExtra(TodosAchadosFragment.FACEBOOK_ID);
        String imageUrl = intent.getStringExtra(TodosAchadosFragment.IMAGE_URL);

        if (id != null) idAchado = id;
        if (nome != null) { nomeAchado.setText("Nome: " + nome); nomeItem = nome; }
        if (categoria != null) categoriaAchado.setText("Categoria: " + categoria);
        if (quantidade != null) unidadesAchado.setText("Quantidade: " + quantidade);
        if (localEncontrado != null) localAchado.setText("Local onde foi encontrado: " + localEncontrado);
        if (localAtual!= null) localAtualAchado.setText("Localização atual: " + localAtual);
        if (descricao != null) descricaoAchado.setText("Descrição: " + descricao);
        if (facebookIdItem != null) facebookIdAchado = facebookIdItem;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child("itens/" + id + ".jpg");
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(pathReference)
                    .override(600, 600)
                    .centerCrop()
                    .into(imagemAchado);
            imagemAchado.setBackgroundResource(android.R.color.transparent);
        }
    }

    private void redirectMeusAchados() {
        Toast.makeText(ReportPerdaActivity.this, "Alerta realizado com sucesso!", Toast.LENGTH_LONG).show();
        /*Intent intent = new Intent(ReportPerdaActivity.this, DashboardActivity.class);
        intent.putExtra("fragment", "todos_achados");
        startActivity(intent);*/
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}