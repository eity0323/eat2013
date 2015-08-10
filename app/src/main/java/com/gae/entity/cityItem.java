package com.gae.entity;

import java.util.ArrayList;

import android.R.array;

public class cityItem {
	private String coid;
	private String conm;
	private String type;
	private String code;
	private String ctime;
	private String memo;
	private String ico;
	public cityItem(String coid, String conm, String type, String code,
			String ctime, String memo,String ico) {
		super();
		this.coid = coid;
		this.conm = conm;
		this.type = type;
		this.code = code;
		this.ctime = ctime;
		this.memo = memo;
		this.ico = ico;
	}
	
	public cityItem() {
		super();
	}
	

	public String getCoid() {
		return coid;
	}
	public void setCoid(String coid) {
		this.coid = coid;
	}
	public String getConm() {
		return conm;
	}
	public void setConm(String conm) {
		this.conm = conm;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getIco() {
		return ico;
	}

	public void setIco(String ico) {
		this.ico = ico;
	}
	
}
