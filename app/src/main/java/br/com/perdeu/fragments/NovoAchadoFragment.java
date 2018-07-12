package br.com.perdeu.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

import br.com.perdeu.DashboardActivity;
import br.com.perdeu.R;
import br.com.perdeu.model.Achado;
import br.com.perdeu.model.Categoria;
import br.com.perdeu.model.ItemDAO;

/**
 * Created by rhau on 6/15/18.
 */

@SuppressLint("ValidFragment")
public class NovoAchadoFragment extends android.app.Fragment {

    View viewNovoAchado;

    private EditText nomeAchado;
    private Spinner categoriaAchado;
    private EditText unidadesAchado;
    private EditText localAchado;
    private EditText localAtualAchado;
    private EditText descricaoAchado;
    private Button btnUploadImgAchado;
    private Button btnNovoAchado;

    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView imgAchado;
    private Uri selectedImageUri;
    private String facebookId;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    StorageReference mStorageRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        viewNovoAchado = inflater.inflate(R.layout.fragment_novo_achado, container, false);

        nomeAchado = viewNovoAchado.findViewById(R.id.nome_item_achado);
        categoriaAchado = viewNovoAchado.findViewById(R.id.categoria_item_achado);
        unidadesAchado = viewNovoAchado.findViewById(R.id.unidades_item_achado);
        localAchado = viewNovoAchado.findViewById(R.id.lugar_item_achado);
        localAtualAchado = viewNovoAchado.findViewById(R.id.lugar_atual_item_achado);
        descricaoAchado = viewNovoAchado.findViewById(R.id.descricao_item_achado);
        imgAchado = viewNovoAchado.findViewById(R.id.img_achado);
        btnUploadImgAchado = viewNovoAchado.findViewById(R.id.bnt_upload_achado);
        btnNovoAchado = viewNovoAchado.findViewById(R.id.btn_novo_achado);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        for (UserInfo profile : currentUser.getProviderData())
            facebookId = profile.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        btnUploadImgAchado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        });

        btnNovoAchado.setOnClickListener(new View.OnClickListener() {
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
                    achado.setNome_item(nome);
                    achado.setCategoria_item(new Categoria(categoria));
                    achado.setQuantidade_item(unidades);
                    achado.setLocalEncontrado_item(local);
                    achado.setLocalAtual_item(localAtual);
                    achado.setDescricao_item(descricao);
                    achado.setFacebookId(facebookId);
                    new ItemDAO().save(achado);
                    if (selectedImageUri != null)
                        uploadImage(achado.getId_item());
                    else
                        redirectMeusAchados();
                }
            }
        });

        return viewNovoAchado;
    }

    private void uploadImage(String idItem) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
                        redirectMeusAchados();
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
                });;
    }

    private void redirectMeusAchados() {
        Toast.makeText(getActivity(), "Novo achado inserido com sucesso!", Toast.LENGTH_LONG).show();
        ((DashboardActivity) getActivity()).showAndSetFab("novo_achado");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new MeusAchadosFragment()).commit();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Meus Achados");
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
            Toast.makeText(viewNovoAchado.getContext(), text, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            if (data != null) {
                try {
                    selectedImageUri = data.getData();
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    float aspectRatio = selectedImage.getWidth() / (float) selectedImage.getHeight();
                    int height = 500;
                    int width = Math.round(height * aspectRatio);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(selectedImage, width, height,false);
                    imgAchado.setBackgroundResource(android.R.color.transparent);
                    imgAchado.setImageBitmap(resizedBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Erro ao exibir a imagem", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Você não escolheu uma imagem",Toast.LENGTH_LONG).show();
        }
    }

}