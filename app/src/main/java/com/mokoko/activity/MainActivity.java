package com.mokoko.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mokoko.fragment.DiskonFragment;
import com.mokoko.adapter.FragmentDrawer;
import com.mokoko.fragment.HotFragment;
import com.mokoko.R;
import com.mokoko.model.TokoUpload;
import com.mokoko.model.UserUpload;
import com.mokoko.fragment.UtamaFragment;
import com.mokoko.adapter.ViewPagerTabAdapter;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.mokoko.fragment.UtamaFragment.viewPager;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private CircleImageView civProfil;
    private static final String Database_Path = "bakos_db/toko";
    private static final String Database_Path_User = "bakos_db/user";
    private DatabaseReference mDatabaseReference;
    private String verified = "0";
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        civProfil = (CircleImageView)findViewById(R.id.userProf);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        ViewPager vpTabs = (ViewPager) findViewById(R.id.vpTab);
        setupViewPager(vpTabs);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(vpTabs);

        SearchView svCari = (SearchView) findViewById(R.id.svCari);

        final Intent search = new Intent(this, SearchActivity.class);
        svCari.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                startActivity(search);
            }
        });

        FragmentDrawer drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        final TextView userEmail = (TextView)findViewById(R.id.userEmail);
        final TextView userJenis = (TextView)findViewById(R.id.userJenis);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                userEmail.setText("Hai, " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                if(userEmail.getText().toString().contains("Toko")) {
                    getInfoToko(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    userJenis.setText("Toko");
                }else{
                    getInfoUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    userJenis.setText("User");
                }
                Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                        .into(civProfil);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }else{
            userEmail.setText("");
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new MainActivity.MyTimerTask(), 2000, 4000);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        adapter.addFragment(new UtamaFragment(), "Beranda");
        adapter.addFragment(new HotFragment(), "Hot Promo");
        adapter.addFragment(new DiskonFragment(), "Diskon Besar");
        viewPager.setAdapter(adapter);
    }

    public void semuaProduk(View view) {
        Intent semua = new Intent(this, AllProductResultActivity.class);
        startActivity(semua);
    }

    public void makanan(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query","Aneka Jenis Makanan");
        semua.putExtra("id","kategori");
        startActivity(semua);
    }

    public void sembako(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query","Kebutuhan Dapur");
        semua.putExtra("id","kategori");
        startActivity(semua);
    }

    public void rumahTangga(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query","Kebutuhan Rumah Tangga");
        semua.putExtra("id","kategori");
        startActivity(semua);
    }

    public void produkAnak(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query","Perlengkapan Anak");
        semua.putExtra("id","kategori");
        startActivity(semua);
    }

    public void alatTulis(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query","Alat Tulis Kantor");
        semua.putExtra("id","kategori");
        startActivity(semua);
    }

    public void potHarga(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query","Diskon");
        semua.putExtra("id","jenisPromo");
        startActivity(semua);
    }

    public void proSama(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query","Gratis Produk Sama");
        semua.putExtra("id","jenisPromo");
        startActivity(semua);
    }

    public void proBeda(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query","Gratis Produk Lain");
        semua.putExtra("id","jenisPromo");
        startActivity(semua);
    }

    public void lainnya(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query","Kategori Lain");
        semua.putExtra("id","kategori");
        startActivity(semua);
    }

    public void minum(View view) {
        Intent semua = new Intent(this, KategoriActivity.class);
        semua.putExtra("query","Aneka Jenis Minuman");
        semua.putExtra("id","kategori");
        startActivity(semua);
    }

    public void daftar(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("User", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(MainActivity.this, SignupUserActivity.class));
                    }
                })
                .setNegativeButton("Toko", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(MainActivity.this, SignupActivity.class));
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
        pesan.setTextSize(15);

        Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        b.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            getMenuInflater().inflate(R.menu.menu_main_login, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                return true;
            case R.id.action_dompet:
                startActivity(new Intent(this, DompetkuActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        alertDialogBuilder
                .setTitle("Keluar dari Mokoko")
                .setMessage("Apakah kamu ingin keluar?")
                .setCancelable(true)
                .setPositiveButton("Keluar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Batal",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        
        TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
        pesan.setTextSize(15);

        Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        b.setTextColor(getResources().getColor(R.color.colorPrimary));

        Button c = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        c.setTextColor(getResources().getColor(R.color.colorHitam));
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }


    private void displayView(int position) {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("Toko")) {
                switch (position) {
                    case 0:
                        if (verified.contains("1")) {
                            startActivity(new Intent(MainActivity.this, UploadActivity.class));
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                            alertDialogBuilder
                                    .setTitle("Verifikasi Data")
                                    .setMessage("Saat ini tim kami sedang melakukan pengecekan data kamu. Mohon tunggu maksimal 1x24 jam.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            final AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                            TextView pesan = (TextView) alertDialog.findViewById(android.R.id.message);
                            pesan.setTextSize(15);

                            Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            b.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, KatalogActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, DompetkuActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, AkunMenuActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, HelpActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, TentangActivity.class));
                        break;
                    case 6:
                        logout();
                        break;
                    default:
                        break;
                }
            }else{
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, NearestTokoActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, DaftarTokoActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, DompetkuActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, AkunMenuActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, HelpActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, TentangActivity.class));
                        break;
                    case 6:
                        logout();
                        break;
                    default:
                        break;
                }
            }
        }else{
            switch (position) {
                case 0:
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(MainActivity.this, NearestTokoActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(MainActivity.this, DaftarTokoActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(MainActivity.this, HelpActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(MainActivity.this, TentangActivity.class));
                    break;
                default:
                    break;
            }
        }
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
                verified = infoToko.getVerified();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                TokoUpload infoToko = new TokoUpload();
                while((iterator.hasNext())){
                    infoToko = iterator.next().getValue(TokoUpload.class);
                }
                verified = infoToko.getVerified();
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

    private void getInfoUser(String namaUser){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Database_Path_User);
        Query searchQuery = mDatabaseReference.orderByChild("info/emailUser").equalTo(namaUser);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                UserUpload infoUser = new UserUpload();
                while((iterator.hasNext())){
                    infoUser = iterator.next().getValue(UserUpload.class);
                }
                verified = infoUser.getVerified();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                UserUpload infoUser = new UserUpload();
                while((iterator.hasNext())){
                    infoUser = iterator.next().getValue(UserUpload.class);
                }
                verified = infoUser.getVerified();
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

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager.getCurrentItem() == 0){
                        viewPager.setCurrentItem(1);
                    } else if(viewPager.getCurrentItem() == 1){
                        viewPager.setCurrentItem(2);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

}