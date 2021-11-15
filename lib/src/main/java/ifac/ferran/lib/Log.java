package ifac.ferran.lib;

import android.os.Looper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

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
        instance.logThread.write(formatData("DEBUG", message, TAG, false));
        if (instance.isDebug) {
            android.util.Log.d(TAG, message);
        }
    }

    public static void d(String TAG, String message, boolean showFrom){
        instance.logThread.write(formatData("DEBUG", message, TAG, showFrom));
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


    public static File zipLogs() {
        while (instance.logThread.pendingToWrite()) {

        }
        ZipManager zipManager = new ZipManager();
        String[] tempFiles  =instance.logsPath.list();
        ArrayList<String> logFiles = new ArrayList<>();
        if (tempFiles != null) {
            for(String s: tempFiles) {
                if (s.endsWith(".log")) {
                    logFiles.add(instance.logsPath + "/" + s);
                }
            }
        }
        return zipManager.zip(logFiles, instance.logsPath + "/" + DateUtils.getCurrentDate("dd_MM_yyy_HH_mm") + ".zip");
    }

}
