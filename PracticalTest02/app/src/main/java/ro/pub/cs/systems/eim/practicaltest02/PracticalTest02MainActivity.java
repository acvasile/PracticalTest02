package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    public static EditText server_port_edit_text;
    public static EditText pokemon_name;
    public static TextView pokemon_ab;
    public static ImageView pokemon_url;
    public static Button connect_button;
    public static Button pokemon_go;
    public static Button pokemon_go20;

    public static ServerThread serverThread;
    public static ClientThread clientThread;

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = server_port_edit_text.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e("serverThread", "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private PokeGo pokeGo = new PokeGo();
    private class PokeGo implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = "127.0.0.1";
            String clientPort = server_port_edit_text.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String pokeName = pokemon_name.getText().toString();
            if (pokeName == null || pokeName.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), pokeName, "poke");
            clientThread.start();
        }
    }

    private PokeGo20 pokeGo20 = new PokeGo20();
    private class PokeGo20 implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = "127.0.0.1";
            String clientPort = server_port_edit_text.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "Get 20 Pokes", Toast.LENGTH_SHORT).show();

            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), "", "");
            clientThread.start();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);


        server_port_edit_text = findViewById(R.id.server_port_edit_text);
        pokemon_name = findViewById(R.id.pokemon_name);
        pokemon_ab = findViewById(R.id.pokemon_ab);
        pokemon_url = findViewById(R.id.pokemon_url);
        connect_button = findViewById(R.id.connect_button);
        pokemon_go = findViewById(R.id.pokemon_go);
        pokemon_go20 = findViewById(R.id.pokemon_go20);

        connect_button.setOnClickListener(connectButtonClickListener);
        pokemon_go.setOnClickListener(pokeGo);
        pokemon_go20.setOnClickListener(pokeGo20);
    }
}
