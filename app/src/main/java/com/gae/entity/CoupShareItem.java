package com.gae.entity;

public class CoupShareItem {
	private String CONTEXT;   //内容
	private String TITLE;     //标题
	private String TYPE;      //类型
	private String UNM;       //发布人
	private String GOOD;      
	private String BAD;
	private String id;	
	public CoupShareItem() {
		super();
		// TODO Auto-generated constructor stub
	}


	public CoupShareItem(String cONTEXT, String tITLE, String tYPE, String uNM,
			String gOOD, String bAD,String iD) {
		super();
		CONTEXT = cONTEXT;
		TITLE = tITLE;
		TYPE = tYPE;
		UNM = uNM;
		GOOD = gOOD;
		BAD = bAD;
		id = iD;
	}	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getGOOD() {
		return GOOD;
	}


	public void setGOOD(String gOOD) {
		GOOD = gOOD;
	}


	public String getBAD() {
		return BAD;
	}


	public void setBAD(String bAD) {
		BAD = bAD;
	}

    public String getCONTEXT() {
		return CONTEXT;
	}
	public void setCONTEXT(String cONTEXT) {
		CONTEXT = cONTEXT;
	}
	public String getTITLE() {
		return TITLE;
	}
	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getUNM() {
		return UNM;
	}
	public void setUNM(String uNM) {
		UNM = uNM;
	}

}
