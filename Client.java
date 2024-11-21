import java.net.*;
import java.util.*;

public class Client{ //A, B, PORT
    public static void main (String [] args) throws Exception{
        String msgSender = args[0];
        String msgReceiver = args[1];
        InetAddress IP = InetAddress.getLocalHost();
        int port = Integer.parseInt(args[2]);
        byte[] aBuff = null;

        int seq = 0;

        DatagramPacket thePacket;
        DatagramSocket theSocket = new DatagramSocket();

        for (int i = 0; i<1000; ++i){
            String s = msgSender + " " + msgReceiver + " " + seq;
            seq = 1-seq;

            aBuff = s.getBytes();
            thePacket = new DatagramPacket(aBuff, aBuff.length, IP, port);

            theSocket.send(thePacket);

            Thread.sleep(500);
        }
        String endMess = "END";
        byte[] endArr = endMess.getBytes();
        thePacket = new DatagramPacket(endArr, endArr.length, IP, port);
        theSocket.send(thePacket);

        theSocket.close();
    }
}
