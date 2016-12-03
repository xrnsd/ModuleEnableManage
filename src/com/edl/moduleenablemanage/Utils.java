package com.edl.moduleenablemanage;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class Utils
{
	public static void setAppEnablebyPackageName(boolean isEnable,final String... packageNames){
		for(String pn:packageNames){
			rootCommandPrivateHandle(isEnable?"pm enable ":"pm disable "+pn);
			}
//		Utils.rootCommand((isChecked?"pm disable ":"pm enable ")
//		+buttonView.getTag().toString());
	}

	public static void setAppEnablebyPackageName(final String... packageNames){
		for(String pn:packageNames){
			rootCommandPrivateHandle("pm enable "+pn);
			}
	}
	
	public static void setAppDisablebyPackageName(final String... packageNames){
		for(String pn:packageNames){
			rootCommandPrivateHandle("pm disable "+pn);
			
		}
	}
	
	private static boolean rootCommandPrivateHandle(final String command){
	String mAppPackageName = null;
	final int ENABLE_PACKAGE_WAIT_TIME_LONG=0;

	 Runnable enablePackageRunnable;
	 Handler enablePackageProcess;
	HandlerThread thread = new HandlerThread("EnabledApp="+mAppPackageName); 
			thread.start(); 
			enablePackageProcess = new Handler(thread.getLooper()); 
			enablePackageRunnable=new Runnable(){
			    public void run(){
			    	rootCommandPrivate(command);
			    }
			};
			return enablePackageProcess.postDelayed(enablePackageRunnable, ENABLE_PACKAGE_WAIT_TIME_LONG);
}
	
	

	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	 *
	 * @param command
	 *            命令：String apkRoot="chmod 777 "+getPackageCodePath();
	 *            rootCommand(apkRoot);
	 * @return 应用程序是/否获取Root权限
	 */
	private static boolean rootCommandPrivate(String command)
	{
		Process process = null;
		DataOutputStream os = null;
		try
			{
				process = Runtime.getRuntime().exec("su");
				os = new DataOutputStream(process.getOutputStream());
				os.writeBytes(command + "\n");
				os.writeBytes("exit\n");
				os.flush();
				process.waitFor();
			}
		catch (Exception e)
			{
				Log.d("123456", "ROOT REE" + e.getMessage());
				return false;
			}
		finally
			{
				try
					{
						if (os != null)
							{
								os.close();
							}
						process.destroy();
					}
				catch (Exception e)
					{
					}
			}
		Log.d("123456", "Root SUC ");
		return true;
	}

	// execCommand("pm","enable", packageName);
	// execCommand("pm","disable", packageName);
//	/**
//	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
//	 *
//	 * @param command
//	 *            命令：String apkRoot="chmod 777 "+getPackageCodePath();
//	 *            rootCommand(apkRoot);
//	 * @return 应用程序是/否获取Root权限
//	 */
	public static void execCommand(final String... command)
	{
		new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					Process process = null;
					InputStream errIs = null;
					InputStream inIs = null;
					String result = "";

					try
						{
							process = new ProcessBuilder().command(command).start();
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							int read = -1;
							errIs = process.getErrorStream();
							while ((read = errIs.read()) != -1)
								{
									baos.write(read);
								}
							inIs = process.getInputStream();
							while ((read = inIs.read()) != -1)
								{
									baos.write(read);
								}
							result = new String(baos.toByteArray());
							if (inIs != null)
								inIs.close();
							if (errIs != null)
								errIs.close();
							process.destroy();
						}
					catch (IOException e)
						{
							result = e.getMessage();
						}
					// return result;
					Log.d("123456", "result="+result.toString());
				}

			}).start();
	}

}
