package com.dali.app.base.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class ConfigUtil {
	/**
	 * 
	 * @param context
	 *            ������
	 * @param key
	 *            ��Ҫ��ȡ��key��
	 * @return �ַ���
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
