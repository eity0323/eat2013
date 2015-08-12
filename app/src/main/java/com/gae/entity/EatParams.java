package com.gae.entity;
/*
 * author:eity
 * version:2013-3-1
 * description:公共数据类
 * */
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.widget.TabHost;
import android.widget.Toast;

public class EatParams {
	private static EatParams instance;			//实例名称
	private String appName = "eat2013";			//应用名称
	private String version = "1.0";				//版本号
	private String DataBathName = "eat2013";	//数据库名称
	private String session = "";				//事务id
	private String urlServer = "http://223.4.7.139/cgi/fgcs.fe";//"http://www.onpad.cn/cgi/fgcs.fe";	//服务器路径
//	private String urlServer = "http://10.168.2.12/cgi/fgcs.fe";//"http://www.onpad.cn/cgi/fgcs.fe";	//服务器路径
	private String configUrl = "http://www.onpad.cn:9000/conf/eat2013.xml";	//配置文件路径
	private String downloadUrl = "";			//下载路径
	private String serverVersion = "1";			//服务器应用版本
	private String downloadDir = "eat2013/";	// 安装目录
	private int otherViewIndex = -1;			//<0 主view,>0 子view
	public String areaDbName="";                //区域数据

    private String uid = "";					//用户id
    private String usename="";                  //用户名称
    private String mail = "";					//邮箱
    private String addr = "";					//地址
    private String mb = "";						//手机
    private String shopmb = "";						//商家电话
    private String vid = "";					//商家id
	public String mLongitude ="114.094044"; 	// "22.541370";//经度
	public String mLatitude = "22.541277";		//"114.093973";//纬度
	public String GpsCityname="";               //定位获取城市名
	private String GpsAddr="";               	//定位地址
	private String areaName="";					//大厦名称
	private String areaCode="";					//大厦编码
	private String atArea="";					//所在商圈
	
	private int netPosWorking = 0;				//网络是否有效，0网络有效+定位有效，1网络有效+定位无效，2网络无效+定位无效
	private String eateryOffline = "";			//允许餐馆使用离线数据
	
	private String deviceType = "";				//设备类型
	public static TabHost tabHost;
	public String myuuid = "";                  //没登录时的uid
	
	public String cityId = "00014";             //城市Id
	public String childId="";
	public int guideNum=0;
	private String SDdbname="eat2013";              //SD卡数据库名称
	/**
	 * 数据库文件名
	 */
	private String DateBase_Path = "eat2013";
	
	private int petStatus = 0;//宠物状态，0未启动，1启动正常显示，2关闭,3隐藏
	private int petInfoStatus = 0;//宠物提示消息状态，0未启动，1显示，2关闭
	private int petMenuStatus = 0;//宠物菜单状态，0未启动，1显示，2关闭
	
	private int screenHeight = 0;//屏幕高度
	private int screenWidth = 0;//屏幕看到
	
	private float petY = 0;	//宠物纵坐标
	private int [] perAlertIndex = {0,0,0,0};//宠物提示索引
	private String petAlertType = "S";//宠物提示类型
	private ArrayList<PetAlertInfo> sAlertList = null;//宠物系统提示数据源
	private ArrayList<PetAlertInfo> tAlertList = null;//宠物时间提示数据源
	private ArrayList<PetAlertInfo> fAlertList = null;//宠物功能提示数据源
	private ArrayList<PetAlertInfo> wAlertList = null;//宠物网络提示数据源
	
	private long lastPetInfoRefresh = 0;			//上一次宠物信息更新的时间
	private long petLiveHour = 0;					//每次开启宠物的生存时间
	private long petLifeTime = 0;					//宠物总生存时间
	private String petLastAlertType = "W";			//上一次显示宠物信息的类型
	
	private boolean voiced = false;				//是否打开语音界面了
	private boolean carShowed = false;			//是否打开了购物车服务
	/**
	 * @return 唯一编码
	 */
	public String getMyUUID() {
		if (myuuid == "") {
			UUID uuid = UUID.randomUUID();
			myuuid = uuid.toString();
		}
		return myuuid;
	}
	//购物车数据源
	public ArrayList<CarInfo> list = new ArrayList<CarInfo>();
	public ArrayList<areaDbInfo> areaDblist = new ArrayList<areaDbInfo>();
	public ArrayList<FoodTypeItem> fstyplist = new ArrayList<FoodTypeItem>();// 菜品种类数据源
//	public ArrayList<AreaInfo> arealist = new ArrayList<AreaInfo>();
//	//菜品数据源
	public List<Info> contactlist = new ArrayList<Info>();
	public ArrayList<FoodItem> fooditemlist = new ArrayList<FoodItem>();
//
//	//短信信息数据源
//	public ArrayList<MessageInfo> messagelist = new ArrayList<MessageInfo>();
//
//	//附近分类数据源
//	public ArrayList<Near_class1> near_c1_list = new ArrayList<Near_class1>();
//	public ArrayList<Near_class1> near_c2_list = new ArrayList<Near_class1>();
	
