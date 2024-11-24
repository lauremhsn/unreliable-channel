import java.net.*;
import java.util.*;

public class Client{ //A, B, PORT
    public static void main (String [] args) throws Exception{
        String msgSender = args[0];
        String msgReceiver = args[1];
        InetAddress IP = InetAddress.getLocalHost();
        int port = Integer.parseInt(args[2]);

        int seq = 0;
        Random rnd = new Random();

        DatagramSocket theSocket = new DatagramSocket();

        sendPackets(msgSender, msgReceiver, seq, port, IP, theSocket, rnd); //A

        sendPackets(msgReceiver, msgSender, seq, port, IP, theSocket, rnd); //B

        
        String endMess = "END";
        byte[] endArr = endMess.getBytes();
        DatagramPacket lastPacket = new DatagramPacket(endArr, endArr.length, IP, port);
        theSocket.send(lastPacket);

        theSocket.close();
    }

    private static void sendPackets(String msgSender, String msgReceiver, int seq, int port, InetAddress IP, DatagramSocket theSocket, Random rnd) throws Exception{
        for (int i = 0; i<100; ++i){
            int msgAdditionSize = rnd.nextInt(0,31); //variable packet lengths
            String addOn = "";
            for (int j = 0; j < msgAdditionSize; ++j) {
                addOn += "h";
            }

            String s = msgSender + " " + msgReceiver + " " + seq + " " + addOn + " | IP Address: " + IP;
            seq = 1-seq;

            byte[] aBuff = s.getBytes();
            DatagramPacket thePacket = new DatagramPacket(aBuff, aBuff.length, IP, port);

            theSocket.send(thePacket);

            Thread.sleep(500);
        }
    }
        
}
