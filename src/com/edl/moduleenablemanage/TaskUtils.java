package com.edl.moduleenablemanage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

/**
 * 任务相关工具类
 * @author liuyazhuang
 *
 */
public class TaskUtils {
	
	/**
	 * 获取当前正在进行的进程数
	 * @param context
	 * @return
	 */
	public static int getRunningAppProcessInfoSize(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}
	
	/**
	 * 获取系统可用内存
	 * @param context
	 * @return
	 */
	public static long getAvailMem(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//得到可用内存
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		long availMem = outInfo.availMem; //单位是byte
		return availMem;
	}
	
	/**
	 * 获取系统所有的进程信息列表
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context){
		List<TaskInfo> taskInfos  = new ArrayList<TaskInfo>();
		PackageManager pm = context.getPackageManager();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info : runningAppProcesses){
			TaskInfo taskInfo = new TaskInfo();
			//进程名称
			String packageName = info.processName;
			taskInfo.setPackageName(packageName);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				//图标
				Drawable task_icon = applicationInfo.loadIcon(pm);
				if(task_icon == null){
					taskInfo.setTask_icon(context.getResources().getDrawable(R.drawable.ic_launcher));
				}else{
					taskInfo.setTask_icon(task_icon);
				}
				//名称
				String task_name = applicationInfo.loadLabel(pm).toString();
				taskInfo.setTask_name(task_name);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//进程id
			int pid = info.pid;
			taskInfo.setPid(pid);
			//获取进程占用的内存
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{pid});
			android.os.Debug.MemoryInfo memoryInfo  = processMemoryInfo[0];
			long totalPrivateDirty = memoryInfo.getTotalPrivateDirty(); //KB
			taskInfo.setTask_memory(totalPrivateDirty);
			taskInfos.add(taskInfo);
		}
		return taskInfos;
	}

	/**
	 * 获取系统所有的进程信息列表
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context,Map<String, String> all){
		List<TaskInfo> taskInfos  = new ArrayList<TaskInfo>();
		PackageManager pm = context.getPackageManager();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		TaskInfo taskInfo;
		ApplicationInfo applicationInfo;
		
		String packageName,taskName;
		Drawable task_icon;
		
		for(RunningAppProcessInfo info :  am.getRunningAppProcesses()){
			 taskInfo = new TaskInfo();
			//进程名称
			 packageName = info.processName;
			if(packageName.contains(":"))
				packageName = packageName.substring(0, packageName.lastIndexOf(":"));
			if(all.containsKey(packageName))
				continue;
			
			try {
				applicationInfo = pm.getApplicationInfo(packageName, 0);
				//图标
				task_icon = applicationInfo.loadIcon(pm);
				taskInfo.setTask_icon(task_icon == null?context.getResources().getDrawable(R.drawable.default_launcher):task_icon);
				//名称
				taskName=applicationInfo.loadLabel(pm).toString();
				taskInfo.setTask_name(TextUtils.isEmpty(taskName)?packageName:taskName);
			} catch (NameNotFoundException e) {
				taskInfo.setPackageName(packageName);
				taskInfo.setTask_icon(context.getResources().getDrawable(R.drawable.default_launcher));
				taskInfo.setTask_name(packageName);
				e.printStackTrace();
			}
			taskInfos.add(taskInfo);
		}
		return taskInfos;
	}
}
