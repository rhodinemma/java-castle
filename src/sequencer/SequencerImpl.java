package sequencer;

import java.net.*;
import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SequencerImpl extends UnicastRemoteObject implements Sequencer {

    public static int port = 7777;
    public static MulticastSocket socket;
    public static String groupIPAddress = "234.27.7.95";
    public static InetAddress host;
    public static long sequence = 0;
    public static DatagramPacket outPacket;
    public static InetAddress sender;

    public SequencerImpl(String groupIPAddress) throws IOException {
        SequencerImpl.groupIPAddress = groupIPAddress;
    }

    public static void main(String[] args) {
        try {
            /*
            creating a registry object on which we are going 
            to bind the Sequencer Server
            */
            new Registry();
            SequencerImpl stub = new SequencerImpl(groupIPAddress);
            Naming.rebind("//localhost/Sequencer", stub);  
            //binding the identification string onto the Sequencer object
            System.out.println("Sequencer bound in registry");
            System.out.println("Waiting for client to connect..........");
        } catch (IOException ex) {
            Logger.getLogger(SequencerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public SequencerJoinInfo join(String sender) throws RemoteException {
        SequencerJoinInfo retToSender;
        try {
            //Extract the InetAddress from the IP format string
            host = InetAddress.getByName(sender);
        } catch (java.net.UnknownHostException ex) {
            Logger.getLogger(SequencerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        retToSender = new SequencerJoinInfo(host, sequence);

        /*
        create an object that holds the IP address of
        the multicast group to the Client to connect to
        */
        return retToSender;
    }

    @Override
    public void send(String sender, byte[] msg, long msgID, long lastSequenceReceived) throws RemoteException {
        try {
            socket = new MulticastSocket();
            host = InetAddress.getByName(sender);
            lastSequenceReceived++;  
            //increment the received sequence number by one
            String message = new String(msg);   
            message = message + " " + msgID + " " + lastSequenceReceived; 
            //appending new seq number and message ID to the outgoing message
            msg = message.getBytes();   
            outPacket = new DatagramPacket(msg, msg.length, host, port);
            System.out.println(new String(msg));
            //insert a little time lag before the Sequencer multicasts the message
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(SequencerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            socket.send(outPacket); 
            //send the packet out to all clients listening on port 7777
            System.out.println("Message received and sent to "
                    + "multicast group (sender inclusive)");
            
            //this code stores a copy of that sent message to the History.txt file
            File file = new File("History.txt");
            if (!file.exists()) {
                file.createNewFile();
            } else {

                String gotString = new String(msg);
                Scanner input = new Scanner(file);
                String str = input.nextLine();
                str = gotString + ", " + str;
                try (PrintWriter output = new PrintWriter(file)) {
                    output.println(str);
                }
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Override
    public void leave(String sender) throws RemoteException {
        try {
            SequencerImpl.sender = InetAddress.getByName(sender);
            socket.leaveGroup(SequencerImpl.sender);
            socket.close();
        } catch (IOException ex) {
            ex.getMessage();
        }
    }
}