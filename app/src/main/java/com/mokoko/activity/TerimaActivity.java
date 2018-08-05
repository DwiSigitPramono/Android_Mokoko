package com.mokoko.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mokoko.adapter.NumberTextWatcherForThousand;
import com.mokoko.R;
import com.mokoko.model.TokoUpload;
import com.mokoko.model.UserUpload;

import java.util.Iterator;

public class TerimaActivity extends AppCompatActivity {

    private static final String Database_Path = "bakos_db/toko";
    private static final String Database_Path_User = "bakos_db/user";
    private DatabaseReference mDatabaseReference;
    TextView tvNamaPenerima,tvIdPenerima;
    ImageView ivQr;
    EditText etNominal;
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terima);
        ivQr = (ImageView) findViewById(R.id.ivQr);
        etNominal = (EditText)findViewById(R.id.etNominal);
        tvIdPenerima = (TextView)findViewById(R.id.tvIdTerima);
        tvNamaPenerima = (TextView)findViewById(R.id.tvNamaPenerima);

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

        etNominal.addTextChangedListener(new NumberTextWatcherForThousand(etNominal));
        etNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivQr.setImageBitmap(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvNamaPenerima.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }

    public static String ekripsi(String value) {

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
        return new String(Base64.encode(new String(values).getBytes(), Base64.DEFAULT));
    }

    public void terimaSaldo(View view) {
        String text2Qr = etNominal.getText().toString().replaceAll("\\D+","") + ":" + id + ":" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+":"+FirebaseAuth.getInstance().getCurrentUser().getUid();
        text2Qr = ekripsi(text2Qr);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    private void getInfoUser(String namaUser) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path_User);
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
                id = infoUser.getIdUser();
                tvIdPenerima.setText(id);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                UserUpload infoUser = new UserUpload();
                while ((iterator.hasNext())) {
                    infoUser = iterator.next().getValue(UserUpload.class);
                }
                id = infoUser.getIdUser();
                tvIdPenerima.setText(id);
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
                id = infoToko.getIdToko();
                tvIdPenerima.setText(id);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                TokoUpload infoToko = new TokoUpload();
                while((iterator.hasNext())){
                    infoToko = iterator.next().getValue(TokoUpload.class);
                }
                id = infoToko.getIdToko();
                tvIdPenerima.setText(id);
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