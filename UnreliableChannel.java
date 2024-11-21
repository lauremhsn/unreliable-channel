import java.net.*;
import java.util.*;

public class UnreliableChannel{ //port, probability, minD, maxD
    //take in port
    //receive packet
    //random to simulate packet loss and delay (do we keep or not)
    public static void main (String [] args) throws Exception{
        int port = Integer.parseInt(args[0]);
        byte[] anotherBuff = new byte[1024];


        DatagramSocket theSocketII = new DatagramSocket(port);
        DatagramPacket thePacketII = new DatagramPacket(anotherBuff, anotherBuff.length);
        double probs = 0.3; //loss p

        int loserA = 0;
        int keeperA = 0;

        int loserB = 0;
        int keeperB = 0;


        while(true){
            theSocketII.receive(thePacketII);
            Random rnd = new Random();
            if (rnd.nextDouble()<=probs){
                continue;
            }
            else{

            }




            if (new String(thePacketII.getData()) == "END") {
                //counters
                break;
            }
        }


        
    }
}
