package com.sxu.baselibrary.commonutils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/*******************************************************************************
 * Description: 文件相关的操作
 *
 * Author: Freeman
 *
 * Date: 2017/6/20
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class FileUtil {

	private FileUtil() {

	}

	/**
	 * 默认的字符编码
	 */
	private final static String DEFAULT_CHARSET_NAME = "UTF-8";

	/**
	 * SD卡是否有效
	 * @return
	 */
	public static boolean sdcardIsValid() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * 文件是否存在
	 * @param filePath
	 * @return
	 */
	public static boolean fileIsExist(String filePath) {
		if (sdcardIsValid()) {
			File file = new File(filePath);
			return file.exists();
		}

		return false;
	}

	/**
	 * 创建新的目录
	 * @param path
	 * @return
	 */
	public static boolean mkdirs(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return file.mkdirs();
		}

		return false;
	}

	/**
	 * 获取APP的缓存路径 data/data/包名/cache
	 * @param context
	 * @return
	 */
	public static String getPrivatePath(Context context) {
		return context.getCacheDir().getAbsolutePath();
	}

	/**
	 * 保存文件到指定目录
	 * @param directoryPath
	 * @param fileName
	 * @param content
	 */
	public static void saveFile(String directoryPath, String fileName, String content) {
		if (!sdcardIsValid()) {
			return;
		}

		File directory = new File(directoryPath);
		boolean directoryCreated = directory.exists();
		if (!directoryCreated) {
			directoryCreated = directory.mkdirs();
			if (!directoryCreated) {
				return;
			}
		}

		File file = new File(directoryPath, fileName);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			outputStream.write(content.getBytes(DEFAULT_CHARSET_NAME));
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
					e.printStackTrace(System.out);
				}
			}
		}
	}

	/**
	 * 删除指定文件
	 * @param directoryPath
	 * @param fileName
	 */
	public static void deleteFile(String directoryPath, String fileName) {
		deleteFile(directoryPath + File.separator + fileName);
	}

	/**
	 * 删除指定文件
	 * @param filePath
	 */
	public static void deleteFile(String filePath) {
		if (sdcardIsValid()) {
			return;
		}

		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 读取指定文件的内容
	 * @param directoryPath
	 * @param fileName
	 * @return
	 */
	public static String readFile(String directoryPath, String fileName) {
		return readFile(directoryPath + File.separator + fileName);
	}

	/**
	 * 读取指定文件的内容
	 * @param filePath
	 * @return
	 */
	public static String readFile(String filePath) {
		if (!sdcardIsValid()) {
			return null;
		}

		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			int len = inputStream.available();
			byte[] buffer = new byte[len];
			inputStream.read(buffer);
			inputStream.close();
			return Arrays.toString(buffer);
		} catch (Exception e) {
			quietClose(inputStream);
		}

		return null;
	}

	/**
	 * 读取Asset中的文件
	 * @param fileName
	 * @return
	 */
	public static String readAssetFile(Context context, String fileName) {
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = context.getAssets().open(fileName);
			reader = new BufferedReader(new InputStreamReader(inputStream, DEFAULT_CHARSET_NAME));
			String lineStr;
			StringBuilder builder = new StringBuilder();
			while ((lineStr = reader.readLine()) != null) {
				builder.append(lineStr);
			}
			inputStream.close();
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			quietClose(reader);
			quietClose(inputStream);
		}

		return null;
	}

	/**
	 * 获取指定文件的大小
	 * @param directoryPath
	 * @param fileName
	 * @return
	 */
	public static long getFileSize(String directoryPath, String fileName) {
		return getFileSize(directoryPath + File.separator + fileName);
	}

	/**
	 * 获取指定文件的大小
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		if (sdcardIsValid()) {
			File file = new File(filePath);
			if (file.exists()) {
				return file.getTotalSpace();
			}
		}

		return 0;
	}

	public static void quietClose(Closeable obj) {
		if (obj == null) {
			return;
		}

		try {
			obj.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
