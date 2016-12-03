package com.edl.moduleenablemanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ModuleListAdapter extends BaseAdapter implements android.view.View.OnClickListener
{

	private Context mContext;
	private PackageManager mPackageManager;

	//item;
	private int mCount;
	private View mVIcon;
	private TextView mTvItem;
	private ResolveInfo mRiAppInfo;
	private TaskInfo mGoneAppInfo;
	private String[] mLabels;

	//ccache
	private View[] mViewCache;
	private int mViewCacheSize = 0;

	//info
	private List<ResolveInfo> mApplist;
	private List<TaskInfo> mAppGoneList;
	private  Map<String, String> mMapDisble;

	private ModuleListAdapter(Context context, String[] objects,List<ResolveInfo> list, Map<String, String> mapDisable,List<TaskInfo> listGone)
	{
		mContext=context;
		mLabels=objects;
		mApplist=list;
		mAppGoneList=listGone;
		mPackageManager=context.getPackageManager();
		mCount=list.size()+listGone.size();
		mMapDisble=mapDisable;
	}
	 public static int getLauncherPackageName(Context context,String type) {
//	        final Intent intent = new Intent(Intent.ACTION_MAIN);
//	        intent.addCategory(Intent.CATEGORY_HOME);
//	        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
//	        if (res.activityInfo == null) {
//	            // should not happen. A home is always installed, isn't it?
//	            return null;
//	        }
//	        if (res.activityInfo.packageName.equals("android")) {
//	            // 有多个桌面程序存在，且未指定默认项时；     
//	            return null;
//	        } else {
//	            return res.activityInfo.packageName;
//	        }

			Intent mainIntent = new Intent(Intent.ACTION_MAIN).addCategory(type);
			PackageManager pm=context.getPackageManager();
			List<ResolveInfo> listAll = pm.queryIntentActivities(mainIntent,pm.GET_DISABLED_COMPONENTS);
			return listAll.size();
	    }

	public static ModuleListAdapter getAdapter(Context context)
	{
		Log.d("123456", getLauncherPackageName(context,Intent.CATEGORY_HOME)+"   sssss");
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager pm=context.getPackageManager();
		List<ResolveInfo> listAll = pm.queryIntentActivities(mainIntent,pm.GET_DISABLED_COMPONENTS);
		List<ResolveInfo> listDefault = pm.queryIntentActivities(mainIntent,pm.COMPONENT_ENABLED_STATE_DEFAULT);
		List<ResolveInfo> listDisable = new ArrayList<ResolveInfo>();

		List<ApplicationInfo> listApps= pm.getInstalledApplications(pm.GET_ACTIVITIES);
		//可禁用桌面应用+已禁用的桌面应用+可禁用的隐藏应用+已禁用的隐藏应用+不可禁用的桌面应用+不可禁用的隐藏应用
		Map<String, String> mapAll=new HashMap<String, String>();
		//可禁用桌面应用+不可禁用的桌面应用
		Map<String, String> mapDefault=new HashMap<String, String>();
		//已禁用的桌面应用+已禁用的隐藏应用
		Map<String, String> mapDisble=new HashMap<String, String>();
		//不可禁用的桌面应用+不可禁用的隐藏应用
		Map<String, String> mapNoDisble=new HashMap<String, String>();

		String[] names=new String[listAll.size()];
		int index=0;
		String dialer=null,label=null;
		for(ResolveInfo ri:listDefault)
			{
				label=ri.activityInfo.packageName;
				mapDefault.put(label, String.valueOf(index));
			}
		
		//不可禁用的桌面应用+不可禁用的隐藏应用 列表初始化
		index=0;
		String[] noDisableApps=context.getResources().getStringArray(R.array.black_app_group);
		for(String  pn:noDisableApps)
			{
				mapAll.put(pn, String.valueOf(index));
				mapNoDisble.put(pn, String.valueOf(index));
			}

		index=0;
		String packageName;
		for (Iterator iter = listAll.iterator(); iter.hasNext();) {
			ResolveInfo ri = (ResolveInfo)iter.next();
			packageName=ri.activityInfo.packageName;

			if(mapNoDisble.containsKey(packageName)){
					iter.remove();
					continue;
			}else{
					names[index]=(String)ri.loadLabel(pm);
					//可禁用桌面应用+已禁用的桌面应用
					mapAll.put(packageName, String.valueOf(index));
					index++;
					if(mapDefault.containsKey(packageName))
						continue;
					listDisable.add(ri);
					mapDisble.put(packageName, String.valueOf(index));
				}
		   }
		//TaskUtils.getTaskInfos(context,mapAll)//可禁用的隐藏应用+已禁用的隐藏应用
		return new ModuleListAdapter(context,names,listAll,mapDisble,TaskUtils.getTaskInfos(context,mapAll));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (position >= mViewCacheSize)
			{
				flushViewCache();
			}
		convertView = mViewCache[position];
		if(convertView==null)
			{
				convertView=LayoutInflater.from(mContext).inflate(R.layout.edl_fingerprint_app_list_item, null);
				convertView.setLayoutParams(new LayoutParams(480, 120));
				convertView.setOnClickListener(this);
			}
		mVIcon=convertView.findViewById(R.id.v_icon);
		mTvItem=(TextView)convertView.findViewById(R.id.tv_content);

		String  appPackageName=null;
		if(position<mApplist.size())
			{
				mRiAppInfo=mApplist.get(position);
				if(mRiAppInfo!=null)
					{
						if(mLabels!=null)
							mTvItem.setText(mLabels[position]);
						appPackageName=mRiAppInfo.activityInfo.packageName.replace(" ", "");
						mVIcon.setBackground(mRiAppInfo.loadIcon(mPackageManager));
					}
			}else {
				mGoneAppInfo=mAppGoneList.get(position-mApplist.size());
				if(mGoneAppInfo!=null)
					{
						mTvItem.setText(mGoneAppInfo.getTask_name());
						appPackageName=mGoneAppInfo.getPackageName();
						mVIcon.setBackground(mGoneAppInfo.getTask_icon());
					}
			}
		convertView.setTag(appPackageName);
		if(mMapDisble.containsKey(appPackageName))
			{
				convertView.setBackgroundResource(R.color.list_item_backup_pressed);
			}
		mViewCache[position]=convertView;
		return convertView;
	}

	@Override
	public int getCount()
	{
		return mCount;
	}

	@Override
	public Object getItem(int position)
	{
		if(position<=mViewCacheSize)
			return mViewCache[position];
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	public boolean flushViewCache() {
		mViewCacheSize = getCount();
		mViewCache = null;
		mViewCache = new View[mViewCacheSize];
		return true;
	}

	@Override
	public void onClick(View arg0)
	{
		try
			{
				String packageName=arg0.getTag().toString();
				boolean isEnable=mMapDisble.containsKey(packageName);
				Utils.execCommand("pm",isEnable?"enable":"disable",packageName);
				if(isEnable)
					{
						mMapDisble.remove(packageName);
						arg0.setBackgroundColor(Color.TRANSPARENT);
					}
				else
					{
						mMapDisble.put(packageName,String.valueOf(mMapDisble.size()));
					}
				notifyDataSetChanged();
				isEnable=!isEnable;
			}
		catch (Exception e)
			{
				Log.d("123456",e.toString());
			}
	}


}
