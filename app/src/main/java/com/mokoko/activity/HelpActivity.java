package com.mokoko.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.mokoko.adapter.ExpandableListAdapter;
import com.mokoko.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("Apa itu Mokoko?");
        listDataHeader.add("Mengapa menggunakan Mokoko?");
        listDataHeader.add("Bagaimana cara melihat promo-promo di Mokoko ?");
        listDataHeader.add("Bagaimana cara memesan produk di Mokoko?");
        listDataHeader.add("Apakah Mokoko menerima layanan pesan antar?");
        listDataHeader.add("Apakah ada diskon khusus di Mokoko?");
        listDataHeader.add("Bagaimana cara mendaftar jadi mitra di Mokoko?");

        List<String> p1 = new ArrayList<String>();
        p1.add("Mokoko adalah aplikasi yang menyediakan berbagai promo produk sembako di toko swadaya sekitar kita.");

        List<String> p2 = new ArrayList<String>();
        p2.add("Karena menggunakan Mokoko, dapat memudahkan dalam memperoleh informasi promo produk sembako di sekitar kita dengan cepat.");

        List<String> p3 = new ArrayList<String>();
        p3.add("Pilih salah satu kategori produk sembako di menu Beranda, kamu dapat melihat berbagai promo sembako dari toko-toko swadaya");

        List<String> p4 = new ArrayList<String>();
        p4.add("Maaf, saat ini Mokoko belum menyediakan fitur pemesanan barang.");

        List<String> p5 = new ArrayList<String>();
        p5.add("Maaf, saat ini Mokoko belum menyediakan fitur pesan antar.");

        List<String> p6 = new ArrayList<String>();
        p6.add("Setiap promo yang ada di Mokoko sesuai dengan ketentuan dari masing-masing toko.");

        List<String> p7 = new ArrayList<String>();
        p7.add("Untuk mendaftar menjadi mitra di Mokoko, kamu dapat memilih menu Daftar sebagai Toko, kemudian " +
                "ikuti setiap instruksi yang tertera. Kalau sudah, tunggu tim kami untuk memverifikasi data kamu.");

        listDataChild.put(listDataHeader.get(0), p1);
        listDataChild.put(listDataHeader.get(1), p2);
        listDataChild.put(listDataHeader.get(2), p3);
        listDataChild.put(listDataHeader.get(3), p4);
        listDataChild.put(listDataHeader.get(4), p5);
        listDataChild.put(listDataHeader.get(5), p6);
        listDataChild.put(listDataHeader.get(6), p7);
    }
}