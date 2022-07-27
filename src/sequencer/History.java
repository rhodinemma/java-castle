package sequencer;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class History {
    //the arraylist going to store all recent messages, a stipulated number
    public static ArrayList<String> history = null;

    //file from which we are going to read the previous messages
    public static File file = new File("History.txt");

    //set maximum number of messages in the arraylist
    private static final int maxSize = 5;

    public static void main(String[] args) {
        try {
            //scanner for reading from the text file
            Scanner input = new Scanner(file);
            String msg = input.nextLine();
            History hist = new History();
            //call function to store the read messages into the arraylist
            hist.storeInList(msg);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
    }

    public void storeInList(String msg) {
        history = new ArrayList<>();  //our arraylist object
        String[] allMsgs = msg.split(", ");
        //spilt the entire string of messages from the text file into constituent messages
        for (String allMsg : allMsgs) {   
            //for each message
            if (history.size() >= maxSize) {  
                //if size exists our maximum size
                history.add(allMsg);    
                //add message on top
                history.remove(maxSize);    
                //remove the last one, this way we ensure arraylist never exceeds size, it also ensures obsolete messages are replaced with newer messages
            } else {
                history.add(allMsg);
            }
        }
        for (String s : history) {
            System.out.println(s);
        }
        System.out.println("The size of the ArrayList is " + history.size() + " and "
                + "the total number of \nmessages in the History "
                + "File is " + allMsgs.length + " meaning we "
                + "discarded " + (allMsgs.length - history.size()) + " obsolete "
                + "messages");
    }

   
}

