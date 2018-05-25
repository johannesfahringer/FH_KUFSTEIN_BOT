import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ApiBuilder {

    final String restApi = "https://webproxy.fh-kufstein.ac.at/cafeteria/getcafeteriadata;";

    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    Date date = new Date();

    public String tomorrow (){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);

        String param = dateFormat.format(c.getTime());

        return String.format("%sfrom=%s", restApi, param);
    }

    public String week (){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String param1 = dateFormat.format(c.getTime());

        c.add(Calendar.DATE, 7);
        String param2 = dateFormat.format(c.getTime());

        return String.format("%sfrom=%s;until=%s", restApi, param1, param2);
    }

}
