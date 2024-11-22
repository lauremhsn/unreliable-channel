import java.io.IOException;
import java.net.*;
import java.util.*;

public class UnreliableChannel { // port, probability, minimum delay, maximum delay
    public static void main(String[] args) {

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
        long loserInterval = 2000; // (ms)
        long loserDuration = 4000; // (ms)

        DatagramSocket theSocketII = null;
        try {
            // prepare to receive a packet 
            theSocketII = new DatagramSocket(port);
            System.out.println("Server running on port " + port);
        } catch (SocketException e) { // something wrong with port 
            System.err.println("Socket creation error on the port " + port);
            return;
        }

        byte[] anotherBuff = new byte[1024];
        DatagramPacket thePacketII = new DatagramPacket(anotherBuff, anotherBuff.length);

        // counters 
        int totalPacketsA = 0, loserA = 0, keeperA = 0, totalDelayA = 0;
        int totalPacketsB = 0, loserB = 0, keeperB = 0, totalDelayB = 0;

        // random generator 
        Random rnd = new Random();
        
        try {
            while (true) {
                try {
                    // actually receive the packet and put the data in a string
                    theSocketII.receive(thePacketII);
                    String str = new String(thePacketII.getData()).trim();

                    if (str.equals("END")) { // .trim() for better security
                        break; // all packets sent
                    }

                    char user = str.charAt(0);  // which client sent this? A or B?

                    // current time (obvs)
                    long currentTime = System.currentTimeMillis();

                    // are we within the losing interval?
                    if (currentTime - lastTimePacketLost > loserInterval) {
                        lastTimePacketLost = currentTime; // yes, we exceeded the interval so khalas we start losing again
                        lossPeriod = true;
                    }

                    if (lossPeriod) {
                        if (currentTime - lastTimePacketLost <= loserDuration) { // within the period, lose packet
                            System.out.println("Packet lost within time loss interval.");
                            if (user == 'A') {
                                loserA++;
                                totalPacketsA++;
                            } else {
                                loserB++;
                                totalPacketsB++;
                            }
                            continue; // skip fr
                        } else {
                            lossPeriod = false; // khlosna
                        }
                    }

                    if (rnd.nextDouble() <= probs) {
                        if (user == 'A') {
                            loserA++;
                            totalPacketsA++;
                        } else {
                            loserB++;
                            totalPacketsB++;
                        }
                        continue; // packet loss skip over it 
                    }

                    int packetDelay = rnd.nextInt(maxD - minD + 1) + minD;
                    Thread.sleep(packetDelay);

                    // packet successfully sent
                    if (user == 'A') { // sent to which client?
                        keeperA++;
                        totalPacketsA++;
                        totalDelayA += packetDelay;
                    } else {
                        keeperB++;
                        totalPacketsB++;
                        totalDelayB += packetDelay;
                    }
                } catch (IOException e) {
                    System.err.println("Error receiving packet: " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            System.err.println("Thread interruption during sleep: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            // average delays 
            double avgDelayA = keeperA > 0 ? (double) totalDelayA / keeperA : 0; // check for 0 to avoid errors
            double avgDelayB = keeperB > 0 ? (double) totalDelayB / keeperB : 0;

            // print results
            System.out.println("Packets received from user A: " + totalPacketsA + " | Lost: " + loserA + " | Delivered: " + keeperA);
            System.out.println("Packets received from user B: " + totalPacketsB + " | Lost: " + loserB + " | Delivered: " + keeperB);
            System.out.println("Average delay from A to B: " + avgDelayA + " ms");
            System.out.println("Average delay from B to A: " + avgDelayB + " ms");

            if (theSocketII != null && !theSocketII.isClosed()) {
                theSocketII.close(); // close the socket for sure 
            }
        }
    }
}
