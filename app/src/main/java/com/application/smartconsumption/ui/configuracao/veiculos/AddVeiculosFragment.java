package com.application.smartconsumption.ui.configuracao.veiculos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.application.smartconsumption.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AddVeiculosFragment extends Fragment {
    ArrayList<String> itensMarca = new ArrayList<>();
    ArrayList<String> itensModelo = new ArrayList<>();
    ArrayAdapter<String> marcas, modelos;
    AutoCompleteTextView marca, modelo;
    private EditText hodometro;
    private RadioGroup motor;
    private RadioButton gasolina, flex, diesel;
    private Button btAddVeiculo;
    private String escolherMotor;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference marcasDB, modelosDB; //Para puxar os dados no drop da tela de cadastro;
    CollectionReference veiculosCadastrado, userVeic; //Para criar um documento novo.
    DocumentReference docUser; //para enviar pro db;

    @Override
    public View onCreateView(LayoutInflater inflat,ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflat.inflate(R.layout.fragment_add_veiculos, container, false);

        marca = root.findViewById(R.id.auto_marca_complete);
        modelo = root.findViewById(R.id.auto_modelo_complete);
        hodometro = root.findViewById(R.id.ediTxtHodometro);
        motor = root.findViewById(R.id.combustivel);
        gasolina = root.findViewById(R.id.gasolina);
        flex = root.findViewById(R.id.flex);
        diesel = root.findViewById(R.id.diesel);
        btAddVeiculo = root.findViewById(R.id.buttonAdicionarVeiculo);

        marcasDB =  db.collection("VeiculosCadastrado");
        marcasDB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    for (int i = 0; i < value.size(); i++) {
                        itensMarca.add(value.getDocuments().get(i).getString("Marca"));
                    }
                    HashSet MarcaConvert = new HashSet<>(itensMarca);

                    ArrayList<String> OrdMarca = new ArrayList<>(MarcaConvert);
                    Collections.sort(OrdMarca);

                    marcas = new ArrayAdapter<>(getContext(), R.layout.list_marca_veiculos, new ArrayList<>(OrdMarca));

                    marca.setAdapter(marcas);
                }
            }
        });

        marca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                callback(adapterView.getItemAtPosition(i).toString());
                modelo.setText("");
            }
        });

        return root;
    }

    private void callback(String marca) {
        itensModelo.clear();
        modelosDB = db.collection("VeiculosCadastrado");
        modelosDB.whereEqualTo("Marca", marca).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value,FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    for (int i = 0; i < value.size(); i++) {
                        itensModelo.add(value.getDocuments().get(i).getString("Modelo"));
                    }
                    HashSet ModeloConvert = new HashSet<>(itensModelo);
                    ArrayList<String> OrdModelo = new ArrayList<>(ModeloConvert);
                    Collections.sort(OrdModelo);
                    modelos = new ArrayAdapter<>(getContext(), R.layout.list_marca_veiculos, new ArrayList<>(OrdModelo));
                    modelo.setAdapter(modelos);
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        motor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(view.findViewById(i).equals(gasolina)) {
                    escolherMotor = gasolina.getText().toString();
                } else if (view.findViewById(i).equals(flex)) {
                    escolherMotor = flex.getText().toString();
                }else if (view.findViewById(i).equals(flex)) {
                    escolherMotor = diesel.getText().toString();
                }else{
                    escolherMotor = null;
                }
            }

        });

        btAddVeiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!marca.getText().toString().isEmpty() && !modelo.getText().toString().isEmpty() && !hodometro.getText().toString().isEmpty() && escolherMotor != null ) {
                    carregarVeiculo(modelo.getText().toString());
                }else{
                    Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void carregarVeiculo(String modelo){
        veiculosCadastrado = db.collection("VeiculosCadastrado");
        veiculosCadastrado.whereEqualTo("Modelo", modelo).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    DocumentReference carro = db.collection("VeiculosCadastrado").document(value.getDocuments().get(0).getId());
                    salvarDados(carro, hodometro.getText().toString(), escolherMotor);
                }
            }
        });
    }

    private void salvarDados(DocumentReference docVei, String hodo, String motor) {
        Map<String, Object> veiUser = new HashMap<>();
        docUser = db.collection("Usuarios").document(auth.getCurrentUser().getUid());

        veiUser.put("Hodometro", Integer.parseInt(hodo));
        veiUser.put("Motor", motor);
        veiUser.put("TanqueAtual", 0);
        veiUser.put("Usuario", docUser);
        veiUser.put("Veiculo", docVei);

        adicionarVeiculo(veiUser);

    }

    private void adicionarVeiculo(Map<String, Object> dados) {
        userVeic = db.collection("VeiculosUsuarios");
        userVeic.add(dados).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getContext(), "Veiculo adicionado com Sucesso", Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.fragVeiculo, new ListaVeiculosFragment()).commit();
                getActivity().findViewById(R.id.btAdd).setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Falha: "+e, Toast.LENGTH_SHORT).show();
            }
        });

    }

}