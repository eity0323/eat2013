package com.gae.entity;
/**
 * 
 * @author eity
 * @version 2013-5-6 14:29
 * @description 宠物提示信息
 *
 */
public class PetAlertInfo {
	
	private String pid = "";			//提示
	private String type = "";			//内容
	private String content = "";		//提示类型,T固定时间提示信息， S系统提示信息，F功能提示信息，W网络提示信息
	private String grade = "";			//优先级别
	private String link = "";			//链接
	private String time = "";			//提醒时间
	private String showable = "";		//是否可显示
	
	public static String TYPE_SYSTEM = "S";	//系统类型，如：功能提醒；只要联网，我就能播报天气哟；听说摇一摇很好玩，主人要不要试试？
	public static String TYPE_TIMED = "T";		//固定时间类型，如：早晨问候，午餐提醒
	public static String TYPE_FUCTION = "F";	//功能类型，如：应用功能介绍
	public static String TYPE_WEB = "W";		//网络提示信息，如：今天XX菜热卖啦，主人要不要尝一下；又有达人发现新美食啦，主人要去看看吗？
	
	public PetAlertInfo(){
		
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getShowable() {
		return showable;
	}

	public void setShowable(String showable) {
		this.showable = showable;
	}
	
	
}
