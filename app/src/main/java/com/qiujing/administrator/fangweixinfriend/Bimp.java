package com.qiujing.administrator.fangweixinfriend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Bimp {

	public static List<String> drr = new ArrayList<String>();
	public static List<String> albumDir = new ArrayList<String>();

	public static Bitmap revisionImageSize(String path) throws Exception {

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		Bitmap bitmap = null;
		int i = 0;
		while (true) {
			if (options.outWidth >> i <= 1000 && options.outHeight >> i <= 1000) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;

		}

		return bitmap;

	}
	public static Bitmap compressImage(Bitmap image){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (image != null){
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();//重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;//每次都减少10
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
			return bitmap;
		}
		return null;
	}
	public static File saveBitmapInFile(String filePath){
		File file = new File(filePath);
		try {
			Bitmap bitmap = revisionImageSize(filePath);
			FileOutputStream fOut = new FileOutputStream(file);
			if (filePath.endsWith(".jpg")) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut);
			} else {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			}
			fOut.flush();
			bitmap.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}
}
