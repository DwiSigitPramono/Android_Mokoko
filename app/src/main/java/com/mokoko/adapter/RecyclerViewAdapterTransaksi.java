package com.mokoko.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.mokoko.R;
import com.mokoko.activity.ProdukDetilActivity;
import com.mokoko.model.Transaksi;
import com.mokoko.model.Transaksi;

import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class RecyclerViewAdapterTransaksi extends RecyclerView.Adapter<RecyclerViewAdapterTransaksi.ViewHolder> {

    private Context context;
    private List<Transaksi> TransaksiList;

    public RecyclerViewAdapterTransaksi(Context context, List<Transaksi> TempList) {

        this.TransaksiList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items_transaksi, parent, false);

        return new ViewHolder(view);
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

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Transaksi transInfo = TransaksiList.get(position);
        if(transInfo.getNamaPenerima().contains(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
            holder.tvJenis.setText("Terima");
            holder.tvPihak.setText(transInfo.getNamaPengirim());
            holder.tvPihakId.setText("["+transInfo.getIdPengirim()+"]");
            holder.tvNominal.setText("Rp."+getMoney(transInfo.getNominal()));
        }else{
            holder.tvJenis.setText("Kirim");
            holder.tvPihak.setText(transInfo.getNamaPenerima());
            holder.tvPihakId.setText("["+transInfo.getIdPenerima()+"]");
            holder.tvNominal.setText("-Rp."+getMoney(transInfo.getNominal()));
        }
        Date date2 = new Date();
        date2.setTime(Long.parseLong(transInfo.getTimeStamp()));
        String formattedDate = new SimpleDateFormat("dd MMM yyyy, H:m").format(date2);
        holder.tvTime.setText(formattedDate);
        holder.tvIdTans.setText("#"+transInfo.getIdTrans().substring(1));
    }

    @Override
    public int getItemCount() {

        return TransaksiList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvJenis, tvNominal, tvPihak, tvPihakId, tvTime, tvIdTans;

        public ViewHolder(View itemView) {
            super(itemView);

            tvJenis = (TextView)itemView.findViewById(R.id.tvJenisTransaksi);
            tvNominal = (TextView)itemView.findViewById(R.id.tvNominal);
            tvPihak = (TextView)itemView.findViewById(R.id.tvPihak);
            tvPihakId = (TextView)itemView.findViewById(R.id.tvPihakId);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
            tvIdTans = (TextView)itemView.findViewById(R.id.tvIdTrans);
        }
    }
}
