package ifac.flopez.logger;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;

public class LogThread extends Thread {

    private static final String TAG = LogThread.class.getSimpleName();

    Queue<String> messages = new LinkedList<>();

    private String currentFileName;
    private String path;

    public LogThread(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String tempName = DateUtils.getCurrentDate(null);
                if (!tempName.equals(currentFileName)) {
                    currentFileName = tempName;
                }
                new File(path).mkdir();
                if (!messages.isEmpty()) {
                    File f = new File(path + "/" + currentFileName + ".log");
                    FileWriter fileWriter = new FileWriter(f.getAbsolutePath(), true);
                    fileWriter.write(messages.remove());
                    fileWriter.close();
                }
            }
        } catch (Exception e) {
            android.util.Log.d(TAG, "" + e);
        }
    }

    public synchronized void write(String message) {
        messages.add(message);
    }

    public synchronized boolean pendingToWrite() {
        return !this.messages.isEmpty();
    }
}
