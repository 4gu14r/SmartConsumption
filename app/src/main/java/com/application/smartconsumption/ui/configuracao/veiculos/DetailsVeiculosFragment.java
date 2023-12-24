package com.application.smartconsumption.ui.configuracao.veiculos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.smartconsumption.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

public class DetailsVeiculosFragment extends Fragment {

    private TextView Marca, Modelo, Motor, Consumo, Hodometro, Tanque;
    private Button botao;
    String idVeiculoUsuario, marca, modelo, motor, hodometro, consumo, tanque;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_veiculos, container, false);

        Marca = view.findViewById(R.id.marca);
        Modelo = view.findViewById(R.id.modelo);
        Consumo = view.findViewById(R.id.ctn_consumo);
        Motor = view.findViewById(R.id.ctn_motor);
        Hodometro = view.findViewById(R.id.ctn_hodometro);
        Tanque = view.findViewById(R.id.ctn_tanque);
        botao = view.findViewById(R.id.btExcluir);

        Bundle args = getArguments();

        if (args != null) {

            idVeiculoUsuario = args.getString("veiculoID");
            marca = args.getString("marca");
            modelo = args.getString("modelo");
            motor = args.getString("motor");
            hodometro = args.getString("hodometro");
            consumo = args.getString("consumo");
            tanque = args.getString("tanque");

            // Define o texto dos elementos TextView com os valores dos argumentos
            Marca.setText(marca);
            Modelo.setText(modelo);
            Consumo.setText(consumo+"Km/L");
            Hodometro.setText(hodometro+"Km");
            Tanque.setText(tanque+"L");
            Motor.setText(motor);
        }

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirCarro(idVeiculoUsuario);
            }
        });

    }

    private void excluirCarro(String idVeiculoUsuario) {
        db.collection("VeiculosUsuarios").document(idVeiculoUsuario).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Veiculo Deletado com Sucesso", Toast.LENGTH_SHORT).show();
                        Log.d("db_delete", "Veiculo Deletado com Sucesso");
                        startActivity(new Intent(getContext(), VeiculosActivity.class));
                        getActivity().finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Falha ao Deletar Veiculo: "+e, Toast.LENGTH_SHORT).show();
                        Log.d("db_delete", "Falha ao Deletar Veiculo");
                    }
                });
            }
        });
    }

    public static DetailsVeiculosFragment newInstance(String veiculoId, String marca, String modelo, String motor, String hodometro, String consumo, String tanque){
        DetailsVeiculosFragment fragment = new DetailsVeiculosFragment();

        Bundle args = new Bundle();

        args.putString("veiculoID", veiculoId);
        args.putString("marca", marca);
        args.putString("modelo", modelo);
        args.putString("motor", motor);
        args.putString("hodometro", hodometro);
        args.putString("consumo", consumo);
        args.putString("tanque", tanque);

        fragment.setArguments(args);
        return fragment;

    }

}