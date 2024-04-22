import java.util.*;
import java.io.*;
import java.net.*;
public class GWackChannel {
    private ServerSocket serverSocket;
    private volatile ArrayList<ClientThread> connectedClients;
    //private volatile ArrayList<String> clientList;
    private volatile Queue<String> outputQueue;
    public GWackChannel(int port){
        try {
            serverSocket = new ServerSocket(port);
            connectedClients = new ArrayList<>();
            outputQueue = new LinkedList<>();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    public ServerSocket getServerSocket(){
        return serverSocket;
    }
    public ArrayList<ClientThread> getConnectedClients(){
        return connectedClients;
    }
    public Queue<String> getOutputQueue(){
        return outputQueue;
    }
    public synchronized void addOutput(String msg)
    {
        outputQueue.add(msg);
    }
    public synchronized String removeOutput()
    {
        return outputQueue.poll();
    }
    public synchronized void broadcast(String msg)
    {
        for(ClientThread client : connectedClients)
        {
            client.sendMessage(msg);
        }
    }
    public String getClientList(){
        String list = "START_CLIENT_LIST" + "\n";
        for(int i=0; i<connectedClients.size(); i++)
        {
            list += connectedClients.get(i).username + "\n";
        }
        list += "END_CLIENT_LIST";
        return list;
    }
    public void addClient(ClientThread c){
        connectedClients.add(c);
    }
    public void removeClients(){
        for(int i=0; i<getConnectedClients().size(); i++)
        {
            if(!(getConnectedClients().get(i).valid))
            {
                getConnectedClients().remove(getConnectedClients().get(i));
            }
        }
    }
    public synchronized void serve(int x){
        while(x == -1){
            try{
                //accept incoming connection
                Socket clientSock = serverSocket.accept();
                //System.out.println("New connection: "+clientSock.getRemoteSocketAddress());
                
                //start the thread
                ClientThread client = new ClientThread(clientSock, this);
                client.start();
                
                //continue looping
            }catch(Exception e){} //exit serve if exception
        }
        if(x != -1)
        {
            for(int i=0; i<x; i++)
            {
                try{
                    //accept incoming connection
                    Socket clientSock = serverSocket.accept();
                    //System.out.println("New connection: "+clientSock.getRemoteSocketAddress());
                    
                    //start the thread
                    ClientThread client = new ClientThread(clientSock, this);
                    client.start();
                    
                    //continue looping
                }catch(Exception e){}
            }
        }
    }
    public static void main(String[] args) {
        GWackChannel c = new GWackChannel(Integer.parseInt(args[0]));
        c.serve(-1);
    }
}