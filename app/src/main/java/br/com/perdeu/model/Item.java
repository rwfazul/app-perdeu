package br.com.perdeu.model;

/**
 * Created by rhau on 6/15/18.
 */

public class Item {

    private String id_item;
    private String nome_item;
    private String descricao_item;
    private String quantidade_item;
    private String observacoes_item;
    private Categoria categoria_item;
    private Status status_item;
    private String facebookId;
    private String imageUrl;

    public Item() {
    }

    public Item(String id_item) {
        this.id_item = id_item;
    }

    public Item(String nome_item, String descricao_item, String quantidade_item, String observacoes_item, Categoria categoria_item, Status status_item) {
        this.nome_item = nome_item;
        this.descricao_item = descricao_item;
        this.quantidade_item = quantidade_item;
        this.observacoes_item = observacoes_item;
        this.categoria_item = categoria_item;
        this.status_item = status_item;
    }

    public Item(String id_item, String nome_item, String descricao_item, String quantidade_item, String observacoes_item, Categoria categoria_item, Status status_item) {
        this.id_item = id_item;
        this.nome_item = nome_item;
        this.descricao_item = descricao_item;
        this.quantidade_item = quantidade_item;
        this.observacoes_item = observacoes_item;
        this.categoria_item = categoria_item;
        this.status_item = status_item;
    }

    public String getId_item() {
        return id_item;
    }

    public void setId_item(String id_item) {
        this.id_item = id_item;
    }

    public String getNome_item() {
        return nome_item;
    }

    public void setNome_item(String nome_item) {
        this.nome_item = nome_item;
    }

    public String getDescricao_item() {
        return descricao_item;
    }

    public void setDescricao_item(String descricao_item) {
        this.descricao_item = descricao_item;
    }

    public String getQuantidade_item() {
        return quantidade_item;
    }

    public void setQuantidade_item(String quantidade_item) {
        this.quantidade_item = quantidade_item;
    }

    public String getObservacoes_item() {
        return observacoes_item;
    }

    public void setObservacoes_item(String observacoes_item) {
        this.observacoes_item = observacoes_item;
    }

    public Categoria getCategoria_item() {
        return categoria_item;
    }

    public void setCategoria_item(Categoria categoria_item) {
        this.categoria_item = categoria_item;
    }

    public Status getStatus_item() {
        return status_item;
    }

    public void setStatus_item(Status status_item) {
        this.status_item = status_item;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id_item='" + id_item + '\'' +
                ", nome_item='" + nome_item + '\'' +
                ", descricao_item='" + descricao_item + '\'' +
                ", quantidade_item='" + quantidade_item + '\'' +
                ", observacoes_item='" + observacoes_item + '\'' +
                ", categoria_item=" + categoria_item +
                ", status_item=" + status_item +
                ", facebookId=" + facebookId +
                '}';
    }

}