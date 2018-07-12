package br.com.perdeu.model;

/**
 * Created by rhau on 7/9/18.
 */

public class Alerta {

    private String titulo;
    private String conteudo;
    private String facebookIdDest; // destinatario

    public Alerta() {
    }

    public Alerta(String titulo, String conteudo, String facebookIdDest) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.facebookIdDest = facebookIdDest;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getFacebookIdDest() {
        return facebookIdDest;
    }

    public void setFacebookIdDest(String facebookIdDest) {
        this.facebookIdDest = facebookIdDest;
    }

    @Override
    public String toString() {
        return "Alerta{" +
                "titulo='" + titulo + '\'' +
                ", conteudo='" + conteudo + '\'' +
                ", facebookIdDest='" + facebookIdDest + '\'' +
                '}';
    }

}