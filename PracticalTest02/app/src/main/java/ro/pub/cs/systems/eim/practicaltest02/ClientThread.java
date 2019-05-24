package ro.pub.cs.systems.eim.practicaltest02;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String pokemon;

    private Socket socket;

    public ClientThread(String address, int port, String pokemon) {
        this.address = address;
        this.port = port;
        this.pokemon = pokemon;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e("ClientThread", "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("ClientThread", "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(pokemon);
            printWriter.flush();

            String pokeInfo;
            while ((pokeInfo = bufferedReader.readLine()) != null) {

                String abil = pokeInfo.split(";")[0];
                String url = pokeInfo.split(";")[1];

                final String finalizedWeateherInformation = abil;
                PracticalTest02MainActivity.pokemon_ab.post(new Runnable() {
                    @Override
                    public void run() {
                        PracticalTest02MainActivity.pokemon_ab.setText(finalizedWeateherInformation);
                    }
                });

                URL urlReal = new URL(url);
                final Bitmap bmp = BitmapFactory.decodeStream(urlReal.openConnection().getInputStream());
                PracticalTest02MainActivity.pokemon_url.post(new Runnable() {
                    @Override
                    public void run() {
                        PracticalTest02MainActivity.pokemon_url.setImageBitmap(bmp);
                    }
                });

            }
        } catch (Exception ex) {
            Log.e("ClientThread", "[CLIENT THREAD] An exception has occurred: " + ex.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("ClientThread", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }

}