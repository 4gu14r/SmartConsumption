package com.application.smartconsumption.ui.configuracao;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.smartconsumption.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracaoViewModel extends ViewModel {

    FirebaseAuth user = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<ConfiguracaoItem>> configuracoes;

    public ConfiguracaoViewModel() {
        configuracoes = new MutableLiveData<>();
        FirebaseUser userLogin = user.getCurrentUser();
        String Usuario = userLogin.getUid();

        if (userLogin != null) {

            db.collection("Usuarios").document(Usuario).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (value != null && value.exists()) {
                        DocumentReference perfil = value.getDocumentReference("Perfil_id");

                        db.collection("Perfil").document(perfil.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null && value.exists()) {
                                    configuracoes.setValue(criarListaFixaDeConfiguracoes(value.get("perfil").toString()));
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public LiveData<List<ConfiguracaoItem>> getConfiguracoes() {
        return configuracoes;
    }

    // Método para criar uma lista fixa de configurações para testar a RecyclerView
    private List<ConfiguracaoItem> criarListaFixaDeConfiguracoes(String perfil) {
        List<ConfiguracaoItem> configuracoesList = new ArrayList<>();
        configuracoesList.add(new ConfiguracaoItem("Perfil"));
        configuracoesList.add(new ConfiguracaoItem("Veículos"));
        configuracoesList.add(new ConfiguracaoItem("Relatórios"));
        if (perfil.equals("Administrador") && perfil != null) {
            configuracoesList.add(new ConfiguracaoItem("Cadastrar Veículos"));
            configuracoesList.add(new ConfiguracaoItem("Tipos de Combustível"));
        }
        configuracoesList.add(new ConfiguracaoItem("Sair"));
        // Adicione mais itens de configuração aqui, se desejar
        return configuracoesList;
    }
}