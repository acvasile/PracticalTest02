package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
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

    public String get20Poke() {
        String pokes = "";

        try
        {
            for (int i = 1; i <= 20; i++) {
                Log.i("20poke", "Query for Pokemon"  + i);
                String httpLink = "https://www.pokeapi.co/api/v2/pokemon/" + i;
                JSONObject json = readJsonFromUrl(httpLink);
                pokes += json.getJSONObject("species").getString("name") + ", ";
            }
            pokes = pokes.substring(0, pokes.length() - 2);
            pokes += "!";

        } catch (Exception ex) {}

        return pokes;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e("CommunicationThread", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("CommunicationThread", "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i("CommunicationThread", "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            String action = bufferedReader.readLine();
            if (action.contains("poke")) {
                String pokemon = bufferedReader.readLine();
                if (pokemon == null || pokemon.isEmpty()) {
                    Log.e("CommunicationThread", "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                    return;
                }

                Log.i("CommunicationThread", "[COMMUNICATION THREAD] Getting the information from the webservice...");
                String httpLink = "https://www.pokeapi.co/api/v2/pokemon/" + pokemon;

                JSONObject json = readJsonFromUrl(httpLink);
                String abil = "";
                JSONArray arr = json.getJSONArray("abilities");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    abil += obj.getJSONObject("ability").getString("name")+ ", ";
                }
                String url = json.getJSONObject("sprites").getString("front_default");

                printWriter.println(abil + ";" + url);
                printWriter.flush();

            } else {
                printWriter.println(get20Poke());
                printWriter.flush();
            }

        } catch (Exception ex) {
            Log.e("COMMUNICATION", "[COMMUNICATION THREAD] An exception has occurred: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("COMMUNICATION", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }

}