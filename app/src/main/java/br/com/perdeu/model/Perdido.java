package br.com.perdeu.model;

/**
 * Created by rhau on 6/15/18.
 */

public class Perdido extends Item {

    private String provavelLocalPerda_item;
    private String preferenciaRetirada_item;

    public Perdido() {

    }

    public Perdido(String id_item) {
        super(id_item);
    }

    public String getProvavelLocalPerda_item() {
        return provavelLocalPerda_item;
    }

    public void setProvavelLocalPerda_item(String provavelLocalPerda_item) {
        this.provavelLocalPerda_item = provavelLocalPerda_item;
    }

    public String getPreferenciaRetirada_item() {
        return preferenciaRetirada_item;
    }

    public void setPreferenciaRetirada_item(String preferenciaRetirada_item) {
        this.preferenciaRetirada_item = preferenciaRetirada_item;
    }

    @Override
    public String toString() {
        return "Perdido{" +
                "provavelLocalPerda_item='" + provavelLocalPerda_item + '\'' +
                ", preferenciaRetirada_item='" + preferenciaRetirada_item + '\'' +
                '}';
    }

}