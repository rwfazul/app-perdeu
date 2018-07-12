package br.com.perdeu.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rhau on 6/15/18.
 */

public class AlertaDAO {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference db;
    private String uid;
    private String reference;

    public AlertaDAO() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getProviderData().get(currentUser.getProviderData().size() - 1).getUid();
    }

    public void save(Alerta a) {
        reference = "users/" + uid + "/alertas";
        db = FirebaseDatabase.getInstance().getReference(reference);
        db.child(db.push().getKey()).setValue(a);
    }

    public void send(Alerta a) {
        reference = "users/" + a.getFacebookIdDest() + "/alertas";
        db = FirebaseDatabase.getInstance().getReference(reference);
        a.setFacebookIdDest(uid);
        db.child(db.push().getKey()).setValue(a);
    }

    public void deleteAll() {
        reference = "users/" + uid;
        db = FirebaseDatabase.getInstance().getReference(reference);
        db.child("alertas").removeValue();
    }

}