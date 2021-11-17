package ifac.flopez.logger;

import java.io.File;
import java.util.ArrayList;

/*
 * Created by Taxitronic (FLopez) at 17/11/21
 */
public class ZipThread extends Thread {

    private static final String TAG = "ZipThread";

    private final ZipThreadCallback callback;
    private final File path;

    public ZipThread(File path, ZipThreadCallback callback) {
        this.path = path;
        this.callback = callback;
    }

    public interface ZipThreadCallback{
        void onSuccess(File zippedFile);
        void onError(String error);
    }

    @Override
    public void run() {
        android.util.Log.d(TAG, Thread.currentThread().getName());
        try {
            ZipManager zipManager = new ZipManager();
            String[] tempFiles  = path.list();
            ArrayList<String> logFiles = new ArrayList<>();
            if (tempFiles != null) {
                for(String s: tempFiles) {
                    if (s.endsWith(".log")) {
                        logFiles.add(path + "/" + s);
                    }
                }
            }
            File zippedFile = zipManager.zip(logFiles, path + "/TaxiIqnos_" + DateUtils.getCurrentDate("dd_MM_yyy_HH_mm") + ".zip");
            this.callback.onSuccess(zippedFile);
        } catch (Exception e) {
            Log.d(TAG, "run: " + e);
            this.callback.onError(e.toString());
        }
    }

}

