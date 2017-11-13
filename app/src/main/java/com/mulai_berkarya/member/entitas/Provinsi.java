package com.mulai_berkarya.member.entitas;

public class Provinsi {
	private String id_prov;
	private String kode_prov;
	private String provinsi;
	private String kode_kab;
	private String kabupaten_kota;
	private String kec;
	
	public String getIdprov(){
		return id_prov;
	}
	
	public void setIdprov(String id_prov){
		this.id_prov = id_prov;
	}
	
	public String getKdprov(){
		return kode_prov;
	}
	
	public void setKdprov(String kode_prov){
		this.kode_prov = kode_prov;
	}
	
	public String getProv(){
		return provinsi;
	}
	
	public void setProv(String provinsi){
		this.provinsi = provinsi;
	}
	
	public String getKdkab(){
		return kode_kab;
	}
	
	public void setKdkab(String kode_kab){
		this.kode_kab = kode_kab;
	}
	
	public String getKabkot(){
		return kabupaten_kota;
	}
	
	public void setKabkot(String kabupaten_kota){
		this.kabupaten_kota = kabupaten_kota;
	}
	
	public String getKec(){
		return kec;
	}
	
	public void setKec(String kec){
		this.kec = kec;
	}

}
