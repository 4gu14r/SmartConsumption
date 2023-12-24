package com.application.smartconsumption.ui.configuracao.relatorios;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.smartconsumption.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Grafico2EspecificoRelatorioFragment extends Fragment {

    int contador = 0;
    private BarChart grafico;
    private List<String> datas = datas = new ArrayList<>();
    ArrayList<BarEntry> entries;
    private List<BarDataSet> datasets = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth user = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grafico1_relatorio, container, false);

        grafico = view.findViewById(R.id.grafico);
        grafico.getAxisRight().setDrawLabels(false);

        Bundle args = getArguments();

        if(args != null){
            pegarAbastecimentos(args.getString("idVei"), args.getString("modeloVei"));
        }

        return view;
    }

    private void pegarAbastecimentos(String veiculoId, String modelo) {
        DocumentReference veiculoRef = db.collection("VeiculosUsuarios").document(veiculoId);
        db.collection("Abastecer").whereEqualTo("VeiculosUsuarios", veiculoRef)
                .orderBy("DataHora", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && !value.isEmpty()) {
                            entries = new ArrayList<>();
                            for (int i = 0; i < value.size(); i++) {
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM");
                                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                                datas.add(df.format(new Date(value.getDocuments().get(i).getTimestamp("DataHora").getSeconds() * 1000)));
                                entries.add(new BarEntry(contador, Float.parseFloat(value.getDocuments().get(i).getString("PrecoGasto").replace(",","."))));
                                contador++;
                            }

                            BarDataSet dataSet = new BarDataSet(entries, modelo);
                            dataSet.setColors(getRandomColor());
                            dataSet.setValueTextSize(12);
                            dataSet.setDrawValues(true);
                            datasets.add(dataSet);
                        }


                        setupChart();
                    }
                });
    }


    private void setupChart() {
        BarData barData = new BarData(datasets.toArray(new BarDataSet[0]));
        barData.setBarWidth(0.5f);
        grafico.setData(barData);

        XAxis xAxis = grafico.getXAxis();
        xAxis.setAxisMinimum(0f - 0.5f);
        xAxis.setAxisMaximum(datas.size() - 0.5f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(datas));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setLabelRotationAngle(0);

        YAxis yAxis = grafico.getAxisLeft();
        yAxis.setAxisMinimum(0f);

        grafico.getAxisLeft().setEnabled(true);
        grafico.getDescription().setEnabled(false);
        grafico.getLegend().setTextSize(12f);
        grafico.setTouchEnabled(true);
        grafico.setDragEnabled(true);

        grafico.animateY(1000);
        grafico.invalidate();
    }

    private int getRandomColor() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return Color.rgb(r, g, b);
    }

    public static Grafico2EspecificoRelatorioFragment newInstance(String id, String marca, String modelo){
        Grafico2EspecificoRelatorioFragment fragment = new Grafico2EspecificoRelatorioFragment();

        Bundle args = new Bundle();

        args.putString("idVei", id);
        args.putString("marcaVei", marca);
        args.putString("modeloVei", modelo);

        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(args != null) {
                    getParentFragmentManager().popBackStack();
                    getParentFragmentManager().beginTransaction().replace(R.id.fragRelatorio, RelatorioFragment.newIntance(args.getString("idVei"), args.getString("marcaVei"), args.getString("modeloVei"))).addToBackStack(null).commit();
                }
            }
        });
    }
}
