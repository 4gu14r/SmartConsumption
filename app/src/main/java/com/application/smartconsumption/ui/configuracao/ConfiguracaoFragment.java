package com.application.smartconsumption.ui.configuracao;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.smartconsumption.R;
import com.application.smartconsumption.databinding.FragmentConfiguracaoBinding;
import com.application.smartconsumption.login.LoginActivity;
import com.application.smartconsumption.ui.configuracao.combustivel.CombustivelActivity;
import com.application.smartconsumption.ui.configuracao.perfil.PerfilActivity;
import com.application.smartconsumption.ui.configuracao.relatorios.RelatoriosActivity;
import com.application.smartconsumption.ui.configuracao.veiculos.VeiculosActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ConfiguracaoFragment extends Fragment implements ConfiguracaoInterface {

    private FragmentConfiguracaoBinding binding;
    private ConfiguracaoAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentConfiguracaoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerViewConfiguracoes = root.findViewById(R.id.recyclerViewConfiguracoes);
        ConfiguracaoViewModel configuracaoViewModel =
                new ViewModelProvider(this).get(ConfiguracaoViewModel.class);

        configuracaoViewModel.getConfiguracoes().observe(getViewLifecycleOwner(), configuracoes -> adapter.setConfiguracoes(configuracoes));

        adapter = new ConfiguracaoAdapter(new ArrayList<>());
        adapter.setOnClickItem(this);
        recyclerViewConfiguracoes.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewConfiguracoes.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     */

    @Override
    public void itemClick(ConfiguracaoItem config) {

        switch (config.getTitulo()) {
            case "Perfil":
                startActivity(new Intent(getContext(), PerfilActivity.class));
                break;
            case "Veículos":
                startActivity(new Intent(getContext(), VeiculosActivity.class));
                break;
            case "Tipos de Combustível":
                startActivity(new Intent(getContext(), CombustivelActivity.class));
                break;
            case "Relatórios":
                startActivity(new Intent(getContext(), RelatoriosActivity.class));
                break;
            case "Sair":
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
                break;
            default:
                Toast.makeText(getContext(), config.getTitulo(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

}