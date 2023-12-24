package com.application.smartconsumption.ui.configuracao;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.smartconsumption.R;
import android.widget.TextView;

import java.util.List;

public class ConfiguracaoAdapter extends RecyclerView.Adapter<ConfiguracaoAdapter.ViewHolder> {
    private List<ConfiguracaoItem> configuracoes;
    private ConfiguracaoInterface onClickItem;
    public ConfiguracaoAdapter(List<ConfiguracaoItem> configuracoes) {
        this.configuracoes = configuracoes;
    }
    public void setConfiguracoes(List<ConfiguracaoItem> configuracoes) {
        this.configuracoes = configuracoes;
        notifyDataSetChanged();
    }
    public void setOnClickItem(ConfiguracaoInterface onClickItem) {
        this.onClickItem = onClickItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_configuracao, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConfiguracaoItem configuracao = configuracoes.get(position);
        holder.txtTituloConfiguracao.setText(configuracao.getTitulo());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickItem.itemClick(configuracao);
            }
        });
    }
    @Override
    public int getItemCount() {
        return configuracoes.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTituloConfiguracao;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTituloConfiguracao = itemView.findViewById(R.id.txtTituloConfiguracao);
        }
    }
}
