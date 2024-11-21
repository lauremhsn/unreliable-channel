import java.net.*;
import java.util.*;

public class UnreliableChannel{ //port, probability, minimum delay, maximum delay
    //take in port
    //receive packet
    //random to simulate packet loss and delay (do we keep or not)
    public static void main (String [] args) throws Exception{
        int port = Integer.parseInt(args[0]);
        double probs = Double.parseDouble(args[1]);
        int minD = Integer.parseInt(args[2]);
        int maxD = Integer.parseInt(args[3]);
        byte[] anotherBuff = new byte[1024];

        
        DatagramSocket theSocketII = new DatagramSocket(port);
        DatagramPacket thePacketII = new DatagramPacket(anotherBuff, anotherBuff.length);
        
        //counters
        int counterOfPackets = 0;

        int loserA = 0;
        int keeperA = 0;
        int delayA = 0;

        int loserB = 0;
        int keeperB = 0;
        int delayB = 0;

        while(true){
            theSocketII.receive(thePacketII);

            String str = new String(thePacketII.getData());
            if (str.equals("END")) {
                break; 
            }

            counterOfPackets++; 

            Random rnd = new Random();
            int packetDelay = rnd.nextInt(minD, maxD + 1);

            if (rnd.nextDouble()<=probs){
                if (str.charAt(0) == 'A'){
                    ++loserA;
                }
                else{
                    ++loserB;
                }
                continue;
            }
            else {
                Thread.sleep(packetDelay);

                if (str.charAt(0) == 'A'){
                    ++keeperA;
                    delayA += packetDelay;
                }
                else{
                    ++keeperB;
                    delayB += packetDelay;
                } 
            }

        }
        double averageDelayA = 0, averageDelayB = 0;
        if (keeperA > 0)
            averageDelayA = delayA / keeperA;
        if (keeperB > 0)
            averageDelayB = delayB / keeperB;
        System.out.println("Packets delivered from user A: " + counterOfPackets + " | Lost: " + loserA + " | Delayed: " + keeperA);
        System.out.println("Packets delivered from user B: " + counterOfPackets + " | Lost: " + loserB + " | Delayed: " + keeperB);
        System.out.println("Average delay from A to B: " + averageDelayA);
        System.out.println("Average delay from A to B: " + averageDelayB);

        theSocketII.close();
    }
}
