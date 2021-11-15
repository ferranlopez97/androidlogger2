package ifac.ferran.lib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipManager {

    public File zip(ArrayList<String> filesToZip, String fileName) {

        final int BUFFER = 2048;
        File f = null;
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(fileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < filesToZip.size(); i++) {
                android.util.Log.v("Compress", "Adding: " + filesToZip.get(i));
                FileInputStream fi = new FileInputStream(filesToZip.get(i));
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(filesToZip.get(i).substring(filesToZip.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
            f = new File(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

}
