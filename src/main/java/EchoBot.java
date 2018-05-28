import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class EchoBot extends TelegramLongPollingBot {

    ApiBuilder apiBuilder = new ApiBuilder();
    JSONObject cafeteriaObject = null;
    String jsonString = "";


    public void onUpdateReceived(Update update) {


        if (update.hasMessage() &&
                update.getMessage().hasText()) {
            System.out.println(apiBuilder.period("Dienstag", "Samstag")); // zum Testen (Dario)

            jsonString = apiBuilder.period("Dienstag", "Sonntag");
            System.out.println(showJsonData(jsonString));

            String response = showJsonData(jsonString);

            /*try {
                System.out.println(readJsonFromUrl("https://webproxy.fh-kufstein.ac.at/cafeteria/getcafeteriadata;from=21.04.2018;"));
            } catch (IOException e) {
                e.printStackTrace();
            }*/






            if (!response.isEmpty()) {
                SendMessage message = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(response);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    System.out.println("FUNKTIONIERT NICHT");
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public String getResponse(String message) {
        if (message.matches("(?i)echo: .*")) {
            return message.substring(6);
        }
        return "";
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static JSONObject readJsonFromUrlItem(String url, JSONObject name) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            String jsonnew = json.getString("preis");
            System.out.println(jsonnew);
            return json;
        } finally {
            is.close();
        }
    }

    public String showJsonData(String URL){
        String result = "";
        String symbol = "";
        String name = "";
        String preis = "";
        String datum = "";
        try {
            cafeteriaObject = (readJsonFromUrl(jsonString));
            // System.out.println(cafeteriaObject);
            JSONArray cafeteria = cafeteriaObject.getJSONArray("cafeteriaData");
            //System.out.println(cafeteria);

            //JSONObject cafeteriaObject = cafeteria.getJSONObject(0);

            for(int n = 0; n < cafeteria.length(); n++)
            {
                JSONObject object = cafeteria.getJSONObject(n);
                //System.out.println(cafeteriaObject);
                if (object.getString("name").startsWith("Feiertag")||object.getString("name").startsWith("DINER GESCHLOSSEN")){
                    datum = object.getString("tag");
                    result = result.concat("Am " + datum + " hat die Cafeteria geschlossen " + System.lineSeparator());
                }else {
                    symbol = object.getString("symbol");
                    preis = object.getString("preis");
                    name = object.getString("name");
                    datum = object.getString("tag");
                    result = result.concat("Am "  + datum + " gibt es " + name.replace(System.lineSeparator(), " ") + " für  " + preis + System.lineSeparator());
                }
               /* System.out.println(object);
                System.out.println(result);*/
            }



            //System.out.println(symbol + " " + preis + " " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
     return result;
    }



    public String getBotUsername() {
        return "Cafeteria_FH_bot";
    }

    public String getBotToken() {
        return "561379899:AAE1ihEja2UH42vGxVmH4jk1xyYT_iu1BFE";
    }
}
