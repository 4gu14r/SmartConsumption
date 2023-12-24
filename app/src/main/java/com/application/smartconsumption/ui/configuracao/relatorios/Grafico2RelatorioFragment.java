package com.application.smartconsumption.ui.configuracao.relatorios;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.smartconsumption.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Grafico2RelatorioFragment extends Fragment {

    int contador = 0;
    private BarChart grafico;
    private List<String> datas;
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

        pegarVeiculos(user.getCurrentUser().getUid());

        return view;
    }

    private void pegarVeiculos(String uid) {
        DocumentReference uvRef = db.collection("Usuarios").document(uid);
        db.collection("VeiculosUsuarios").whereEqualTo("Usuario", uvRef).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    datas = new ArrayList<>();
                    for (int i = 0; i < value.size(); i++) {
                        String veiculoId = value.getDocuments().get(i).getId();
                        DocumentReference dVei = value.getDocuments().get(i).getDocumentReference("Veiculo");
                        pegarModelo(veiculoId, dVei);
                    }
                }
            }
        });
    }

    private void pegarModelo(String id, DocumentReference veiculoId) {
        db.document(veiculoId.getPath()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    pegarAbastecimentos(id, value.getString("Modelo"));
                }
            }
        });
    }

    private void pegarAbastecimentos(String veiculoId, String veiculoNome) {
        System.out.println(veiculoNome);
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

                            BarDataSet dataSet = new BarDataSet(entries, veiculoNome);
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
}
