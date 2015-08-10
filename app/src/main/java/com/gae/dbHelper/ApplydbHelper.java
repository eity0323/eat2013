package com.gae.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ApplydbHelper {
	/**
	 * 数据库名称
	 */
	public String DATABASE_NAME = "eat2013.db";
	/**
	 * 数据库版本
	 */
	public static final int DATABASE_VERSON = 1;
	/**
	 * 应用表名称
	 */
	public static final String TABLE_APPLY = "w_apply";
	public static final String APPLY_ID="_id";
	public static final String APPLY_NAME="name";
	public static final String APPLY_MEMO="memo";
	public static final String APPLY_LINK="link";
	public static final String APPLY_LKNUM="lknum";
	public static final String APPLY_GRIDICO="gridico";
	public static final String APPLY_SLIICO="sliico";
	public static final String APPLY_SHOW="show";
	public static final String APPLY_GROUP="grouping";
	public static final String APPLY_TIME="time";
	public static final String APPLY_HELP = "help";
	
	/**
	 * 应用上下文
	 */
	private Context context;
	/**
	 * 数据库代理类
	 */
	private DatabaseHelper dbHelper;
	/**
	 * 数据库
	 */
	private SQLiteDatabase mSQLiteDatabase = null;

	public ApplydbHelper(Context context) {
		this.context = context;
	}
//	public static final String sql = "CREATE TABLE " + TABLE_APPLY + " ("
//			+ APPLY_ID + " TEXT NOT NULL, " + APPLY_NAME + " TEXT NOT NULL, "
//			+ APPLY_MEMO + " TEXT NOT NULL, " + APPLY_LINK
//			+ " TEXT NOT NULL, " + APPLY_LKNUM + " TEXT NOT NULL, "
//			+ APPLY_GRIDICO + " TEXT NOT NULL, " + APPLY_SLIICO + " TEXT NOT NULL, " + APPLY_SHOW+ " INTEGER NOT NULL, " + APPLY_GROUP
//			+ " TEXT NOT NULL, " + APPLY_TIME + " TEXT NOT NULL)";
	/**
	 * 打开数据库、如果不存在则创建
	 * 
	 * @param dbName
	 * @return 是否存在数据库，1 存在；0不存在
	 */
	public int open(String dbName) {
		if (dbName.indexOf(".db") == -1) {
			dbName += ".db";
		}
		int res = 0;
		DATABASE_NAME = dbName;
		dbHelper = new DatabaseHelper(context, dbName);
		mSQLiteDatabase = dbHelper.getWritableDatabase();
		try {
			mSQLiteDatabase.query(TABLE_APPLY, null, null, null, null, null,
					null);
			res = 1;
		} catch (Exception e) {
//			System.out.println("------------------ 没有数据库！！");
			mSQLiteDatabase = null;
			res = 0;
		}
		return res;
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		dbHelper.close();
	}
	// 数据库代理类
	private static class DatabaseHelper extends SDSQLiteOpenHelper {
		public DatabaseHelper(Context context, String dbName) {
			super(context, dbName, null, DATABASE_VERSON);
		}

		// 创建数据库，并初始化记录
		@Override
		public void onCreate(SQLiteDatabase db) {    
//			 APPLY_ID, APPLY_NAME, APPLY_MEMO, APPLY_LINK,
//				APPLY_LKNUM,APPLY_GRIDICO,APPLY_SLIICO, APPLY_SHOW, APPLY_GROUP, APPLY_TIME};
			String sql="Create table  "+TABLE_APPLY+"("+APPLY_ID+" integer primary key autoincrement,"
					+APPLY_NAME+" text,"
					+APPLY_MEMO+" text,"
					+APPLY_LINK+" text,"
					+APPLY_LKNUM+" text,"
			        +APPLY_GRIDICO+" text,"
			        +APPLY_SLIICO+" text,"
			        +APPLY_SHOW+" text,"
			        +APPLY_GROUP+" text,"
			        +APPLY_TIME+" text,"
			        +APPLY_HELP + " text);";
			db.execSQL(sql);
			// 插入数据
			ContentValues cv1 = new ContentValues();
			cv1.put(APPLY_ID, "0001");
			cv1.put(APPLY_NAME, "附近菜品 ");
			cv1.put(APPLY_MEMO, "菜品信息");
			cv1.put(APPLY_LINK, "FoodActivity");
			cv1.put(APPLY_LKNUM, "8");
			cv1.put(APPLY_GRIDICO, "activityfoodorder");
			cv1.put(APPLY_SLIICO, "slifoodorder");
			cv1.put(APPLY_SHOW, "Y");
			cv1.put(APPLY_GROUP, "F");
			cv1.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv1.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv1);
			
			ContentValues cv2 = new ContentValues();
			cv2.put(APPLY_ID, "0002");
			cv2.put(APPLY_NAME, "附近餐馆");
			cv2.put(APPLY_MEMO, "餐馆信息");
			cv2.put(APPLY_LINK, "ShopActivity");
			cv2.put(APPLY_LKNUM, "5");
			cv2.put(APPLY_GRIDICO, "activityshoporder");
			cv2.put(APPLY_SLIICO, "slishoporder");
			cv2.put(APPLY_SHOW, "Y");
			cv2.put(APPLY_GROUP, "F");
			cv2.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv2.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv2);
			
//			ContentValues cv3 = new ContentValues();
//			cv3.put(APPLY_ID, "0003");
//			cv3.put(APPLY_NAME, "附近店长 ");
//			cv3.put(APPLY_MEMO, "店长信息");
//			cv3.put(APPLY_LINK, "ManagerActivity");
//			cv3.put(APPLY_LKNUM, "6");
//			cv3.put(APPLY_GRIDICO, "activitymanagerorder");
//			cv3.put(APPLY_SLIICO, "slimanagerorder");
//			cv3.put(APPLY_SHOW, "Y");
//			cv3.put(APPLY_GROUP, "F");
//			cv3.put(APPLY_TIME, "2013-04-25 09:10:00");
//			cv3.put(APPLY_HELP, "HelpDetailActivity");
//			db.insert(TABLE_APPLY, null, cv3);
			
			ContentValues cv4 = new ContentValues();
			cv4.put(APPLY_ID, "0004");
			cv4.put(APPLY_NAME, "常点收藏");
			cv4.put(APPLY_MEMO, "收藏信息");
			cv4.put(APPLY_LINK, "CollectActivity");
			cv4.put(APPLY_LKNUM, "1");
			cv4.put(APPLY_GRIDICO, "activitycollect");
			cv4.put(APPLY_SLIICO, "slicollect");
			cv4.put(APPLY_SHOW, "Y");
			cv4.put(APPLY_GROUP, "F");
			cv4.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv4.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv4);
			
			ContentValues cv5 = new ContentValues();
			cv5.put(APPLY_ID, "0005");
			cv5.put(APPLY_NAME, "财务账单");
			cv5.put(APPLY_MEMO, "财务账单");
			cv5.put(APPLY_LINK, "BillActivity");
			cv5.put(APPLY_LKNUM, "7");
			cv5.put(APPLY_GRIDICO, "activitybill");
			cv5.put(APPLY_SLIICO, "slibill");
			cv5.put(APPLY_SHOW, "Y");
			cv5.put(APPLY_GROUP, "F");
			cv5.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv5.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv5);
			
			ContentValues cv6 = new ContentValues();
			cv6.put(APPLY_ID, "0006");
			cv6.put(APPLY_NAME, "订单");
			cv6.put(APPLY_MEMO, "订单信息");
			cv6.put(APPLY_LINK, "OrderActivity");
			cv6.put(APPLY_LKNUM, "4");
			cv6.put(APPLY_GRIDICO, "activityfinance");
			cv6.put(APPLY_SLIICO, "slifinance");
			cv6.put(APPLY_SHOW, "Y");
			cv6.put(APPLY_GROUP, "C");
			cv6.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv6.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv6);
			
			ContentValues cv7 = new ContentValues();
			cv7.put(APPLY_ID, "0007");
			cv7.put(APPLY_NAME, "餐桌宠物");
			cv7.put(APPLY_MEMO, "小小宠物");
			cv7.put(APPLY_LINK, "PetActivity");
			cv7.put(APPLY_LKNUM, "9");
			cv7.put(APPLY_GRIDICO, "activitypet");
			cv7.put(APPLY_SLIICO, "slipet");
			cv7.put(APPLY_SHOW, "Y");
			cv7.put(APPLY_GROUP, "C");
			cv7.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv7.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv7);
			
