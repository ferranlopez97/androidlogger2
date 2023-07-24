package ifac.flopez.logger;

import android.os.Looper;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Log {


    private static Log instance;

    private LogExternalInterface externalInterface;

    private File rootFile = null;
    private File logsPath = null;

    private static final String BLANK_SPACE = " ";

    public static long file_expiration_time;

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


    public static void d(String TAG, String message, Throwable tr){
        instance.logThread.write(formatData("D/", message + " " + getStackTraceString(tr), TAG, false));
        if (instance.isDebug) {
            android.util.Log.d(TAG, message);
        }
    }


    public static void e(String TAG, String message){
        instance.logThread.write(formatData("E/", message, TAG, false));
        if (instance.isDebug) {
            android.util.Log.e(TAG, message);
        }
    }

    public static void e(String TAG, String message, Throwable tr){
        instance.logThread.write(formatData("E/", message + " " + getStackTraceString(tr), TAG, false));
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

    public static void w(String TAG, String message, Throwable tr){
        instance.logThread.write(formatData("W/", message + " " + getStackTraceString(tr), TAG, false));
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

    public static void deleteLogs(DeleteLogsCallback deleteLogsCallback) {
        DeleteThread deleteThread = new DeleteThread(instance.logsPath, deleteLogsCallback);
        deleteThread.start();
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

    public interface DeleteLogsCallback {
        void onSuccess();
        void onError();
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

    private static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static void setFile_expiration_time(long file_expiration_time) {
        Log.file_expiration_time = file_expiration_time;
    }
}
