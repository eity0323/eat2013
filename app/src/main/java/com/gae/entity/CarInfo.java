package com.gae.entity;
/**
 * author:wing
 * version:2012-03-1 14:06:00
 * describe:购物车实体类
 */
public class CarInfo {
	public String id;      //唯一id
	public String uid;     //用户id
	public String vid;     //餐馆id
	public String pid;     //菜品id
	public String pname;   //菜品名称
	public String price;   //菜品价格
	public String vnm;     //人数
	public String ctime;   //下单时间
	public int snum;       //菜品数量
	public String discv;   //菜品折扣
	public String gdiscv;  //店长折扣
	public String state;   //状态
	public String ico;     //菜品图片
	public FoodItem carInfo ;
	public CarInfo(String id, String uid, String vid, String pid, String pname,
			String price, String vnm, String ctime, int snum, String discv,
			String gdiscv, String state, String ico) {
		super();
		this.id = id;
		this.uid = uid;
		this.vid = vid;
		this.pid = pid;
		this.pname = pname;
		this.price = price;
		this.vnm = vnm;
		this.ctime = ctime;
		this.snum = snum;
		this.discv = discv;
		this.gdiscv = gdiscv;
		this.state = state;
		this.ico = ico;
	}
	public CarInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getIco() {
		return ico;
	}
	public void setIco(String ico) {
		this.ico = ico;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getVnm() {
		return vnm;
	}
	public void setVnm(String vnm) {
		this.vnm = vnm;
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
	public int getSnum() {
		return snum;
	}
	public void setSnum(int snum) {
		this.snum = snum;
	}
	public String getDiscv() {
		return discv;
	}
	public void setDiscv(String discv) {
		this.discv = discv;
	}
	public String getGdiscv() {
		return gdiscv;
	}
	public void setGdiscv(String gdiscv) {
		this.gdiscv = gdiscv;
	}
	public FoodItem getCarInfo() {
		return carInfo;
	}
	public void setCarInfo(FoodItem carInfo) {
		this.carInfo = carInfo;
	}
	
}
