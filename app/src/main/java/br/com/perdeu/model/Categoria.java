package br.com.perdeu.model;

/**
 * Created by rhau on 6/15/18.
 */

public class Categoria {

    private String tipo_item;

    public Categoria() {
    }

    public Categoria(String tipo_item) {
        this.tipo_item = tipo_item;
    }

    public String getTipo_item() {
        return tipo_item;
    }

    public void setTipo_item(String tipo_item) {
        this.tipo_item = tipo_item;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "tipo_item='" + tipo_item + '\'' +
                '}';
    }

}