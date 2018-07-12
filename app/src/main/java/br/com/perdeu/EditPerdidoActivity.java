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
import br.com.perdeu.fragments.MeusPerdidosFragment;
import br.com.perdeu.model.Categoria;
import br.com.perdeu.model.ItemDAO;
import br.com.perdeu.model.Perdido;

public class EditPerdidoActivity extends AppCompatActivity {

    private String idPerdido;
    private EditText nomePerdido;
    private Spinner categoriaPerdido;
    private EditText unidadesPerdido;
    private EditText localPerdido;
    private EditText preferenciaPerdido;
    private EditText descricaoPerdido;
    private String facebookIdPerdido;
    private ImageView imagemPerdido;
    private Button btnUploadImgPerdido;
    private Button btnEditarPerdido;
    private Button btnRemoverPerdido;

    private static int RESULT_LOAD_IMAGE = 1;
    private Boolean newImage = false;
    private String imageUrl;
    private Uri selectedImageUri;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perdido);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nomePerdido = findViewById(R.id.nome_item_perdido);
        categoriaPerdido = findViewById(R.id.categoria_item_perdido);
        unidadesPerdido = findViewById(R.id.unidades_item_perdido);
        localPerdido = findViewById(R.id.lugar_item_perdido);
        preferenciaPerdido = findViewById(R.id.preferencia_item_perdido);
        descricaoPerdido = findViewById(R.id.descricao_item_perdido);
        imagemPerdido = findViewById(R.id.edit_img_item_perdido);
        btnUploadImgPerdido = findViewById(R.id.bnt_upload_item_perdido);
        btnEditarPerdido = findViewById(R.id.btn_editar_perdido);
        btnRemoverPerdido = findViewById(R.id.btn_remover_perdido);

        getIntentData();

        btnEditarPerdido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomePerdido.getText().toString().trim();
                String categoria = categoriaPerdido.getSelectedItem().toString();
                String unidades = unidadesPerdido.getText().toString();
                String local = localPerdido.getText().toString().trim();
                String preferencia = preferenciaPerdido.getText().toString().trim();
                String descricao = descricaoPerdido.getText().toString().trim();
                if ( validaCampos(nome, unidades, local, preferencia) ) {
                    Perdido perdido = new Perdido();
                    perdido.setId_item(idPerdido);
                    perdido.setNome_item(nome);
                    perdido.setCategoria_item(new Categoria(categoria));
                    perdido.setQuantidade_item(unidades);
                    perdido.setProvavelLocalPerda_item(local);
                    perdido.setPreferenciaRetirada_item(preferencia);
                    perdido.setDescricao_item(descricao);
                    perdido.setFacebookId(facebookIdPerdido);
                    if (imageUrl != null && !imageUrl.isEmpty())
                        perdido.setImageUrl(imageUrl);
                    new ItemDAO().update(perdido);
                    if (newImage)
                        uploadImage(perdido.getId_item());
                    else
                        redirectMeusPerdidos("atualizado");
                }
            }
        });

        btnRemoverPerdido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ItemDAO().delete(new Perdido(idPerdido));
                redirectMeusPerdidos("removido");
            }
        });

        btnUploadImgPerdido.setOnClickListener(new View.OnClickListener() {
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

        String id = intent.getStringExtra(MeusPerdidosFragment.ID);
        String nome = intent.getStringExtra(MeusPerdidosFragment.NOME);
        String categoria = intent.getStringExtra(MeusPerdidosFragment.CATEGORIA);
        String quantidade = intent.getStringExtra(MeusPerdidosFragment.QUANTIDADE);
        String localProvavelPerda = intent.getStringExtra(MeusPerdidosFragment.LOCAL_PROVAVEL_PERDA);
        String preferenciaRetirada = intent.getStringExtra(MeusPerdidosFragment.PREFERENCIA_RETIRADA);
        String descricao = intent.getStringExtra(MeusPerdidosFragment.DESCRICAO);
        String facebookIdItem = intent.getStringExtra(MeusPerdidosFragment.FACEBOOK_ID);
        imageUrl = intent.getStringExtra(MeusAchadosFragment.IMAGE_URL);

        if (id != null) idPerdido = id;
        if (nome != null) nomePerdido.setText(nome);
        if (categoria != null) categoriaPerdido.setSelection( getIndex(categoria) );
        if (quantidade != null) unidadesPerdido.setText(quantidade);
        if (localProvavelPerda != null) localPerdido.setText(localProvavelPerda);
        if (preferenciaRetirada != null) preferenciaPerdido.setText(preferenciaRetirada);
        if (descricao != null) descricaoPerdido.setText(descricao);
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

    private int getIndex(String value) {
        for (int i = 0; i < categoriaPerdido.getCount(); i++) {
            if ( categoriaPerdido.getItemAtPosition(i).toString().equalsIgnoreCase(value) )
                return i;
        }
        return 0;
    }

    private Boolean validaCampos(String nome, String unidades, String local, String preferencia) {
        String text = null;
        if ( nome.isEmpty() )
            text = "Por favor, informe o nome do item perdido.";
        else if ( unidades.isEmpty() )
            text = "Por favor, informe o número de unidades perdidas.";
        else if ( local.isEmpty() )
            text = "Por favor, informe o local onde você acha que perdeu o item.";
        else if ( preferencia.isEmpty() )
            text = "Por favor, informe onde você prefere retirar o item caso seja achado.";

        if (text != null) {
            Toast.makeText(EditPerdidoActivity.this, text, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void redirectMeusPerdidos(String action) {
        Toast.makeText(EditPerdidoActivity.this, "Perdido " + action + " com sucesso!", Toast.LENGTH_LONG).show();
        /*Intent intent = new Intent(EditPerdidoActivity.this, DashboardActivity.class);
        intent.putExtra("fragment", "meus_perdidos");
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
                    imagemPerdido.setBackgroundResource(android.R.color.transparent);
                    imagemPerdido.setImageBitmap(resizedBitmap);
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
        StorageReference perdidosRef = mStorageRef.child("itens/" + idItem + ".jpg");
        perdidosRef.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        new ItemDAO().linkItemImage(id, "perdidos", taskSnapshot.getDownloadUrl());
                        progressDialog.dismiss();
                        redirectMeusPerdidos("atualizado");
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