package com.mulai_berkarya.member.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mulai_berkarya.member.cimol.R;
import com.mulai_berkarya.member.entitas.Barang;
import com.mulai_berkarya.member.server.Config;

import java.util.ArrayList;

public class AdapterBarang extends ArrayAdapter<Barang> {
    Context mContext;
    ArrayList<Barang> list;

    int layout;
    String URL_GAMBAR = Config.URL_BASE+ "/Gambar";

    public AdapterBarang(Context context, int textViewResourceId, ArrayList<Barang> list_params) {
        super(context, textViewResourceId, list_params);
        this.mContext = context;
        this.layout = textViewResourceId;
        this.list = list_params;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent)
// ;
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(layout, null);

            holder = new ViewHolder();
            holder.nama = (TextView) convertView.findViewById(R.id.textBarange);
            holder.harga = (TextView) convertView.findViewById(R.id.textQty);

            holder.stok = (TextView) convertView.findViewById(R.id.barang_stok);
            holder.gambar = (ImageView) convertView.findViewById(R.id.barang_gambar);

            holder.persen = (TextView) convertView.findViewById(R.id.barang_persen);
            holder.diskon = (TextView) convertView.findViewById(R.id.barang_diskon);
            holder.terjual = (TextView) convertView.findViewById(R.id.jum_jual);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list.size() != 0) {
            Barang m = list.get(position);
            //holder.nama.setText("Nama  	 : " + m.getnama_barang());
            holder.nama.setText(m.getnama_barang());
            //holder.harga.setText("Harga 	 : Rp." + m.getharga());
            holder.harga.setText("Rp." + m.getharga());
            holder.stok.setText("Stok  	 : " + m.getStok());
            holder.terjual.setText("Terjual : " + m.getTerjual());

            if (null != m.getDiskonPersen() && !m.getDiskonPersen().equalsIgnoreCase("0")) {
                holder.persen.setVisibility(View.VISIBLE);
                holder.persen.setText(m.getDiskonPersen() + "%");
            } else {
                holder.persen.setVisibility(View.GONE);
            }

            if (null != m.getHargaDiskon() && !m.getHargaDiskon().equalsIgnoreCase("0")) {
                holder.diskon.setVisibility(View.VISIBLE);
                holder.harga.setTextColor(Color.parseColor("#000000"));
                holder.diskon.setText("Rp. " + m.getHargaDiskon());
                holder.harga.setPaintFlags(holder.harga.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.diskon.setVisibility(View.GONE);
                holder.harga.setPaintFlags(holder.harga.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                holder.harga.setTextColor(mContext.getResources().getColor(R.color.price_color));
            }

            Glide
                    .with(mContext)
                    .load(URL_GAMBAR + "/" + m.getgambar())
                    .fitCenter()
                    //.override(250, 250)
                    .crossFade()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.gambar);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView nama, harga, stok, diskon, persen, terjual;
        ImageView gambar;
    }
}
