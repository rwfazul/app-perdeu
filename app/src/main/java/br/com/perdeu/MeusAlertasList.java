package br.com.perdeu;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.perdeu.model.Alerta;

/**
 * Created by rhau on 6/10/18.
 */

public class MeusAlertasList extends ArrayAdapter<Alerta> {

    private Activity context;
    private List<Alerta> alertas;

    public MeusAlertasList(Activity context, List<Alerta> alertas) {
        super(context, R.layout.list_alertas_layout, alertas);
        this.context = context;
        this.alertas = alertas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewAlerta = inflater.inflate(R.layout.list_alertas_layout, null, true);

        TextView titulo = listViewAlerta.findViewById(R.id.alerta_titulo);
        TextView conteudo = listViewAlerta.findViewById(R.id.alerta_conteudo);

        Alerta a = alertas.get(position);
        if (a.getTitulo() != null) titulo.setText(a.getTitulo());
        if (a.getConteudo() != null) conteudo.setText(a.getConteudo());

        return listViewAlerta;
    }

}