package com.gae.entity;

public class Info {

    public boolean is_Checked;
	private String number;
	private String name;
	public Info (boolean b, String n, String phoneName)
	{
		this.is_Checked = b;
		this.number = n;
		this.name=phoneName;
	}
	public boolean isIs_Checked() {
		return is_Checked;
	}
	public void setIs_Checked(boolean is_Checked) {
		this.is_Checked = is_Checked;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
