package com.gae.eat2013;
/**
 * @author eity
 * @version 2013-06-04
 * @description 天气预报
 * */

import android.os.Bundle;
import android.app.Activity;
import java.io.BufferedInputStream;  
import java.io.InputStream;  
import java.net.URL;  
import java.net.URLConnection;  
import java.util.HashMap;  
import java.util.Map;  
  
import org.json.JSONObject;  

import com.gae.basic.NameIDMap;
import com.gae.basic.WeatherHttpDownloader;
  
import android.graphics.Bitmap;  
import android.graphics.BitmapFactory;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.AdapterView;  
import android.widget.AdapterView.OnItemSelectedListener;  
import android.widget.ArrayAdapter;  
import android.widget.Button;  
import android.widget.EditText;  
import android.widget.ImageView;  
import android.widget.Spinner;  
import android.widget.TextView; 

public class WeatherActivity extends Activity {

	 private Spinner spinPro;  	//省
	    private Spinner spinCity;  //市
	    private Button button1;  //按省市查找
	    private EditText etInput;  //输入框
	    private Button button2;  //按输入城市查找
	    private ImageView image1;  //今天天气图片
	    private ImageView image2;  //今天
	    private ImageView image3;  //明天天气图片
	    private ImageView image4;  //明天
	    private ImageView image5;  //后天天气图片
	    private ImageView image6;  //后天
	    private TextView text1;  //今天文字天气说明
	    private TextView text2;  //明天天气说明
	    private TextView text3;  //后天天气说明
	  
	    private Map<String, String> mapAllNameID;  //所以城市对应id
	    private String[] strNamePro;				// 所有的市  
	    private String[][] strNameCity;				// 所有的城市  
	    private String name;						// 要查询地址  
	    private String id;							// 要查询的id  
	    private String strUrl;  					//查询url
	    private String jsonData;  					//返回json数据
	    // 要现实的天气信息  
	    private StringBuffer weatherInfo1;  
	    private StringBuffer weatherInfo2;  
	    private StringBuffer weatherInfo3;  
	    // 图片资源14,25,36  
	    private String weatherPicUrl1;  
	    private String weatherPicUrl2;  
	    private String weatherPicUrl3;  
	    private String weatherPicUrl4;  
	    private String weatherPicUrl5;  
	    private String weatherPicUrl6;  
	  
	    private ArrayAdapter<CharSequence> adapterCity;  //城市列表
	    private ArrayAdapter<CharSequence> adapterPro;  //省份列表
	  
