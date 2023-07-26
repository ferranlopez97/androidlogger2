package ifac.flopez.logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class LogThread extends Thread {

    private static final String TAG = LogThread.class.getSimpleName();

    Queue<String> messages = new LinkedList<>();

    private String currentFileName;
    private String path;
    BufferedOutputStream bufferedOutputStream;

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
            //fileWriter = new FileWriter(f.getAbsolutePath(), true);
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(f, true));
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
                    if (bufferedOutputStream != null) {
                        String msg = messages.poll();
                        if (msg != null) {
                            bufferedOutputStream.write(msg.getBytes(), 0, msg.getBytes().length);
                        }
                    } else {
                        initFile();
                    }
                } else {
                    closeFile();
                    bufferedOutputStream = null;
                }
            }
        } catch (Exception e) {
            android.util.Log.d(TAG, "" + e);
            closeFile();
        } finally {
            closeFile();
        }
    }

    public void closeFile() {
        try {
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
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
