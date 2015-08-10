package com.gae.dbHelper;
/*
 * author:eity
 * version:2013-3-1
 * description:数据库代理
 * */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class dbHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME="eat2013";			//数据库
    private final static int DATABASE_VERSION=1;					//数据库版本
    private final static String TABLE_NAME="eatset";				//表格名称
    public final static String FIELD_ID="id";						//唯一id
    public final static String FIELD_MB="mb"; 						//手机号
    public final static String FIELD_PWD="pwd";						//密码
    public final static String FIELD_SFOOD="food";					//收藏菜品
    public final static String FIELD_SEATERY="eatery";				//收藏餐馆
    public final static String FIELD_SSHOP="shop";					//收藏店长
    public final static String FIELD_SNEAR="near";                  //收藏附加分类
    public final static String FIELD_SFLWIN="flwin";                  //浮动窗口
	public dbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public dbHelper(Context context)
    {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }
    
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String sql="Create table "+TABLE_NAME+"("+FIELD_ID+" integer primary key autoincrement,"
				+FIELD_MB+" text,"
				+FIELD_PWD+" text,"
				+FIELD_SFOOD+" text,"
				+FIELD_SEATERY+" text,"
		        +FIELD_SSHOP+" text,"
		        +FIELD_SFLWIN+" text,"
		        +FIELD_SNEAR + " text);";
		arg0.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		 String sql=" DROP TABLE IF EXISTS "+TABLE_NAME;
	     db.execSQL(sql);
	     onCreate(db);
	}
	
	//查询记录
	public Cursor select()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME, null, null, null, null, null,  " mb asc");
        return cursor;
    }
    
	//添加记录
    public long insert(ContentValues cv)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        long row=db.insert(TABLE_NAME, null, cv);
        return row;
    }
    
    //删除记录
    public void delete(String mb)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String where=FIELD_MB + "=?";
        String[] whereValue={mb};
        db.delete(TABLE_NAME, where, whereValue);
    }
    
    //更新记录
    public void update(ContentValues cv,String mb)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String where=FIELD_MB + "=?";
        String[] whereValue={mb};
        db.update(TABLE_NAME, cv, where, whereValue);
    }
    
    public void update(String field,String value,String where){
    	
    }

    //执行指定sql
  	public void executeSql(String sql, Object[] bindArgs){
  		SQLiteDatabase db=this.getWritableDatabase();
  		db.execSQL(sql,bindArgs);
  	}
}
