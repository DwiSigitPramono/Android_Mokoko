package com.mokoko.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mokoko.R;
import com.mokoko.model.TokoUpload;
import com.mokoko.model.UserUpload;

import java.util.Iterator;

public class DompetkuActivity extends AppCompatActivity {

    private static final String Database_Path = "bakos_db/toko";
    private static final String Database_Path_User = "bakos_db/user";
    private DatabaseReference mDatabaseReference;
    private TextView tvSaldo;
    private ScrollView svDompet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dompetku);

        svDompet = (ScrollView)findViewById(R.id.svDompet);
        Intent i = getIntent();
        Bundle bd = i.getExtras();
        if(i.getStringExtra("status") != null){
            String status = (String)bd.get("status");
            if((status != null)&&(status.contains("1"))) {
                Snackbar.make(svDompet, "Transaksi berhasil.", Snackbar.LENGTH_SHORT).show();
            }else {
                Snackbar.make(svDompet,"Transaksi gagal.", Snackbar.LENGTH_SHORT).show();
            }
            bd.remove("status");
        }


        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("Toko")) {
                    getInfoToko(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }else{
                    getInfoUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    public void pindai(View view) {
        startActivity(new Intent(this, PindaiActivity.class));
    }

    public void terima(View view) {
        startActivity(new Intent(this, TerimaActivity.class));
    }

    private void getInfoUser(String namaUser) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path_User);
        Query searchQuery = mDatabaseReference.orderByChild("info/emailUser").equalTo(namaUser);
        tvSaldo = (TextView) findViewById(R.id.tvSaldo);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                UserUpload infoUser = new UserUpload();
                while ((iterator.hasNext())) {
                    infoUser = iterator.next().getValue(UserUpload.class);
                }
                tvSaldo.setText(getMoney(String.valueOf(infoUser.getCredit())));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                UserUpload infoUser = new UserUpload();
                while ((iterator.hasNext())) {
                    infoUser = iterator.next().getValue(UserUpload.class);
                }
                tvSaldo.setText(getMoney(String.valueOf(infoUser.getCredit())));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getInfoToko(String namaToko){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        Query searchQuery = mDatabaseReference.orderByChild("info/email").equalTo(namaToko);
        tvSaldo = (TextView) findViewById(R.id.tvSaldo);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                TokoUpload infoToko = new TokoUpload();
                while((iterator.hasNext())){
                    infoToko = iterator.next().getValue(TokoUpload.class);
                }
                tvSaldo.setText(getMoney(infoToko.getCredit()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                TokoUpload infoToko = new TokoUpload();
                while((iterator.hasNext())){
                    infoToko = iterator.next().getValue(TokoUpload.class);
                }
                tvSaldo.setText(getMoney(infoToko.getCredit()));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getMoney(String str2){
        StringBuilder str = new StringBuilder(str2);
        int idx = str.length()-3;

        while(idx >0){
            str.insert(idx,".");
            idx = idx-3;
        }

        return str.toString();
    }

    public void transaksi(View view) {
        startActivity(new Intent(this, TransaksiActivity.class));
    }

    public void topup(View view) { startActivity(new Intent(this, TopupActivity.class)); }

    public void withdraw(View view) { startActivity(new Intent(this, WithdrawActivity.class));}
}
