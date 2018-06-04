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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EchoBot extends TelegramLongPollingBot {

    ApiBuilder apiBuilder = new ApiBuilder();
    JSONObject cafeteriaObject = null;

    boolean isTomorrow = false;


    public void onUpdateReceived(Update update) {


        if (update.hasMessage() &&
                update.getMessage().hasText()) {

            //######################################################
            //So könnte ein funktionierendes Beispiel mit Heute und Morgen aussehen (noch nicht getestet)
            String jsonString = getResponse(update.getMessage().getText());
            System.out.println(jsonString);
            System.out.println();
            String response = showJsonData(jsonString);

            //######################################################
            // Problem mit showJsonData Funktion - Arbeitet nicht mit URL sondern mit jsonString Variable.

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
        if (message.contains("heute")) {
            String response = apiBuilder.today();
            isTomorrow = false;
            return response;
        } else if (message.contains("morgen")) {
            String response = apiBuilder.tomorrow();
            isTomorrow = true;
            return response;
        } else if (message.contains ("woche")) {
            String response = apiBuilder.week();
            return response;

        }else if (message.matches("(.*) am (Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)(.*)")) {
            String result = "";
            System.out.println(message);
            Pattern teil2 = Pattern.compile("(Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)");
            Matcher m = teil2.matcher(message);
            while(m.find()){
                System.out.print("HALLO" + m.group() + "KKKKKKK");
                result = m.group();
            }

            String response = apiBuilder.day(result);
            System.out.println("Message: " + message);
            System.out.println("response: " + response);
            return response;
        }else if (message.matches("(?<=von )(\\w+? )|(?<=bis )(\\w+? )")){
            Pattern von = Pattern.compile("(?<=von )(\\w+? )");
            Matcher mVon = von.matcher(message);
            Pattern bis = Pattern.compile("(?<=bis )(\\w+? )");
            Matcher mBis = bis.matcher(message);
            String param1 = mVon.group(1);
            String param2 = mBis.group(1);
            System.out.println(param1);
            System.out.println(param2);
            String response = apiBuilder.period(param1, param2);

            return response;
        }
        return "ungültige Eingabe";
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

    public String showJsonData(String jsonString) {
        String result = "";
        String symbol = "";
        String name = "";
        String preis = "";
        String datum = "";
        try {
            System.out.println("HALLO ANFANG SHOW JSON:  " + jsonString);
            cafeteriaObject = (readJsonFromUrl(jsonString));
            System.out.println(cafeteriaObject);
            JSONArray cafeteria = cafeteriaObject.getJSONArray("cafeteriaData");
            System.out.println(cafeteria);
            System.out.println("LÄNGE CAFETERIA" + cafeteria.length());
            //JSONObject cafeteriaObject = cafeteria.getJSONObject(0);
            if (cafeteria.length()==0) {
                result =  "Keine Daten vorhanden";
                return result;
            }
            if (isTomorrow) {
                for (int n = 0; n < cafeteria.length(); n++) {
                    JSONObject cafeteriaData = cafeteria.getJSONObject(n);
                    System.out.println("ZEIGE DICH" + cafeteriaObject);
                    if (cafeteriaData.getString("name").startsWith("Feiertag") || cafeteriaData.getString("name").isEmpty() || cafeteriaData.getString("name").startsWith("DINER GESCHLOSSEN")) {
                        datum = cafeteriaData.getString("tag");
                        result = result.concat("Am " + datum + " hat die Cafeteria geschlossen " + System.lineSeparator());
                    } else {
                        symbol = cafeteriaData.getString("symbol");
                        preis = cafeteriaData.getString("preis");
                        name = cafeteriaData.getString("name");
                        datum = cafeteriaData.getString("tag");
                        result = result.concat("Morgen gibt es " + name.replace(System.lineSeparator(), " ") + " für  " + preis + System.lineSeparator());

                    }
                }
                isTomorrow=false;
                return result;
            } else {
                for (int n = 0; n < cafeteria.length(); n++) {
                    System.out.println(n);
                    JSONObject object = cafeteria.getJSONObject(n);
                    System.out.println(cafeteriaObject);
                    if (object.getString("name").startsWith("Feiertag") || object.getString("name").startsWith("DINER GESCHLOSSEN")) {
                        datum = object.getString("tag");
                        result = result.concat("Am " + datum + " hat die Cafeteria geschlossen " + System.lineSeparator());
                    } else {
                        symbol = object.getString("symbol");
                        preis = object.getString("preis");
                        name = object.getString("name");
                        datum = object.getString("tag");
                        result = result.concat("Am " + datum + " gibt es " + name.replace(System.lineSeparator(), " ") + " für  " + preis + System.lineSeparator());
                    }
                    System.out.println(object);
                    System.out.println(result);

                }
            }


            //System.out.println(symbol + " " + preis + " " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getBotUsername() {
        return "FloCsaBot";//"Cafeteria_FH_bot";
    }

    public String getBotToken() {
        return "568821728:AAHHxh6iWbm6W7L2Tdjd4hwGhK5-ycdJKSY"; //"561379899:AAE1ihEja2UH42vGxVmH4jk1xyYT_iu1BFE";
    }
}
