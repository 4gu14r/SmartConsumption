package com.application.smartconsumption.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.smartconsumption.MainActivity;
import com.application.smartconsumption.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity{
    private String id, marca, modelo, kmRegistro, kmPecorrido, precosRegistro, combustivel, valor, consumo, litros,data, hora;
    private TextView marcaTXT, modeloTXT, dataTXT, horaTXT, hodometroTXT, consumoTXT, combustivelTXT;
    private EditText precoTXTEnable, valorTXTEnable, precoTXTDisable, valorTXTDisable, litroTXTEnable, litroTXTDisable;
    private Button btEditar, btSalvar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        initComp();
        carregarValores();
    }

    private void carregarValores() {
        marcaTXT.setText(marca);
        modeloTXT.setText(modelo);
        dataTXT.setText(data);
        horaTXT.setText(hora);
        hodometroTXT.setText(kmPecorrido + "Km");
        litroTXTEnable.setText(litros+"L");
        litroTXTDisable.setText(litros+"L");
        precoTXTEnable.setText("R$: " + formatarNumero(precosRegistro));
        valorTXTEnable.setText("R$: " + formatarNumero(valor));
        consumoTXT.setText(consumo+"Km/L");

        combustivelTXT.setText(combustivel);
    }

    private String formatarNumero(String numero) {
        if (numero != null) {
            return numero.replace(" ", "").replace(".", ",");
        } else {
            return "";
        }
    }

    public void initComp() {
        marcaTXT = findViewById(R.id.marca);
        modeloTXT = findViewById(R.id.modelo);
        dataTXT = findViewById(R.id.data);
        horaTXT = findViewById(R.id.hora);
        hodometroTXT = findViewById(R.id.ctn_hodometro);
        litroTXTEnable = findViewById(R.id.ctn_tanqueEnable);
        litroTXTDisable = findViewById(R.id.ctn_tanqueDisable);
        consumoTXT = findViewById(R.id.ctn_consumo);
        precoTXTEnable = findViewById(R.id.ctn_precoGastoEnable);
        valorTXTEnable = findViewById(R.id.ctn_valorGasosaEnable);
        precoTXTDisable = findViewById(R.id.ctn_precoGastoDisable);
        valorTXTDisable = findViewById(R.id.ctn_valorGasosaDisable);
        combustivelTXT = findViewById(R.id.ctn_combustivel);
        btEditar = findViewById(R.id.btEditar);
        btSalvar = findViewById(R.id.btSalvar);

        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
            marca = getIntent().getStringExtra("marca");
            modelo = getIntent().getStringExtra("modelo");
            kmRegistro = getIntent().getStringExtra("hodometro");
            kmPecorrido = getIntent().getStringExtra("distancia");
            precosRegistro = getIntent().getStringExtra("precoGasto");
            combustivel = getIntent().getStringExtra("combustivel");
            valor = getIntent().getStringExtra("valor");
            consumo = getIntent().getStringExtra("consumo");
            litros = getIntent().getStringExtra("litros");
            data = getIntent().getStringExtra("data");
            hora = getIntent().getStringExtra("hora");
        }

        precoTXTEnable.setEnabled(false);
        valorTXTEnable.setEnabled(false);
        litroTXTEnable.setEnabled(false);
        btSalvar.setEnabled(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        btEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarAbastecimento();
            }
        });

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!valorTXTDisable.getText().toString()
                        .replace(" ", "")
                        .replace("R$:", "")
                        .replace(",", ".").isEmpty()
                && !precoTXTDisable.getText().toString()
                        .replace(" ", "")
                        .replace("R$:", "")
                        .replace(",", ".").isEmpty()) {
                    salvarAbastecimento();
                } else {
                    Toast.makeText(HomeActivity.this, "Não há valores inseridos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public Map<String, Object> dados(){

        Map<String, Object> combus = new HashMap<>();
        combus.put("Combustivel", combustivel);
        combus.put("Valor", valorTXTDisable.getText().toString().replace(" ", "").replace("R$:", "").replace(",", "."));

        Map<String, Object> d = new HashMap<>();
        d.put("PrecoGasto", precoTXTDisable.getText().toString().replace(" ", "").replace("R$:", "").replace(",", "."));
        d.put("LitrosAbastecido", litroTXTDisable.getText().toString().replace(" ", "").replace("L", "").replace(",", "."));
        d.put("CombustivelRegistro", combus);

        return d;
    }
    private void salvarAbastecimento() {
        db.collection("Abastecer").document(id)
                .update(dados())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("up_log", "Preço Atualizado com Sucesso!");
                            finish();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("marca", marca);
                            intent.putExtra("modelo", modelo);
                            intent.putExtra("distancia", kmPecorrido);
                            intent.putExtra("litros", litroTXTDisable.getText().toString().replace(" ", "").replace("L", "").replace(",", "."));
                            intent.putExtra("precoGasto", precoTXTDisable.getText().toString().replace(" ", "").replace("R$:", "").replace(",", "."));
                            intent.putExtra("combustivel", combustivel);
                            float litrosConsumido = Float.parseFloat(litroTXTDisable.getText().toString().replace(" ", "").replace("L", "").replace(",", "."));
                            float distancia = Float.parseFloat(kmPecorrido.toString());
                            float resposta = distancia/litrosConsumido;
                            intent.putExtra("consumo", Float.toString(resposta));
                            intent.putExtra("valor", valorTXTDisable.getText().toString().replace(" ", "").replace("R$:", "").replace(",", "."));
                            intent.putExtra("data", data);
                            intent.putExtra("hora", hora);
                            startActivity(intent);
                        } else {
                            Log.d("up_log", "Não foi Atualizado o preço!");
                        }
                    }
                });
    }

    private void editarAbastecimento() {
        btEditar.setEnabled(false);
        btSalvar.setEnabled(true);
        valorTXTEnable.setVisibility(View.INVISIBLE);
        precoTXTEnable.setVisibility(View.INVISIBLE);
        litroTXTEnable.setVisibility(View.INVISIBLE);
        valorTXTDisable.setVisibility(View.VISIBLE);
        precoTXTDisable.setVisibility(View.VISIBLE);
        litroTXTDisable.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
