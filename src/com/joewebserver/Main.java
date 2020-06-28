package com.joewebserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ServerSocket server;
        Socket client;



        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress(8000));
            System.out.println("Listening on port 8000");
            ArrayList<ChatSocket> users = new ArrayList<ChatSocket>();


            for(int i = 0;;i++) {
                client = server.accept();
                System.out.println("Accepted new connection!");
                System.out.println(String.format("Port: %s\tLocal port: %s\tIs connected: %s", client.getPort(), client.getLocalPort(), client.isConnected()));

                ChatSession chat = new ChatSession(client, users, i);
                Thread chatThread = new Thread(chat);
                chatThread.start();
            }

        } catch (IOException | SecurityException ex ) {
            String errMsg = String.format("Got error %s", ex);
            System.err.println(errMsg);
        }
    }
}
