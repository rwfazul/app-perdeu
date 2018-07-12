package br.com.perdeu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

import br.com.perdeu.fragments.MeusAchadosFragment;
import br.com.perdeu.model.Achado;
import br.com.perdeu.model.Categoria;
import br.com.perdeu.model.ItemDAO;

public class EditAchadoActivity extends AppCompatActivity {

    private String idAchado;
    private EditText nomeAchado;
    private Spinner categoriaAchado;
    private EditText unidadesAchado;
    private EditText localAchado;
    private EditText localAtualAchado;
    private EditText descricaoAchado;
    private String facebookIdAchado;
    private ImageView imagemAchado;
    private Button btnUploadImgAchado;
    private Button btnEditarAchado;
    private Button btnRemoverAchado;

    private static int RESULT_LOAD_IMAGE = 1;
    private Boolean newImage = false;
    private String imageUrl;
    private Uri selectedImageUri;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_achado);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nomeAchado = findViewById(R.id.nome_item_achado);
        categoriaAchado = findViewById(R.id.categoria_item_achado);
        unidadesAchado = findViewById(R.id.unidades_item_achado);
        localAchado = findViewById(R.id.lugar_item_achado);
        localAtualAchado = findViewById(R.id.lugar_atual_item_achado);
        descricaoAchado = findViewById(R.id.descricao_item_achado);
        imagemAchado = findViewById(R.id.edit_img_item_achado);
        btnUploadImgAchado = findViewById(R.id.bnt_upload_item_achado);
        btnEditarAchado = findViewById(R.id.btn_editar_achado);
        btnRemoverAchado = findViewById(R.id.btn_remover_achado);

        getIntentData();

        btnEditarAchado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeAchado.getText().toString().trim();
                String categoria = categoriaAchado.getSelectedItem().toString();
                String unidades = unidadesAchado.getText().toString();
                String local = localAchado.getText().toString().trim();
                String localAtual = localAtualAchado.getText().toString().trim();
                String descricao = descricaoAchado.getText().toString().trim();
                if ( validaCampos(nome, unidades, local, localAtual) ) {
                    Achado achado = new Achado();
                    achado.setId_item(idAchado);
                    achado.setNome_item(nome);
                    achado.setCategoria_item(new Categoria(categoria));
                    achado.setQuantidade_item(unidades);
                    achado.setLocalEncontrado_item(local);
                    achado.setLocalAtual_item(localAtual);
                    achado.setDescricao_item(descricao);
                    achado.setFacebookId(facebookIdAchado);
                    if (imageUrl != null && !imageUrl.isEmpty())
                        achado.setImageUrl(imageUrl);
                    new ItemDAO().update(achado);
                    if (newImage)
                        uploadImage(achado.getId_item());
                    else
                        redirectMeusAchados("atualizado");
                }
            }
        });

        btnRemoverAchado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ItemDAO().delete(new Achado(idAchado));
                redirectMeusAchados("removido");
            }
        });

        btnUploadImgAchado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();

        String id = intent.getStringExtra(MeusAchadosFragment.ID);
        String nome = intent.getStringExtra(MeusAchadosFragment.NOME);
        String categoria = intent.getStringExtra(MeusAchadosFragment.CATEGORIA);
        String quantidade = intent.getStringExtra(MeusAchadosFragment.QUANTIDADE);
        String localEncontrado = intent.getStringExtra(MeusAchadosFragment.LOCAL_ENCONTRADO);
        String localAtual = intent.getStringExtra(MeusAchadosFragment.LOCAL_ATUAL);
        String descricao = intent.getStringExtra(MeusAchadosFragment.DESCRICAO);
        String facebookIdItem = intent.getStringExtra(MeusAchadosFragment.FACEBOOK_ID);
        imageUrl = intent.getStringExtra(MeusAchadosFragment.IMAGE_URL);

        if (id != null) idAchado = id;
        if (nome != null) nomeAchado.setText(nome);
        if (categoria != null) categoriaAchado.setSelection( getIndex(categoria) );
        if (quantidade != null) unidadesAchado.setText(quantidade);
        if (localEncontrado != null) localAchado.setText(localEncontrado);
        if (localAtual!= null) localAtualAchado.setText(localAtual);
        if (descricao != null) descricaoAchado.setText(descricao);
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

    private int getIndex(String value) {
        for (int i = 0; i < categoriaAchado.getCount(); i++) {
            if ( categoriaAchado.getItemAtPosition(i).toString().equalsIgnoreCase(value) )
                return i;
        }
        return 0;
    }

    private Boolean validaCampos(String nome, String unidades, String local, String localAtual) {
        String text = null;
        if ( nome.isEmpty() )
            text = "Por favor, informe o nome do item encontrado.";
        else if ( unidades.isEmpty() )
            text = "Por favor, informe o número de unidades encontradas.";
        else if ( local.isEmpty() )
            text = "Por favor, informe o local onde o item foi encontrado.";
        else if ( localAtual.isEmpty() )
            text = "Por favor, informe onde o item se encontra no momento.";

        if (text != null) {
            Toast.makeText(EditAchadoActivity.this, text, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void redirectMeusAchados(String action) {
        Toast.makeText(EditAchadoActivity.this, "Achado " + action + " com sucesso!", Toast.LENGTH_LONG).show();
        /*Intent intent = new Intent(EditAchadoActivity.this, DashboardActivity.class);
        intent.putExtra("fragment", "meus_achados");
        startActivity(intent);*/
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            if (data != null) {
                try {
                    selectedImageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    float aspectRatio = selectedImage.getWidth() / (float) selectedImage.getHeight();
                    int height = 500;
                    int width = Math.round(height * aspectRatio);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(selectedImage, width, height, false);
                    imagemAchado.setBackgroundResource(android.R.color.transparent);
                    imagemAchado.setImageBitmap(resizedBitmap);
                    newImage = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erro ao exibir a imagem", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(this, "Você não escolheu uma imagem", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImage(String idItem) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        final String id = idItem;
        progressDialog.setTitle("Processando imagem...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        StorageReference achadosRef = mStorageRef.child("itens/" + idItem + ".jpg");
        achadosRef.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        new ItemDAO().linkItemImage(id, "achados", taskSnapshot.getDownloadUrl());
                        progressDialog.dismiss();
                        redirectMeusAchados("atualizado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Upload em "+(int)progress+"%");
                    }
                });
    }

}