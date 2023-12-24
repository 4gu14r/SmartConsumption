package com.application.smartconsumption.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.smartconsumption.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private RecyclerView listaAbastecimento;
    private HomeAdapter adapter;
    private TextView textView;
    private ArrayList<Home> listAbatecer;
    FirebaseAuth user = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    int contador;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initComp();

        DocumentReference userRef = db.collection("Usuarios").document(user.getCurrentUser().getUid());

        db.collection("VeiculosUsuarios").whereEqualTo("Usuario", userRef).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                carregarLista(value);
            }
        });

        return root;
    }

    private void initComp() {
        listaAbastecimento = binding.listAbastecimento;
        textView = binding.textHome;
    }


    public void carregarLista(@Nullable QuerySnapshot value) {
        if (value != null && !value.isEmpty()) {

            textView.setVisibility(View.GONE);
            listaAbastecimento.setVisibility(View.VISIBLE);

            listAbatecer = new ArrayList<>();

            contador = 0;

            for (int i = 0; i < value.size(); i++) {

                DocumentReference veiUserR = db.collection("VeiculosUsuarios").document(value.getDocuments().get(i).getId());
                db.collection("VeiculosCadastrado")
                        .document(value.getDocuments().get(i).getDocumentReference("Veiculo").getId())
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                if (value != null && value.exists()) {
                                    String marca = value.getString("Marca");
                                    String modelo = value.getString("Modelo");
                                    listarAbastecer(veiUserR, marca, modelo);
                                }
                            }
                        });
            }
        } else {
            textView.setVisibility(View.VISIBLE);
            listaAbastecimento.setVisibility(View.GONE);
        }
    }

    private void listarAbastecer(DocumentReference veiUserR, String Marca, String Modelo) {
        db.collection("Abastecer").whereEqualTo("VeiculosUsuarios", veiUserR).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    contador+= value.size();
                    limparVeiculo(Marca, Modelo);
                    textView.setVisibility(View.GONE);
                    listaAbastecimento.setVisibility(View.VISIBLE);
                    listaAbastecimento.setLayoutManager(new LinearLayoutManager(getContext()));
                    for (int i = 0; i < value.size(); i++) {
                        Map<String, Object> mapaCombustivel = (Map<String, Object>) value.getDocuments().get(i).get("CombustivelRegistro");
                        String hodometroRegistro = value.getDocuments().get(i).get("HodometroRegistro") != null ? value.getDocuments().get(i).get("HodometroRegistro").toString() : "";
                        String hodometroPercorrido = (value.getDocuments().get(i).contains("HodometroPecorrido") && value.getDocuments().get(i).get("HodometroPecorrido") != null) ? value.getDocuments().get(i).get("HodometroPecorrido").toString(): "";
                        String tanqueRegistro = (value.getDocuments().get(i).contains("TanqueRegistro") && value.getDocuments().get(i).get("TanqueRegistro") != null) ? value.getDocuments().get(i).get("TanqueRegistro").toString(): "";
                        String precoGasto = (value.getDocuments().get(i).contains("PrecoGasto") && value.getDocuments().get(i).get("PrecoGasto") != null) ? value.getDocuments().get(i).get("PrecoGasto").toString(): "";
                        String combustivel = (mapaCombustivel.get("Combustivel") != null) ? mapaCombustivel.get("Combustivel").toString() : "teste";
                        String valor = (mapaCombustivel.get("Valor") != null) ? mapaCombustivel.get("Valor").toString() : "teste";
                        String consumo = (value.getDocuments().get(i).contains("Consumo") && value.getDocuments().get(i).get("Consumo") != null) ? value.getDocuments().get(i).get("Consumo").toString(): "";
                        String litrosAbastecido = (value.getDocuments().get(i).contains("LitrosAbastecido") && value.getDocuments().get(i).get("LitrosAbastecido") != null) ? value.getDocuments().get(i).get("LitrosAbastecido").toString(): "";
                        System.out.println("Combustivel: "+combustivel);
                        System.out.println("Valor: "+valor);
                            listAbatecer.add(
                                    new Home(
                                            value.getDocuments().get(i).getId(),
                                            Marca,
                                            Modelo,
                                            hodometroRegistro,
                                            hodometroPercorrido,
                                            tanqueRegistro,
                                            precoGasto,
                                            combustivel,
                                            valor,
                                            consumo,
                                            litrosAbastecido,
                                            value.getDocuments().get(i).getTimestamp("DataHora").getSeconds()
                                    ));
                    }

                    Collections.sort(listAbatecer, new Comparator<Home>() {
                        @Override
                        public int compare(Home home, Home t1) {
                            return Long.compare(t1.getTimestamp(), home.getTimestamp());
                        }
                    });

                    adapter = new HomeAdapter(listAbatecer);
                    listaAbastecimento.setAdapter(adapter);

                } else {
                    if(contador == 0) {
                        textView.setVisibility(View.VISIBLE);
                        listaAbastecimento.setVisibility(View.GONE);
                    }
                }

            }

            private void limparVeiculo(String marca, String modelo) {
                for (int i = listAbatecer.size() - 1; i > -1; i--) {
                    if (listAbatecer.get(i).getMarca().equals(marca) && listAbatecer.get(i).getModelo().equals(modelo)) {
                        listAbatecer.remove(i);
                    }
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
