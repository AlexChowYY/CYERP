package com.facewarrant.fw.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.facewarrant.fw.global.Constant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by licrynoob on 2016/7/12 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 图片工具类
 */
public class ImageUtil {
    /**
     * 从路径获取压缩图片
     *
     * @param path      路径
     * @param reqWidth  需求图片宽
     * @param reqHeight 需求图片高
     * @return 压缩后的Bitmap
     */
    public static Bitmap decodeSampledBitmapByPath(String path, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 从Resource获取压缩图片
     *
     * @param res       资源文件
     * @param resId     资源Id
     * @param reqWidth  需求图片宽
     * @param reqHeight 需求图片高
     * @return 压缩后的Bitmap
     */
    public static Bitmap decodeSampledBitmapByResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * FileDescriptorBitmap压缩 比decodeFile省内存
     *
     * @param fd        资源文件
     * @param reqWidth  需求图片宽
     * @param reqHeight 需求图片高
     * @return 压缩后的Bitmap
     */
    public static Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /**
     * 获得裁剪图片的InSampleSize
     *
     * @param options   options
     * @param reqWith   需求的宽度
     * @param reqHeight 需求的高度
     * @return inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWith, int reqHeight) {
        //原来图片宽高
        final int oldWith = options.outWidth;
        final int oldHeight = options.outHeight;
        int inSampleSize = 1;
        //默认不压缩
        if (reqWith == 0 || reqHeight == 0) {
            return inSampleSize;
        }
        if (oldWith > reqWith || oldHeight > reqHeight) {
            //宽比例
            final int withRatio = Math.round((float) oldWith / (float) reqWith);
            //高比例
            final int heightRatio = Math.round((float) oldHeight / (float) reqHeight);
            //选取小的比例,保证需求的图片宽和高都不小于原图片
            inSampleSize = withRatio < heightRatio ? withRatio : heightRatio;
        }
        return inSampleSize;
    }

    /**
     * Bitmap转Base64字符串
     *
     * @param bitmap Bitmap
     * @return Base64String
     */
    public static String bitmapToBase64String(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * Bitmap转Base64字符串
     *
     * @param bitmap Bitmap
     * @return Base64String
     */
    public static String bitmapToBase64String(Bitmap bitmap, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    /**
     * Drawable 转 Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap 转 Drawable
     *
     * @param bitmap Bitmap
     * @return Drawable
     */
    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        BitmapDrawable mBitmapDrawable = null;
        try {
            if (bitmap == null) {
                return null;
            }
            mBitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmapDrawable;
    }

    /**
     * 保存bitmap
     *
     * @param bitmap Bitmap
     * @param path   图片Uri
     */
    public static void saveBitmapToPath(Bitmap bitmap, String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static final String TAG = "ImageUtil";

    /**
     * 对Bitmap进行质量压缩,压缩到不大于IMAGE_SIZE(KB)
     *
     * @param bitmap
     * @param maxkb
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > maxkb && options > 0) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 5;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, null);
        LogUtil.e(TAG, options + "," + baos.toByteArray().length / 1024);
        return bitmap;
    }


    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于IMAGE_SIZE(KB)
     *
     * @param bitmap
     * @param maxkb
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length / 1024 > maxkb && options > 0) {
            output.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);
            options -= 10;
        }
        return output.toByteArray();
    }


    //保存图片相册
    public static boolean saveImageToAlbum(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Constant.SDCARD_PATH;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
