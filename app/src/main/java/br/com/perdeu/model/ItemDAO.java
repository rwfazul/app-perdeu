package br.com.perdeu.model;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rhau on 6/15/18.
 */

public class ItemDAO {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference db;
    private String uid;
    private String reference;

    public ItemDAO() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getProviderData().get(currentUser.getProviderData().size() - 1).getUid();
    }

    public void save(Item i) {
        reference = "users/" + uid + findCollection(i);
        db = FirebaseDatabase.getInstance().getReference(reference);
        i.setId_item(db.push().getKey());
        db.child(i.getId_item()).setValue(i);
    }

    public void update(Item i) {
        reference = "users/" + uid + findCollection(i);
        db = FirebaseDatabase.getInstance().getReference(reference);
        db.child(i.getId_item()).setValue(i);
    }

    public void delete(Item i) {
        reference = "users/" + uid + findCollection(i);
        db = FirebaseDatabase.getInstance().getReference(reference);
        db.child(i.getId_item()).removeValue();
    }

    private String findCollection(Item i) {
        if (i instanceof Achado)
            return "/achados";
        else if (i instanceof Perdido)
            return "/perdidos";
        return "/unknownItens";
    }

    public void linkItemImage(String idItem, String collection, Uri urlDownloadImage) {
        reference = "users/" + uid + "/"  + collection;
        db = FirebaseDatabase.getInstance().getReference(reference);
        db.keepSynced(true);
        db.child(idItem).child("imageUrl").setValue(urlDownloadImage.toString());
    }

}