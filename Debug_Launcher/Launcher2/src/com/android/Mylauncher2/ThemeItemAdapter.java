package com.android.Mylauncher2;

import java.util.List;

import com.android.Mylauncher.R;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ThemeItemAdapter extends BaseAdapter {
	
	private LayoutInflater layoutInflater;   
	private List<ThemeItem> allThemeApps = null;
	
	public ThemeItemAdapter() {
	}
	
	public ThemeItemAdapter(Activity activity, List<ThemeItem> allThemeApps) {
		this.allThemeApps = allThemeApps;
		layoutInflater = LayoutInflater.from(activity);
	}

	@Override
	public int getCount() {
		return allThemeApps.size();
	}

	@Override
	public Object getItem(int position) {
		return allThemeApps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
        if(convertView==null) {
        	convertView = layoutInflater.inflate(R.layout.theme_item, null);
        	
        	holder = new ViewHolder();
        	holder.ivIcon = (ImageView)convertView.findViewById(R.id.theme_image);
        	holder.tvTitle = (TextView)convertView.findViewById(R.id.theme_title);
        	
        	convertView.setTag(holder);
        }
        else { 
			holder = (ViewHolder)convertView.getTag(); 
		}
        
        holder.ivIcon.setImageDrawable(allThemeApps.get(position).getImageId());
        holder.tvTitle.setText(allThemeApps.get(position).getTitle());
        if(allThemeApps.get(position).isCurrentTheme()) {
        	//holder.tvTitle.setTextColor(Color.parseColor("#669900"));
        	//convertView.setBackgroundResource(Color.parseColor("#669900"));
        	//convertView.setBackgroundColor(Color.parseColor("#669900"));
        	convertView.setBackgroundResource(R.drawable.selected_theme_bg);
        }
    	return convertView;
	}
	
	class ViewHolder {
    	
    	public ImageView ivIcon;
    	
    	public TextView tvTitle;
    }

}
