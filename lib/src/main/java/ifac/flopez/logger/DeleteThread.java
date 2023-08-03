package ifac.flopez.logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DeleteThread extends Thread {

    private static final String TAG = "DeleteThread";

    private final File path;
    private final Log.DeleteLogsCallback callback;

    public DeleteThread(File path, Log.DeleteLogsCallback callback) {
        this.path = path;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (path.list() != null) {
            String[] files = path.list();
            if (files != null) {
                for (String s: files) {
                    File file = new File(path + "/" + s);
                    if (file.exists()) {
                        if (deleteFile(file)) {
                            file.delete();
                        }
                    }
                }
                callback.onSuccess();
                return;
            }
        }
        callback.onError();
    }


    public boolean deleteFile(File file) {
        try {
            long lastModified = file.lastModified();
            android.util.Log.d(TAG, "lastModified=" + lastModified);
            long diff = System.currentTimeMillis() - lastModified;
            android.util.Log.d(TAG, "diff=" + diff);
            if (diff >= Log.file_expiration_time) {
                android.util.Log.d(TAG, "diff greater than configured time for file " + file.getName());
                return true;
            }
        } catch (Exception e) {
            android.util.Log.d(TAG, "" + e);
        }
        return false;
    }
}
