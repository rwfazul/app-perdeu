package br.com.perdeu.model;

/**
 * Created by rhau on 6/15/18.
 */

public class Status {

    private String situacao_item;

    public Status() {
        situacao_item = "no_match";
    }

    public Status(String situacao_item) {
        this.situacao_item = situacao_item;
    }

    public String getSituacao_item() {
        return situacao_item;
    }

    public void setSituacao_item(String situacao_item) {
        this.situacao_item = situacao_item;
    }

    public void updateStatus() {
        setSituacao_item("match_find");
    }

    @Override
    public String toString() {
        return "Status{" +
                "situacao_item='" + situacao_item + '\'' +
                '}';
    }

}