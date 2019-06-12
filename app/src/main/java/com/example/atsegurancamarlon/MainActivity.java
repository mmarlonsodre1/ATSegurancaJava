package com.example.atsegurancamarlon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.github.rtoshiro.util.format.MaskFormatter;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.ads.MobileAds;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {
    EditText nome, email, senha, confirmarSenha, cpf;
    private final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 2;
    private List<String> mNomeLista = new ArrayList<>();
    private List<String> mEmailLista = new ArrayList<>();
    private List<String> mSenhaLista = new ArrayList<>();
    private List<String> mCPFLista = new ArrayList<>();

    private InterstitialAd mInterstitialAd;

    private String mNomeSalvo = "NOME";
    private String mSenhaSalvo = "SENHA";
    private String mEmailSalvo = "EMAIL";
    private String mCPFSalvo = "CPF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cpf = findViewById(R.id.cpf);
        //mascara cpf
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher mtw = new MaskTextWatcher(cpf, smf);
        cpf.addTextChangedListener(mtw);

        carregarPermissao();


        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

    }

    public void confirmar (View view){
         EditText email = (EditText)findViewById(R.id.email);
         String mEmail = email.getText().toString().trim();


         String mCpf = cpf.getText().toString().trim();

        EditText nome = (EditText)findViewById(R.id.nome);
         String mNome = nome.getText().toString().trim();

        EditText senha = (EditText)findViewById(R.id.senha);
         String msenha = senha.getText().toString().trim();
        EditText confirmSenha = (EditText)findViewById(R.id.confirmSenha);
         String mConfirmSenha =  confirmSenha.getText().toString().trim();


        Acao acao = new Acao();

                if ((isValidEmail(mEmail)) & (msenha.equals(mConfirmSenha))  & !mCpf.isEmpty()  &  !mNome.isEmpty() )
                {
                    acao.setNome(mNome);
                    acao.setEmail(mEmail);
                    acao.setSenha(msenha);
                    acao.setCpf(mCpf);

                    mNomeLista.add(acao.getNome());
                    StringBuilder nomes = new StringBuilder("");
                    for (String nomeContato : mNomeLista)
                        nomes.append(nomeContato).append("-");

                    try {
                        FileOutputStream fos = openFileOutput(mNomeSalvo, Context.MODE_PRIVATE);

                        fos.write(nomes.toString().getBytes());
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Deu Ruim", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Deu Ruim", Toast.LENGTH_SHORT).show();
                    }

                    mSenhaLista.add(acao.getSenha());
                    StringBuilder senhas = new StringBuilder();
                    for (String senhaCadastro : mSenhaLista)
                        senhas.append(senhaCadastro).append("-");

                    try {
                        FileOutputStream fos = openFileOutput(mSenhaSalvo, Context.MODE_PRIVATE);

                        fos.write(senhas.toString().getBytes());
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Deu Ruim", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Deu Ruim", Toast.LENGTH_SHORT).show();
                    }

                    mEmailLista.add(acao.getEmail());
                    StringBuilder emails = new StringBuilder("");
                    for (String emailContato : mEmailLista)
                        emails.append(emailContato).append("-");

                    try {
                        FileOutputStream fos = openFileOutput(mEmailSalvo, Context.MODE_PRIVATE);

                        fos.write(emails.toString().getBytes());
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Deu Ruim", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Deu Ruim", Toast.LENGTH_SHORT).show();
                    }

                    mCPFLista.add(acao.getCpf());
                    StringBuilder cpfs = new StringBuilder("");
                    for (String cpfCadastro : mCPFLista)
                        cpfs.append(cpfCadastro).append("-");

                    try {
                        FileOutputStream fos = openFileOutput(mCPFSalvo, Context.MODE_PRIVATE);

                        fos.write(cpfs.toString().getBytes());
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Deu Ruim", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Deu Ruim", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(getApplicationContext(),"Salvo em " + getFilesDir() ,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Campos inválidos",Toast.LENGTH_SHORT).show();
                }

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "Anuncio não carregado");
        }

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void carregarPermissao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    carregarPermissao();
                }
                break;

            default:
                Toast.makeText(this, "Precisa de permissão", Toast.LENGTH_SHORT).show();
        }

    }
}
