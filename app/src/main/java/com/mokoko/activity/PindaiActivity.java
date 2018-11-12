package com.mokoko.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.zxing.Result;
import com.mokoko.R;
import com.mokoko.model.TokoUpload;
import com.mokoko.model.Transaksi;
import com.mokoko.model.UserUpload;

import java.util.Iterator;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class PindaiActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String Database_Path = "";
    private String Database_Path_Toko = "bakos_db/toko";
    private String Database_Path_User = "bakos_db/user";
    private String Database_Path_Transaksi = "bakos_db/transaksi";
    private DatabaseReference mDatabaseReference, drTrans;
    String saldoAwal = "";
    String idPengirim = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        setContentView(mScannerView);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        String[] hasil = dekripsi(rawResult.getText()).split(":");
        if(Integer.parseInt(hasil[0].replaceAll("\\D+","")) <= Integer.parseInt(saldoAwal.replaceAll("\\D+",""))) {
            showDialog(hasil[0], hasil[1], hasil[2], idPengirim);
        }else{
            showDialogError("Saldo kamu tidak cukup.");
        }
        mScannerView.resumeCameraPreview(this);
    }

    public void showDialog(final String m1, final String m2, final String m3, final String m4){
        mScannerView.stopCamera();
        mScannerView.stopCameraPreview();

        Intent dompet = new Intent(PindaiActivity.this, ResultTransActivity.class);
        dompet.putExtra("saldoAwal", saldoAwal);
        dompet.putExtra("m1", m1);
        dompet.putExtra("m2", m2);
        dompet.putExtra("m3", m3);
        dompet.putExtra("m4", m4);
        startActivity(dompet);
    }

    public void showDialogError(String m1){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        alertDialogBuilder
                .setMessage(m1)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
        pesan.setTextSize(15);

        Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        b.setTextColor(getResources().getColor(R.color.colorPrimary));
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

    public void kirimSaldo(String userName, String saldoAwal, String saldo, String namaPenerima, String idPengirim){
        if((!saldo.isEmpty())&&(!saldoAwal.isEmpty())) {
            String saldoBaru = String.valueOf(Integer.parseInt(saldoAwal)-Integer.parseInt(saldo));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Proses ...");
            progressDialog.show();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
            mDatabaseReference.child(userName + "/info").child("credit").setValue(saldoBaru);
            terimaSaldo(namaPenerima, saldo, idPengirim);
            progressDialog.dismiss();
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
                            Intent dompet = new Intent(PindaiActivity.this, DompetkuActivity.class);
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
                            Intent dompet = new Intent(PindaiActivity.this, DompetkuActivity.class);
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

    public void writeTrans(String idPengirim, String namaPengirim, String idPenerima, String namaPenerima, String nominal){
        drTrans = FirebaseDatabase.getInstance().getReference(Database_Path_Transaksi);
        String time = String.valueOf(System.currentTimeMillis());
        String id = String.valueOf((9999999999999L+(-1 * Long.valueOf(time))));
        String idTrans = drTrans.push().getKey();
        Transaksi tr = new Transaksi(idTrans, idPengirim, namaPengirim, idPenerima, namaPenerima, nominal, time, id);
        drTrans.child(idTrans).setValue(tr);
    }

    public static String dekripsi(String value) {

        value = new String(Base64.decode(value, Base64.DEFAULT));

        char[] values = value.toCharArray();
        for (int i = 0; i < values.length; i++) {
            char letter = values[i];

            if (letter >= 'a' && letter <= 'z') {

                if (letter > 'm') {
                    letter -= 13;
                } else {
                    letter += 13;
                }
            } else if (letter >= 'A' && letter <= 'Z') {
                if (letter > 'M') {
                    letter -= 13;
                } else {
                    letter += 13;
                }
            }
            values[i] = letter;
        }
        return new String(values);
    }
}