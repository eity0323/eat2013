package com.gae.control;

import android.content.Context;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gae.entity.EatParams;
import com.gae.listener.GpsCompleteListener;

public class GpsCityControl {
	private String loc = null; // 保存定位信息
	public LocationClient mLocationClient = null;
	public Context mcontext;
	
	private GpsCompleteListener gclistener;
	public BDLocationListener myListener = new MyLocationListener();
	public GpsCityControl(Context tempcontext){
		mcontext = tempcontext;
	}
	
	public void setGpsCompleteListener(GpsCompleteListener glistener){
		gclistener = glistener;
	}
	
	public void InitGPS(){
		mLocationClient = new LocationClient(mcontext); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开GPS
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(36000);// 设置发起定位请求的间隔时间为36000ms
		option.disableCache(false);// 禁止启用缓存定位
		option.setPriority(LocationClientOption.NetWorkFirst);// 网络定位优先
		mLocationClient.setLocOption(option);// 使用设置
		mLocationClient.start();// 开启定位SDK
		mLocationClient.requestLocation();// 开始请求位置
    }
	public class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{
			if (location != null)
			{
				StringBuffer sb = new StringBuffer(128);// 接受服务返回的缓冲区
//				sb.append(location.getCity());// 获得城市
//				Toast.makeText(mcontext, location.getCity(), 3000).show();
				EatParams.getInstance().GpsCityname=location.getCity();
//				String addr=location.getAddrStr();
//				String aaa=location.getStreet();
//				String aarra=location.getPoi();
//				String aarreea=location.getDistrict();
//				String kek=location.getStreetNumber();
				sb.append(location.getDistrict());
				sb.append(location.getStreet());
				sb.append(location.getStreetNumber());
				EatParams.getInstance().setGpsAddr(sb.toString());
				EatParams.getInstance().mLatitude=String.valueOf(location.getLatitude());
				EatParams.getInstance().mLongitude=String.valueOf(location.getLongitude());
				
				for(int i=0;i<EatParams.getInstance().areaDblist.size();i++){
					if(EatParams.getInstance().areaDblist.get(i).getCity().equals(location.getCity())){
						EatParams.getInstance().areaDbName = EatParams.getInstance().areaDblist.get(i).getDbname();
//						EatParams.getInstance().areaDbName="eat_gz";
					}
					
				}
				
				if(gclistener != null){
					gclistener.onComplete(sb.toString());
				}
				
			} else
			{
				Toast.makeText(mcontext, location.getCity(), Toast.LENGTH_LONG).show();
				return;
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0)
		{
			// TODO Auto-generated method stub

		}

	}

	/**
	 * 停止，减少资源消耗
	 */
	public void stopListener()
	{
		if (mLocationClient != null && mLocationClient.isStarted())
		{
			mLocationClient.stop();// 关闭定位SDK
			mLocationClient = null;
		}
	}
}