//			ContentValues cv8 = new ContentValues();
//			cv8.put(APPLY_ID, "0008");
//			cv8.put(APPLY_NAME, "发现新大陆");
//			cv8.put(APPLY_MEMO, "日常应用");
//			cv8.put(APPLY_LINK, "CommomActivity");
//			cv8.put(APPLY_LKNUM, "3");
//			cv8.put(APPLY_GRIDICO, "activitycommom");
//			cv8.put(APPLY_SLIICO, "slicommom");
//			cv8.put(APPLY_SHOW, "Y");
//			cv8.put(APPLY_GROUP, "C");
//			cv8.put(APPLY_TIME, "2013-04-25 09:10:00");
//			cv8.put(APPLY_HELP, "HelpDetailActivity");
//			db.insert(TABLE_APPLY, null, cv8);
			
			ContentValues cv9 = new ContentValues();
			cv9.put(APPLY_ID, "0009");
			cv9.put(APPLY_NAME, "语音订餐");
			cv9.put(APPLY_MEMO, "说一声美食到");
			cv9.put(APPLY_LINK, "SpellOrderActivity");
			cv9.put(APPLY_LKNUM, "4");
			cv9.put(APPLY_GRIDICO, "activityspellorder");
			cv9.put(APPLY_SLIICO, "slispellorder");
			cv9.put(APPLY_SHOW, "Y");
			cv9.put(APPLY_GROUP, "F");
			cv9.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv9.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv9);
			
			ContentValues cv10 = new ContentValues();
			cv10.put(APPLY_ID, "0010");
			cv10.put(APPLY_NAME, "帐户设置");
			cv10.put(APPLY_MEMO, "应用设置");
			cv10.put(APPLY_LINK, "SetActivity");
			cv10.put(APPLY_LKNUM, "1");
			cv10.put(APPLY_GRIDICO, "activityset");
			cv10.put(APPLY_SLIICO, "sliset");
			cv10.put(APPLY_SHOW, "Y");
			cv10.put(APPLY_GROUP, "C");
			cv10.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv10.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv10);
			
