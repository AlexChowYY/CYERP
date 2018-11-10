package com.facewarrant.fw.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class FileUtils {

    public static File createFileFromInputStream(Context context, String filePath) {

        try{
            File f = new File(context.getExternalCacheDir()+ "/"+ filePath);
            if (f.exists()){
                return f;
            }

            AssetManager am = context.getAssets();
            InputStream inputStream = am.open(filePath);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            //Logging exception
        }

        return null;
    }
    public static File createFileFromInputStream(Context context,String filePath,String prefix){
        try{
            File f = new File(context.getExternalCacheDir()+ "/"+ prefix+filePath);
            if (f.exists()){
                return f;
            }

            AssetManager am = context.getAssets();
            InputStream inputStream = am.open(filePath);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            //Logging exception
        }

        return null;
    }
}

