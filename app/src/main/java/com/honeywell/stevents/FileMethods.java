package com.honeywell.stevents;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
public class FileMethods {

    public static void closeDB(HandHeld_SQLiteOpenHelper myDbHelper)
    {
        if (myDbHelper.getWritableDatabase().isOpen()) {
            myDbHelper.close();
        }
    }
    public static byte[] readDbFileBytes(String dbPath) throws IOException {
        File file = new File(dbPath);
        // Use try-with-resources for automatic resource management
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            Log.i("rest api", "File Size"+ file.length());

            // Check file size to prevent OOM errors for very large databases
            if (file.length() > Integer.MAX_VALUE) {
                throw new IOException("File is too large to read into a single byte array");
            }

            int size = (int) file.length();
            byte[] bytes = new byte[size];
            bis.read(bytes, 0, bytes.length);
            return bytes;
        }
    }
}