//			ContentValues cv11 = new ContentValues();
//			cv11.put(APPLY_ID, "0011");
//			cv11.put(APPLY_NAME, "请客吃饭");
//			cv11.put(APPLY_MEMO, "日常活动");
//			cv11.put(APPLY_LINK, "ShareEatActivity");
//			cv11.put(APPLY_LKNUM, "1");
//			cv11.put(APPLY_GRIDICO, "activityshareeat");
//			cv11.put(APPLY_SLIICO, "slishareeat");
//			cv11.put(APPLY_SHOW, "Y");
//			cv11.put(APPLY_GROUP, "C");
//			cv11.put(APPLY_TIME, "2013-04-25 09:10:00");
//			cv11.put(APPLY_HELP, "HelpDetailActivity");
//			db.insert(TABLE_APPLY, null, cv11);
			
			ContentValues cv12 = new ContentValues();
			cv12.put(APPLY_ID, "0012");
			cv12.put(APPLY_NAME, "预订下单");
			cv12.put(APPLY_MEMO, "预订订单信息");
			cv12.put(APPLY_LINK, "BookOrderActivity");
			cv12.put(APPLY_LKNUM, "1");
			cv12.put(APPLY_GRIDICO, "activitybookorder");
			cv12.put(APPLY_SLIICO, "slibookorder");
			cv12.put(APPLY_SHOW, "Y");
			cv12.put(APPLY_GROUP, "C");
			cv12.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv12.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv12);
			
