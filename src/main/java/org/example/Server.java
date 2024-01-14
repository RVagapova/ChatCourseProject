package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends Thread {

    private final ServerSocket serverSocket;
    public static final List<ClientSocket> clients = new CopyOnWriteArrayList<>();
    public static Logger logger = LoggerFactory.getLogger(Server.class);

    public Server() throws IOException {
        //логгер
        InputStream resources = getClass().getClassLoader().getResourceAsStream("properties.yaml");
        Properties properties = new Properties();
        properties.load(resources);
        if (resources != null) {
            resources.close();
        }
        InetAddress inetAddress = InetAddress.getByName(properties.getProperty("host"));
        serverSocket = new ServerSocket(Integer.parseInt(properties.getProperty("port")), 50, inetAddress);
        logger.info("Server has started");
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                clients.add(new ClientSocket(clientSocket));
                logger.info("The new client has joined");
            } catch (IOException e) {
                logger.error("The client couldn't connect");
                throw new RuntimeException(e);
            }
        }
    }
}
