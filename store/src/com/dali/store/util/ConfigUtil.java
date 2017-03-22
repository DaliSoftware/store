package com.dali.store.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/***
 * 获取配置文件信息 lrx 2014-4-17 v1.0
 */
public class ConfigUtil {
	/**
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            需要获取的key名
	 * @return 字符串
	 * @throws NameNotFoundException
	 */
	public static String getValue(Context context, String key)
			throws NameNotFoundException {
		PackageManager pm = context.getPackageManager();
		ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(),
				PackageManager.GET_META_DATA);
		Bundle bundle = ai.metaData;
		String str = bundle.getString(key);
		String result = str.substring(0, str.length() - 1);
		return result;
	}
}
