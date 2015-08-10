package com.gae.basic;
/*
 * author:eity
 * version:2013-3-1
 * description:访问网络数据
 * */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetHttp {

//	public static String inputStream2String(InputStream is) throws IOException {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		int i = -1;
//		while ((i = is.read()) != -1) {
//			baos.write(i);
//		}
//		return baos.toString();
//	}

	public String contentToString(String urlname,String coding)   
	{   
		String html = "";   
		try  
		{   
			URL url = new URL(urlname);   
			HttpURLConnection connect = (HttpURLConnection) url.openConnection();   
			connect.setRequestProperty("User-agent","Mozilla/4.0");    
			connect.connect();   
			int count = connect.getContentLength();
			InputStream is = connect.getInputStream();
			
			//html=inputStream2String(is);

			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					coding), 65536);

			String data = "";
			while ((data = br.readLine()) != null) {
				sb.append(data);
			}
			html = sb.toString();
			
			br.close();
			connect.disconnect();
		}   
		catch (IOException ex)   
		{   
			ex.printStackTrace();
			return "";   
		}   
		catch (Exception e)   
		{   
			System.out.println("出现错误" + e.getStackTrace());   
		}   
		return html;   
	}   
	
	public String contentToString(String urlname,String params,String coding)   
	{   
		String html = "";   
		try  
		{   
			URL url = new URL(urlname);   
		
			// 使用HttpURLConnection打开连接  			
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();  
            //因为这个是post请求,设立需要设置为true  
            urlConn.setDoOutput(true);  
            urlConn.setDoInput(true);  
            // 设置以POST方式  
            urlConn.setRequestMethod("POST");  
            // Post 请求不能使用缓存  
            urlConn.setUseCaches(false);  
            urlConn.setInstanceFollowRedirects(true);  
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的  
            urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");  
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，  
            // 要注意的是connection.getOutputStream会隐含的进行connect。  
            urlConn.connect();  
            //DataOutputStream流  
            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());  
//            //要上传的参数  
//            String content = "par=" + URLEncoder.encode(params, "GB2312");  
            //将要上传的内容写入流中  
            out.writeBytes(params);   
            //刷新、关闭  
            out.flush();  
            out.close();   
            
            InputStream is = urlConn.getInputStream();
            StringBuffer sb = new StringBuffer();
            //获取数据  
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,
					coding), 65536);

			String data = "";
			//使用循环来读取获得的数据  
			while ((data = reader.readLine()) != null) {
				sb.append(data);
			}			        
            reader.close();    
            urlConn.disconnect();										//关闭http连接  

            html = sb.toString(); 
		}   
		catch (IOException ex)   
		{   
			return "";   
		}   
		catch (Exception e)   
		{   
			System.out.println("出现错误" + e.getStackTrace());   
		}   
		return html;   
	} 
}