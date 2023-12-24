package com.application.smartconsumption.ui.configuracao.perfil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.application.smartconsumption.R;
import com.application.smartconsumption.ferramentas.DataHora;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private EditText nome, sobre, dtNascimento, email, senha;
    private Button btEditar, btSalvar;
    private RadioGroup sexo;
    private RadioButton masc, femi;
    private ProgressBar progBar;
    FirebaseUser firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference userRef;
    DataHora aniversario;
    String usuarioID, emailFire;
    Map<String, Object> mapaNome;
    Timestamp tempoNasc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        iniciarComponentes();

        dtNascimento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                DatePickerDialog datePickerDialog = aniversario.calendario();
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        aniversario.setDataNascString(String.format("%02d", i2) + "/" + String.format("%02d", i1 + 1) + "/" + i);
                        aniversario.setDataNascimentoNum(new int[]{i2, i1, i});
                        dtNascimento.setText(aniversario.getDataNascString());
                    }
                });

                if (b) {
                    datePickerDialog.show();
                }else{
                    salvarData();
                }

            }
        });

        btEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarPerfil();
            }
        });

    }

    public void carregarDados(){
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    mapaNome = (Map<String, Object>) value.get("Nome");
                    nome.setText((CharSequence) mapaNome.get("Primeiro Nome"));
                    sobre.setText((CharSequence) mapaNome.get("Ultimo Nome"));
                    tempoNasc = value.getTimestamp("Data de Nascimento");
                    Date date = tempoNasc.toDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = dateFormat.format(date);
                    dtNascimento.setText(formattedDate);
                    if (value.getString("Sexo").equals("Masculino")) {
                        sexo.check(masc.getId());
                    } else {
                        sexo.check(femi.getId());
                    }
                    email.setText(emailFire);
                }
            }
        });
    }

    public String escolherSexo(){
        if(sexo.getCheckedRadioButtonId() == masc.getId()) {
            return masc.getText().toString();
        }else{
            return femi.getText().toString();
        }
    }

    public void salvarData(){
        aniversario.setDataNascString(dtNascimento.getText().toString());
        dtNascimento.setText(aniversario.getDataNascString());
        aniversario.setDataNascimentoNum(aniversario.converterDataStrToInt(dtNascimento.getText().toString().split("/")));
    }

    @Override
    protected void onStart() {
        super.onStart();

        emailFire = firebaseAuth.getEmail();
        usuarioID = firebaseAuth.getUid();
        userRef = db.collection("Usuarios").document(usuarioID);

        carregarDados();

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if(mapaNome.equals(nome) || tempoNasc.equals(dtNascimento.getText().toString()) || genero.equals(sexo.toString())){}
                /*salvarData();

                userRef.update(userName(nome.getText().toString(), sobre.getText().toString(),aniversario.dataNascimento(), escolherSexo())).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PerfilActivity.this, "Usuário editado com sucesso", Toast.LENGTH_SHORT).show();
                        salvarPerfil();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PerfilActivity.this, "Falha: "+e, Toast.LENGTH_SHORT).show();
                    }
                });*/

                if(!emailFire.equals(email.getText().toString())){
                    firebaseAuth.updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            System.out.println("Teste1");
                            if(task.isSuccessful()) {
                                Log.d("email", "Email alterado");
                            }else{
                                Log.d("email", "Não alterado");
                            }
                            if(!senha.getText().toString().isEmpty()){
                                firebaseAuth.updatePassword(senha.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d("senha", "Senha trocada");
                                            System.out.println(senha.getText().toString());
                                        }else{
                                            Log.d("senha", "Erro ao alterar");
                                            System.out.println(senha.getText().toString());
                                        }
                                        salvarData();
                                        userRef.update(userName(nome.getText().toString(), sobre.getText().toString(),aniversario.dataNascimento(), escolherSexo())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(PerfilActivity.this, "Usuário editado com sucesso", Toast.LENGTH_SHORT).show();
                                                salvarPerfil();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(PerfilActivity.this, "Falha: "+e, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else{
                                salvarData();
                                userRef.update(userName(nome.getText().toString(), sobre.getText().toString(),aniversario.dataNascimento(), escolherSexo())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(PerfilActivity.this, "Usuário editado com sucesso", Toast.LENGTH_SHORT).show();
                                        salvarPerfil();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PerfilActivity.this, "Falha: "+e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }else{
                    if(!senha.getText().toString().isEmpty()){
                        firebaseAuth.updatePassword(senha.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("senha", "Senha trocada");
                                    System.out.println(senha);
                                }else{
                                    Log.d("senha", "Erro ao alterar");
                                    System.out.println(senha);
                                }
                                salvarData();
                                userRef.update(userName(nome.getText().toString(), sobre.getText().toString(),aniversario.dataNascimento(), escolherSexo())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(PerfilActivity.this, "Usuário editado com sucesso", Toast.LENGTH_SHORT).show();
                                        salvarPerfil();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PerfilActivity.this, "Falha: "+e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }else{
                        salvarData();
                        userRef.update(userName(nome.getText().toString(), sobre.getText().toString(),aniversario.dataNascimento(), escolherSexo())).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(PerfilActivity.this, "Usuário editado com sucesso", Toast.LENGTH_SHORT).show();
                                salvarPerfil();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PerfilActivity.this, "Falha: "+e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            }
        });

    }

    public Map<String, Object> userName(String nome, String sobre, Date data, String genero){
        Map<String, Object> usuario = new HashMap<>();
        Map<String, String> contato = new HashMap<>();

        contato.put("Primeiro Nome", nome);
        contato.put("Ultimo Nome", sobre);

        usuario.put("Nome", contato);
        usuario.put("Data de Nascimento", new Timestamp(data));
        usuario.put("Sexo", genero);

        return usuario;
    }

    public void iniciarComponentes(){
        nome = findViewById(R.id.ediTxtNome);
        sobre = findViewById(R.id.ediTxtSobre);
        dtNascimento = findViewById(R.id.ediTxtNasc);
        email = findViewById(R.id.editTxtEmail);
        senha = findViewById(R.id.editTxtSenhaAtual);
        btEditar = findViewById(R.id.buttonEditUser);
        btSalvar = findViewById(R.id.buttonSaveUser);
        sexo = findViewById(R.id.sexo);
        masc = findViewById(R.id.masculino);
        femi = findViewById(R.id.feminino);
        progBar = findViewById(R.id.progBar);
        firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        aniversario = new DataHora(PerfilActivity.this);
    }

    private void salvarPerfil() {
        btSalvar.setVisibility(View.INVISIBLE);
        btEditar.setEnabled(true);
        senha.setText("P@s5W0rd3");
        nome.setEnabled(false);
        sobre.setEnabled(false);
        dtNascimento.setEnabled(false);
        masc.setEnabled(false);
        femi.setEnabled(false);
        email.setEnabled(false);
        senha.setEnabled(false);
        progBar.setVisibility(View.VISIBLE);
    }
    public void editarPerfil(){
        btSalvar.setVisibility(View.VISIBLE);
        btEditar.setEnabled(false);
        senha.setText("");
        nome.setEnabled(true);
        sobre.setEnabled(true);
        dtNascimento.setEnabled(true);
        masc.setEnabled(true);
        femi.setEnabled(true);
        email.setEnabled(true);
        senha.setEnabled(true);
    }

}