package br.com.perdeu.model;

/**
 * Created by rhau on 6/15/18.
 */

public class Achado extends Item {

    private String localEncontrado_item;
    private String localAtual_item;

    public Achado() {
    }

    public Achado(String id_item) {
        super(id_item);
    }

    public String getLocalEncontrado_item() {
        return localEncontrado_item;
    }

    public void setLocalEncontrado_item(String localEncontrado_item) {
        this.localEncontrado_item = localEncontrado_item;
    }

    public String getLocalAtual_item() {
        return localAtual_item;
    }

    public void setLocalAtual_item(String localAtual_item) {
        this.localAtual_item = localAtual_item;
    }

    @Override
    public String toString() {
        return "Achado{" +
                "localEncontrado_item='" + localEncontrado_item + '\'' +
                ", localAtual_item='" + localAtual_item + '\'' +
                '}';
    }

}