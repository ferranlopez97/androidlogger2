package ifac.flopez.logger;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DeleteThread extends Thread {


    private final File path;
    private final Log.DeleteLogsCallback callback;

    public DeleteThread(File path, Log.DeleteLogsCallback callback) {
        this.path = path;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (path.list() != null) {
            ArrayList<String> files = (ArrayList<String>) Arrays.asList(path.list());
            for (String s: files) {
                File file = new File(path + "/" + s);
                if (file.exists()) {
                    if (deleteFile(file)) {
                        file.delete();
                    }
                }
            }
            callback.onSuccess();
        }
        callback.onError();
    }


    public boolean deleteFile(File file) {
        String name = file.getName().substring(0, file.getName().indexOf("."));
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(name);
            Date currentDate = new Date(System.currentTimeMillis());

            long diffInMillies = Math.abs(date.getTime() - currentDate.getTime());

            android.util.Log.d("LOG", "diffInMillies=" + diffInMillies);
            if (diffInMillies >= Log.file_expiration_time) {
                android.util.Log.d("LOG", "name=" + name + " true");
                return true;
            }
            android.util.Log.d("LOG", "name=" + name + " false");
            return false;
        } catch (ParseException e) {

            throw new RuntimeException(e);
        }
    }
}
