import java.io.IOException;
import java.net.*;
import java.util.*;

public class UnreliableChannel{ //port, probability, minimum delay, maximum delay
    //take in port
    //receive packet
    //random to simulate packet loss and delay (do we keep or not)
    public static void main (String [] args) throws Exception{
        // command line input
        int port = 0, minD = 0, maxD = 0;
        double probs = 0.0;
        try {
            port = Integer.parseInt(args[0]);
            probs = Double.parseDouble(args[1]);
            minD = Integer.parseInt(args[2]);
            maxD = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) { //input ghalat
            System.err.println("Invalid input. Ensure that the input is in the following order: PORT (int), loss probability (double), minimum delay, and maximum delay (int)");
            return;
        }

        // time limit packet losses (a packet is lost every time a period of time passes)
        long lastTimePacketLost = 0; // when was the last packet lost?
        boolean lossPeriod = false; // am i going to lose a packet now or not?
        long loserInterval = 13200; // (ms)
        long loserDuration = 2000; // (ms)

        byte[] anotherBuff = new byte[1024];

        DatagramSocket theSocketII = null;

        try {
            // prepare to receive a packet 
            theSocketII = new DatagramSocket(port);
            System.out.println("Server running on port " + port);
        } catch (SocketException e) { // something wrong with port 
            System.err.println("Socket creation error on the port " + port);
            return;
        }
        
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

        try{
            while(true){
                try {
                    // actually receive the packet and put the data in a string
                    theSocketII.receive(thePacketII);

                    String str = new String(thePacketII.getData(), 0, thePacketII.getLength()).trim(); // .trim() for better security
                    if (str.equals("END")) {
                        break; // all packets sent
                    }

                    char user = str.charAt(0);  // which client sent this? A or B?
                    long currentTime = System.currentTimeMillis();

                    // are we within the losing interval?
                    if (currentTime - lastTimePacketLost > loserInterval) {
                        lastTimePacketLost = currentTime;
                        lossPeriod = true;
                    }

                    if (user == 'A'){ // which client sent this? A or B?
                        ++totalPacketsA;
                        System.out.println("total packets A " + totalPacketsA); //to be removed
                    }
                    else{
                        ++totalPacketsB;
                        System.out.println("total packets B " + totalPacketsB); //to be removed
                    }
                    
                    
                    int packetDelay = rnd.nextInt(minD, maxD + 1);

                        
                    if (lossPeriod && currentTime - lastTimePacketLost < loserDuration){
                        System.out.println("Packet lost within time loss interval.");
                        if (user == 'A'){
                            System.out.println("time loss A");
                            loserA++;
                        }
                        else{
                            System.out.println("time loss B");
                            loserB++;
                        }
                        continue; //skip fr
                    }
                    else if (rnd.nextDouble()<=probs){
                        lossPeriod = false;
                        if (user == 'A'){
                            System.out.println("nromal loss");
                            ++loserA;
                        }
                        else{
                            System.out.println("normal loss");
                            ++loserB;
                        }
                        continue; //packet lost, skip to next one
                    }
                    else {
                        lossPeriod = false;
                        Thread.sleep(packetDelay); //delay sim
                        //packet is sent successfully 
                        if (user == 'A'){
                            ++keeperA;
                            String s = new String(thePacketII.getData());
                            System.out.println("Message received from A to B: " + s);
                            delayA += packetDelay;
                        }
                        else{
                            ++keeperB;
                            String s = new String(thePacketII.getData());
                            System.out.println("Message received from B to A: " + s);
                            delayB += packetDelay;
                        } 
                    }
                } catch(IOException e){
                    System.err.println("Error receiving packet: " + e.getMessage());
                }

            }
        } catch (InterruptedException e) {
            System.err.println("Thread interruption during sleep: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
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
}
