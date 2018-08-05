package com.mokoko.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mokoko.R;

public class AkunMenuActivity extends AppCompatActivity {

    TextView tvProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun_menu);

        tvProfil = (TextView)findViewById(R.id.tvProfil);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("Toko")) {
                    tvProfil.setText("Profil Toko");
                }else{
                    tvProfil.setText("Profil Pengguna");
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public void tampilProfilToko(View view) {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("Toko")) {
                    Intent semua = new Intent(this, ProfileTokoActivity.class);
                    startActivity(semua);
                }else{
                    Intent semua = new Intent(this, ProfileUserActivity.class);
                    startActivity(semua);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public void tampilProfilAkun(View view) {
        Intent semua = new Intent(this, AkunActivity.class);
        startActivity(semua);
    }
}