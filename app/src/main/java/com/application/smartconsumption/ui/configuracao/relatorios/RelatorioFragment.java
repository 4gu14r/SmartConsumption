package com.application.smartconsumption.ui.configuracao.relatorios;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.application.smartconsumption.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class RelatorioFragment extends Fragment {

    private double precoTotal, combustivelTotal, consumoMedio;
    private TextView marca, modelo, abasterTXT, precoTXT, combustivelTXT, consumoTXT;
    private Button b1, b2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_relatorio, container, false);

        marca = view.findViewById(R.id.marca);
        modelo = view.findViewById(R.id.modelo);
        abasterTXT = view.findViewById(R.id.ctn_Abastecimentos);
        precoTXT = view.findViewById(R.id.ctn_precoTotal);
        combustivelTXT = view.findViewById(R.id.ctn_Combustivel);
        consumoTXT = view.findViewById(R.id.ctn_consumo);
        b1 = view.findViewById(R.id.bt1);
        b2 = view.findViewById(R.id.bt2);

        Bundle args = getArguments();

        if (args != null){
            carregarInformacoes(args.getString("veiId"));
            marca.setText(args.getString("veiMarca"));
            modelo.setText(args.getString("veiModelo"));
        }

        return view;
    }

    private void carregarInformacoes(String veiId) {
        precoTotal = 0; combustivelTotal = 0; consumoMedio = 0;
        DocumentReference uvRef = db.collection("VeiculosUsuarios").document(veiId);
        db.collection("Abastecer").whereEqualTo("VeiculosUsuarios", uvRef).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    for (int i = 0; i < value.size(); i++) {
                        precoTotal += Double.parseDouble(value.getDocuments().get(i).get("PrecoGasto").toString().replace(",", "."));
                        combustivelTotal += Double.parseDouble(value.getDocuments().get(i).get("LitrosAbastecido").toString().replace(",", "."));
                        consumoMedio = (consumoMedio + Double.parseDouble(value.getDocuments().get(i).get("Consumo").toString().replace(",", "."))) / value.size();
                    }
                    mostrarInfo(value.size(), precoTotal, combustivelTotal, consumoMedio);
                }
            }
        });
    }

    private void mostrarInfo(int size, double precoTotal, double combustivelTotal, double consumoMedio) {
        abasterTXT.setText(Integer.toString(size));
        precoTXT.setText("R$: "+String.format("%.2f", precoTotal).replace(".",","));
        combustivelTXT.setText(String.format("%.2f", combustivelTotal).replace(".",",")+"L");
        consumoTXT.setText(String.format("%.2f", consumoMedio).replace(".",",")+"Km/L");
    }

    public static RelatorioFragment newIntance(String veiculoID, String marca, String modelo){
        RelatorioFragment fragment = new RelatorioFragment();

        Bundle args = new Bundle();

        args.putString("veiId", veiculoID);
        args.putString("veiMarca", marca);
        args.putString("veiModelo", modelo);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(args != null) {
                    getParentFragmentManager().popBackStack();
                    getParentFragmentManager().beginTransaction().replace(R.id.fragRelatorio, Grafico1EspecificoRelatorioFragment.newInstance(args.getString("veiId"), args.getString("veiMarca"), args.getString("veiModelo"))).addToBackStack(null).commit();
                }
                b1.setVisibility(View.GONE);
                b2.setVisibility(View.GONE);}
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(args != null) {
                    getParentFragmentManager().popBackStack();
                    getParentFragmentManager().beginTransaction().replace(R.id.fragRelatorio, Grafico2EspecificoRelatorioFragment.newInstance(args.getString("veiId"), args.getString("veiMarca"), args.getString("veiModelo"))).addToBackStack(null).commit();
                }
                b1.setVisibility(View.GONE);
                b2.setVisibility(View.GONE);}
        });
    }

}