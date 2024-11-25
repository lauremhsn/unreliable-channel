import java.net.*;
import java.util.*;

public class UnreliableChannel {//PORT PROB MIND MAXD, EX: 9999 0.3 0 200
    //take in port
    //receive packet
    //random to simulate packet loss and delay (do we keep or not)
    public static void main (String [] args) throws Exception{
        // command line input
        int port = 0;
        int minD = 0, maxD = 0;
        double probs = 0.0;
        port = Integer.parseInt(args[0]);
        probs = Double.parseDouble(args[1]);
        minD = Integer.parseInt(args[2]);
        maxD = Integer.parseInt(args[3]);

        if (probs < 0 || probs >= 1){
            System.err.println("Probabilistic loss rate factor should be between 0 and 1");
            return;
        }

        byte[] anotherBuff = new byte[1024];

        DatagramSocket theSocketII = null;

        // prepare to receive a packet 
        theSocketII = new DatagramSocket(port);
        System.out.println("Server running on port " + port);
        
        DatagramPacket thePacketII = new DatagramPacket(anotherBuff, anotherBuff.length);

        
        //counters
        int totalPacketsA = 0;
        int totalPacketsB = 0;

        int loserA = 0;
        int keeperA = 0;
        int delayA = 0;

        int loserB = 0;
        int keeperB = 0;
        int delayB = 0;

        System.out.println("Waiting for packets...");

        Random rnd = new Random();

        while(true){
            // actually receive the packet and put the data in a string
            theSocketII.receive(thePacketII);

            String str = new String(thePacketII.getData(), 0, thePacketII.getLength()).trim(); // .trim() for better security
            if (str.equals("END")) {
                break; // all packets sent
            }

            char user = str.charAt(0);  // which client sent this? A or B?

            if (user == 'A'){ // which client sent this? A or B?
                ++totalPacketsA;
            }
            else{
                ++totalPacketsB;
            }
            
            int packetDelay = rnd.nextInt(minD, maxD + 1);

                
           if (rnd.nextDouble()<=probs){
                if (user == 'A'){
                    ++loserA;
                }
                else{
                    ++loserB;
                }
                continue; //packet lost, skip to next one
            }
            else {
                Thread.sleep(packetDelay); //delay simulation
                //packet is sent successfully 
                if (user == 'A'){
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
        
        System.out.println("Packets delivered from user A: " + totalPacketsA + " | Lost: " + loserA + " | Delayed: " + keeperA);
        System.out.println("Packets delivered from user B: " + totalPacketsB + " | Lost: " + loserB + " | Delayed: " + keeperB);
        System.out.println("Average delay from A to B: " + averageDelayA + " ms");
        System.out.println("Average delay from B to A: " + averageDelayB + " ms");

        if (theSocketII != null && !theSocketII.isClosed()) {
            theSocketII.close(); // close the socket for sure 
        }
        
    }
}
