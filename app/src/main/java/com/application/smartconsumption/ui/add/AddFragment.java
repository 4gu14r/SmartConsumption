package com.application.smartconsumption.ui.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.application.smartconsumption.MainActivity;
import com.application.smartconsumption.R;
import com.application.smartconsumption.databinding.FragmentAddBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFragment extends Fragment {
    int tanqueAgora;
    float tanqueAnterior;
    double tanqueAgoraAntigo, tanqueAntigo;
    long distanciaPecorrida, HodometroAntigo;
    String consumoCarro, tanqueTotal;

    ArrayList<String> veiculos = new ArrayList<>();
    ArrayAdapter<String> itensVeiculo, itensCombustivel;
    private FragmentAddBinding binding;
    private TextView textView, textHodometro, textCombustivel, textTanque;
    private EditText edtHodometro;
    private TextInputLayout textCombustivelDrop, textVeiculoDrop;
    private SeekBar volumeTanque;
    private String idVei, idVeiUser;
    private Button btAbastecer;
    private AutoCompleteTextView selectCombustivel, selectVeiculo;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth user = FirebaseAuth.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initComp();

        db.collection("VeiculosUsuarios").whereEqualTo("Usuario", db.collection("Usuarios").document(user.getCurrentUser().getUid())).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot carro, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore Error", "Error getting carros", error);
                    return;
                }

                if (carro == null || carro.size() == 0) {
                    habilitarComponentes();
                } else {
                    if(!carro.isEmpty()) {
                        desabilitarComponentes();
                        for (int i = 0; i < carro.size(); i++) {
                            DocumentReference carID = (DocumentReference) carro.getDocuments().get(i).get("Veiculo");
                            carregarVeiculos(carID);
                        }
                        selectVeiculo.setText("");
                        edtHodometro.setText("");
                        selectCombustivel.setText("");
                    }
                }

                selectVeiculo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        edtHodometro.setText("");
                        selectCombustivel.setText("");
                        verificarCarro(adapterView.getItemAtPosition(i).toString());
                    }
                });

                edtHodometro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        edtHodometro.setText(edtHodometro.getText().toString());
                        verificarCarro(selectVeiculo.getText().toString());
                        selectCombustivel.setText("");
                    }
                });

            }
        });

        return root;
    }

    private void verificarCarro(String carro) {

        db.collection("VeiculosCadastrado").whereEqualTo("Modelo", carro).addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    for (int i = 0; i < value.size(); i++) {
                        idVei = value.getDocuments().get(i).getId();
                        tanqueTotal = value.getDocuments().get(i).get("TanqueTotal").toString();
                        consumoCarro = value.getDocuments().get(i).get("Consumo").toString();
                    }
                    qualMotor(idVei, consumoCarro);
                    valorTanqueTotal(tanqueTotal);
                }

            }
        });
    }

    private void valorTanqueTotal(String tanqueTotal) {
        if (tanqueTotal != null && !tanqueTotal.isEmpty()) {
            int valor = Integer.parseInt(tanqueTotal);
            volumeTanque.setMax(valor);
        } else {
            Toast.makeText(getContext(), "valor é nulo", Toast.LENGTH_SHORT).show();
        }
    }

    private void qualMotor(String idVei, String consumoCarro) {
        db.collection("VeiculosUsuarios")
                .whereEqualTo("Veiculo", db.collection("VeiculosCadastrado").document(idVei))
                .whereEqualTo("Usuario", db.collection("Usuarios").document(user.getCurrentUser().getUid()))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && !value.isEmpty()) {
                            for (int i = 0; i < value.size(); i++) {
                                carregarCombustivel(value.getDocuments().get(i).getString("Motor"));
                                if (edtHodometro.getText().toString().isEmpty()) {
                                    edtHodometro.setText(value.getDocuments().get(i).get("Hodometro").toString());
                                    idVeiUser = value.getDocuments().get(i).getId();
                                }
                                valorTanqueAtual(value.getDocuments().get(i).getDouble("TanqueAtual"), value.getDocuments().get(i).getLong("Hodometro"), consumoCarro);
                                pegarDadosAntigo(value.getDocuments().get(i).getLong("Hodometro"), value.getDocuments().get(i).getDouble("TanqueAtual"));
                            }
                        }
                    }
                });
    }

    private void pegarDadosAntigo(long hodometro, double tanque) {
        this.HodometroAntigo = hodometro;
        this.tanqueAntigo = tanque;
    }

    private void valorTanqueAtual(Double TanqueAtual, Long Hodometro, String Consumo) {

        String hodometroTexto = edtHodometro.getText().toString();

        if (!hodometroTexto.isEmpty()) {
            long hodometroNovo = Integer.parseInt(hodometroTexto);

            long kmPecorridos = hodometroNovo - Hodometro;
            if (kmPecorridos != 0) {
                distanciaPecorrida = kmPecorridos;
            }

            float LitroUsados = (kmPecorridos / Float.parseFloat(Consumo));


            if (LitroUsados < 0) {
                LitroUsados = 0.0F;
                if (LitroUsados > TanqueAtual) {
                    volumeTanque.setProgress(0);
                } else {
                    volumeTanque.setProgress((int) (TanqueAtual - (int) LitroUsados));
                    tanqueAnterior(TanqueAtual, LitroUsados);
                }
            } else {
                volumeTanque.setProgress((int) (TanqueAtual - (int) LitroUsados));
                tanqueAnterior(TanqueAtual, LitroUsados);
            }

            tanqueAgora = volumeTanque.getProgress();

        }

    }

    private void tanqueAnterior(double tanqueAtual, float litroUsados) {
        double quantidadeUtilizada = litroUsados;
        BigDecimal valorDecimal = new BigDecimal(quantidadeUtilizada);
        BigDecimal valorFormatado = valorDecimal.setScale(2, RoundingMode.DOWN);
        if ((tanqueAtual - litroUsados) != tanqueAtual && (tanqueAtual - litroUsados) > 0) {
            tanqueAgoraAntigo = tanqueAtual;
            tanqueAnterior = (float) (tanqueAtual - valorFormatado.floatValue());
        }
    }


    private void carregarCombustivel(String motor) {
        ArrayList<String> combustivel = new ArrayList<>();
        db.collection("TipoCombustiveis").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (getContext() != null) {
                    for (int i = 0; i < value.size(); i++) {
                        if (motor.equals("Gasolina")) {
                            if (value.getDocuments().get(i).getString("Combustível").contains("Gasolina")) {
                                combustivel.add(value.getDocuments().get(i).getString("Combustível"));
                            }
                        } else if (motor.equals("Diesel")) {
                            if (value.getDocuments().get(i).getString("Combustível").contains("Diesel")) {
                                combustivel.add(value.getDocuments().get(i).getString("Combustível"));
                            }
                        } else {
                            if (!value.getDocuments().get(i).getString("Combustível").contains("Diesel")) {
                                combustivel.add(value.getDocuments().get(i).getString("Combustível"));
                            }
                        }
                    }
                    itensCombustivel = new ArrayAdapter<>(getContext(), R.layout.list_modelos_veiculos_pessoal, combustivel);
                    selectCombustivel.setAdapter(itensCombustivel);
                }
            }
        });
    }

    private void carregarVeiculos(DocumentReference carID) {
        db.collection("VeiculosCadastrado").document(carID.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (getContext() != null) {
                    if (value != null && value.exists()){
                        veiculos.add(value.getString("Modelo"));
                        itensVeiculo = new ArrayAdapter<>(getContext(), R.layout.list_modelos_veiculos_pessoal, veiculos);
                        selectVeiculo.setAdapter(itensVeiculo);
                    }
                }

            }
        });

    }

    public void initComp() {
        selectCombustivel = binding.autoModeloComplete;
        selectVeiculo = binding.autoVeiculosList;
        textView = binding.textAdd;
        textHodometro = binding.textHodometro;
        edtHodometro = binding.edtHodometro;
        textCombustivel = binding.textCombustivel;
        textCombustivelDrop = binding.textCombustivelDrop;
        textVeiculoDrop = binding.Veiculos;
        textTanque = binding.textTanque;
        volumeTanque = binding.volumeTanque;
        btAbastecer = binding.btAbastecerAdd;

    }

    public void habilitarComponentes() {
        textHodometro.setVisibility(View.GONE);
        edtHodometro.setVisibility(View.GONE);
        textCombustivel.setVisibility(View.GONE);
        textVeiculoDrop.setVisibility(View.GONE);
        textCombustivelDrop.setVisibility(View.GONE);
        textTanque.setVisibility(View.GONE);
        volumeTanque.setVisibility(View.GONE);
        btAbastecer.setVisibility(View.GONE);
        selectCombustivel.setVisibility(View.GONE);
        selectVeiculo.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

    public void desabilitarComponentes() {
        textHodometro.setVisibility(View.VISIBLE);
        edtHodometro.setVisibility(View.VISIBLE);
        textCombustivel.setVisibility(View.VISIBLE);
        textVeiculoDrop.setVisibility(View.VISIBLE);
        textCombustivelDrop.setVisibility(View.VISIBLE);
        textTanque.setVisibility(View.VISIBLE);
        volumeTanque.setVisibility(View.VISIBLE);
        btAbastecer.setVisibility(View.VISIBLE);
        selectCombustivel.setVisibility(View.VISIBLE);
        selectVeiculo.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btAbastecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectVeiculo.getText().toString().isEmpty() && !edtHodometro.getText().toString().isEmpty() && !selectCombustivel.getText().toString().isEmpty()) {
                    if (Integer.parseInt(edtHodometro.getText().toString()) > HodometroAntigo) {
                        if (volumeTanque.getProgress() > tanqueAgora) {
                            db.collection("TipoCombustiveis").whereEqualTo("Combustível", selectCombustivel.getText().toString()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (value != null && !value.isEmpty()) {
                                        atualizarHodometroTanque(edtHodometro.getText().toString(), value.getDocuments().get(0).getString("Combustível"), value.getDocuments().get(0).getDouble("valor"));
                                    }
                                }
                            });
                        }else{
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Abastecer Tanque de Combustível", Toast.LENGTH_SHORT).show();
                            } else {
                                // Lógica apropriada para lidar com o contexto nulo
                            }
                        }
                    } else {
                        // Verifique se getContext() não é nulo antes de usar o Toast
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Hodômetro não Atualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            // Lógica apropriada para lidar com o contexto nulo
                        }
                    }
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Preencha Todos os Campos", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lógica apropriada para lidar com o contexto nulo
                    }}
            }
        });



    }

    public Map<String, Object> dadosAtualizar() {
        Map<String, Object> dados = new HashMap<>();
        dados.put("Hodometro", Integer.parseInt(edtHodometro.getText().toString()));
        dados.put("TanqueAtual", volumeTanque.getProgress());
        return dados;
    }

    private void atualizarHodometroTanque(String hodo, String nomeCombustivel, double valorCombustivel) {
        db.collection("VeiculosUsuarios").document(idVeiUser).update(dadosAtualizar()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("up_log", "Hodometro e Tanque Atualizado!");
                    abastecer(hodo, nomeCombustivel, valorCombustivel);
                } else {
                    Log.d("up_log", "Não foi Atualizado!");
                    Toast.makeText(getContext(), "Hodometro e o tanque de Combustivel não atualizado", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("up_log", "Falha" + e);
                Toast.makeText(getContext(), "Falha ao atualizar o Hodometro e o tanque de Combustivel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String precoGasto(double valorCombustivel, float tanqueAtual) {
        return new DecimalFormat("0.00").format(valorCombustivel * (tanqueAtual - tanqueAnterior));
    }

    public Map<String, Object> dadosSalvar(String hodometroTexto, String nomeCombus, double valorCombus) {

        DocumentReference veiculoUsuarios = db.collection("VeiculosUsuarios").document(idVeiUser);

        Map<String, Object> dados = new HashMap<>();

        Map<String, Object> combustivel = new HashMap<>();
        combustivel.put("Combustivel", nomeCombus);
        combustivel.put("Valor", valorCombus);

        float litros = tanqueAgora - tanqueAnterior;
        float consumo = (float) distanciaPecorrida / litros;

        if (!hodometroTexto.isEmpty()) {
            dados.put("HodometroRegistro", Integer.parseInt(hodometroTexto));
            dados.put("HodometroPecorrido", distanciaPecorrida);
            dados.put("TanqueRegistro", (float) tanqueAgora);
            dados.put("VeiculosUsuarios", veiculoUsuarios);
            dados.put("DataHora", new Timestamp(new Date()));
            dados.put("CombustivelRegistro", combustivel);
            dados.put("PrecoGasto", precoGasto(valorCombus, (float) tanqueAgora));
            dados.put("LitrosAbastecido", String.format("%.2f", litros));
            dados.put("Consumo", String.format("%.2f", consumo));
        }

        return dados;
    }

    private void abastecer(String hodo, String nome, double valor) {

        if (dadosSalvar(edtHodometro.getText().toString(), selectCombustivel.getText().toString(), valor) != null) {
            db.collection("Abastecer").add(dadosSalvar(hodo, nome, valor)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Abastecimento Completo", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Falha ao abastecer", Toast.LENGTH_SHORT).show();
                    Log.d("error", "Falha: " + e);
                }
            });
        } else {
            abastecer(hodo, nome, valor);
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}