
package sequencer;
import java.io.*;
import java.net.*;

/*
*   This is basically a client who will be part of the sequencer group
*   and should be able to receive messages sent by the other client through the sequencer
*/

public class Receive {
    
    public static int port = 7777;
    public static String group = "234.27.7.95";
    public static MulticastSocket socket;
    public static byte[] buffer;
    public static DatagramPacket inPacket;
    public static InetAddress host;
    
    public static void main(String[] args)
    {
        try
        {
            socket = new MulticastSocket(port);
            host = InetAddress.getByName(group);
            socket.joinGroup(host); 
            System.out.println("Waiting for message from Multicast group");
            buffer = new byte[1024];

            while(true){
                inPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(inPacket);
                String rec = new String(inPacket.getData());
                String[] mgs = rec.split(" ");
                System.out.println("Message received is: " + mgs[0]);
            }
        }
        catch(IOException e){e.getMessage();
        }
        finally{socket.close();
        }
    }   
}
