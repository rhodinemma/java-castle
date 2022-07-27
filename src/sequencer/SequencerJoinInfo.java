package sequencer;

import java.io.*;
import java.net.*;

public class SequencerJoinInfo implements Serializable
{
    public InetAddress addr; //multicast IP address
    public long sequence; //sequence number

    public SequencerJoinInfo(InetAddress addr, long sequence)
    {
        this.addr = addr;
        this.sequence = sequence;
    }
}