package com.mokoko.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mokoko.R;

public class ProfileUserActivity extends AppCompatActivity {

    private static final String Database_Path = "bakos_db/user";
    private DatabaseReference mDatabaseReference;
    private Query query;
    private String userName;

    private ScrollView svToko;
    private ProgressDialog progressDialog;
    private EditText etNamaUser;
    private EditText etTelepon;
    private Button btUbah;
    private Button btSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        svToko = (ScrollView)findViewById(R.id.svToko);

        progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        etNamaUser = (EditText)findViewById(R.id.etNamaSales);
        etTelepon = (EditText)findViewById(R.id.etTelp);
        btUbah = (Button)findViewById(R.id.Ubah);
        btSimpan = (Button)findViewById(R.id.Simpan);
        
        etNamaUser.setInputType(0);
        etTelepon.setInputType(0);
        etNamaUser.setTextColor(ContextCompat.getColor(this, R.color.colorTextDis));
        etTelepon.setTextColor(ContextCompat.getColor(this, R.color.colorTextDis));

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                userName = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }else{
            userName = "";
        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        query = mDatabaseReference.child(userName+"/info");
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                etNamaUser.setText(snapshot.child("namaUser").getValue(String.class));
                etTelepon.setText(snapshot.child("telpUser").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void ubah(View view) {
        etTelepon = (EditText)findViewById(R.id.etTelp);
        btUbah = (Button)findViewById(R.id.Ubah);
        btSimpan = (Button)findViewById(R.id.Simpan);

        etTelepon.setInputType(InputType.TYPE_CLASS_NUMBER);
        etTelepon.setTextColor(ContextCompat.getColor(this, R.color.colorHitam));
        btSimpan.setVisibility(View.VISIBLE);
        btUbah.setVisibility(View.GONE);
    }

    public void simpan(View view) {
        String tlp = etTelepon.getText().toString();
        ubahTelepon(tlp);
    }

    private void ubahTelepon(String telp){
        if(!telp.isEmpty()) {
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Proses ...");
            progressDialog.show();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
            mDatabaseReference.child(userName + "/info").child("telpUser").setValue(telp);
            progressDialog.dismiss();
            Snackbar.make(svToko,"Perubahan profil pengguna berhasil.", Snackbar.LENGTH_SHORT).show();
            btSimpan.setVisibility(View.GONE);
            btUbah.setVisibility(View.VISIBLE);
            etTelepon.setInputType(0);
            etTelepon.setTextColor(ContextCompat.getColor(this, R.color.colorTextDis));
        }else{
            Snackbar.make(svToko,"Periksa kembali data kamu.", Snackbar.LENGTH_SHORT).show();
        }
    }
}