package com.gae.dbHelper;
/**
 * author:eity
 * version:2013-05-03
 * description:宠物提示消息
 */
import java.util.ArrayList;

import com.gae.entity.PetAlertInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class petInfoSDCardHelper {
	public static final String TAG = "dbSDCardHelper";	//标识
	public static final String DATABASE_NAME="pet2013";	//数据库名称
	public static final int DATABASE_VERSON=1;			//数据库版本

	public static final String TABLE_CONTACTS="petInfo";//表名
    public final static String FIELD_ID="pid"; 			//ID
    public final static String FIELD_CONTENT="content";	//提示内容
    public final static String FIELD_TYPE = "type";		//提示类型,T固定时间提示信息， S系统提示信息，F功能提示信息，W网络提示信息
    public final static String FIELD_GRADE = "grade";	//优先级别
    public final static String FIELD_LINK = "link";		//链接
    public final static String FIELD_TIME = "time";		//提醒时间
    public final static String FIELD_SHOWABLE = "showable";//是否可显示
   
    
	public static final String TABLECONTACTS=
		"create table "+ TABLE_CONTACTS +"("+
		FIELD_ID+ " integer primary key autoincrement,"+
		FIELD_CONTENT + " TEXT ," +
		FIELD_TYPE + " TEXT ," +
		FIELD_GRADE + " TEXT ," +
		FIELD_LINK + " TEXT ," +
		FIELD_TIME + " TEXT ," + 
		FIELD_SHOWABLE + " TEXT ); ";
	
	private Context context;							//应用上下文
	private DatabaseHelper dbHelper;					//数据库代理类
	private SQLiteDatabase mSQLiteDatabase = null;		//数据库

	public petInfoSDCardHelper(Context context){
		this.context=context;
	}
	//打开数据库，获取表格，如果不存在则创建
	public void open(){
		dbHelper=new DatabaseHelper(context);
		mSQLiteDatabase=dbHelper.getWritableDatabase();
		try{
			mSQLiteDatabase.query(TABLE_CONTACTS, null, null, null, null, null,null);
		}catch(Exception e){
			dbHelper.onCreate(mSQLiteDatabase);
		}
	}
	//关闭数据库
	public void close(){
		dbHelper.close();
	}
	
	//代理数据库操作
	private static class DatabaseHelper extends SDSQLiteOpenHelper{
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSON);
		}

		//创建数据库、初始化数据、
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLECONTACTS);
			
			//初始化数据
			ArrayList<PetAlertInfo> temp = initData();
			
			for(int i=0;i<temp.size();i++){
				ContentValues cv = new ContentValues();
				cv.put(FIELD_CONTENT, temp.get(i).getContent());
				cv.put(FIELD_TYPE, temp.get(i).getType());
				cv.put(FIELD_GRADE, temp.get(i).getGrade());
				cv.put(FIELD_LINK, temp.get(i).getLink());
				cv.put(FIELD_TIME, temp.get(i).getTime());
				cv.put(FIELD_SHOWABLE, temp.get(i).getShowable());
				db.insert(TABLE_CONTACTS, null, cv);
			}
		}
		
		private ArrayList<PetAlertInfo> initData(){
			ArrayList<PetAlertInfo> alist = new ArrayList<PetAlertInfo>();
			PetAlertInfo pobj = null;			
			pobj = new PetAlertInfo();
			pobj.setContent("早上好");
			pobj.setGrade("1");
			pobj.setLink("");
			pobj.setTime("8:00");
			pobj.setType(pobj.TYPE_TIMED);
			pobj.setShowable("false");
			alist.add(pobj);
			
			pobj = new PetAlertInfo();
			pobj.setContent("吃饭啦");
			pobj.setGrade("1");
			pobj.setLink("");
			pobj.setTime("11:00");
			pobj.setType(pobj.TYPE_TIMED);
			pobj.setShowable("false");
			alist.add(pobj);
			
			pobj = new PetAlertInfo();
			pobj.setContent("下班啦");
			pobj.setGrade("1");
			pobj.setLink("");
			pobj.setTime("18:00");
			pobj.setType(pobj.TYPE_TIMED);
			pobj.setShowable("false");
			alist.add(pobj);
			
			pobj = new PetAlertInfo();
			pobj.setContent("听说摇一摇很好玩，主人要不要试试？");
			pobj.setGrade("1");
			pobj.setLink("");
			pobj.setTime("");
			pobj.setType(pobj.TYPE_SYSTEM);
			pobj.setShowable("true");
			alist.add(pobj);
			
			pobj = new PetAlertInfo();
			pobj.setContent("我不会告诉你点我就可以查看我全部功能的！");
			pobj.setGrade("1");
			pobj.setLink("");
			pobj.setTime("");
			pobj.setType(pobj.TYPE_SYSTEM);
			pobj.setShowable("true");
			alist.add(pobj);
			
			pobj = new PetAlertInfo();
			pobj.setContent("今天XX菜热卖啦，主人要不要尝一下");
			pobj.setGrade("1");
			pobj.setLink("");
			pobj.setTime("");
			pobj.setType(pobj.TYPE_WEB);
			pobj.setShowable("true");
			alist.add(pobj);
			
			pobj = new PetAlertInfo();
			pobj.setContent("宠物设置功能，可以在这个页面查看我的成长状况");
			pobj.setGrade("1");
			pobj.setLink("");
			pobj.setTime("");
			pobj.setType(pobj.TYPE_FUCTION);
			pobj.setShowable("false");
			alist.add(pobj);
			return alist;
		}
		//更新表结构
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACTS);
			onCreate(db);
			
		}
		//打开数据库
		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
		}
		
	}	
	
	//表格字段
	String contactProjection[]={
			FIELD_ID,
			FIELD_CONTENT,
			FIELD_TYPE,
			FIELD_GRADE,
			FIELD_LINK,
			FIELD_TIME,
			FIELD_SHOWABLE
	}; 

	//创建表格
	public void createTable(){
		if(tabbleIsExist(TABLE_CONTACTS)){
    		return;
    	}
		
		mSQLiteDatabase.execSQL(TABLECONTACTS);
	}
	
	//查询
	public Cursor select(){
		if(!tabbleIsExist(TABLE_CONTACTS)){
    		return null;
    	}
		
		return mSQLiteDatabase.query(
				TABLE_CONTACTS, 
				contactProjection, 
				null, 
				null, null, null, null);
	}

	//查询
	public Cursor selectBySql(String sql,String selectionArgs[]){
		if(!tabbleIsExist(TABLE_CONTACTS)){
    		return null;
    	}
		return mSQLiteDatabase.rawQuery(sql, selectionArgs);
	}
	
	//添加
	public long insert(ContentValues cv){
		if(!tabbleIsExist(TABLE_CONTACTS)){
    		return -1;
    	}
		
		int res = -1;
		mSQLiteDatabase.beginTransaction(); 
        try{
        	mSQLiteDatabase.insert(TABLE_CONTACTS, null, cv);
        	mSQLiteDatabase.setTransactionSuccessful();  
	    }catch(Exception err){
	    	Log.e("dbHelper", "an error occured when operate database.", err); 
	    }finally{  
	        //结束事务  
	    	mSQLiteDatabase.endTransaction();  
	    }  
		return res;		
	}
	
	//更新
	public int update(ContentValues cv,String id){
		if(!tabbleIsExist(TABLE_CONTACTS)){
    		return -1;
    	}
		
		int res = -1;
		mSQLiteDatabase.beginTransaction(); 
        try{
        	mSQLiteDatabase.update(TABLE_CONTACTS, cv, FIELD_ID+"=?", new String[]{id});
        	mSQLiteDatabase.setTransactionSuccessful();  
	    }catch(Exception err){
	    	Log.e("dbHelper", "an error occured when operate database.", err); 
	    }finally{  
	        //结束事务  
	    	mSQLiteDatabase.endTransaction();  
	    }  
		return res;
	}
		
	//删除指定id记录
	public int delete(int id){
		if(!tabbleIsExist(TABLE_CONTACTS)){
    		return -1;
    	}
		
		int res = -1;
		mSQLiteDatabase.beginTransaction(); 
        try{
        	mSQLiteDatabase.delete(TABLE_CONTACTS, FIELD_ID+"='"+id+"'", null);
        	mSQLiteDatabase.setTransactionSuccessful();  
	    }catch(Exception err){
	    	Log.e("dbHelper", "an error occured when operate database.", err); 
	    }finally{  
	        //结束事务  
	    	mSQLiteDatabase.endTransaction();  
	    }  
		return res;
	}
	
	//删除所以记录
	public int deleteAllData(){
		if(!tabbleIsExist(TABLE_CONTACTS)){
    		return -1;
    	}
		
		int res = -1;
		mSQLiteDatabase.beginTransaction(); 
        try{
        	mSQLiteDatabase.delete(TABLE_CONTACTS, null, null);
        	mSQLiteDatabase.setTransactionSuccessful();  
	    }catch(Exception err){
	    	Log.e("dbHelper", "an error occured when operate database.", err); 
	    }finally{  
	        //结束事务  
	    	mSQLiteDatabase.endTransaction();  
	    }  
		return res;
	}
	
	//删除表格
	public void deleteTable(){
		if(!tabbleIsExist(TABLE_CONTACTS)){
    		return;
    	}
		
		String sql = "DROP TABLE  IF EXISTS " + TABLE_CONTACTS;
		mSQLiteDatabase.beginTransaction(); 
        try{
        	mSQLiteDatabase.execSQL(sql);
        	mSQLiteDatabase.setTransactionSuccessful();  
	    }catch(Exception err){
	    	Log.e("dbHelper", "an error occured when operate database.", err); 
	    }finally{  
	        //结束事务  
	    	mSQLiteDatabase.endTransaction();  
	    }  
	}

	//执行指定sql
	public void executeSql(String sql){		
		if(!tabbleIsExist(TABLE_CONTACTS)){
    		return;
    	}
		
		mSQLiteDatabase.beginTransaction(); 
        try{
        	mSQLiteDatabase.execSQL(sql);
        	mSQLiteDatabase.setTransactionSuccessful();  
	    }catch(Exception err){
	    	Log.e("dbHelper", "an error occured when operate database.", err); 
	    }finally{  
	        //结束事务  
	    	mSQLiteDatabase.endTransaction();  
	    }  
	}
	
	//执行指定sql
	public void executeSql(String sql,Object[] Args){
		if(!tabbleIsExist(TABLE_CONTACTS)){
    		return;
    	}
		
		mSQLiteDatabase.beginTransaction(); 
        try{
			mSQLiteDatabase.execSQL(sql, Args);
			mSQLiteDatabase.setTransactionSuccessful();  
	    }catch(Exception err){
	    	Log.e("dbHelper", "an error occured when operate database.", err); 
	    }finally{  
	        //结束事务  
	    	mSQLiteDatabase.endTransaction();  
	    }  
	}
	
	//判断表是否存在
  	public boolean tabbleIsExist(String tableName){  
        boolean result = false;  
        if(tableName == null){  
            return false;  
        }  
        Cursor cursor = null;  
        try {  
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";  
            cursor = mSQLiteDatabase.rawQuery(sql, null);  
            if(cursor.moveToNext()){  
                int count = cursor.getInt(0);  
                if(count>0){  
                    result = true;  
                }  
            }  
              
        } catch (Exception e) {  
            // TODO: handle exception  
        	Log.e("dbHelper", "an error occured when operate database.", e);
        }      
        
        cursor.close(); 
        return result;  
  	}  
}

