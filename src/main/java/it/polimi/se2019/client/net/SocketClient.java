package it.polimi.se2019.client.net;


import it.polimi.se2019.client.gui.MainApp;
import it.polimi.se2019.client.util.ClientCommandHandler;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.util.Request;
import it.polimi.se2019.util.Response;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class SocketClient {
    private static final Logger logger = Logger.getLogger(SocketClient.class.getName());
    private String nickname;
    private String serverHost;
    private PrintWriter out;

    public SocketClient(String nickname, String serverHost) {
        this.nickname = nickname;
        this.serverHost = serverHost;
    }

    public void start(MainApp mainApp) {
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            int socketPort = Integer.parseInt(prop.getProperty("socket.port"));
            Socket socket = new Socket(serverHost, socketPort);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ClientCommandHandler commandHandler = new ClientCommandHandler(mainApp);
            new SocketClientWorker(commandHandler, in).start();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    public void send(Request request) {
        this.out.println(request.serialize());
    }

    public static class SocketClientWorker extends Thread {
        ClientCommandHandler commandHandler;
        BufferedReader in;

        public SocketClientWorker(ClientCommandHandler commandHandler, BufferedReader in) {
            this.commandHandler = commandHandler;
            this.in = in;
        }

        public void run(){
            try {
                String inputLine;
                while ((inputLine = this.in.readLine()) != null) {
                    Response request = (Response) new Response(null, false, "").deserialize(inputLine);
                    commandHandler.handle(request);
                }
            } catch (IOException e) {
                // do something if connection fails
            }
        }
    }
}
