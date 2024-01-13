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
                throw new RuntimeException(e);
            }
            if (name != null) {
                //проверка имеется ли активный клиент с таким же никнеймом
                if (!Server.clients.stream().map(ClientSocket::getClientName).toList().contains(name)) {
                    this.clientName = name;
                    Server.sendMsgToAllClients(clientName, "We are welcoming new member " + name);
                    break;
                }
            }
        }

        while (true) {
            String msg;
            try {
                msg = reader.readLine();
                System.out.println(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (msg != null) {
                if (msg.equals("/exit")) {
//                    TODO запрос на отключение
                    Server.sendMsgToAllClients(clientName, "We are saying good bye to " + clientName);
                    stopClient();
                    break;
                } else {
                    System.out.println("msg != exit");
                    Server.sendMsgExceptSender(clientName, msg);
//                        TODO Отправка сообщения
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
//                TODO отключение клиента
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMsg(String msg) {
        try {
            writer.write(msg + "\n");
            writer.flush();
//            TODO отправлено сообщение
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
