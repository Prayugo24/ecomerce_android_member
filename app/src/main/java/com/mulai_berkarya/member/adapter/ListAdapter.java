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

public class ListAdapter extends ArrayAdapter<String>{
	private final Context context;
	private final String[] vnamabrg;
	private final String[] vharga;
	private final String[] vdesk;
	private final String[] vgambar;
	
	public ListAdapter(Context context, String[] vnamabrg, 
			String[] vharga, String[] vdesk, String[] vgambar){
		super(context, R.layout.barang_thok, vnamabrg);
		this.context = context;
		this.vnamabrg = vnamabrg;
		this.vharga = vharga;
		this.vdesk = vdesk;
		this.vgambar = vgambar;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.barang_thok, parent, false);
		TextView namabrg = (TextView)rowView.findViewById(R.id.namabrgnya);
		TextView harga = (TextView)rowView.findViewById(R.id.harganya);
		TextView deskripsi = (TextView)rowView.findViewById(R.id.deskripsinya);
		
		namabrg.setText(vnamabrg[position]);
		harga.setText(vharga[position]);
		deskripsi.setText(vdesk[position]);

		Glide
				.with(context)
//                .load(URL_GAMBAR + "/" + barang.getgambar())
				.load(vgambar[position])
				.asBitmap()
				.fitCenter()
				.error(R.drawable.ic_bayar)
//                .into(imgBarang);
				.into((ImageView)rowView.findViewById(R.id.gambar_barang));


		return rowView;
	}
	

}
