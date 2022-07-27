package sequencer;

import java.util.Scanner;

public class MsgHandlerImpl implements Group.MsgHandler {

    public MsgHandlerImpl() {
    }

    public String getMessage() {
        //prompt client to send a message
        Scanner input = new Scanner(System.in);
        System.out.print("Enter message to send: \n");
        return input.next();
    }

    @Override
    public void handle(int count, byte[] msg) {
        String mg = new String(msg);
        String[] mgs = mg.split(" ");
        System.out.println("Message received is: " + mgs[0] + ", its unique ID is " + mgs[1] + " its sequence number is " + mgs[2]);
    }
}

