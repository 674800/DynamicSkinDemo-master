package com.xinshen.dynamtheme;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述:皮肤控件接口监听类
 * @create 2018-08-25 13:02
 */
public class MySkinFactory implements LayoutInflater.Factory {

    //自定义的控件，这里面放包控件的包路径

    private List<SkinItem> skinItems = new ArrayList<>();

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = createView(name,context,attrs);
        Log.e("MySkinFactory","name="+name);
        if (view!=null){
            collectViewAttr(view,context,attrs);
        }
        return view;
    }

    private View createView(String name, Context context, AttributeSet attrs) {
        View view = null;
        try {
            if (-1 == name.indexOf('.')){	//不带".",说明是系统的View
                if ("View".equals(name)) {
                    view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs);
                }
            }else {	//带".",说明是自定义的View
                view = LayoutInflater.from(context).createView(name, null, attrs);
            }
        } catch (Exception e) {
            view = null;
        }
        return view;
    }

    /**
     * 筛选，过滤，把符合条件的控件和资源存储起来
     * @param view
     * @param context
     * @param attrs
     */
    private void collectViewAttr(View view,Context context, AttributeSet attrs) {
        List<SkinAttr> skinAttrs = new ArrayList<>();
        int attCount = attrs.getAttributeCount();
        for (int i = 0;i<attCount;++i){
            String attributeName = attrs.getAttributeName(i);//textColor
            String attributeValue = attrs.getAttributeValue(i);//@7f030000 R.java
            Log.e("MySkinFactory","attributeName="+attributeName);
           Log.e("MySkinFactory","attributeValue="+attributeValue);
            if (isSupportedAttr(attributeName)){
                if (attributeValue.startsWith("@")){
                    //得到属性值id
                    int resId = Integer.parseInt(attributeValue.substring(1));
                    //得到属性名
                    String resName = context.getResources().getResourceEntryName(resId);
                    //得到属性类型
                    String attrType = context.getResources().getResourceTypeName(resId);
                    //创建皮肤实例
                    skinAttrs.add(new SkinAttr(attributeName,attrType,resName,resId));
                    //创建实例，保存,用来换肤
                    SkinItem skinItem = new SkinItem(view, skinAttrs);
//                    Log.e("MySkinFactory","resName ="+resName +"::"+"attrType="+attrType);
                    if (SkinManager.getInstance().isExternalSkin()){
                        skinItem.apply();
                    }
                    skinItems.add(skinItem);
                }
                Log.e("MySkinFactory","----------------------------------------------------------------");
            }

        }
    }
    //如果是背景或者textColor属性
    private boolean isSupportedAttr(String attributeName){
        return "background".equals(attributeName) || "textColor".equals(attributeName);
    }

    public void apply(){
        for (SkinItem item : skinItems) {
            item.apply();
        }
    }

}
