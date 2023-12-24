package com.application.smartconsumption.ui.configuracao.relatorios;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.smartconsumption.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class GeralRelatorioFragment extends Fragment {

    int abastecimentos;
    double precos, combustiveis;
    private TextView abastecimento, preco, combustivel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth user = FirebaseAuth.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_geral_relatorio, container, false);

        abastecimento = root.findViewById(R.id.ctn_Abastecimentos);
        preco = root.findViewById(R.id.ctn_precoTotal);
        combustivel = root.findViewById(R.id.ctn_Combustivel);

        carregarRelatorio();

        return root;
    }

    private void carregarRelatorio() {
        DocumentReference usuario = db.collection("Usuarios").document(user.getCurrentUser().getUid());
        abastecimentos = 0;
        precos = 0;
        combustiveis = 0;
        db.collection("VeiculosUsuarios").whereEqualTo("Usuario", usuario).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    for (int i = 0; i < value.size(); i++) {
                        Abastecimento(value.getDocuments().get(i).getId());
                    }
                }
            }
        });
    }

    private void Abastecimento(String veiculosUsuarios) {
        DocumentReference uvRef = db.collection("VeiculosUsuarios").document(veiculosUsuarios);
        db.collection("Abastecer").whereEqualTo("VeiculosUsuarios", uvRef).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    abastecimentos += value.size();
                    for (int i = 0; i < value.size(); i++) {
                        String precoGastoStr = value.getDocuments().get(i).get("PrecoGasto").toString().replace(",", ".");
                        precos += Double.parseDouble(precoGastoStr);

                        String litrosAbastecidoStr = value.getDocuments().get(i).get("LitrosAbastecido").toString().replace(",", ".");
                        combustiveis += Double.parseDouble(litrosAbastecidoStr);

                    }
                    ajustaDados(abastecimentos, precos, combustiveis);
                }
            }
        });
    }

    private void ajustaDados(int Abastecimentos, double Precos, double Combustivel) {
        abastecimento.setText(String.valueOf(Abastecimentos));
        preco.setText("R$: "+String.format("%.2f", Precos).replace(".",","));
        combustivel.setText(String.format("%.2f", Combustivel).replace(".",",")+"L");
    }

}