package com.gae.entity;

public class FoodItem {
	public String UID;
	public String ICO1; //图片路径
	public String SCALL;//评价
	public String CLSN; //1级名称
	public String MCLSN;//2级名称
	public String RECMD;//推荐
	public String PRICE;//原价
	public String DPRICE; 
	public String GPRICE;
	public String LKNUM;
	public String PID;  //菜品id
	public String PNAME;//菜名
	public String VNAME;//餐馆名称
	public String VID;  //餐馆id
	public String memo = "";//备注信息
	public String DISC; //折扣
	public String GDISC;
	public String ISVAL;
	public String MODE;
	public String DIST;
	public boolean STATE;
	
	
	public FoodItem(String uID, String iCO1, String sCALL, String cLSN,
			String mCLSN, String rECMD, String pRICE,String dPRICE,String gPRICE,String gLKNUM, String pID, String pNAME,
			String vNAME, String vID, String memo, String dISC,
			String gDISC,String iSVAL,String mODE,String dIST) {
		super();
		UID = uID;
		ICO1 = iCO1;
		SCALL = sCALL;
		CLSN = cLSN;
		MCLSN = mCLSN;
		RECMD = rECMD;
		PRICE = pRICE;
		DPRICE = dPRICE;
		GPRICE = gPRICE;
		PID = pID;
		LKNUM=gLKNUM;
		PNAME = pNAME;
		VNAME = vNAME;
		VID = vID;
		MODE = mODE;
		this.memo = memo;
		DISC = dISC;
		GDISC = gDISC;
		ISVAL = iSVAL;
		DIST=dIST;
	}
	public FoodItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public boolean isSTATE() {
		return STATE;
	}
	public void setSTATE(boolean sTATE) {
		STATE = sTATE;
	}
	public String getLKNUM() {
		return LKNUM;
	}
	public void setLKNUM(String lKNUM) {
		LKNUM = lKNUM;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getDISC() {
		return DISC;
	}
	public void setDISC(String dISC) {
		DISC = dISC;
	}
	public String getGDISC() {
		return GDISC;
	}
	public void setGDISC(String gDISC) {
		GDISC = gDISC;
	}
	public String getUID() {
		return UID;
	}
	public void setUID(String iD) {
		UID = iD;
	}
	public String getICO1() {
		return ICO1;
	}
	public void setICO1(String iCO1) {
		ICO1 = iCO1;
	}
	public String getSCALL() {
		return SCALL;
	}
	public void setSCALL(String sCALL) {
		SCALL = sCALL;
	}
	public String getCLSN() {
		return CLSN;
	}
	public void setCLSN(String cLSN) {
		CLSN = cLSN;
	}
	public String getMCLSN() {
		return MCLSN;
	}
	public void setMCLSN(String mCLSN) {
		MCLSN = mCLSN;
	}
	public String getRECMD() {
		return RECMD;
	}
	public void setRECMD(String rECMD) {
		RECMD = rECMD;
	}
	public String getPRICE() {
		return PRICE;
	}
	public void setPRICE(String pRICE) {
		PRICE = pRICE;
	}
	public String getPID() {
		return PID;
	}
	public void setPID(String pID) {
		PID = pID;
	}
	public String getPNAME() {
		return PNAME;
	}
	public void setPNAME(String pNAME) {
		PNAME = pNAME;
	}
	public String getVNAME() {
		return VNAME;
	}
	public void setVNAME(String vNAME) {
		VNAME = vNAME;
	}
	public String getVID() {
		return VID;
	}
	public void setVID(String vID) {
		VID = vID;
	}
	public String getISVAL() {
		return ISVAL;
	}
	public void setISVAL(String iSVAL) {
		ISVAL = iSVAL;
	}
	public String getDPRICE() {
		return DPRICE;
	}
	public void setDPRICE(String dPRICE) {
		DPRICE = dPRICE;
	}
	public String getGPRICE() {
		return GPRICE;
	}
	public void setGPRICE(String gPRICE) {
		GPRICE = gPRICE;
	}
	public String getMODE() {
		return MODE;
	}
	public void setMODE(String mODE) {
		MODE = mODE;
	}
	public String getDIST() {
		return DIST;
	}
	public void setDIST(String dIST) {
		DIST = dIST;
	}
	
	
}