	public HashMap<String,Object> map = new HashMap<String, Object>();
	public EatParams() { 
		
	} 

	public static EatParams getInstance() { 

		if (instance == null) { 
			instance = new EatParams(); 
		} 

		return instance; 
	}
	//获取其他页面view索引
	public int getOtherViewIndex() {
		return otherViewIndex;
	}
	//设置其他页面view索引
	public void setOtherViewIndex(int otherViewIndex) {
		this.otherViewIndex = otherViewIndex;
	}

	//获取下载文件路径
	public String getDownloadDir() {
		return downloadDir;
	}
	//设置下载文件路径
	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}
	
	//是否进入向导
	public int getGuideNum() {
		return guideNum;
	}

	public void setGuideNum(int guideNum) {
		this.guideNum = guideNum;
	}

	//是否打开了语音界面
	public boolean getVoiced() {
		return voiced;
	}

	public void setVoiced(boolean voiced) {
		this.voiced = voiced;
	}

	//设置购物车服务
	public boolean getCarShowed() {
		return carShowed;
	}

	public void setCarShowed(boolean carShowed) {
		this.carShowed = carShowed;
	}

	public String getAppName() {
		return appName;
	}

	//获取配置文件路径
	public String getConfigUrl() {
		return configUrl;
	}
	//设置配置文件路径
	public void setConfigUrl(String configUrl) {
		this.configUrl = configUrl;
	}
	//获取下载文件路径
	public String getDownloadUrl() {
		return downloadUrl;
	}
	//设置下载文件路径
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	//获取服务器版本号
	public String getServerVersion() {
		return serverVersion;
	}
	//设置服务器版本号
	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}
	//获取事务id
	public String getSession() {
		return session;
	}
	//设置事务id
	public void setSession(String session) {
		this.session = session;
	}
	
	//获取数据库名称
	public String DataBathName(){
		return DataBathName;
	}
	
	//获取版本号
	public String getVersion(){
		return version;
	}

	//设置版本号
	public void setVersion(String v){
		version = v;
	}
	
	//获取服务器路径
	public String getUrlServer() {
		return urlServer;
	}

	//设置服务器路径
	public void setUrlServer(String urlServer) {
		this.urlServer = urlServer;
	}

	//获取用户id
	public String getUid() {
		return uid;
	}
	//设置用户id
	public void setUid(String uid) {
		this.uid = uid;
	}
	//获取用户邮箱
	public String getMail() {
		return mail;
	}
	//设置用户邮箱
	public void setMail(String mail) {
		this.mail = mail;
	}
	//获取用户地址
	public String getAddr() {
		return addr;
	}
	//设置用户地址
	public void setAddr(String addr) {
		this.addr = addr;
	}
	//获取用户电话
	public String getMb() {
		return mb;
	}
	//设置用户电话
	public void setMb(String mb) {
		this.mb = mb;
	}
	//获取商家id
     public String getVid() {
		return vid;
	}
    //设置商家id
	public void setVid(String vid) {
		this.vid = vid;
	}

	//获得经度
	public String getmLongitude() {
		return mLongitude;
	}
     //设置经度
	public void setmLongitude(String mLongitude) {
		this.mLongitude = mLongitude;
	}
	//获得纬度
	public String getmLatitude() {
		return mLatitude;
	}
    //设置纬度
	public void setmLatitude(String mLatitude) {
		this.mLatitude = mLatitude;
	}
	//获得区域数据
	public String getAreaDbName() {
		return areaDbName;
	}
	//设置区域数据
	public void setAreaDbName(String areaDbName) {
		this.areaDbName = areaDbName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAtArea() {
		return atArea;
	}

	public void setAtArea(String atArea) {
		this.atArea = atArea;
	}

	public String getChildId() {
		return childId;
	}

	public void setChildId(String childId) {
		this.childId = childId;
	}

	//====================pet start
	public int getPetStatus() {
		return petStatus;
	}

	public void setPetStatus(int petStatus) {
		this.petStatus = petStatus;
	}

	public int getPetInfoStatus() {
		return petInfoStatus;
	}

	public void setPetInfoStatus(int petInfoStatus) {
		this.petInfoStatus = petInfoStatus;
	}

	public int getPetMenuStatus() {
		return petMenuStatus;
	}

	public void setPetMenuStatus(int petMenuStatus) {
		this.petMenuStatus = petMenuStatus;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public float getPetY() {
		return petY;
	}

	public void setPetY(float petY) {
		this.petY = petY;
	}
	
	public int[] getPerAlertIndex() {
		return perAlertIndex;
	}

	public void setPerAlertIndex(int[] perAlertIndex) {
		this.perAlertIndex = perAlertIndex;
	}

	public String getPetAlertType() {
		return petAlertType;
	}

	public void setPetAlertType(String petAlertType) {
		this.petAlertType = petAlertType;
	}

	public ArrayList<PetAlertInfo> getsAlertList() {
		return sAlertList;
	}

	public void setsAlertList(ArrayList<PetAlertInfo> sAlertList) {
		this.sAlertList = sAlertList;
	}

	public ArrayList<PetAlertInfo> gettAlertList() {
		return tAlertList;
	}

	public void settAlertList(ArrayList<PetAlertInfo> tAlertList) {
		this.tAlertList = tAlertList;
	}

	public ArrayList<PetAlertInfo> getfAlertList() {
		return fAlertList;
	}

	public void setfAlertList(ArrayList<PetAlertInfo> fAlertList) {
		this.fAlertList = fAlertList;
	}

	public ArrayList<PetAlertInfo> getwAlertList() {
		return wAlertList;
	}

	public void setwAlertList(ArrayList<PetAlertInfo> wAlertList) {
		this.wAlertList = wAlertList;
	}

	//===========================================================================
	public String getFormatDate(){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date now=new Date(System.currentTimeMillis());		
		String formatTime=format.format(now);
		return formatTime;
	}
	
	//获取格式化时间字符串
	public String getFormatTime(){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
		Date now=new Date(System.currentTimeMillis());		
		String formatTime=format.format(now);
		return formatTime;
	}

	//获取指定格式的格式化时间字符串
	public String getFormatTime(String tformat){
		if(tformat.length() <= 0) 	tformat = "yyyy-MM-dd HH:MM:ss.FFFFFF";

		DateFormat format = new SimpleDateFormat( tformat );
		Date now=new Date(System.currentTimeMillis());

		String formatTime=format.format(now);
		return formatTime;
	}

	//获取指定时间格式化字符串
	public String getFormatTime(String tformat,String tdate){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");

		Date date = string2Date(tformat,tdate);
		if(date == null) date = new Date(System.currentTimeMillis());
		String formatTime=format.format(date);
		return formatTime;    	
	}

	//字符串转换为日期
	private Date string2Date(String format,String date){
		DateFormat df = new SimpleDateFormat(format);
		Date tpday = null;
		try
		{
			tpday = df.parse(date);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		return tpday;
	}
	//=====================pet end
	
	public List<Info> getContactlist() {
		return contactlist;
	}

	public void setContactlist(List<Info> contactlist) {
		this.contactlist = contactlist;
	}

	public String getGpsAddr() {
		return GpsAddr;
	}

	public void setGpsAddr(String gpsAddr) {
		GpsAddr = gpsAddr;
	}

	public List<FoodTypeItem> getFstyplist() {
		return fstyplist;
	}

	public void setFstyplist(ArrayList<FoodTypeItem> fstyplist) {
		this.fstyplist = fstyplist;
	}

	public String getShopmb() {
		return shopmb;
	}

	public void setShopmb(String shopmb) {
		this.shopmb = shopmb;
	}

	/**
	 * 获取订单项数量
	 * 
	 * @param carinfo 菜品类
	 * @return 份数
	 */
	public int getSnum(CarInfo carinfo) {
		int num = 0;
		for (int index = 0; index < list.size(); index++) {
			if (list.get(index).pid.equals(carinfo.getId())) {
				num = list.get(index).snum;
				break;
			}
		}
		return num;
	}

	/**
	 * 减指定份数
	 * 
	 * @param ordernum
     * 份数
	 * @param carinfo
     * 菜品类
	 */
	public void minus(int ordernum, CarInfo carinfo) {
		boolean cuted = false;
		for (int index = 0; index < list.size(); index++) {
			if (list.get(index).pid.equals(carinfo.getPid())) {
				int numb = list.get(index).getSnum();// 获取整数
				list.get(index).setSnum(numb - ordernum);
				cuted = true;
					if (list.get(index).getSnum() <= 0){
						list.remove(index);
					}
				break;
			}
		}
		if(cuted) return;
	}
	
	/**
	 * 加指定份数
	 * 
	 * @param ordernum 份数
	 * @param carinfo 菜品类
	 */
	public void insert(int ordernum, FoodItem carinfo) {
		boolean added = false;
		for (int index = 0; index < list.size(); index++) {
			if (list.get(index).pid.equals(carinfo.getPID())) {
				list.get(index).snum = list.get(index).snum + ordernum;
				added = true;
				break;
			}
		}
		if(added)return;
		
		CarInfo carin = new CarInfo();
		carin.pid = carinfo.getPID();
		carin.pname = carinfo.getPNAME();
		carin.vid = carinfo.getVID();
		carin.price = carinfo.getPRICE();
		carin.snum = ordernum;
		carin.carInfo = carinfo;
		list.add(carin);
	}

//	public ArrayList<AreaInfo> getArealist() {
//		return arealist;
//	}
//
//	public void setArealist(ArrayList<AreaInfo> arealist) {
//		this.arealist = arealist;
//	}

	public ArrayList<FoodItem> getFooditemlist() {
		return fooditemlist;
	}

	public void setFooditemlist(ArrayList<FoodItem> fooditemlist) {
		this.fooditemlist = fooditemlist;
	}
	
	public String getUsename() {
		return usename;
	}

	public void setUsename(String usename) {
		this.usename = usename;
	}

	public String getSDdbname() {
		return SDdbname;
	}

	public void setSDdbname(String sDdbname) {
		SDdbname = sDdbname;
	}

	public String getDateBase_Path() {
		return DateBase_Path;
	}

	public void setDateBase_Path(String dateBase_Path) {
		DateBase_Path = dateBase_Path;
	}

	//判断平板 | 手机
	public String checkDevices(Context mcontext){
		if(deviceType.equals("")){
	        TelephonyManager telephony = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
	        int type = telephony.getPhoneType();
		    if (type == TelephonyManager.PHONE_TYPE_NONE) {
		    	deviceType = "pad";
		    }else{
		    	deviceType = "phone";
		    }
		}
		return deviceType;
	}

	//发送短信（不推荐使用 ）
	public void sendMsg(Activity actity,String mobile,String content){
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getBroadcast(actity, 0, new Intent(), 0);
        
        if(content.length() >= 70){
            //短信字数大于70，自动分条
            List<String> ms = smsManager.divideMessage(content);           
            for(String str : ms ){
                //短信发送
                smsManager.sendTextMessage(mobile, null, str, sentIntent, null);
            }
        }else
        {
            smsManager.sendTextMessage(mobile, null, content, sentIntent, null);
        }        
        Toast.makeText(actity, "发送成功！", Toast.LENGTH_LONG).show();
	}

	public long getLastPetInfoRefresh() {
		return lastPetInfoRefresh;
	}

	public void setLastPetInfoRefresh(long lastPetInfoRefresh) {
		this.lastPetInfoRefresh = lastPetInfoRefresh;
	}
	
	

	public long getPetLiveHour() {
		return petLiveHour;
	}

	public void setPetLiveHour(long petLiveHour) {
		this.petLiveHour = petLiveHour;
	}

	public long getPetLifeTime() {
		return petLifeTime;
	}

	public void setPetLifeTime(long petLifeTime) {
		this.petLifeTime = petLifeTime;
	}

	public String getPetLastAlertType() {
		return petLastAlertType;
	}

	public void setPetLastAlertType(String petLastAlertType) {
		this.petLastAlertType = petLastAlertType;
	}

	public int getNetPosWorking() {
		return netPosWorking;
	}

	public void setNetPosWorking(int netPosWorking) {
		this.netPosWorking = netPosWorking;
	}
	
	public String getEateryOffline() {
		return eateryOffline;
	}

	public void setEateryOffline(String eateryOffline) {
		this.eateryOffline = eateryOffline;
	}

	/**
	 * 判断是否有可用网络
	 * 
	 * @return
	 */
	public static boolean isNetworkVailable(Context context) {
		ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}
	
}
