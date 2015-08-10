package com.gae.entity;

public class ShopItem {
	
	public String getSCAVG() {
		return SCAVG;
	}

	public void setSCAVG(String sCAVG) {
		SCAVG = sCAVG;
	}

	public String ICO1;
	public String DISTAN;
	public String SPTYP;//JD
	public String CURPY;//22.5285
	public String CURPX;//114.064
	public String TEL;
	public String PRAVG;//人均
	public String TITLE;
	public String ADDR;
	public String ID;
	public String VID;
	public String MCLSN;
	public String WPRICE;
	public String WNUM;
	public String MB;
	public String SCAVG;


	public ShopItem(String iCO1, String dISTAN, String sPTYP, String cURPY,
			String cURPX, String tEL, String pRAVG, String tITLE, String aDDR,
			String iD, String vID, String mCLSN, String wPRICE, String wNUM,
			String mB, String sCAVG) {
		super();
		ICO1 = iCO1;
		DISTAN = dISTAN;
		SPTYP = sPTYP;
		CURPY = cURPY;
		CURPX = cURPX;
		TEL = tEL;
		PRAVG = pRAVG;
		TITLE = tITLE;
		ADDR = aDDR;
		ID = iD;
		VID = vID;
		MCLSN = mCLSN;
		WPRICE = wPRICE;
		WNUM = wNUM;
		MB = mB;
		SCAVG = sCAVG;
	}

	public ShopItem() {
		super();
	}
	
	

	public String getICO1() {
		return ICO1;
	}
	public void setICO1(String iCO1) {
		ICO1 = iCO1;
	}
	public String getDISTAN() {
		return DISTAN;
	}
	public void setDISTAN(String dISTAN) {
		DISTAN = dISTAN;
	}
	public String getSPTYP() {
		return SPTYP;
	}
	public void setSPTYP(String sPTYP) {
		SPTYP = sPTYP;
	}
	public String getCURPY() {
		return CURPY;
	}
	public void setCURPY(String cURPY) {
		CURPY = cURPY;
	}
	public String getCURPX() {
		return CURPX;
	}
	public void setCURPX(String cURPX) {
		CURPX = cURPX;
	}
	public String getTEL() {
		return TEL;
	}
	public void setTEL(String tEL) {
		TEL = tEL;
	}
	public String getPRAVG() {
		return PRAVG;
	}
	public void setPRAVG(String pRAVG) {
		PRAVG = pRAVG;
	}
	public String getTITLE() {
		return TITLE;
	}
	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}
	public String getADDR() {
		return ADDR;
	}
	public void setADDR(String aDDR) {
		ADDR = aDDR;
	}
	public String getID() {
		return ID;
	}
	public void setID(String vID) {
		ID = vID;
	}
	
	public String getVID() {
		return VID;
	}

	public void setVID(String vID) {
		VID = vID;
	}

	public String getMCLSN() {
		return MCLSN;
	}
	public void setMCLSN(String mCLSN) {
		MCLSN = mCLSN;
	}

	public String getWPRICE() {
		return WPRICE;
	}

	public void setWPRICE(String wPRICE) {
		WPRICE = wPRICE;
	}

	public String getWNUM() {
		return WNUM;
	}

	public void setWNUM(String wNUM) {
		WNUM = wNUM;
	}

	public String getMB() {
		return MB;
	}

	public void setMB(String mB) {
		MB = mB;
	}
	
	
	
}
