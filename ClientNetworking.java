import java.util.*;
import java.io.*;
import java.net.*;

public class ClientNetworking {
    private String name;
    private String IPAddress;
    private int port;
    private GWackClientGUI gui;
    private PrintWriter pw;
    private Socket socket;
    private BufferedReader bf;
    public ClientNetworking(String name, String IPAddress, int port, GWackClientGUI gui) {
        this.name = name;
        this.IPAddress = IPAddress;
        this.port = port;
        this.gui = gui;
        try {
            socket = new Socket(IPAddress, port);
            pw = new PrintWriter(socket.getOutputStream());
            //prints out secret key
            pw.println("SECRET");
            pw.flush();
            pw.println("3c3c4ac618656ae32b7f3431e75f7b26b1a14a87");
            pw.flush();
            pw.println("NAME");
            pw.flush();
            pw.println(name);
            pw.flush();
            bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //creates a reading thread
            ReadingThread reader = new ReadingThread(bf, gui);
            reader.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMsg(String msg) {
        pw.println(msg);
        pw.flush();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public void disconnect() {
        try {
            pw.println("LOGOUT");
            pw.flush();
            pw.close();
            bf.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getOut() {
        return pw;
    }


    //private reading thread class
    private class ReadingThread extends Thread {
        private BufferedReader buffer;
        private GWackClientGUI readingGUI;
        private String members = "";
        public ReadingThread(BufferedReader buffer, GWackClientGUI readingGUI) {
            this.buffer = buffer;
            this.readingGUI = readingGUI;
        }

        public void run() {
            while (true) {
                try {
                    String message = this.buffer.readLine();
                    //sends members list to the GUI
                    if(message.equals("START_CLIENT_LIST"))
                    {
                        while(!(message = buffer.readLine()).equals("END_CLIENT_LIST"))
                        {
                            this.readingGUI.getMembersTextArea().append(message + "\n");
                            readingGUI.pack();
                        }
                        readingGUI.pack();
                    }
                    //sends messages to the GUI
                    if (message != null && this.readingGUI.getDisplayTextArea() != null && !(message.equals("null")) && readingGUI != null && !(message.equals("END_CLIENT_LIST"))) {
                        this.readingGUI.getDisplayTextArea().append(message + "\n");
                        readingGUI.pack();
                    }
                } catch (Exception e) {
                    //System.out.println(e);
                }
            }
            
        }
    }
}