package com.gae.entity;

public class GourmetMaster {
	private String NAME;
	private String ICO;
	private String GRADE;
	private String RCNUM;
	private String RINUM;
	
	
	public GourmetMaster(String nAME, String iCO, String gRADE, String rCNUM,
			String rINUM) {
		super();
		NAME = nAME;
		ICO = iCO;
		GRADE = gRADE;
		RCNUM = rCNUM;
		RINUM = rINUM;
	}
	public GourmetMaster() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	public String getICO() {
		return ICO;
	}
	public void setICO(String iCO) {
		ICO = iCO;
	}
	public String getGRADE() {
		return GRADE;
	}
	public void setGRADE(String gRADE) {
		GRADE = gRADE;
	}
	public String getRCNUM() {
		return RCNUM;
	}
	public void setRCNUM(String rCNUM) {
		RCNUM = rCNUM;
	}
	public String getRINUM() {
		return RINUM;
	}
	public void setRINUM(String rINUM) {
		RINUM = rINUM;
	}

	
	
}
