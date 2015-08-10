package com.gae.entity;

public class FoodTypeItem {
	private String CNAME;
    private String ID;
	public FoodTypeItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FoodTypeItem(String cNAME) {
		super();
		CNAME = cNAME;
	}

	public String getCNAME() {
		return CNAME;
	}

	public void setCNAME(String cNAME) {
		CNAME = cNAME;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

}
