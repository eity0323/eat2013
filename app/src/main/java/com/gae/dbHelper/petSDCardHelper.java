package com.gae.dbHelper;
/**
 * author:eity
 * version:2013-05-03
 * description:宠物资料
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class petSDCardHelper {
	public static final String TAG = "dbSDCardHelper";//标识
	public static final String DATABASE_NAME="pet2013";//数据库名称
	public static final int DATABASE_VERSON=1;			//数据库版本

	public static final String TABLE_CONTACTS="pet";	//表名
    public final static String FIELD_ID="pid"; 			//ID
    public final static String FIELD_NICKNAME="nickname";	//昵称
    public final static String FIELD_GENDER = "gender";		//性别
    public final static String FIELD_MEMO = "memo";			//描述
    public final static String FIELD_BIRTH = "birth";		//生日
    public final static String FIELD_ENERGE = "energy";		//体力
    public final static String FIELD_MOOD = "mood";			//心情
    public final static String FIELD_SCORE = "score";		//积分
    public final static String FIELD_HEALTH = "health";		//健康
    public final static String FIELD_CLOSED = "closed";		//是否关闭
    public final static String FIELD_SOUND = "sound";		//音效
    public final static String FIELD_WEATHER = "weather";	//天气
    public final static String FIELD_LIFETIME = "lifetime";//生存期
    
	public static final String TABLECONTACTS=
		"create table "+ TABLE_CONTACTS +"("+
		FIELD_ID+ " integer primary key autoincrement,"+
		FIELD_NICKNAME+ " TEXT ," +
		FIELD_GENDER+ " TEXT ," +
		FIELD_MEMO+ " TEXT ," +
		FIELD_BIRTH+ " TEXT ," +
		FIELD_ENERGE+ " TEXT ," +
		FIELD_MOOD+ " TEXT ," +
		FIELD_SCORE+ " TEXT ," +
		FIELD_HEALTH+ " TEXT ," +
		FIELD_CLOSED+ " TEXT ," +
		FIELD_SOUND+ " TEXT ," +
		FIELD_WEATHER+ " TEXT ," +
		FIELD_LIFETIME+ " TEXT ); ";
	
	private Context context;							//应用上下文
	private DatabaseHelper dbHelper;					//数据库代理类
	private SQLiteDatabase mSQLiteDatabase = null;		//数据库

	public petSDCardHelper(Context context){
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
			FIELD_NICKNAME,
			FIELD_GENDER,
			FIELD_MEMO,
			FIELD_BIRTH,
			FIELD_ENERGE,
			FIELD_MOOD,
			FIELD_SCORE,
			FIELD_HEALTH,
			FIELD_CLOSED,
			FIELD_SOUND,
			FIELD_WEATHER,
			FIELD_LIFETIME
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

