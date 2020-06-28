package com.joewebserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ChatSession implements Runnable {
    private ChatSocket chat;
    private ArrayList<ChatSocket> users;
    private String username;
    private int id;
    private static String welcomeMsg = "\r\n" +
            "     _  ___  ___ _ ___    ___ _  _   _ _____   ___ ___ _____   _____ ___ \r\n" +
            "  _ | |/ _ \\| __( ) __|  / __| || | /_\\_   _| / __| __| _ \\ \\ / / __| _ \\\r\n" +
            " | || | (_) | _||/\\__ \\ | (__| __ |/ _ \\| |   \\__ \\ _||   /\\ V /| _||   /\r\n" +
            "  \\__/ \\___/|___| |___/  \\___|_||_/_/ \\_\\_|   |___/___|_|_\\ \\_/ |___|_|_\\\r\n" +
            "                                                                         ";



    public ChatSession(Socket socket, ArrayList<ChatSocket> users, int id) throws IOException {
        this.chat = new ChatSocket(socket, id);
        this.id = id;
        this.users = users;
    }

    public void run() {
        this.users.add(this.chat);
        try {
            // Get username
            chat.writeMsgLn(welcomeMsg);
            chat.writeMsgLn("Welcome to Joe's Chat Server");
            chat.writeMsg("What is your username? ");
            this.username = chat.readMsg();

            chat.writeMsgLn(String.format("Welcome %s. There are %d other user/s online\n\n", username, users.size() - 1));
            this.sendAll(String.format("%s joined the server", username));

            while(true) {
                String msg = chat.readMsg();
                if (!msg.isBlank()) {
                    msg = String.format("%s: %s", username, msg);
                    this.sendAll(msg);
                }
            }
        } catch (IOException ex) {
            System.err.println(String.format("ChatSession with id %d closed", this.id));
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).id == this.id) {
                    users.remove(i);
                }
            }

            this.sendAll(String.format("%s left", username));
        }
    }

    private void sendAll(String msg) {
        for (ChatSocket s : users) {
            try {
                s.writeMsgLn(msg);
                s.bell();
            } catch (IOException ex) {
                // If we can't write to another client, let the other ChatSession deal with it
            }
        }
    }
}
