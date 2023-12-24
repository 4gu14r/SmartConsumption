package com.application.smartconsumption.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.application.smartconsumption.R;
import com.application.smartconsumption.ui.configuracao.veiculos.DetailsVeiculosFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.TimeZone;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>{

    private ArrayList<Home> listaAbastecer;

    public HomeAdapter(ArrayList<Home> listaAbastecer) {
        this.listaAbastecer = listaAbastecer;
    }

    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_registro_abastecer, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Home home = listaAbastecer.get(position);

        holder.hodometro.setText(home.getKmRegistro()+"Km");
        holder.modelo.setText(home.getModelo());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Intent intent = new Intent(activity.getApplicationContext(), HomeActivity.class);
                intent.putExtra("id", home.getId());
                intent.putExtra("marca", home.getMarca());
                intent.putExtra("modelo", home.getModelo());
                intent.putExtra("hodometro", home.getKmRegistro());
                intent.putExtra("distancia", home.getKmPecorrido());
                intent.putExtra("tanque", home.getTanqueRegistro());
                intent.putExtra("precoGasto", home.getPrecosRegistro());
                intent.putExtra("combustivel", home.getCombustivel());
                intent.putExtra("valor", home.getValor());
                intent.putExtra("consumo", home.getConsumo());
                intent.putExtra("litros", home.getLitrosAbastecido());
                intent.putExtra("data", home.dataString());
                intent.putExtra("hora", home.horaString());
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaAbastecer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView hodometro, modelo;
        public ViewHolder( View itemView) {
            super(itemView);

            hodometro = itemView.findViewById(R.id.hodometro);
            modelo = itemView.findViewById(R.id.modelo);
        }
    }
}