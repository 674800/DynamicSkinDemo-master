package com.xinshen.dynamtheme.customView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinshen.dynamtheme.R;

import java.util.ArrayList;

/**
 * Created by ybf on 2019/4/11.
 */
public class CustumAdapter extends BaseAdapter {
    private String TAG ="CustumAdapter";
    ArrayList<String> list;
    Context mContext;
    public CustumAdapter( Context mContext){
        this.mContext = mContext;
         list = new ArrayList<>();
        list.add("aadfa");
        list.add("badfa");
        list.add("cadfa");
        list.add("dadfa");
        list.add("eadff");
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_item, null);
            holder.text = (TextView) convertView.findViewById(R.id.ItmeText);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.e(TAG,"list.get(position)="+list.get(position));
        holder.text.setText(list.get(position));
        return convertView;
    }

    /**
     * ViewHolder类用以储存item中控件的引用
     */
    final class ViewHolder {
        ImageView image;
        TextView title;
        TextView text;
    }
}
