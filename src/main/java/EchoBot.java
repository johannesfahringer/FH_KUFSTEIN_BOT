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
    boolean messageOk = false;
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
            String error = "Anleitung: Es scheint als hätten Sie eine ungültige Nachricht eingegeben. " +System.lineSeparator() +
                    " Deshalb folgen Sie bitte dieser Anleitung: " + System.lineSeparator() +
                    " Gerichte vom heutigen Tag werden Ihnen durch die Eingabe des Schlüsselworts: heute angezeigt." + System.lineSeparator()
                    + "EIN BEISPIEL: Was gibt es heute zu essen?" + System.lineSeparator() +
                    " Gerichte vom morgigen Tag werden Ihnen durch die Eingabe des Schlüsselworts: morgen angezeigt." + System.lineSeparator()
                    + " EIN BEISPIEL: Was gibt es morgen zu essen?" + System.lineSeparator() +
                    " Gerichte von der gesamten Woche werden Ihnen durch die Eingabe des Schlüsselworts: woche angezeigt." + System.lineSeparator()
                    + " EIN BEISPIEL: Was gibt es diese woche zu essen?" + System.lineSeparator() +
                    " Sie können auch gezielt nach einem Tag fragen. In diesem Fall muss das Schlüsselwort am gefolgt von einem Wochentag eingegeben werden" + System.lineSeparator() +
                    " EIN BEISPIEL: Was gibt es am Donnerstag zu essen?"+ System.lineSeparator() +
                    " Um die Gerichte innerhalb einer Zeitspanne zu bekommen müssen sie die Schlüsselwörter von, ein beliebiger Wochentag1, bis, ein beliebiger Wochentag2 eingeben" + System.lineSeparator() +
                    " EIN BEISPIEL: Was gibt es von Donnerstag bis Freitag zu essen?" + System.lineSeparator();

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
                    SendMessage message1 = new SendMessage()
                            .setChatId(update.getMessage().getChatId())
                            .setText(error);
                    System.out.println("FUNKTIONIERT NICHT");
                    System.err.println(e.getMessage());
                }
            }

            if (messageOk){
                SendMessage message1 = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(error);

            try {
                execute(message1);
            } catch (TelegramApiException e) {
                System.out.println("FUNKTIONIERT NICHT");
                System.err.println(e.getMessage());
            }
        }
        }
    }

    public String getResponse(String message) {
        if (message.contains("heute")) {
            messageOk=false;
            String response = apiBuilder.today();
            isTomorrow = false;
            return response;
        } else if (message.contains("morgen")) {
            messageOk=false;
            String response = apiBuilder.tomorrow();
            isTomorrow = true;
            return response;
        } else if (message.contains ("woche")) {
            messageOk=false;
            String response = apiBuilder.week();
            return response;
        }else if (message.matches("(.*) am (Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag)(.*)")) {
            messageOk=false;
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
        }else if (message.matches("(.*) bis (.*)")) {
            Pattern von = Pattern.compile("(?<=von )(\\w+? )");
            Matcher mVon = von.matcher(message);
            Pattern bis = Pattern.compile("(?<=bis )(\\w+? )");
            Matcher mBis = bis.matcher(message);
            String response = "";
            if (mBis.find() && mVon.find()) {
                String param1 = mVon.group(1).replace(" ","");
                String param2 = mBis.group(1).replace(" ","");
                System.out.println(param1);
                System.out.println(param2);
                response = apiBuilder.period(param1, param2);
            }
            return response;
        }else {
            messageOk=true;
        }
        /*return "Anleitung: Es scheint als hätten Sie eine ungültige Nachricht eingegeben. " +System.lineSeparator() +
                " Deshalb folgen Sie bitte dieser Anleitung: " + System.lineSeparator() +
                " Gerichte vom heutigen Tag werden Ihnen durch die Eingabe des Schlüsselworts: heute angezeigt." + System.lineSeparator()
                + "EIN BEISPIEL: Was gibt es heute zu essen?" + System.lineSeparator() +
                " Gerichte vom morgigen Tag werden Ihnen durch die Eingabe des Schlüsselworts: morgen angezeigt." + System.lineSeparator()
                + " EIN BEISPIEL: Was gibt es morgen zu essen?" + System.lineSeparator() +
                " Gerichte von der gesamten Woche werden Ihnen durch die Eingabe des Schlüsselworts: woche angezeigt." + System.lineSeparator()
                + " EIN BEISPIEL: Was gibt es diese woche zu essen?" + System.lineSeparator() +
                " Sie können auch gezielt nach einem Tag fragen. In diesem Fall muss das Schlüsselwort am gefolgt von einem Wochentag eingegeben werden" + System.lineSeparator() +
                " EIN BEISPIEL: Was gibt es am Donnerstag zu essen?"+ System.lineSeparator() +
                " Um die Gerichte innerhalb einer Zeitspanne zu bekommen müssen sie die Schlüsselwörter von, ein beliebiger Wochentag1, bis, ein beliebiger Wochentag2 eingeben" + System.lineSeparator() +
                " EIN BEISPIEL: Was gibt es von Donnerstag bis Freitag zu essen?" + System.lineSeparator();*/



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
        return "Cafeteria_FH_bot";
    }

    public String getBotToken() {
        return "561379899:AAE1ihEja2UH42vGxVmH4jk1xyYT_iu1BFE";
    }
}
