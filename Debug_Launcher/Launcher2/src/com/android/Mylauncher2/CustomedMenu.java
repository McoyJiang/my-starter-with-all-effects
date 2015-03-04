package com.android.Mylauncher2;

import java.util.List;

import com.android.Mylauncher.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;


/**
 * �Զ�����ʽ�˵�
 */
public class CustomedMenu extends PopupWindow {

	private GridView gvMenu;
    
	private View view;
	
	public static final int MENU_ADD = 0;
	public static final int MENU_ITEM_EDIT_SCREEN = 1;
	public static final int MENU_ITEM_LAUNCHER_THEME = 2;
	public static final int MENU_ITEM_WALLPAPER = 3;
	public static final int MENU_ITEM_SYSTEM_SETTINGS = 4;
	public static final int MENU_ITEM_LAUNCHER_SETTINGS = 5;
	
    /**
     * @param activity
     * @param menus
     */
    public CustomedMenu(Activity activity, List<CustomedMenuItem> menus) {  
        super(activity); 
        
        view = activity.getLayoutInflater().inflate(R.layout.menu, null);  
        //��ѡ����   
        gvMenu = (GridView)view.findViewById(R.id.gv_menu);
        
        //����Ĭ����   
        this.setContentView(view);  
        this.setWidth(LayoutParams.FILL_PARENT);  
        this.setHeight(LayoutParams.WRAP_CONTENT);
        
        //�˵�����
        Drawable drawable = activity.getResources().getDrawable(R.drawable.selector_focused_shape); 
        
        this.setBackgroundDrawable(drawable);
        this.setAnimationStyle(R.style.PopupAnimation);  
        this.setFocusable(true);// menu�˵���ý��� ���û�л�ý���menu�˵��еĿؼ��¼��޷���Ӧ
        
        //���menu��ť popup�޷���ʧ����
        gvMenu.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_MENU)&&(isShowing())) {  
					dismiss();// ����д��ģ��menu��PopupWindow�˳�����   
			        return true;  
			    }  
				return false;
			}
		});
        
        adapter = new MenuAdapter(activity, menus);
        gvMenu.setAdapter(adapter);  
    }
    
    public void setMenuItemListener(OnItemClickListener menuClickListener) {
    	gvMenu.setOnItemClickListener(menuClickListener);  
    }
    
    
    
    private MenuAdapter adapter;
    
    public void dataUpdate() {
    	adapter.notifyDataSetChanged();
    }
      
    /** 
     * �Զ���Adapter
     *  
     */  
    class MenuAdapter extends BaseAdapter {  
        
    	private List<CustomedMenuItem> menus;
    	
    	//������ת����View����
    	private LayoutInflater layoutInflater;
    	
    	
    	
        /** 
         * ����TabMenu�ķ�ҳ���� 
         * @param context ���÷��������� 
         * @param texts ��ť���ϵ��ַ����� 
         * @param resID ��ť���ϵ�ͼ����Դ���� 
         * @param fontSize ��ť�����С 
         * @param color ��ť������ɫ 
         */  
        public MenuAdapter(Activity activity, List<CustomedMenuItem> menus) {  
            this.menus = menus;  
            layoutInflater = LayoutInflater.from(activity);
        }
        
        @Override
        public int getCount() {  
            return menus.size();  
        }  
        
        @Override
        public Object getItem(int position) {  
            return menus.get(position)  ;
        }  
        
        @Override
        public long getItemId(int position) {  
            return position;  
        }  
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {  
            MenuHolder holder = null;
            if(convertView==null) {
            	convertView = layoutInflater.inflate(R.layout.item_menu, null);
            	
            	holder = new MenuHolder();
            	holder.ivIcon = (ImageView)convertView.findViewById(R.id.iv_menu_item_icon);
            	holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_menu_item_title);
            	
            	convertView.setTag(holder);
            }
            else { 
    			holder = (MenuHolder)convertView.getTag(); 
    		}
            
            holder.ivIcon.setImageResource(menus.get(position).getIconID());
            holder.tvTitle.setText(menus.get(position).getTitle());
        	return convertView;
        }
        
        class MenuHolder {
        	
        	/**
        	 * �˵�itemͼ��
        	 */
        	public ImageView ivIcon;
        	
        	/**
        	 * �˵�item���
        	 */
        	public TextView tvTitle;
        }
    }  
}
