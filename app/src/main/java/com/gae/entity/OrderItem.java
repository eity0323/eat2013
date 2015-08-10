package com.gae.entity;

import java.util.List;

/*
 * author:eity
 * version:2013-3-1
 * description:订单实体
 * */
public class OrderItem {
	private String id;					//流水号
	private String ctime;				//下单时间
	private String state;				//订单状态
	private String vname;				//店名
	private String unm;
	private String total;
	private String pname;
	private String numb;
	private String price;
//	private List<OrderR1Item> list; 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getVname() {
		return vname;
	}
	public void setVname(String vname) {
		this.vname = vname;
	}
	public String getUnm() {
		return unm;
	}
	public void setUnm(String unm) {
		this.unm = unm;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getNumb() {
		return numb;
	}
	public void setNumb(String numb) {
		this.numb = numb;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
//	public List<OrderR1Item> getList() {
//		return list;
//	}
//	public void setList(List<OrderR1Item> list) {
//		this.list = list;
//	}
	
}
