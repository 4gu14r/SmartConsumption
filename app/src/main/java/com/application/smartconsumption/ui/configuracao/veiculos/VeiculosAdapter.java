package com.application.smartconsumption.ui.configuracao.veiculos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.smartconsumption.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class VeiculosAdapter extends RecyclerView.Adapter<VeiculosAdapter.ViewHolder> {

    private ArrayList<Veiculos> veiculos;
    public VeiculosAdapter(ArrayList<Veiculos> veiculos) {
        this.veiculos = veiculos;
    }

    @Override
    public VeiculosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_veiculos, parent, false));
    }

    @Override
    public void onBindViewHolder(VeiculosAdapter.ViewHolder holder, int position) {
        Veiculos listVei = veiculos.get(position);
        holder.marca.setText(listVei.getMarca());
        holder.modelo.setText(listVei.getModelo());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                FloatingActionButton fab = activity.findViewById(R.id.btAdd);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragVeiculo, DetailsVeiculosFragment.newInstance(listVei.getId(), listVei.getMarca(), listVei.getModelo(), listVei.getMotor(),listVei.getHodometro(), listVei.getConsumo(), listVei.getTanque())).addToBackStack(null).commit();
                fab.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return veiculos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView marca, modelo;

        public ViewHolder( View itemView) {
            super(itemView);
            marca = itemView.findViewById(R.id.txtMarcaVeiculo);
            modelo = itemView.findViewById(R.id.txtModeloVeiculo);

        }
    }

}
