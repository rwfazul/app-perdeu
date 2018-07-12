package br.com.perdeu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rhau on 6/15/18.
 */

public class Usuario {

    private String id_usuario;
    private String email_usuario;
    private List<Item> itens_usuario = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Usuario(String id_usuario, String email_usuario) {
        this.id_usuario = id_usuario;
        this.email_usuario = email_usuario;
    }

    public Usuario(String id_usuario, String email_usuario, List<Item> itens_usuario) {
        this.id_usuario = id_usuario;
        this.email_usuario = email_usuario;
        this.itens_usuario = itens_usuario;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getEmail_usuario() {
        return email_usuario;
    }

    public void setEmail_usuario(String email_usuario) {
        this.email_usuario = email_usuario;
    }

    public List<Item> getItens_usuario() {
        return itens_usuario;
    }

    public void setItens_usuario(List<Item> itens_usuario) {
        this.itens_usuario = itens_usuario;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id_usuario='" + id_usuario + '\'' +
                ", email_usuario='" + email_usuario + '\'' +
                ", itens_usuario=" + itens_usuario +
                '}';
    }

}