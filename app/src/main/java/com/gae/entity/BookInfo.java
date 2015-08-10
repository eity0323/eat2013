package com.gae.entity;

public class BookInfo {
	private String id;
	private String mb;
	private String unm;
	private String reqinfo;
	private String addr;
	private String reqtime;
	private String price;
	private String awnm;
	private String awstate;
	private String awtime;

	public String getAwtime() {
		return awtime;
	}

	public void setAwtime(String awtime) {
		this.awtime = awtime;
	}

	public String getAwstate() {
		return awstate;
	}

	public void setAwstate(String awstate) {
		this.awstate = awstate;
	}

	public String getAwnm() {
		return awnm;
	}

	public void setAwnm(String awnm) {
		this.awnm = awnm;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMb() {
		return mb;
	}

	public void setMb(String mb) {
		this.mb = mb;
	}

	public String getUnm() {
		return unm;
	}

	public void setUnm(String unm) {
		this.unm = unm;
	}

	public String getReqinfo() {
		return reqinfo;
	}

	public void setReqinfo(String reqinfo) {
		this.reqinfo = reqinfo;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getReqtime() {
		return reqtime;
	}

	public void setReqtime(String reqtime) {
		this.reqtime = reqtime;
	}

}
