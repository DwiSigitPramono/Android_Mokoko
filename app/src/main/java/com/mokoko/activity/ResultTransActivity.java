package com.mokoko.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.mokoko.model.Transaksi;
import com.mokoko.model.UserUpload;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class ResultTransActivity extends AppCompatActivity {

    TextView tvNamaPenerima,tvIdPenerima, tvNominalTrans, tvWaktuTrans;
    private String Database_Path = "";
    private String Database_Path_Toko = "bakos_db/toko";
    private String Database_Path_User = "bakos_db/user";
    private String Database_Path_Transaksi = "bakos_db/transaksi";
    private DatabaseReference mDatabaseReference, drTrans;
    String saldoAwal = "";
    String idPengirim = "";
    String m1, m2, m3, m4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_trans);

        tvIdPenerima = (TextView)findViewById(R.id.tvIdPenerima);
        tvNamaPenerima = (TextView)findViewById(R.id.tvNamaPenerima);
        tvNominalTrans = (TextView)findViewById(R.id.tvNominalTrans);
        tvWaktuTrans = (TextView)findViewById(R.id.tvWaktuTrans);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("Toko")) {
                    Database_Path = "bakos_db/toko";
                    getInfoToko(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }else{
                    Database_Path = "bakos_db/user";
                    getInfoUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        Intent i = getIntent();
        Bundle bd = i.getExtras();
        m1 = (String) bd.get("m1");
        m2 = (String) bd.get("m2");
        m3 = (String) bd.get("m3");
        m4 = (String) bd.get("m4");
        bd.remove("m1");
        bd.remove("m2");
        bd.remove("m3");
        bd.remove("m4");

        tvNamaPenerima.setText(m3);
        tvNominalTrans.setText(getMoney(m1));
        tvIdPenerima.setText(m2);
        Date date = new Date();
        date.setTime(Long.valueOf(System.currentTimeMillis()));
        tvWaktuTrans.setText(new SimpleDateFormat("dd MMM yyyy, H:m").format(date));
    }

    public void kirimTrans(View view) {
        kirimSaldo(FirebaseAuth.getInstance().getCurrentUser().getUid(),saldoAwal, m1, m3, m4);
        finish();
    }

    public void batalTrans(View view) {
        finish();
    }

    public void writeTrans(String idPengirim, String namaPengirim, String idPenerima, String namaPenerima, String nominal){
        drTrans = FirebaseDatabase.getInstance().getReference(Database_Path_Transaksi);
        String time = String.valueOf(System.currentTimeMillis());
        String id = String.valueOf((9999999999999L+(-1 * Long.valueOf(time))));
        String idTrans = drTrans.push().getKey();
        Transaksi tr = new Transaksi(idTrans, idPengirim, namaPengirim, idPenerima, namaPenerima, nominal, time, id);
        drTrans.child(idTrans).setValue(tr);
    }

    public void kirimSaldo(String userName, String saldoAwal, String saldo, String namaPenerima, String idPengirim){
        if((!saldo.isEmpty())&&(!saldoAwal.isEmpty())) {
            String saldoBaru = String.valueOf(Integer.parseInt(saldoAwal)-Integer.parseInt(saldo));
            mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
            mDatabaseReference.child(userName + "/info").child("credit").setValue(saldoBaru);
            terimaSaldo(namaPenerima, saldo, idPengirim);
            try {
                Intent dompet = new Intent(this, DompetkuActivity.class);
                dompet.putExtra("status", "1");
                startActivity(dompet);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            try {
                Intent dompet = new Intent(this, DompetkuActivity.class);
                dompet.putExtra("status", "0");
                startActivity(dompet);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void terimaSaldo(final String namaPenerima, final String saldo, final String idPengirim){
        if(!saldo.isEmpty()) {
            if(namaPenerima.contains("Toko")) {
                mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path_Toko);
                Query searchQuery = mDatabaseReference.orderByChild("info/namaToko").equalTo(namaPenerima);
                searchQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        TokoUpload infoToko = new TokoUpload();
                        while((iterator.hasNext())){
                            infoToko = iterator.next().getValue(TokoUpload.class);
                        }
                        String saldoAwal = infoToko.getCredit();
                        final String saldoBaru = String.valueOf(Integer.parseInt(saldoAwal)+Integer.parseInt(saldo));
                        String idPenerima = infoToko.getzID();
                        mDatabaseReference.child(idPenerima + "/info").child("credit").setValue(saldoBaru);
                        try {
                            writeTrans(idPengirim, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),infoToko.getIdToko(), namaPenerima, saldo);
                            Intent dompet = new Intent(ResultTransActivity.this, DompetkuActivity.class);
                            dompet.putExtra("status", "1");
                            startActivity(dompet);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
            }else{
                mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path_User);
                Query searchQuery = mDatabaseReference.orderByChild("info/namaUser").equalTo(namaPenerima);
                searchQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        UserUpload infoUser = new UserUpload();
                        while((iterator.hasNext())){
                            infoUser = iterator.next().getValue(UserUpload.class);
                        }
                        String saldoAwal = infoUser.getCredit();
                        final String saldoBaru = String.valueOf(Integer.parseInt(saldoAwal)+Integer.parseInt(saldo));
                        String idPenerima = infoUser.getzID();
                        mDatabaseReference.child(idPenerima + "/info").child("credit").setValue(saldoBaru);
                        try {
                            writeTrans(idPengirim, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),infoUser.getIdUser(), namaPenerima, saldo);
                            Intent dompet = new Intent(ResultTransActivity.this, DompetkuActivity.class);
                            dompet.putExtra("status", "1");
                            dompet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dompet);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
        }else {
            try {
                Intent dompet = new Intent(this, DompetkuActivity.class);
                dompet.putExtra("status", "0");
                dompet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dompet);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void getInfoUser(String namaUser) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        Query searchQuery = mDatabaseReference.orderByChild("info/emailUser").equalTo(namaUser);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                UserUpload infoUser = new UserUpload();
                while ((iterator.hasNext())) {
                    infoUser = iterator.next().getValue(UserUpload.class);
                }
                saldoAwal = infoUser.getCredit();
                idPengirim = infoUser.getIdUser();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                UserUpload infoUser = new UserUpload();
                while ((iterator.hasNext())) {
                    infoUser = iterator.next().getValue(UserUpload.class);
                }
                saldoAwal = infoUser.getCredit();
                idPengirim = infoUser.getIdUser();
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
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                TokoUpload infoToko = new TokoUpload();
                while((iterator.hasNext())){
                    infoToko = iterator.next().getValue(TokoUpload.class);
                }
                saldoAwal = infoToko.getCredit();
                idPengirim = infoToko.getIdToko();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                TokoUpload infoToko = new TokoUpload();
                while((iterator.hasNext())){
                    infoToko = iterator.next().getValue(TokoUpload.class);
                }
                saldoAwal = infoToko.getCredit();
                idPengirim = infoToko.getIdToko();
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
}