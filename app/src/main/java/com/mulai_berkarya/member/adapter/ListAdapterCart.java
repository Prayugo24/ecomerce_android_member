package com.mulai_berkarya.member.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mulai_berkarya.member.cimol.R;

public class ListAdapterCart extends ArrayAdapter<String> {
    private final Context context;
    private final String[] vnamabrg;
    private final String[] vjumlah;
    private final String[] vqty;
    private final String[] vgambar;

    public ListAdapterCart(Context context, String[] vnamabrg,
                           String[] vjumlah, String[] vqty, String[] vgambar) {
        super(context, R.layout.barang_beli, vnamabrg);
        this.context = context;
        this.vnamabrg = vnamabrg;
        this.vjumlah = vjumlah;
        this.vqty = vqty;
        this.vgambar = vgambar;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.barang_beli, parent, false);
        TextView namabrg = (TextView) rowView.findViewById(R.id.textBarange);
        TextView jumlah = (TextView) rowView.findViewById(R.id.textHrg);
        TextView qty = (TextView) rowView.findViewById(R.id.textQty);

        namabrg.setText("Nama : "+vnamabrg[position]);
        jumlah.setText("Harga : Rp. "+vjumlah[position]);
        qty.setText("QTY : "+vqty[position]);

        Glide
                .with(context)
                .load(vgambar[position])
                .asBitmap()
                .fitCenter()
                .error(R.drawable.ic_cart)
                .into((ImageView) rowView.findViewById(R.id.imviBrg));

        return rowView;
    }

}
