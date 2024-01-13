package org.example;

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
    public static List<ClientSocket> clients = new CopyOnWriteArrayList<>();

    public Server() throws IOException {
        InputStream resources = getClass().getClassLoader().getResourceAsStream("properties.yaml");
        Properties properties = new Properties();
        properties.load(resources);
        if (resources != null) {
            resources.close();
        }
        InetAddress inetAddress = InetAddress.getByName(properties.getProperty("host"));
        serverSocket = new ServerSocket(Integer.parseInt(properties.getProperty("port")), 50, inetAddress);
//        TODO сервер запущен
        System.out.println("Server has started");
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("The new client has joined");
                clients.add(new ClientSocket(clientSocket));
//                TODO подключение клиента
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void sendMsgExceptSender(String name, String msg) {
        for (ClientSocket client : clients) {
            if (!client.getClientName().equals(name)) {
                client.sendMsg(name + ": " + msg);
            }
        }
    }

    public static void sendMsgToAllClients(String name, String msg) {
        for (ClientSocket client : clients) {
            client.sendMsg(name + ": " + msg);
        }
    }
}
