package ifac.ferran.lib;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {


    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate(String format) {
        if (format == null) {
            format = "dd_MM_yyyy";
        }
        return new SimpleDateFormat(format).format(Calendar.getInstance().getTime());
    }
}
