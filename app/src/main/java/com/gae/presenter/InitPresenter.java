package com.gae.presenter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import com.gae.UInterface.IInitAction;
import com.gae.basic.MyApplication;
import com.gae.basic.UpdateService;
import com.gae.eat2013.R;
import com.gae.entity.EatParams;
import com.sd.core.network.async.AsyncTaskManager;

/**
 * 初始化页面逻辑处理类
 * Created by sien on 2015/8/10.
 */
public class InitPresenter extends BasePresenter{

    private IInitAction impl;
    private Context context;

    private PackageInfo pi = null;
    private AsyncTaskManager mAsyncTaskManager;
    private String url_class = "";			//url连接
    private EatParams eatparamInstance; //公共参数实例对象

    public InitPresenter(Context context){
        this.context = context;
        impl = (IInitAction) context;

        eatparamInstance = EatParams.getInstance();

        //初始化异步框架
        mAsyncTaskManager = AsyncTaskManager.getInstance(context);
    }

    //获取配置信息
    public void getConfigInfo(){

    }

    private void loadRemoteConfig(){

    }

    /**设置版本信息*/
    public void setVersionInfo(){
        PackageManager pm = context.getPackageManager();
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);

            if(impl != null){
                impl.setVersion(pi.versionName);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initUI(){
        if( impl != null)  impl.check2Page();
    }

    /***
     * 检查是否更新版本
     */
    public void checkVersion() {
        MyApplication myApplication = (MyApplication) context.getApplicationContext();
        if (myApplication.localVersion < Integer.valueOf(pi.versionCode)) {

            // 发现新版本，提示用户更新
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("软件升级")
                    .setMessage("发现新版本,建议立即更新使用.")
                    .setPositiveButton("更新",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // 开启更新服务UpdateService
                                    // 这里为了把update更好模块化，可以传一些updateService依赖的值
                                    // 如布局ID，资源ID，动态获取的标题,这里以app_name为例
                                    Intent updateIntent = new Intent(
                                            context,
                                            UpdateService.class);
                                    updateIntent.putExtra(
                                            "app_name",
                                            eatparamInstance.getAppName());
                                    context.startService(updateIntent);
                                    SharedPreferences sharedPreferences = context.getSharedPreferences("guidnum", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                                    editor.putString("guidnum", "");
                                    editor.commit();//提交修改
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    initUI();
                                    dialog.dismiss();
                                }
                            });
            alert.create().show();

        }else{
            initUI();
        }
    }
}
