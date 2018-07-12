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
import br.com.perdeu.fragments.TodosPerdidosFragment;
import br.com.perdeu.model.Alerta;
import br.com.perdeu.model.AlertaDAO;

public class ReportEncontroActivity extends AppCompatActivity {

    private String idPerdido;
    private TextView nomePerdido;
    private TextView categoriaPerdido;
    private TextView unidadesPerdido;
    private TextView localPerdido;
    private TextView preferenciaPerdido;
    private TextView descricaoPerdido;
    private ImageView imagemPerdido;
    private String facebookIdPerdido;
    private String nomeItem;
    private Button btnRelatoEncontro;
    private Button btnAlertaEncontro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_encontro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nomePerdido = findViewById(R.id.nome_item_perdido);
        categoriaPerdido = findViewById(R.id.categoria_item_perdido);
        unidadesPerdido = findViewById(R.id.unidades_item_perdido);
        localPerdido = findViewById(R.id.lugar_item_perdido);
        preferenciaPerdido = findViewById(R.id.preferencia_item_perdido);
        descricaoPerdido = findViewById(R.id.descricao_item_perdido);
        imagemPerdido = findViewById(R.id.img_item_perdido);
        btnRelatoEncontro = findViewById(R.id.btn_relato_encontro);
        btnAlertaEncontro = findViewById(R.id.btn_alerta_encontro);

        getIntentData();

        btnRelatoEncontro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("fb-messenger://user/");
                uri = ContentUris.withAppendedId(uri, Long.parseLong(facebookIdPerdido));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ReportEncontroActivity.this, "Por favor, instale o Facebook Messenger!", Toast.LENGTH_LONG).show();
                    Uri marketUri = Uri.parse("market://search?q=pname:com.facebook.orca");
                    Intent market = new Intent(Intent.ACTION_VIEW).setData(marketUri);
                    startActivity(market);
                }
            }
        });

        btnAlertaEncontro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alerta a = new Alerta();
                a.setTitulo("Relato de encontro");
                a.setConteudo("Você realizou um alerta sobre o encontro do item '"
                        + nomeItem + "'. Clique para iniciar o chat com o dono.");
                a.setFacebookIdDest(facebookIdPerdido);
                new AlertaDAO().save(a);
                Alerta send = new Alerta();
                a.setTitulo("Seu item foi encontrado");
                a.setConteudo("Alguém está lhe alertando sobre o item '"
                        + nomeItem + "'. Clique para inciar o chat com o portador atual do item.");
                a.setFacebookIdDest(facebookIdPerdido);
                new AlertaDAO().send(a);
                redirectMeusAchados();
            }
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();

        String id = intent.getStringExtra(TodosPerdidosFragment.ID);
        String nome = intent.getStringExtra(TodosPerdidosFragment.NOME);
        String categoria = intent.getStringExtra(TodosPerdidosFragment.CATEGORIA);
        String quantidade = intent.getStringExtra(TodosPerdidosFragment.QUANTIDADE);
        String localProvavelPerda = intent.getStringExtra(TodosPerdidosFragment.LOCAL_PROVAVEL_PERDA);
        String preferenciaRetirada = intent.getStringExtra(TodosPerdidosFragment.PREFERENCIA_RETIRADA);
        String descricao = intent.getStringExtra(TodosPerdidosFragment.DESCRICAO);
        String facebookIdItem = intent.getStringExtra(TodosPerdidosFragment.FACEBOOK_ID);
        String imageUrl = intent.getStringExtra(TodosAchadosFragment.IMAGE_URL);

        if (id != null) idPerdido = id;
        if (nome != null) { nomePerdido.setText("Nome: " + nome); nomeItem = nome; }
        if (categoria != null) categoriaPerdido.setText("Categoria: " + categoria);
        if (quantidade != null) unidadesPerdido.setText("Quantidade: " + quantidade);
        if (localProvavelPerda != null) localPerdido.setText("Provável local da perda: " + localProvavelPerda);
        if (preferenciaRetirada != null) preferenciaPerdido.setText("Preferência da retirada: " + preferenciaRetirada);
        if (descricao != null) descricaoPerdido.setText("Descrição: " + descricao);
        if (facebookIdItem != null) facebookIdPerdido = facebookIdItem;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child("itens/" + id + ".jpg");
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(pathReference)
                    .override(600, 600)
                    .centerCrop()
                    .into(imagemPerdido);
            imagemPerdido.setBackgroundResource(android.R.color.transparent);
        }

    }

    private void redirectMeusAchados() {
        Toast.makeText(ReportEncontroActivity.this, "Alerta realizado com sucesso!", Toast.LENGTH_LONG).show();
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
