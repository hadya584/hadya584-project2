import java.util.*;
import java.io.*;
import java.net.*;
public class ClientThread extends Thread {
    private volatile Socket socket;
    private volatile GWackChannel server;
    public volatile boolean valid;
    private boolean validKey;
    public volatile String username;
    public ClientThread(Socket socket, GWackChannel server) {
        this.socket = socket;
        this.server = server;
        valid = false;
        validKey = false;
    }

    
    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;
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
                        String nameH = in.readLine();
                        //checks for key
                        if(key.equals("3c3c4ac618656ae32b7f3431e75f7b26b1a14a87"))
                        {
                            validKey = true;
                            valid = true;
                            username = in.readLine();
                            server.addClient(this);
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
                            //gets the Client list 
                            if(message.equals("START_CLIENT_LIST"))
                            {
                                server.getClientList();
                            }
                            //sends out messages in the order they are recieved
                            else 
                            {
                                String v = "[" + username + "] " + message;
                                server.addOutput(v);
                                out.println(server.removeOutput());
                                out.flush();
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
        //removes any clients no longer connected
        server.removeClients();
        out.println(server.getClientList());
        out.flush();
    }
}