//			ContentValues cv13 = new ContentValues();
//			cv13.put(APPLY_ID, "0013");
//			cv13.put(APPLY_NAME, "一起去吃饭");
//			cv13.put(APPLY_MEMO, "一起去吃饭");
//			cv13.put(APPLY_LINK, "TogetherEatActivity");
//			cv13.put(APPLY_LKNUM, "1");
//			cv13.put(APPLY_GRIDICO, "activitytogethereat");
//			cv13.put(APPLY_SLIICO, "slitogethereat");
//			cv13.put(APPLY_SHOW, "Y");
//			cv13.put(APPLY_GROUP, "C");
//			cv13.put(APPLY_TIME, "2013-04-25 09:10:00");
//			cv13.put(APPLY_HELP, "HelpDetailActivity");
//			db.insert(TABLE_APPLY, null, cv13);
//			
//			ContentValues cv14 = new ContentValues();
//			cv14.put(APPLY_ID, "0014");
//			cv14.put(APPLY_NAME, "掷骰子");
//			cv14.put(APPLY_MEMO, "掷骰子");
//			cv14.put(APPLY_LINK, "ThrowActivity");
//			cv14.put(APPLY_LKNUM, "1");
//			cv14.put(APPLY_GRIDICO, "activitythrow");
//			cv14.put(APPLY_SLIICO, "slithrow");
//			cv14.put(APPLY_SHOW, "Y");
//			cv14.put(APPLY_GROUP, "F");
//			cv14.put(APPLY_TIME, "2013-04-25 09:10:00");
//			cv14.put(APPLY_HELP, "HelpDetailActivity");
//			db.insert(TABLE_APPLY, null, cv14);
			
			ContentValues cv15 = new ContentValues();
			cv15.put(APPLY_ID, "0015");
			cv15.put(APPLY_NAME, "美食达人");
			cv15.put(APPLY_MEMO, "美食达人");
			cv15.put(APPLY_LINK, "GourmetMasterActivity");
			cv15.put(APPLY_LKNUM, "2");
			cv15.put(APPLY_GRIDICO, "activitygourmemaster");
			cv15.put(APPLY_SLIICO, "sligourmemaster");
			cv15.put(APPLY_SHOW, "Y");
			cv15.put(APPLY_GROUP, "C");
			cv15.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv15.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv15);
			
			ContentValues cv16 = new ContentValues();
			cv16.put(APPLY_ID, "0016");
			cv16.put(APPLY_NAME, "摇美食");
			cv16.put(APPLY_MEMO, "摇一摇美食出来");
			cv16.put(APPLY_LINK, "ShakeFoodActivity");
			cv16.put(APPLY_LKNUM, "5");
			cv16.put(APPLY_GRIDICO, "activityshake");
			cv16.put(APPLY_SLIICO, "slishake");
			cv16.put(APPLY_SHOW, "Y");
			cv16.put(APPLY_GROUP, "F");
			cv16.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv16.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv16);
			
			ContentValues cv17 = new ContentValues();
			cv17.put(APPLY_ID, "0017");
			cv17.put(APPLY_NAME, "美食计划");
			cv17.put(APPLY_MEMO, "美食计划");
			cv17.put(APPLY_LINK, "EatPlanActivity");
			cv17.put(APPLY_LKNUM, "5");
			cv17.put(APPLY_GRIDICO, "activityeatplan");
			cv17.put(APPLY_SLIICO, "slieatplan");
			cv17.put(APPLY_SHOW, "Y");
			cv17.put(APPLY_GROUP, "C");
			cv17.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv17.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv17);
			
//			ContentValues cv18 = new ContentValues();
//			cv18.put(APPLY_ID, "0018");
//			cv18.put(APPLY_NAME, "我知你胃");
//			cv18.put(APPLY_MEMO, "我知你胃");
//			cv18.put(APPLY_LINK, "KnowYouActivity");
//			cv18.put(APPLY_LKNUM, "3");
//			cv18.put(APPLY_GRIDICO, "activityknowyou");
//			cv18.put(APPLY_SLIICO, "sliknowyou");
//			cv18.put(APPLY_SHOW, "Y");
//			cv18.put(APPLY_GROUP, "C");
//			cv18.put(APPLY_TIME, "2013-04-25 09:10:00");
//			cv18.put(APPLY_HELP, "HelpDetailActivity");
//			db.insert(TABLE_APPLY, null, cv18);
			
