package ifac.flopez.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class LogThread extends Thread {

    private static final String TAG = LogThread.class.getSimpleName();

    Queue<String> messages = new LinkedList<>();

    private String currentFileName;
    private String path;
    private FileWriter fileWriter;

    public LogThread(String path) {
        this.path = path;
        initFile();
    }

    private void initFile() {
        android.util.Log.d(TAG, "initFile()");
        try {
            String tempName = DateUtils.getCurrentDate(null);
            if (!tempName.equals(currentFileName)) {
                currentFileName = tempName;
            }
            File f = new File(path + "/" + currentFileName + ".log");
            fileWriter = new FileWriter(f.getAbsolutePath(), true);
        } catch (Exception e) {
            android.util.Log.d(TAG, "init: " + e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String tempName = DateUtils.getCurrentDate(null);
                if (!tempName.equals(currentFileName)) {
                    initFile();
                }
                new File(path).mkdir();
                if (!messages.isEmpty()) {
                    if (fileWriter != null) {
                        fileWriter.write(messages.remove());
                    } else {
                        initFile();
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.d(TAG, "" + e);
            closeFile();
        } finally {
            closeFile();
        }
    }

    private void closeFile() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void write(String message) {
        messages.add(message);
    }

    public synchronized boolean pendingToWrite() {
        return !this.messages.isEmpty();
    }
}
