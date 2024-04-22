import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
//GUI for the client side of the GWack channel
public class GWackClientGUI extends JFrame {
    //text fields for name, IP address, and port
    private JTextField name;
    private JTextField IPAddress;
    private JTextField portNum;
    //buttons to connect/disconnect and send messages
    private JButton connectionButton;
    private JButton sendButton;
    //text areas to display members online and messsages and one to compose a message
    private JTextArea memsOnline;
    private JTextArea messages;
    private JTextArea composeMessage;
    private boolean isConnected = false;
    private ClientNetworking c;
    public GWackClientGUI() {
        super();

        name = new JTextField("", 8);
        IPAddress = new JTextField("", 10);
        portNum = new JTextField("", 5);
        connectionButton = new JButton("Connect");
        //adds name, IP address, port, and connection button to the top of the window
        JPanel apanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        apanel.add(new JLabel("Name "));
        apanel.add(name);
        apanel.add(new JLabel("IP Address "));
        apanel.add(IPAddress);
        apanel.add(new JLabel("Port "));
        apanel.add(portNum);
        apanel.add(connectionButton);
        //messages and members online cannot be edited by the user
        messages = new JTextArea(15, 50);
        messages.setEditable(false);
        composeMessage = new JTextArea(3, 50);
        memsOnline = new JTextArea(19, 10);
        memsOnline.setEditable(false);
        //new panel for the displayed messages
        JPanel bpanel = new JPanel(new BorderLayout());
        bpanel.add(new JLabel("Messages "), BorderLayout.NORTH);
        bpanel.add(messages, BorderLayout.SOUTH);
        //new panel for composing messages
        JPanel cpanel = new JPanel(new BorderLayout());
        cpanel.add(new JLabel("Compose"), BorderLayout.NORTH);
        cpanel.add(composeMessage, BorderLayout.SOUTH);
        //new panel for the displayed members online
        JPanel dpanel = new JPanel(new BorderLayout());
        dpanel.add(new JLabel("Members Online "), BorderLayout.NORTH);
        dpanel.add(memsOnline, BorderLayout.SOUTH);
        //new panel for the send button at the bottom right underneath compose messages
        sendButton = new JButton("Send");
        JPanel epanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        epanel.add(sendButton);
        //adds displayed messages ontop of composed messages
        JPanel fpanel = new JPanel(new BorderLayout());
        fpanel.add(bpanel, BorderLayout.NORTH);
        fpanel.add(cpanel, BorderLayout.SOUTH);
        name.setText("Hadya");
        IPAddress.setText("localhost");
        portNum.setText("8082");
        //adds all panels into the window in a proper orientation
        connectionButton.addActionListener((e) -> {
            if(connectionButton.getText().equals("Connect"))
            {
                try {
                    String n = name.getText();
                    String ip = IPAddress.getText();
                    int p = Integer.parseInt(portNum.getText());
                    c = new ClientNetworking(n, ip, p, this);
                    connectionButton.setText("Disconnect");
                    name.setEditable(false);
                    IPAddress.setEditable(false);
                    portNum.setEditable(false);
                    isConnected = true;
                    //messages.setText(c.getIn().readLine());
                }
                catch (Throwable s) {
                    if(portNum.getText().equals(""))
                    {
                        Frame frame = new Frame();
                        JOptionPane.showMessageDialog(frame, "Invalid port", "Connection Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        Frame frame = new Frame();
                        JOptionPane.showMessageDialog(frame,"Cannot Connect", "Connection Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else
            {
                c.disconnect();
                connectionButton.setText("Connect");
                name.setText("");
                IPAddress.setText("");
                portNum.setText("");
                messages.setText("");
                memsOnline.setText("");
                name.setEditable(true);
                IPAddress.setEditable(true);
                portNum.setEditable(true);
                isConnected = false;
            }
        });
        sendButton.addActionListener((e) -> {
            if(isConnected)
            {
                c.writeMsg(composeMessage.getText());
                composeMessage.setText("");
            }
        });
        this.add(apanel, BorderLayout.NORTH);
        this.add(fpanel, BorderLayout.CENTER);
        this.add(dpanel, BorderLayout.WEST);
        this.add(epanel, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

    }
    public static void main(String[] args) {
        GWackClientGUI f = new GWackClientGUI();
        f.setTitle("GWack -- GW Slack Simulator");
        f.setVisible(true);
        
    }
    public JTextArea getMembersTextArea(){
        if(messages != null)
            return memsOnline;
        return null;
    }
    public JTextArea getDisplayTextArea(){
        if(messages != null)
            return messages;
        return null;
    }
}