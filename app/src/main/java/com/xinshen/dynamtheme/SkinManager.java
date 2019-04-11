package com.xinshen.dynamtheme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @create 2018-08-25 13:04
 */
public class SkinManager {
    private String TAG = getClass().getSimpleName();

    private static final SkinManager mInstance = new SkinManager();
    private Resources mSkinResources;
    private Context context;
    private String skinPackageName;
    private boolean isExternalSkin;
    private List<SkinUpdateListener> mListeners = new ArrayList<>();
    private final String KEY = "skin_path";

    private SkinManager() {
    }

    public static SkinManager getInstance() {
        return mInstance;
    }

    private void judge() {
        if (context == null) {
            throw new IllegalStateException("context is null");
        }
    }

    @SuppressLint("StaticFieldLeak")
    class LoadTask extends AsyncTask<String, Void, Resources> {

        @Override
        protected Resources doInBackground(String... paths) {
            try {
                if (paths.length == 1) {
                    String skinPkgPath = paths[0];
                    File file = new File(skinPkgPath);
                    if (!file.exists()) {
                        return null;
                    }
                    PackageManager mPm = context.getPackageManager();

                    PackageInfo mInfo = mPm.getPackageArchiveInfo(skinPkgPath, PackageManager.GET_ACTIVITIES);
                    Log.e(TAG,"mInfo="+mInfo);
                    //得到插件的包名
                    skinPackageName = mInfo.packageName;
                    Log.e(TAG,"skinPackageName="+skinPackageName);
                    AssetManager assetManager = AssetManager.class.newInstance();
                    Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",
                            String.class);
                    addAssetPath.invoke(assetManager, skinPkgPath);
                    Resources superRes = context.getResources();
                    //得到皮肤插件的resource
                    Resources skinResource = new Resources(assetManager, superRes
                            .getDisplayMetrics(), superRes.getConfiguration());
                    saveSkinPath(skinPkgPath);

                    return skinResource;
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Resources resources) {
            super.onPostExecute(resources);
            mSkinResources = resources;
            if (mSkinResources != null) {
                isExternalSkin = true;
                notifySkinUpdate();
            }
        }
    }

    public interface SkinUpdateListener {
        void onSkinUpdate();
    }

    private void notifySkinUpdate() {
        for (SkinUpdateListener listener : mListeners) {
            listener.onSkinUpdate();
        }
    }

    /**
     * API
     */
    public void addSkinUpdateListener(SkinUpdateListener listener) {
        if (listener == null)
            return;
        judge();
        mListeners.add(listener);
    }

    public String getSkinPath() {
        judge();
        String skinPath = (String) SPUtil.get(context, KEY, "");
        Log.i(TAG,"sink path=" +skinPath);
        return TextUtils.isEmpty(skinPath) ? null : skinPath;
    }

    public void saveSkinPath(String path) {
        judge();
        SPUtil.put(context, KEY, path);
    }

    public boolean isExternalSkin() {
        judge();
        return isExternalSkin;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        String skinPath = (String) SPUtil.get(context, KEY, "");
        isExternalSkin = !TextUtils.isEmpty(skinPath);
        loadSkin(getSkinPath());
    }

    /**
     * 加载皮肤，这里是进行模拟插件化形式，所以先把skin.apk复制到设备的sd卡。
     * @param path 皮肤的apk路径  /storage/emulated/0/skin.apk
     */
    public void loadSkin(String path) {
        judge();
        if (path == null)
            return;
        new LoadTask().execute(path);
    }
    /**
     *通过资源名 去插件中找对应的资源，用来替换宿主apk里面的。
     * @param resId 是宿主view 对应的属性对应的id
     * @return
     */
    public int getColor(String resName,int resId) {
        judge();
        int originColor = context.getResources().getColor(resId);
        if(mSkinResources == null || !isExternalSkin){
            return originColor;
        }
        //资源名，资源类型，包名
        int newResId = mSkinResources.getIdentifier(resName, "color", skinPackageName);
        int newColor;
        try{
            newColor = mSkinResources.getColor(newResId);
        }catch(Resources.NotFoundException e){
            e.printStackTrace();
            return originColor;
        }
        return newColor;
    }
    /**
     * 图片资源
     *通过资源名 去插件中找对应的资源，用来替换宿主apk里面的。
     * @param resId 是宿主view 对应的属性对应的id
     * @return
     */
    //1，首先通过宿主的resId找到资源名，然后因为宿主和插件apk的资源名是一样的，
    // 2，通过资源名去插件中找到对应的资源名的id
    // 3，再通过找到的id去获取插件中对应的资源的值，返回给宿主进行替换。
    public Drawable getDrawable(String resName,int resId){
        judge();
        Drawable originDrawable = context.getResources().getDrawable(resId);
        if(mSkinResources == null || !isExternalSkin){
            return originDrawable;
        }
        int newResId = mSkinResources.getIdentifier(resName, "drawable", skinPackageName);
        Drawable newDrawable;
        try{
            if(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                newDrawable = mSkinResources.getDrawable(newResId);
            }else{
                newDrawable = mSkinResources.getDrawable(newResId, null);
            }
        }catch(Resources.NotFoundException e){
            e.printStackTrace();
            return originDrawable;
        }
        return newDrawable;
    }

    /**
     * 默认主题
     */
    public void restoreDefaultTheme(){
        judge();
        SPUtil.put(context, KEY, "");
        isExternalSkin= false;
        mSkinResources = null;
        notifySkinUpdate();
    }

}
