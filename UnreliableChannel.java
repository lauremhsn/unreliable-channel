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
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Ensure that the input is in the following order: PORT (int), loss probability (double), minimum delay, and maximum delay (int)");
            return;
        }

        // time limit packet losses (a packet is lost every time a period of time passes)
        long lastTimePacketLost = 0; // when was the last packet lost?
        boolean lossPeriod = false; // am i going to lose a packet now or not?
        long loserInterval = 10000; // (ms) (increased)
        long loserDuration = 5000; // (ms) (increased)

        DatagramSocket theSocketII = null;
        try {
            theSocketII = new DatagramSocket(port);
            System.out.println("Server running on port " + port);
        } catch (SocketException e) {
            System.err.println("Socket creation error on the port " + port);
            return;
        }

        byte[] anotherBuff = new byte[1024];
        DatagramPacket thePacketII = new DatagramPacket(anotherBuff, anotherBuff.length);

        // counters 
        int totalPacketsA = 0, loserA = 0, keeperA = 0, totalDelayA = 0;
        int totalPacketsB = 0, loserB = 0, keeperB = 0, totalDelayB = 0;

        Random rnd = new Random();
        
        try {
            while (true) {
                try {
                    // Receive packet
                    theSocketII.receive(thePacketII);
                    String str = new String(thePacketII.getData(), 0, thePacketII.getLength()).trim();

                    if (str.equals("END")) {
                        break;
                    }

                    char user = str.charAt(0);  // which client sent this? A or B?
                    long currentTime = System.currentTimeMillis();

                    // check if in loss interval
                    if (currentTime - lastTimePacketLost > loserInterval) {
                        lastTimePacketLost = currentTime;
                        lossPeriod = true;
                    }

                    if (lossPeriod && currentTime - lastTimePacketLost <= loserDuration) {
                        System.out.println("Packet lost within time loss interval.");
                        if (user == 'A') {
                            loserA++;
                            totalPacketsA++;
                        } else {
                            loserB++;
                            totalPacketsB++;
                        }
                        continue;
                    } else {
                        lossPeriod = false;
                    }

                    // probability-based packet loss
                    if (rnd.nextDouble() <= probs) {
                        if (user == 'A') {
                            loserA++;
                            totalPacketsA++;
                        } else {
                            loserB++;
                            totalPacketsB++;
                        }
                        continue;
                    }

                    int packetDelay = rnd.nextInt(maxD - minD + 1) + minD;
                    Thread.sleep(packetDelay);

                    // packet successfully sent
                    if (user == 'A') {
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
            // Average delays
            double avgDelayA = keeperA > 0 ? (double) totalDelayA / keeperA : 0;
            double avgDelayB = keeperB > 0 ? (double) totalDelayB / keeperB : 0;

            // Print results
            System.out.println("Packets received from user A: " + totalPacketsA + " | Lost: " + loserA + " | Delivered: " + keeperA);
            System.out.println("Packets received from user B: " + totalPacketsB + " | Lost: " + loserB + " | Delivered: " + keeperB);
            System.out.println("Average delay from A to B: " + avgDelayA + " ms");
            System.out.println("Average delay from B to A: " + avgDelayB + " ms");

            if (theSocketII != null && !theSocketII.isClosed()) {
                theSocketII.close();
            }
        }
    }
}
