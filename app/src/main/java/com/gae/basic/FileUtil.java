package com.gae.basic;
/*
 * author:eity
 * version:2013-3-1
 * description:保存文件到sdcard
 * */
import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class FileUtil {
	public static File updateDir = null;
	public static File updateFile = null;

	public static void createFile(String name) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory()
					+ "/" + MyApplication.downloadDir);
			updateFile = new File(updateDir + "/" + name + ".apk");

			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
