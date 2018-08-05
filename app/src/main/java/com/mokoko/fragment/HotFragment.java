package com.mokoko.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mokoko.R;
import com.mokoko.activity.UploadActivity;
import com.mokoko.adapter.RecyclerViewAdapterHot;
import com.mokoko.model.PromoUpload;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class HotFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private TextView tvHotPromo;

    private boolean refresh = false;
    private ProgressBar pb;
    private LinearLayout llPb;

    private final List<PromoUpload> list = new ArrayList<>();
    private DatabaseReference databaseReference;

    public HotFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_display_hot, container, false);

        rootView.getRootView().setBackgroundColor(Color.parseColor("#ecf1f4"));
        rootView.setTag(TAG);

        tvHotPromo = (TextView)rootView.findViewById(R.id.tvHotPromo);
        tvHotPromo.setVisibility(View.INVISIBLE);
        tvHotPromo.setText("Promo Produk Terbaru");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        pb = (ProgressBar)rootView.findViewById(R.id.pb);

        refresh = true;
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        databaseReference = FirebaseDatabase.getInstance().getReference(UploadActivity.Database_Path);

        databaseReference.orderByChild("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PromoUpload promoUpload = postSnapshot.getValue(PromoUpload.class);
                    long durasi = TimeUnit.DAYS.toMillis(Integer.parseInt(promoUpload.getDurasi()));
                    String time = promoUpload.getTimeStamp();
                    long dif = (Long.parseLong(time) + durasi) - System.currentTimeMillis();
                    if((dif > 0)&&(list.size()<7)) {
                        list.add(promoUpload);
                    }
                }

                if(refresh) {
                    adapter = new RecyclerViewAdapterHot(getContext(), list);
                    recyclerView.setAdapter(adapter);
                    tvHotPromo.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    refresh = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
