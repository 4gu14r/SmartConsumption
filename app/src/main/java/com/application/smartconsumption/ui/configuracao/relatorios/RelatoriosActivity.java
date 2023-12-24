package com.application.smartconsumption.ui.configuracao.relatorios;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.application.smartconsumption.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RelatoriosActivity extends AppCompatActivity {

    private Button bt1, bt2;
    private MutableLiveData<List<Relatorio>> veiculosLiveData = new MutableLiveData<List<Relatorio>>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth user = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorios);
        initComp();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragRelatorio);

                if (currentFragment instanceof GeralRelatorioFragment) {
                    System.out.println(currentFragment);
                    bt1.setVisibility(View.VISIBLE);
                    bt2.setVisibility(View.VISIBLE);
                } else {
                    bt1.setVisibility(View.GONE);
                    bt2.setVisibility(View.GONE);
                }
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragRelatorio, new GeralRelatorioFragment()).commit();
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragRelatorio, new Grafico1RelatorioFragment()).addToBackStack(null).commit();
                bt1.setVisibility(View.GONE);
                bt2.setVisibility(View.GONE);
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragRelatorio, new Grafico2RelatorioFragment()).addToBackStack(null).commit();
                bt1.setVisibility(View.GONE);
                bt2.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opcao_veiculos_menu, menu);

        veiculosLiveData.observe(this, new Observer<List<Relatorio>>() {
            @Override
            public void onChanged(List<Relatorio> veiculos) {
                menu.clear();

                for (int i = 0; i < veiculos.size(); i++) {
                    Relatorio menuItem = veiculos.get(i);
                    menu.add(Menu.NONE, i, Menu.NONE, menuItem.getModelo())
                            .setIntent(new Intent().putExtra("id", menuItem.getId()).putExtra("modelo", menuItem.getModelo()));
                }
            }
        });
        VericarVeiculos();

        return true;
    }

    private void VericarVeiculos() {
        DocumentReference usuario = db.collection("Usuarios").document(user.getCurrentUser().getUid());
        db.collection("VeiculosUsuarios").whereEqualTo("Usuario", usuario).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    List<Relatorio> veiculos = new ArrayList<>();

                    for (int i = 0; i < value.size(); i++) {
                        String veiculoReferencia = value.getDocuments().get(i).getDocumentReference("Veiculo").getId();
                        pegarVeiculo(veiculoReferencia, veiculos, value.getDocuments().get(i).getId());
                    }
                }
            }
        });
    }

    private void pegarVeiculo(String veiculoReferencia, List<Relatorio> veiculos, String id) {
        db.collection("VeiculosCadastrado").document(veiculoReferencia).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    String marca = value.getString("Marca");
                    String modelo = value.getString("Modelo");

                    veiculos.add(new Relatorio(id, marca, modelo));
                    veiculosLiveData.setValue(veiculos);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        List<Relatorio> itens = veiculosLiveData.getValue();

        if (id >= 0 && id < itens.size()) {
            Relatorio nomeItem = itens.get(id);
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragRelatorio, RelatorioFragment.newIntance(nomeItem.getId(), nomeItem.getMarca(), nomeItem.getModelo())).addToBackStack(null).commit();
            bt1.setVisibility(View.GONE);
            bt2.setVisibility(View.GONE);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void initComp(){
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
    }

}