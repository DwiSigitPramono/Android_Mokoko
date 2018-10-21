package com.mokoko.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mokoko.R;
import com.mokoko.adapter.RecyclerViewAdapterTransaksi;
import com.mokoko.model.Transaksi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TransaksiActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private final String Database_Path = "bakos_db/transaksi";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter ;
    private final List<Transaksi> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);
        adapter = new RecyclerViewAdapterTransaksi(getApplicationContext(), list);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewTrans);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TransaksiActivity.this));
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        findPengirim(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        findPenerima(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }

    private void findPengirim(String name){
        Query katalogQuery = databaseReference.orderByChild("namaPengirim").equalTo(name);
        katalogQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                int i = 0;
                String[] transaksi = new String[8];
                while((iterator.hasNext())){
                    transaksi[i] = iterator.next().getValue().toString();
                    i++;
                }
                Transaksi transInfo = new Transaksi();
                transInfo.setIdPenerima(transaksi[0]);
                transInfo.setIdPengirim(transaksi[1]);
                transInfo.setIdTrans(transaksi[2]);
                transInfo.setNamaPenerima(transaksi[3]);
                transInfo.setNamaPengirim(transaksi[4]);
                transInfo.setNominal(transaksi[5]);
                transInfo.setTimeStamp(transaksi[6]);
                transInfo.setzID(transaksi[7]);
                list.add(transInfo);
                adapter = new RecyclerViewAdapterTransaksi(getApplicationContext(), list);
                try {
                    recyclerView.setAdapter(adapter);
                    Collections.sort(list, new Comparator<Transaksi>() {
                        @Override
                        public int compare(Transaksi o1, Transaksi o2) {
                            Long id1 = Long.valueOf(o1.getzID());
                            Long id2 = Long.valueOf(o2.getzID());
                            return id1.compareTo(id2);
                        }
                    });
                    adapter.notifyDataSetChanged();
                }catch (NullPointerException e){
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
    private void findPenerima(String name){
        Query katalogQuery = databaseReference.orderByChild("namaPenerima").equalTo(name);
        katalogQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                int i = 0;
                String[] transaksi = new String[8];
                while((iterator.hasNext())){
                    transaksi[i] = iterator.next().getValue().toString();
                    i++;
                }
                Transaksi transInfo = new Transaksi();
                transInfo.setIdPenerima(transaksi[0]);
                transInfo.setIdPengirim(transaksi[1]);
                transInfo.setIdTrans(transaksi[2]);
                transInfo.setNamaPenerima(transaksi[3]);
                transInfo.setNamaPengirim(transaksi[4]);
                transInfo.setNominal(transaksi[5]);
                transInfo.setTimeStamp(transaksi[6]);
                transInfo.setzID(transaksi[7]);
                list.add(transInfo);
                adapter = new RecyclerViewAdapterTransaksi(getApplicationContext(), list);
                try {
                    recyclerView.setAdapter(adapter);
                    Collections.sort(list, new Comparator<Transaksi>() {
                        @Override
                        public int compare(Transaksi o1, Transaksi o2) {
                            Long id1 = Long.valueOf(o1.getzID());
                            Long id2 = Long.valueOf(o2.getzID());
                            return id1.compareTo(id2);
                        }
                    });
                    adapter.notifyDataSetChanged();
                }catch (NullPointerException e){
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
}
