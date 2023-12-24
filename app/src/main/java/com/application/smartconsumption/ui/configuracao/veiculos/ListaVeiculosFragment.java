package com.application.smartconsumption.ui.configuracao.veiculos;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.smartconsumption.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class ListaVeiculosFragment extends Fragment{
    private RecyclerView listVeic;
    private VeiculosAdapter adapter;
    String userID;
    FirebaseFirestore db;
    FirebaseAuth user;
    DocumentReference veiRef;
    CollectionReference uVeiRef;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                              Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista_veiculos, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listVeic = view.findViewById(R.id.list_veiculos);
        listVeic.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance();

        carregarVeiculos();


    }
    public void carregarVeiculos(){
        userID = user.getCurrentUser().getUid();
        uVeiRef = db.collection("VeiculosUsuarios");

        uVeiRef.whereEqualTo("Usuario", db.collection("Usuarios").document(userID)).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    ArrayList<Veiculos> veiculo = new ArrayList<>();
                    int i;
                    for (i = 0; i < value.getDocuments().size(); i++) {
                        DocumentReference veiCAR = (DocumentReference) value.getDocuments().get(i).get("Veiculo");
                        String idVeiculoUsuario = value.getDocuments().get(i).getId();
                        String hodometro = value.getDocuments().get(i).get("Hodometro").toString();
                        String motor = value.getDocuments().get(i).getString("Motor");
                        veiRef = db.collection("VeiculosCadastrado").document(veiCAR.getId());
                        veiRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot valor, FirebaseFirestoreException error) {
                                if (valor != null && valor.exists()) {
                                    String marca = valor.getString("Marca");
                                    String modelo = valor.getString("Modelo");
                                    String consumo = valor.getLong("Consumo").toString();
                                    String tanque = valor.getLong("TanqueTotal").toString();
                                    veiculo.add(new Veiculos(idVeiculoUsuario, marca, modelo, motor, hodometro, consumo, tanque));
                                    adapter = new VeiculosAdapter(veiculo);
                                    listVeic.setAdapter(adapter);
                                }
                            }
                        });
                    }

                }
            }
        });
    }
}