//			ContentValues cv18 = new ContentValues();
//			cv18.put(APPLY_ID, "0018");
//			cv18.put(APPLY_NAME, "智能配餐");
//			cv18.put(APPLY_MEMO, "智能配餐");
//			cv18.put(APPLY_LINK, "KnowYouActivity");
//			cv18.put(APPLY_LKNUM, "3");
//			cv18.put(APPLY_GRIDICO, "activityknowyou");
//			cv18.put(APPLY_SLIICO, "sliknowyou");
//			cv18.put(APPLY_SHOW, "Y");
//			cv18.put(APPLY_GROUP, "C");
//			cv18.put(APPLY_TIME, "2013-04-25 09:10:00");
//			cv18.put(APPLY_HELP, "HelpDetailActivity");
//			db.insert(TABLE_APPLY, null, cv18);

//			ContentValues cv19 = new ContentValues();
//			cv19.put(APPLY_ID, "0019");
//			cv19.put(APPLY_NAME, "星级厨师修炼之道");
//			cv19.put(APPLY_MEMO, "星级厨师修炼之道");
//			cv19.put(APPLY_LINK, "PracticeActivity");
//			cv19.put(APPLY_LKNUM, "3");
//			cv19.put(APPLY_GRIDICO, "activitypractice");
//			cv19.put(APPLY_SLIICO, "slipractice");
//			cv19.put(APPLY_SHOW, "Y");
//			cv19.put(APPLY_GROUP, "C");
//			cv19.put(APPLY_TIME, "2013-04-25 09:10:00");
//			cv19.put(APPLY_HELP, "HelpDetailActivity");
//			db.insert(TABLE_APPLY, null, cv19);
			
			ContentValues cv20 = new ContentValues();
			cv20.put(APPLY_ID, "0020");
			cv20.put(APPLY_NAME, "妙招分享");
			cv20.put(APPLY_MEMO, "妙招分享");
			cv20.put(APPLY_LINK, "CoupShareActivity");
			cv20.put(APPLY_LKNUM, "2");
			cv20.put(APPLY_GRIDICO, "activitycoupshare");
			cv20.put(APPLY_SLIICO, "slicoupshare");
			cv20.put(APPLY_SHOW, "Y");
			cv20.put(APPLY_GROUP, "C");
			cv20.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv20.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv20);
			
			ContentValues cv21 = new ContentValues();
			cv21.put(APPLY_ID, "0021");
			cv21.put(APPLY_NAME, "分享有礼");
			cv21.put(APPLY_MEMO, "分享有礼");
			cv21.put(APPLY_LINK, "ShareGiftActivity");
			cv21.put(APPLY_LKNUM, "2");
			cv21.put(APPLY_GRIDICO, "activitysharegift");
			cv21.put(APPLY_SLIICO, "slisharegift");
			cv21.put(APPLY_SHOW, "Y");
			cv21.put(APPLY_GROUP, "O");
			cv21.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv21.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv21);
			
			ContentValues cv22 = new ContentValues();
			cv22.put(APPLY_ID, "0022");
			cv22.put(APPLY_NAME, "关于我们");
			cv22.put(APPLY_MEMO, "关于我们");
			cv22.put(APPLY_LINK, "AboutUsActivity");
			cv22.put(APPLY_LKNUM, "1");
			cv22.put(APPLY_GRIDICO, "activityaboutus");
			cv22.put(APPLY_SLIICO, "sliaboutus");
			cv22.put(APPLY_SHOW, "Y");
			cv22.put(APPLY_GROUP, "O");
			cv22.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv22.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv22);
			
			ContentValues cv23 = new ContentValues();
			cv23.put(APPLY_ID, "0023");
			cv23.put(APPLY_NAME, "午餐指南");
			cv23.put(APPLY_MEMO, "午餐指南");
			cv23.put(APPLY_LINK, "GuideLunchActivity");
			cv23.put(APPLY_LKNUM, "1");
			cv23.put(APPLY_GRIDICO, "activityguidlunch");
			cv23.put(APPLY_SLIICO, "sliguidlunch");
			cv23.put(APPLY_SHOW, "Y");
			cv23.put(APPLY_GROUP, "O");
			cv23.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv23.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv23);
			
