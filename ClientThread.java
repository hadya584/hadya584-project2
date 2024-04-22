import java.util.*;
import java.io.*;
import java.net.*;
public class ClientThread extends Thread {
    private Socket socket;
    private GWackChannel server;
    public boolean valid;
    private boolean validKey = false;
    public String username;
    PrintWriter out = null;
    BufferedReader in = null;
    public ClientThread(Socket socket, GWackChannel server) {
        this.socket = socket;
        this.server = server;
    }
    public synchronized void sendMessage(String message) {
        server.addOutput(message);
        out.println(server.removeOutput());
        //out.println(message);
        out.flush();
    }

    
    public void run() {
        //PrintWriter out = null;
        //BufferedReader in = null;
        try {
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true)
            {
                String message = in.readLine();
                if(message != null)
                {
                    if(message.equals("LOGOUT"))
                        break;
                    if(!validKey)
                    {
                        String key = in.readLine();
                        //System.out.println(key);
                        String nameH = in.readLine();
                        //System.out.println(nameH);
                        if(key.equals("3c3c4ac618656ae32b7f3431e75f7b26b1a14a87"))
                        {
                            validKey = true;
                            valid = true;
                            username = in.readLine();
                            //System.out.println(username + " is connected");
                            server.addClient(this);
                            //out.println(server.getClientList());
                            //out.flush();
                        }
                        else 
                        {
                            System.out.println("wrong key");
                            break;
                        }
                    }
                    else 
                    {
                        if(message != null)
                        {
                            if(message.equals("START_CLIENT_LIST"))
                            {
                                server.getClientList();
                            }
                            else 
                            {
                                //System.out.println(message);
                                //System.out.flush();
                                String v = "[" + username + "] " + message;
                                //server.addOutput(v);
                                server.broadcast(v);
                                //out.println(server.removeOutput());
                                out.flush();
                                //System.out.println(v);
                            }
                        }
                    }
                }
            }
            valid = false;
            out.close();
            in.close();
            socket.close();
        }
        catch(IOException e) {
            //e.printStackTrace();
        }
        server.removeClients();
        out.println(server.getClientList());
        out.flush();
        //System.out.println("Connection lost: " + socket.getRemoteSocketAddress());
    }
}