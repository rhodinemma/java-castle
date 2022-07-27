package sequencer;

import java.net.*;
import java.io.*;
import java.rmi.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Group implements Runnable {

    public static int port = 7777, ttl = 1;
    public static String groupIPAddress = "234.27.7.95";
    public static MulticastSocket socket;
    public static byte[] buffer;
    public static DatagramPacket inPacket;
    public static MsgHandlerImpl handler = null;
    public static byte[] msg = null;

    public Group(String host, MsgHandlerImpl handler, String senderName) {
        try {
            //socket to receive multicast messages from the sequencer
            socket = new MulticastSocket(port);

            //setting the time to live for our socket here
            socket.setTimeToLive(ttl);

            //instantiate sequencer via rmi using the name on which it bound itself
            Sequencer obj = (Sequencer) Naming.lookup("//localhost/Sequencer");
            SequencerJoinInfo inf = obj.join(host);

            //a rmi invocation to receive group multicast address
            //client socket joining the group on its address
            socket.joinGroup(inf.addr);

            //initialise the handler to use in the thread
            Group.handler = handler;

            //create a thread to listen on socket, thread code is in method run
            (new Thread(this, "Data thread")).start();

        } catch (IOException | NotBoundException | SequencerException ex) {
            Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            buffer = new byte[256];
            inPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(inPacket);

            //receive incoming multicast messages from Sequencer
            //get byte format of incoming packet
            msg = inPacket.getData();

            //hand the received message to the Message handler
            handler.handle(msg.length, msg);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void send(byte[] msg) {
        try {
            Sequencer obj = (Sequencer) Naming.lookup("//localhost/Sequencer");
            String str = new String(msg);

            //split message received from client into constituents for special treatment
            String[] strArr = str.split(" ");
            byte[] msgStr = strArr[0].getBytes();
            String idStr = strArr[1];
            String seq = strArr[2];
            long msgID = Long.parseLong(idStr);
            long msgSeq = Long.parseLong(seq);

            //a rmi invocation to the sequencer object to send the message multicastingly
            obj.send(groupIPAddress, msgStr, msgID, msgSeq);

        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void leave() {
        try {
            Sequencer obj = (Sequencer) Naming.lookup("//localhost/Sequencer");
            obj.leave(groupIPAddress);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public interface MsgHandler {
        public void handle(int count, byte[] msg);
    }

    public static class GroupException extends Exception {
        public GroupException(String s) {
            super(s);
        }
    }

    public static class HeartBeater extends Thread {
        private final HeartBeaterHandler handler;
        public HeartBeater(HeartBeaterHandler handler){
            this.handler = handler;
        }
        int i = 0;
        public void run(){
            while(true){
                try{
                    Thread.sleep(60000);
                    handler.handle(i);
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
                i++;
            }
        }

        public interface HeartBeaterHandler{
            void handle(int i);
        }
    }
}