package com.application.smartconsumption.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.smartconsumption.MainActivity;
import com.application.smartconsumption.R;
import com.application.smartconsumption.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView telaRegister, esqueceuSenha;
    private EditText email, senha;
    private Button botao;
    private boolean isPasswordVisible = false;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComp();
        getSupportActionBar().hide();

        telaRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        /*esqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperarSenha();
            }
        });*/

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

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty() || senha.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else{
                    AutenticarUsuario(email.getText().toString(), senha.getText().toString());
                }
            }
        });

    }

    //private void recuperarSenha() {}

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

    private void AutenticarUsuario(String email, String senha) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.VISIBLE);
                    TelaPrincipal();
                } else {
                    // Tratamento de exceções genéricas
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthException e) {
                        // Tratamento de exceções específicas
                        switch (e.getErrorCode()) {
                            case "ERROR_INVALID_EMAIL":
                                Toast.makeText(LoginActivity.this, "E-mail inválido.", Toast.LENGTH_SHORT).show();
                                break;
                            case "ERROR_WRONG_PASSWORD":
                                Toast.makeText(LoginActivity.this, "Senha incorreta.", Toast.LENGTH_SHORT).show();
                                break;
                            case "ERROR_USER_NOT_FOUND":
                                Toast.makeText(LoginActivity.this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Falha: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void TelaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser userLogin = FirebaseAuth.getInstance().getCurrentUser();
        if (userLogin != null){
            TelaPrincipal();
        }
    }

    public void initComp(){
        telaRegister = findViewById(R.id.cadastrarUser);
        //esqueceuSenha = findViewById(R.id.ForgetPassword);
        email = findViewById(R.id.emailEntrar);
        senha = findViewById(R.id.passowordEntrar);
        botao = findViewById(R.id.buttonLogar);
        progressBar = findViewById(R.id.progBar);
    }

}