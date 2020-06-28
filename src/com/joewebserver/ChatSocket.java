package com.joewebserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChatSocket {
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    public final int id;


    public ChatSocket(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.id = id;

        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();

    }

    public void waitForInput() {
        while(true) {
            try {
                if (this.in.available() != 0) {
                    return;
                }
            } catch (IOException ex) {
                return;
            }
        }
    }

    public void writeMsgLn(String msg) throws IOException {
        msg = msg + "\r\n";
        byte[] msgArr = msg.getBytes();
        this.writeMsg(msgArr);
    }

    public void writeMsg(String msg) throws IOException {
        byte[] msgArr = msg.getBytes();
        this.writeMsg(msgArr);
    }

    private void writeMsg(byte[] arr) throws IOException {
        out.write(arr);
    }

    public void bell() throws IOException {
        byte[] arr = {7};
        this.writeMsg(arr);
    }

    public String readMsg() throws IOException {
        char[] msg = new char[256];
        int a;
        for(int i = 0; i < 256; i++) {
            a = in.read();
            //System.out.println(a);
            if (a == 10) {
                // Stop the loop if we see LF (enter)
                msg[i] = (char) a;
                break;
            } else if (a < 127 && a > 31) {
                // Write the character if it's a valid ASCII point
                msg[i] = (char) a;
            } else if (a == -1) {
                // If there are no more characters to read, keep trying
                continue;
            } else if (a == 3) {
                // Special case to deal with puTTY's header it sends on first message
                // Clear out the message array and reset our position to zero
                msg = new char[256];
                i = -1;
            } else {
                // Go back to previous arr position if we couldn't write a character
                i--;
            }
        }

        msg = trimZeros(msg);

        if (msg.length == 0) {
            // In this case our stream is empty, so the socket is closed
            throw new IOException("Socket closed");
        }

        String str = new String(msg);

        // Remove LF from input
        if (str.charAt(str.length() - 1) == 10) {
            return str.substring(0, str.length() - 1);
        } else {
            return str;
        }
    }

    private char[] trimZeros(char[] arr) {
        int lastIndex = 0;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 0) {
                lastIndex = i;
                break;
            }
        }

        if (lastIndex == 0) {
            return new char[0];
        } else {
            char[] trimmed = new char[lastIndex];
            for (int i = 0; i < lastIndex; i++) {
                trimmed[i] = arr[i];
            }

            return trimmed;
        }
    }
}
