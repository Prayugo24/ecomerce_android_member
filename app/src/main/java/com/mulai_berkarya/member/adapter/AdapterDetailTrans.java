package com.mulai_berkarya.member.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mulai_berkarya.member.cimol.R;
import com.mulai_berkarya.member.entitas.Detail;
import com.mulai_berkarya.member.server.Config;

import java.util.ArrayList;

public class AdapterDetailTrans extends ArrayAdapter<Detail> {
    Context mContext;
    ArrayList<Detail> list;

    int layout;
    String URL_GAMBAR = Config.URL_BASE + "/Gambar/";

    public AdapterDetailTrans(Context context,
                              int textViewResourceId,
                              ArrayList<Detail> list_params) {
        super(context, textViewResourceId, list_params);
        this.mContext = context;
        this.layout = textViewResourceId;
        this.list = list_params;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(layout, null);

            holder = new ViewHolder();
            holder.idtrans = (TextView) convertView.findViewById(R.id.textViewidtransaksi22);
            holder.tanggal = (TextView) convertView.findViewById(R.id.textviewtgl_trans22);
            holder.ongkir = (TextView) convertView.findViewById(R.id.textViewongkir22);
            holder.total = (TextView) convertView.findViewById(R.id.textViewttl22);
            holder.resi = (TextView) convertView.findViewById(R.id.textviResi22);
            holder.jumlah = (TextView) convertView.findViewById(R.id.textViewsubt22);
            holder.alamat = (TextView) convertView.findViewById(R.id.textViewAlamat2);
            holder.kec = (TextView) convertView.findViewById(R.id.textviewkec2);
            holder.kab = (TextView) convertView.findViewById(R.id.textViewkab2);
            holder.prov = (TextView) convertView.findViewById(R.id.textViewprof2);
            holder.kode = (TextView) convertView.findViewById(R.id.textViewkodepos2);
            holder.telp = (TextView) convertView.findViewById(R.id.textViewtlp2);
            holder.nama = (TextView) convertView.findViewById(R.id.textatasnama2);
            holder.kurir = (TextView) convertView.findViewById(R.id.textViewkurir);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Detail m = list.get(position);
        holder.idtrans.setText(m.getidtrans());
        holder.tanggal.setText(m.gettanggal());
        holder.ongkir.setText(m.getongkir());
        holder.total.setText(m.gettotal());
        holder.resi.setText(m.getresi());
        holder.jumlah.setText(m.getjumlah());
        holder.alamat.setText(m.getalamat());
        holder.kec.setText(m.getkec());
        holder.kab.setText(m.getkab());
        holder.prov.setText(m.getprov());
        holder.kode.setText(m.getkodepos());
        holder.telp.setText(m.gettelp());
        holder.nama.setText(m.getAtas_nama());
        holder.kurir.setText(m.getKurir());


        /*Glide
                .with(mContext)
                .load(URL_GAMBAR + "/" + m.getgambar())
                .fitCenter()
                //.override(250, 250)
                .crossFade()
                .error(R.drawable.ic_bayar)
                .into(holder.gambar);*/
        return convertView;
    }

    private class ViewHolder {
        TextView idtrans, tanggal, ongkir, total, resi, jumlah, alamat, kec, kab, prov, kode, telp, nama, namarek, norek, jmlrek, kurir;
        //ImageView gambar;
    }
}