	    @Override  
	    public void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.weather_activity);  
	        
	        this.initWidget();  
	        this.initVar();  
	        this.binding();  
	        
	        //返回
	        Button reback = (Button)findViewById(R.id.weatherback);
	        reback.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
	    }  
	  
	    //初始化变量
	    private void initVar() {  
	        // strUrl = "http://m.weather.com.cn/data/101010100.html";  
	        // 得到所有的键值对  
	        mapAllNameID = new HashMap<String, String>();  
	        NameIDMap nameIDMap = new NameIDMap();  
	        mapAllNameID = nameIDMap.getMapAllNameID();  
	        // 初始化省市级  
	        strNamePro = new String[] { "黑龙江", "吉林", "辽宁", "内蒙古", "河北", "山西", "陕西",  
	                "山东", "新疆", "西藏", "青海", "甘肃", "宁夏", "河南", "江苏", "湖北", "浙江",  
	                "安徽", "福建", "江西", "湖南", "贵州", "四川", "广东", "云南", "广西", "海南",  
	                "台湾" };  
	        strNameCity = new String[][] {  
	                { "哈尔滨", "齐齐哈尔", "牡丹江", "佳木斯", "绥化", "黑河", "大兴安岭", "伊春", "大庆",  
	                        "鸡西", "鹤岗", "双鸭山" },  
	                { "长春", "吉林", "延吉", "四平", "通化", "白城", "辽源", "松原", "白山" },  
	                { "沈阳", "大连", "鞍山", "抚顺", "本溪", "丹东", "锦州", "营口", "阜新", "辽阳",  
	                        "铁岭", "朝阳", "盘锦", "葫芦岛" },  
	                { "呼和浩特", "包头", "乌海", "集宁", "通辽", "赤峰", "鄂尔多斯", "临河", "锡林浩特",  
	                        "海拉尔", "乌兰浩特", "阿拉善左旗" },  
	                { "石家庄", "保定", "张家口", "唐山", "廊坊", "沧州", "衡水", "邢台", "邯郸", "秦皇岛" },  
	                { "太原", "大同", "阳泉", "晋中", "长治", "晋城", "临汾", "运城", "朔州", "忻州",  
	                        "离石" },  
	                { "西安", "三原", "延长", "榆林", "渭南", "商洛", "安康", "汉中", "宝鸡", "铜川" },  
	                { "济南", "青岛", "淄博", "德州", "烟台", "潍坊", "济宁", "泰安", "临沂", "菏泽",  
	                        "滨州", "东营", "威海", "枣庄", "日照", "莱芜", "聊城" },  
	                { "乌鲁木齐", "克拉玛依", "石河子", "昌吉", "吐鲁番", "库尔勒", "阿拉尔", "阿克苏",  
	                        "喀什", "伊宁", "塔城", "哈密", "和田", "阿勒泰", "阿图什", "博乐" },  
	                { "拉萨", "日喀则", "山南", "林芝", "昌都", "那曲", "阿里" },  
	                { "西宁", "海东", "黄南", "海南", "果洛", "玉树", "海西", "海北" },  
	                { "兰州", "定西", "平凉", "庆阳", "武威", "金昌", "张掖", "酒泉", "天水", "武都",  
	                        "临夏", "合作", "白银" },  
	                { "银川", "石嘴山", "吴忠", "固原", "中卫" },  
	                { "郑州", "安阳", "新乡", "许昌", "平顶山", "信阳", "南阳", "开封", "洛阳", "商丘",  
	                        "焦作", "鹤壁", "濮阳", "周口", "漯河", "驻马店", "三门峡", "济源" },  
	                { "南京", "无锡", "镇江", "苏州", "南通", "扬州", "盐城", "徐州", "淮安", "连云港",  
	                        "常州", "泰州", "宿迁" },  
	                { "武汉", "襄樊", "鄂州", "孝感", "黄冈", "黄石", "咸宁", "荆州", "宜昌", "恩施",  
	                        "十堰", "神农架", "随州", "荆门", "天门", "仙桃", "潜江" },  
	                { "杭州", "湖州", "嘉兴", "宁波", "绍兴", "台州", "温州", "丽水", "金华", "衢州",  
	                        "舟山" },  
	                { "合肥", "蚌埠", "芜湖", "淮南", "马鞍山", "安庆", "宿州", "阜阳", "亳州", "黄山站",  
	                        "滁州", "淮北", "铜陵", "宣城", "六安", "巢湖", "池州" },  
	                { "福州", "厦门", "宁德", "莆田", "泉州", "漳州", "龙岩", "三明", "南平" },  
	                { "南昌", "九江", "上饶", "抚州", "宜春", "吉安", "赣州", "景德镇", "萍乡", "新余",  
	                        "鹰潭" },  
	                { "长沙", "湘潭", "株洲", "衡阳", "郴州", "常德", "赫山区", "娄底", "邵阳", "岳阳",  
	                        "张家界", "怀化", "黔阳", "永州", "吉首" },  
	                { "贵阳", "遵义", "安顺", "都匀", "凯里", "铜仁", "毕节", "六盘水", "黔西" },  
	                { "成都", "攀枝花", "自贡", "绵阳", "南充", "达州", "遂宁", "广安", "巴中", "泸州",  
	                        "宜宾", "内江", "资阳", "乐山", "眉山", "凉山", "雅安", "甘孜", "阿坝",  
	                        "德阳", "广元" },  
	                { "广州", "韶关", "惠州", "梅州", "汕头", "深圳", "珠海", "顺德", "肇庆", "湛江",  
	                        "江门", "河源", "清远", "云浮", "潮州", "东莞", "中山", "阳江", "揭阳",  
	                        "茂名", "汕尾" },  
	                { "昆明", "大理", "红河", "曲靖", "保山", "文山", "玉溪", "楚雄", "普洱", "昭通",  
	                        "临沧", "怒江", "香格里拉", "丽江", "德宏", "景洪" },  
	                { "南宁", "崇左", "柳州", "来宾", "桂林", "梧州", "贺州", "贵港", "玉林", "百色",  
	                        "钦州", "河池", "北海", "防城港" }, { "海口", "三亚" },  
	                { "台北县", "高雄", "台南", "台中", "桃园", "新竹县", "宜兰", "马公", "嘉义" } };  
	  
	        adapterPro = new ArrayAdapter<CharSequence>(getApplicationContext(),  
	                android.R.layout.simple_spinner_item, strNamePro);  
	        adapterPro  
	                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
	        spinPro.setAdapter(adapterPro);  
	    }  
	  
	    class ProSelectListener implements OnItemSelectedListener {  
	        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,  
	                long arg3) {  
	            adapterCity = new ArrayAdapter<CharSequence>(  
	                    WeatherActivity.this,  
	                    android.R.layout.simple_spinner_item, strNameCity[arg2]);// 定义我们所有的列表项  
	            adapterCity  
	                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
	            spinCity.setAdapter(adapterCity);  
	        }  
	  
	        public void onNothingSelected(AdapterView<?> arg0) {  
	        }  
	    }  
	  
	    class CitySelectListener implements OnItemSelectedListener {  
	        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,  
	                long arg3) {  
	            name = spinCity.getSelectedItem().toString();  
	        }  
	  
	        public void onNothingSelected(AdapterView<?> arg0) {  
	        }  
	    }  
	  
	    class Button1Listener implements OnClickListener {  
	        public void onClick(View v) {  
	            // 根据选中的名字得到对应的id然后下载网页  
	            NameIDMap nameIDMap = new NameIDMap();  
	            mapAllNameID = nameIDMap.getMapAllNameID();  
	            id = mapAllNameID.get(name);  
	            strUrl = "http://m.weather.com.cn/data/" + id + ".html";  
	            // 将网页上的内容下载下来  
	            WeatherHttpDownloader http = new WeatherHttpDownloader();  
	            jsonData = http.download(strUrl);  
	            // 天气情况  
	            parseJson(jsonData);  
	            // 天气图片  
	            weatherPic();  
	        }  
	    }  
	  
	    private void weatherPic() {// 天气图片  
	        try {  
	            image1.setImageBitmap(getBitmap(weatherPicUrl1));  
	            image2.setImageBitmap(getBitmap(weatherPicUrl2));  
	            image3.setImageBitmap(getBitmap(weatherPicUrl3));  
	            image4.setImageBitmap(getBitmap(weatherPicUrl4));  
	            image5.setImageBitmap(getBitmap(weatherPicUrl5));  
	            image6.setImageBitmap(getBitmap(weatherPicUrl6));  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    // 根据图片的url得到要显示的图片  
	    private Bitmap getBitmap(String url) {  
	        Bitmap bm = null;// 生成了一张bmp图像  
	        try {  
	            URL iconurl = new URL("http://m.weather.com.cn/img/b" + url  
	                    + ".gif");  
	            URLConnection conn = iconurl.openConnection();  
	            conn.connect();  
	            // 获得图像的字符流  
	            InputStream is = conn.getInputStream();  
	            BufferedInputStream bis = new BufferedInputStream(is, 8192);  
	            bm = BitmapFactory.decodeStream(bis);  
	            bis.close();  
	            is.close();// 关闭流  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return bm;  
	    }  
	  
	    // json数据解析  
	    private void parseJson(String jsonData) {  
	        try {  
	            JSONObject allWeatherData = new JSONObject(jsonData);  
	            JSONObject weatherData = allWeatherData  
	                    .getJSONObject("weatherinfo");  
	            // 第一天的天气情况  
	            weatherInfo1 = new StringBuffer();  
	            weatherInfo1.append("地点:" + weatherData.getString("city"));  
	            weatherInfo1.append("\n时间:" + weatherData.getString("date_y")  
	                    + "     " + weatherData.getString("week"));  
	            weatherInfo1.append("\n今日天气:" + weatherData.getString("weather1"));  
	            weatherInfo1.append("\n温度:" + weatherData.getString("temp1"));  
	            weatherPicUrl1 = weatherData.getString("img1");  
	            weatherPicUrl2 = weatherData.getString("img2");  
	            weatherInfo1.append("\n风速:" + weatherData.getString("wind1") + "    "  
	                    + weatherData.getString("fl1"));  
	            // 第二天的天气情况  
	            weatherInfo2 = new StringBuffer();  
	            weatherInfo2.append("明天天气:" + weatherData.getString("weather2"));  
	            weatherInfo2.append("\n温度:" + weatherData.getString("temp2"));  
	            weatherPicUrl3 = weatherData.getString("img3");  
	            weatherPicUrl4 = weatherData.getString("img4");  
	            weatherInfo2.append("\n风速:" + weatherData.getString("wind2") + "    "  
	                    + weatherData.getString("fl2"));  
	            // 第三天的天气情况  
	            weatherInfo3 = new StringBuffer();  
	            weatherInfo3.append("后天天气:" + weatherData.getString("weather3"));  
	            weatherInfo3.append("\n温度:" + weatherData.getString("temp3"));  
	            weatherPicUrl5 = weatherData.getString("img5");  
	            weatherPicUrl6 = weatherData.getString("img6");  
	            weatherInfo3.append("\n风速:" + weatherData.getString("wind3") + "    "  
	                    + weatherData.getString("fl3"));  
	  
	            text1.setText(weatherInfo1);  
	            text2.setText(weatherInfo2);  
	            text3.setText(weatherInfo3);  
	  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    class Button2Listener implements OnClickListener {  
	        public void onClick(View v) {  
	            NameIDMap nameIDMap = new NameIDMap();  
	            mapAllNameID = nameIDMap.getMapAllNameID();  
	            id = mapAllNameID.get(etInput.getText().toString());  
	            strUrl = "http://m.weather.com.cn/data/" + id + ".html";  
	            // 将网页上的内容下载下来  
	            WeatherHttpDownloader http = new WeatherHttpDownloader();  
	            jsonData = http.download(strUrl);  
	            // 天气情况  
	            parseJson(jsonData);  
	            // 天气图片  
	            weatherPic();  
	        }  
	    }  
	  
	    private void binding() {// 绑定监听器  
	        button1.setOnClickListener(new Button1Listener());  
	        button2.setOnClickListener(new Button2Listener());  
	        spinPro.setOnItemSelectedListener(new ProSelectListener());  
	        spinCity.setOnItemSelectedListener(new CitySelectListener());  
	    }  
	  
	    private void initWidget() {// 控件初始化  
	        spinPro = (Spinner) findViewById(R.id.spinner1);  
	        spinCity = (Spinner) findViewById(R.id.spinner2);  
	        button1 = (Button) findViewById(R.id.button1);  
	        button2 = (Button) findViewById(R.id.button2);  
	        etInput = (EditText) findViewById(R.id.editText1);  
	        text1 = (TextView) findViewById(R.id.textView1);  
	        text2 = (TextView) findViewById(R.id.textView2);  
	        text3 = (TextView) findViewById(R.id.textView3);  
	        image1 = (ImageView) findViewById(R.id.imageView1);  
	        image2 = (ImageView) findViewById(R.id.imageView2);  
	        image3 = (ImageView) findViewById(R.id.imageView3);  
	        image4 = (ImageView) findViewById(R.id.imageView4);  
	        image5 = (ImageView) findViewById(R.id.imageView5);  
	        image6 = (ImageView) findViewById(R.id.imageView6);  
	    }  

}
