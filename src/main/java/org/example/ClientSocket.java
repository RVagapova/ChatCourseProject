package org.example;

import java.io.*;
import java.net.Socket;

public class ClientSocket extends Thread {
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final Socket socket;
    private String clientName;

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        start();
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public void run() {
        while (true) {
            String name;
            try {
                //запрос имени клиента
                writer.write("Please, enter a unique nickname\n");
                writer.flush();
                name = reader.readLine();
                System.out.println(name);
            } catch (IOException e) {
                Server.logger.warn("The name not received");
                throw new RuntimeException(e);
            }
            if (name != null) {
                //проверка имеется ли активный клиент с таким же никнеймом
                if (!Server.clients.stream().map(ClientSocket::getClientName).toList().contains(name)) {
                    this.clientName = name;
                    sendMsgToAllClients(clientName, "We are welcoming new member " + name);
                    break;
                } else {
                    Server.logger.warn("We have a client with the same name");
                }
            }
        }

        while (true) {
            String msg;
            try {
                msg = reader.readLine();
                System.out.println(msg);
            } catch (IOException e) {
                Server.logger.error("message not received");
                throw new RuntimeException(e);
            }
            if (msg != null) {
                if (msg.equals("/exit")) {
                    sendMsgToAllClients(clientName, "We are saying good bye to " + clientName);
                    stopClient();
                    break;
                } else {
                    sendMsgExceptSender(clientName, msg);
                }
            }
        }

    }

    private void stopClient() {
        try {
            Server.clients.remove(this);
            reader.close();
            writer.close();
            socket.close();
            Server.logger.info("Disconnection with client");
        } catch (IOException e) {
            Server.logger.error("Error with stopClient");
            throw new RuntimeException(e);
        }

    }

    public void sendMsg(String msg) {
        try {
            writer.write(msg + "\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMsgExceptSender(String name, String msg) {
        for (ClientSocket client : Server.clients) {
            if (!client.getClientName().equals(name)) {
                client.sendMsg(name + ": " + msg);
                Server.logger.info("The client " + name + " sent a message: " + msg);
            }
        }
    }

    private void sendMsgToAllClients(String name, String msg) {
        for (ClientSocket client : Server.clients) {
            client.sendMsg(name + ": " + msg);
            Server.logger.info("The client " + name + " sent a message: " + msg);
        }
    }
}
