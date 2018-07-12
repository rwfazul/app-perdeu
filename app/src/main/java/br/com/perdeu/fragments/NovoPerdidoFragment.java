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
import br.com.perdeu.model.Categoria;
import br.com.perdeu.model.ItemDAO;
import br.com.perdeu.model.Perdido;

/**
 * Created by rhau on 6/15/18.
 */

@SuppressLint("ValidFragment")
public class NovoPerdidoFragment extends android.app.Fragment {

    View viewNovoPerdido;

    private EditText nomePerdido;
    private Spinner categoriaPerdido;
    private EditText unidadesPerdido;
    private EditText localPerdido;
    private EditText preferenciaPerdido;
    private EditText descricaoPerdido;
    private Button btnUploadImgPerdido;
    private Button btnNovoPerdido;

    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView imgPerdido;
    private Uri selectedImageUri;
    private String facebookId;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    StorageReference mStorageRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        viewNovoPerdido = inflater.inflate(R.layout.fragment_novo_perdido, container, false);

        nomePerdido = viewNovoPerdido.findViewById(R.id.nome_item_perdido);
        categoriaPerdido = viewNovoPerdido.findViewById(R.id.categoria_item_perdido);
        unidadesPerdido = viewNovoPerdido.findViewById(R.id.unidades_item_perdido);
        localPerdido = viewNovoPerdido.findViewById(R.id.lugar_item_perdido);
        preferenciaPerdido = viewNovoPerdido.findViewById(R.id.preferencia_item_perdido);
        descricaoPerdido = viewNovoPerdido.findViewById(R.id.descricao_item_perdido);
        imgPerdido = viewNovoPerdido.findViewById(R.id.img_perdido);
        btnUploadImgPerdido = viewNovoPerdido.findViewById(R.id.bnt_upload_perdido);
        btnNovoPerdido = viewNovoPerdido.findViewById(R.id.btn_novo_perdido);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        for (UserInfo profile : currentUser.getProviderData())
            facebookId = profile.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        btnUploadImgPerdido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        });

        btnNovoPerdido.setOnClickListener(new View.OnClickListener() {
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
                    perdido.setNome_item(nome);
                    perdido.setCategoria_item(new Categoria(categoria));
                    perdido.setQuantidade_item(unidades);
                    perdido.setProvavelLocalPerda_item(local);
                    perdido.setPreferenciaRetirada_item(preferencia);
                    perdido.setDescricao_item(descricao);
                    perdido.setFacebookId(facebookId);
                    new ItemDAO().save(perdido);
                    if (selectedImageUri != null)
                        uploadImage(perdido.getId_item());
                    else
                        redirectMeusPerdidos();
                }
            }
        });

        return viewNovoPerdido;
    }

    private void uploadImage(String idItem) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
                        redirectMeusPerdidos();
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

    private void redirectMeusPerdidos() {
        Toast.makeText(getActivity(), "Novo perdido inserido com sucesso!", Toast.LENGTH_LONG).show();
        ((DashboardActivity) getActivity()).showAndSetFab("novo_perdido");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new MeusPerdidosFragment()).commit();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Meus Perdidos");
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
            Toast.makeText(viewNovoPerdido.getContext(), text, Toast.LENGTH_LONG).show();
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
                    imgPerdido.setBackgroundResource(android.R.color.transparent);
                    imgPerdido.setImageBitmap(resizedBitmap);
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