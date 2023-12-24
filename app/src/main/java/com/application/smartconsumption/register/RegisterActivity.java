package com.application.smartconsumption.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.ref.Reference;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText nome, sobre, nasc, email, senha, confirmSenha;
    private RadioGroup sexo;
    private RadioButton masc, femi;
    private Button btCadastrar;
    private String usuarioID, genero;

    private boolean isPasswordVisible = false;
    DataHora aniversario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        aniversario = new DataHora(RegisterActivity.this);
        inicializarComponents();

        nasc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                DatePickerDialog datePickerDialog = aniversario.calendario();
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        aniversario.setDataNascString(String.format("%02d", i2) + "/" + String.format("%02d", i1 + 1) + "/" + i);
                        aniversario.setDataNascimentoNum(new int[]{i2, i1, i});
                        nasc.setText(aniversario.getDataNascString());
                    }
                });

                if (b) {
                    datePickerDialog.show();
                    aniversario.setDataNascString(nasc.getText().toString());
                    nasc.setText(aniversario.getDataNascString());
                    aniversario.setDataNascimentoNum(aniversario.converterDataStrToInt(nasc.getText().toString().split("/")));
                }

                aniversario.setDataNascString(nasc.getText().toString());
                nasc.setText(aniversario.getDataNascString());
                aniversario.setDataNascimentoNum(aniversario.converterDataStrToInt(nasc.getText().toString().split("/")));

            }
        });

        senha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int drawableRight = 2; // O ícone está à direita (índice 2)
                    Drawable drawable = senha.getCompoundDrawables()[drawableRight];

                    // Verifique se o toque ocorreu dentro da área do ícone
                    if (drawable != null && motionEvent.getRawX() >= (senha.getRight() - drawable.getBounds().width())) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
                return false;
            }
        });

        confirmSenha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int drawableRight = 2; // O ícone está à direita (índice 2)
                    Drawable drawable = confirmSenha.getCompoundDrawables()[drawableRight];

                    // Verifique se o toque ocorreu dentro da área do ícone
                    if (drawable != null && motionEvent.getRawX() >= (confirmSenha.getRight() - drawable.getBounds().width())) {
                        togglePasswordCorfirmVisibility();
                        return true;
                    }
                }
                return false;
            }
        });


        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camposPreenchidosCorretamente()) {
                    if (verificarSenha(senha, confirmSenha)) {
                        CadastrarUsuario(email.getText().toString(), senha.getText().toString());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Senhas não coincidem", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        // Mude o tipo de entrada do EditText com base na visibilidade da senha
        if (isPasswordVisible) {
            senha.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        } else {
            senha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        // Atualize o ícone à direita do EditText com base na visibilidade da senha
        int drawableResId = isPasswordVisible ? R.drawable.baseline_visibility_off_24 : R.drawable.baseline_remove_red_eye_24;
        Drawable drawable = ContextCompat.getDrawable(this, drawableResId);
        senha.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableResId, 0);

    }

    private void togglePasswordCorfirmVisibility() {
        isPasswordVisible = !isPasswordVisible;

        // Mude o tipo de entrada do EditText com base na visibilidade da senha
        if (isPasswordVisible) {
            confirmSenha.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        } else {
            confirmSenha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        // Atualize o ícone à direita do EditText com base na visibilidade da senha
        int drawableResId = isPasswordVisible ? R.drawable.baseline_visibility_off_24 : R.drawable.baseline_remove_red_eye_24;
        Drawable drawable = ContextCompat.getDrawable(this, drawableResId);
        confirmSenha.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableResId, 0);

    }

    private boolean camposPreenchidosCorretamente() {
        nasc.setText(nasc.getText().toString());
        return nome != null && !nome.getText().toString().isEmpty()
                && sobre != null && !sobre.getText().toString().isEmpty()
                && nasc != null && !nasc.getText().toString().isEmpty()
                && genero != null && !genero.isEmpty()
                && email != null && !email.getText().toString().isEmpty()
                && senha != null && !senha.getText().toString().isEmpty()
                && confirmSenha != null && !confirmSenha.getText().toString().isEmpty();
    }

    private void CadastrarUsuario(String email, String senha) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    criarDoc(salvarUsuario(aniversario.dataNascimento(), genero));
                } else {
                    String erro;

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Mínimo de senha 6 caracteres";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Essa conta já existe";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Email Incorreto";
                    } catch (Exception e) {
                        erro = "Erro ao cadastrar o usuário: " + e;
                    }

                    Toast.makeText(RegisterActivity.this, erro, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void criarDoc(Map<String, Object> usuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);

        documentReference.set(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RegisterActivity.this, "Cadastro Realizado com Sucesso", Toast.LENGTH_SHORT).show();
                Log.d("db", "Usuário Salvo");
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db_error", "Erro: " + e.toString());
                Toast.makeText(RegisterActivity.this, "Falha", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public Map<String, Object> salvarUsuario(Date data, String genero) {

        Map<String, Object> usuario;
        usuario = new HashMap<>();

        Map<String, Object> contato = new HashMap<>();
        contato.put("Primeiro Nome", nome.getText().toString());
        contato.put("Ultimo Nome", sobre.getText().toString());


        usuario.put("Nome", contato);
        usuario.put("Data de Nascimento", new Timestamp(data));
        usuario.put("Sexo", genero);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference perfilRef = db.collection("Perfil").document("kTuCx7LTkRxatMqM8Vic");

        usuario.put("Perfil_id", perfilRef);

        return usuario;

    }

    private void escolherSexo() {
        sexo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (findViewById(i).equals(masc)) {
                    genero = masc.getText().toString();
                } else {
                    genero = femi.getText().toString();
                }
            }
        });
    }

    public void inicializarComponents() {
        nome = findViewById(R.id.ediTxtNome);
        sobre = findViewById(R.id.ediTxtSobre);
        nasc = findViewById(R.id.ediTxtNasc);
        sexo = findViewById(R.id.sexo);
        masc = findViewById(R.id.masculino);
        femi = findViewById(R.id.feminino);
        email = findViewById(R.id.emailCadastrar);
        senha = findViewById(R.id.passwordCadastrar);
        confirmSenha = findViewById(R.id.confirmPassword);
        btCadastrar = findViewById(R.id.buttonCadastrarUser);
        nasc.setText(String.format("%02d", aniversario.calendario().getDatePicker().getDayOfMonth()) + "/" + String.format("%02d", aniversario.calendario().getDatePicker().getMonth() + 1) + "/" + String.format("%02d", aniversario.calendario().getDatePicker().getYear()));
        aniversario.setDataNascString(nasc.getText().toString());
        escolherSexo();
    }

    public Boolean verificarSenha(EditText senha, EditText confirmSenha) {
        if (senha.getText().toString().equals(confirmSenha.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }
}