//			ContentValues cv24 = new ContentValues();
//			cv24.put(APPLY_ID, "0024");
//			cv24.put(APPLY_NAME, "我的评论");
//			cv24.put(APPLY_MEMO, "我的评论");
//			cv24.put(APPLY_LINK, "CommentActivity");
//			cv24.put(APPLY_LKNUM, "1");
//			cv24.put(APPLY_GRIDICO, "activitycomment");
//			cv24.put(APPLY_SLIICO, "slicomment");
//			cv24.put(APPLY_SHOW, "Y");
//			cv24.put(APPLY_GROUP, "O");
//			cv24.put(APPLY_TIME, "2013-04-25 09:10:00");
//			cv24.put(APPLY_HELP, "HelpDetailActivity");
//			db.insert(TABLE_APPLY, null, cv24);
			
			ContentValues cv25 = new ContentValues();
			cv25.put(APPLY_ID, "0025");
			cv25.put(APPLY_NAME, "积分查询");
			cv25.put(APPLY_MEMO, "积分查询");
			cv25.put(APPLY_LINK, "IntegralActivity");
			cv25.put(APPLY_LKNUM, "1");
			cv25.put(APPLY_GRIDICO, "activityintergral");
			cv25.put(APPLY_SLIICO, "sliintergral");
			cv25.put(APPLY_SHOW, "Y");
			cv25.put(APPLY_GROUP, "O");
			cv25.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv25.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv25);
			
			ContentValues cv26 = new ContentValues();
			cv26.put(APPLY_ID, "0026");
			cv26.put(APPLY_NAME, "餐馆应答");
			cv26.put(APPLY_MEMO, "餐馆应答");
			cv26.put(APPLY_LINK, "RestResponseActivity");
			cv26.put(APPLY_LKNUM, "1");
			cv26.put(APPLY_GRIDICO, "activityresponse");
			cv26.put(APPLY_SLIICO, "sliresponse");
			cv26.put(APPLY_SHOW, "Y");
			cv26.put(APPLY_GROUP, "O");
			cv26.put(APPLY_TIME, "2013-04-25 09:10:00");
			cv26.put(APPLY_HELP, "HelpDetailActivity");
			db.insert(TABLE_APPLY, null, cv26);
		}
//常点收藏、找餐馆、找店长、找吃的、餐桌宠物、请客吃饭、预订下单、一起去吃饭、掷骰子、美食达人、摇美食、
//美食计划、我知你胃、星级厨师修炼之道、妙招分享、账户设置、查看账单、查看订单、分享有礼、关于我们、午餐指南、我的评论、积分查询

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLY);
			onCreate(db);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
		}
	}
	// 表字段
	String applyProjection[] = { APPLY_ID, APPLY_NAME, APPLY_MEMO, APPLY_LINK,
			APPLY_LKNUM,APPLY_GRIDICO,APPLY_SLIICO, APPLY_SHOW, APPLY_GROUP, APPLY_TIME,APPLY_HELP};

	// 得到所有应用
	public Cursor getAllApplys() {
		return mSQLiteDatabase.query(TABLE_APPLY, applyProjection, null, null,
				null, null, null);
	}

	// 查询
	public Cursor getCursorBySql(String sql, String selectionArgs[]) {
		return mSQLiteDatabase.rawQuery(sql, selectionArgs);
	}

	// 删除
	public int deleteFoods(String id) {
		return mSQLiteDatabase.delete(TABLE_APPLY, APPLY_ID + "'=" + id
				+ "'", null);
	}
	/**
	 * 更新记录
	 */
//	public void update(ContentValues cv, String where, String[] whereValue) {
//		mSQLiteDatabase = dbHelper.getWritableDatabase();
//		mSQLiteDatabase.update(TABLE_APPLY, cv, where, whereValue);
//	
    public void update(String id,String title){                       //修改是否显示
    	mSQLiteDatabase =dbHelper.getWritableDatabase();
    	String where=APPLY_ID+"=?";
    	String[] whereValue={id};
    	ContentValues cv=new ContentValues();
    	cv.put(APPLY_SHOW, title);
    	mSQLiteDatabase.update(TABLE_APPLY, cv, where, whereValue);
    }

}
