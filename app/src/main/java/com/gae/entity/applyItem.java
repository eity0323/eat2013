package com.gae.entity;

public class applyItem {
private String id;
private String name;               //应用名称
private String memo;               //说明 
private String link;               //联接的Activity
private String lknum;              //点击次数
private String gridico;            //桌面图片名
private String sliico;             //抽屉图片名
private String show;               //是否显示
private String group;               //分组
private String time;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getMemo() {
	return memo;
}
public void setMemo(String memo) {
	this.memo = memo;
}
public String getLink() {
	return link;
}
public void setLink(String link) {
	this.link = link;
}
public String getLknum() {
	return lknum;
}
public void setLknum(String lknum) {
	this.lknum = lknum;
}

public String getGridico() {
	return gridico;
}
public void setGridico(String gridico) {
	this.gridico = gridico;
}
public String getSliico() {
	return sliico;
}
public void setSliico(String sliico) {
	this.sliico = sliico;
}
public String getShow() {
	return show;
}
public void setShow(String show) {
	this.show = show;
}
public String getTime() {
	return time;
}
public void setTime(String time) {
	this.time = time;
}
public String getGroup() {
	return group;
}
public void setGroup(String group) {
	this.group = group;
}


}
