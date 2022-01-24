package ifac.flopez.logger;

import android.os.Looper;

import java.io.File;
import java.util.ArrayList;

public class Log {


    private static Log instance;

    private LogExternalInterface externalInterface;

    private File rootFile = null;
    private File logsPath = null;

    private static final String BLANK_SPACE = " ";

    private Log() {}

    private LogThread logThread;
    private boolean isDebug;

    private static final String LOG_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss:SSS";

    public static void init(LogExternalInterface logExternalInterface, boolean isDebug) {
        instance = new Log();
        instance.externalInterface = logExternalInterface;
        instance.isDebug = isDebug;
        instance.rootFile = logExternalInterface.getContext().getFilesDir();
        instance.logsPath = new File(instance.rootFile + "/logs");
        instance.logThread = new LogThread(instance.logsPath.getAbsolutePath());
        instance.logThread.start();
    }

    public static void d(String TAG, String message){
        instance.logThread.write(formatData("D/", message, TAG, false));
        if (instance.isDebug) {
            android.util.Log.d(TAG, message);
        }
    }


    public static void e(String TAG, String message){
        instance.logThread.write(formatData("ERROR!!", message, TAG, false));
        if (instance.isDebug) {
            android.util.Log.e(TAG, message);
        }
    }

    public static void w(String TAG, String message){
        instance.logThread.write(formatData("W/", message, TAG, false));
        if (instance.isDebug) {
            android.util.Log.e(TAG, message);
        }
    }

    public static void d(String TAG, String message, boolean showFrom){
        instance.logThread.write(formatData("D/", message, TAG, showFrom));
        if (instance.isDebug) {
            android.util.Log.d(TAG, message);
        }
    }

    public static void purge() {
        if (instance != null && instance.logsPath != null) {
            if (instance.logsPath.list() != null) {
                for (String s : instance.logsPath.list()) {
                    File f = new File( instance.logsPath, s);
                    if (f.exists()) {
                        f.delete();
                    }
                }
            }
        }
    }


    private static String formatData(String logType, String content, String tag, boolean showFrom) {
        StringBuilder sb = new StringBuilder()
                .append(DateUtils.getCurrentDate(LOG_DATE_FORMAT))
                .append(BLANK_SPACE)
                .append(logType)
                .append(BLANK_SPACE)
                .append(tag + ":")
                .append(BLANK_SPACE)
                .append(content);

        if (showFrom) {
            String className = Looper.getMainLooper().getThread().getStackTrace()[4].getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            String methodName = Looper.getMainLooper().getThread().getStackTrace()[4].getMethodName();
            sb.append(" from: ")
                    .append(className + "." + methodName);
        }
        sb.append("\n");

        return sb.toString();
    }


    public static interface ZipCallback {
        void onSuccess(File f);
        void onError(String e);
    }

    public static void zipLogs(ZipCallback callback) {
        String fileName = "Log";
        if (instance.externalInterface != null) {
            if (instance.externalInterface.getZipFileName() != null && !instance.externalInterface.getZipFileName().isEmpty()) {
                fileName = instance.externalInterface.getZipFileName();
            }
        }
        new ZipThread(instance.logsPath, new ZipThread.ZipThreadCallback() {
            @Override
            public void onSuccess(File zippedFile) {
                callback.onSuccess(zippedFile);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        }, fileName).start();
    }

}
