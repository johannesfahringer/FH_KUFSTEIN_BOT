import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ApiBuilder {

    final String restApi = "https://webproxy.fh-kufstein.ac.at/cafeteria/getcafeteriadata;";

    HashMap<String, String> map = new HashMap<>();

    public ApiBuilder() {
        map.put("Montag", "Monday");
        map.put("Dienstag", "Tuesday");
        map.put("Mittwoch", "Wednesday");
        map.put("Donnerstag", "Thursday");
        map.put("Freitag", "Friday");
        map.put("Samstag", "Saturday");
        map.put("Sonntag", "Sunday");
    }

    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    DateFormat dateFormatString = new SimpleDateFormat("EEEE");

    public String tomorrow() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);

        String param = dateFormat.format(c.getTime());

        return String.format("%sfrom=%s", restApi, param);
    }

    public String week() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String param1 = dateFormat.format(c.getTime());

        c.add(Calendar.DATE, 7);
        String param2 = dateFormat.format(c.getTime());

        return String.format("%sfrom=%s;until=%s", restApi, param1, param2);
    }

    public String day(String day) {
        boolean control = true;
        String param = null;

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        while (control) {
            c.add(Calendar.DATE, 1);
            if (dateFormatString.format(c.getTime()).equals(day)) {
                param = dateFormat.format(c.getTime());
                control = false;
            }
        }
        return String.format("%sfrom=%s", restApi, param);
    }

    public String period(String from, String until) {
        boolean control = true;
        String param1 = "";
        String param2 = "";

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        while (control) {
            c.add(Calendar.DATE, 1);
            if (param1.equals("") && dateFormatString.format(c.getTime()).equals(from)) {
                param1 = dateFormat.format(c.getTime());
            } else if (!param1.equals("") && dateFormatString.format(c.getTime()).equals(until)) {
                param2 = dateFormat.format(c.getTime());
                control = false;
            }
        }
        return String.format("%sfrom=%s;until=%s", restApi, param1, param2);

    }

}
