
import java.net.*;
import java.io.*;
import java.util.*;

public class RUDPDestination {
    public static void main(String[] args) throws Exception {
        int recvPort = 0;

        // parse command-line args
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")) {
                recvPort = Integer.parseInt(args[i + 1]);
            }
        }

        DatagramSocket theSocketII = new DatagramSocket(recvPort);
        byte[] buffer = new byte[1028]; // offset + buffer size (1024)
        DatagramPacket thePacketIII = new DatagramPacket(buffer, buffer.length);

        FileOutputStream fileOutputStream = null;
        File file = null;

        double packetLossRate = 0.3; // 30% loss probability fr
        File directory = new File("filesReceived");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        boolean fileNameReceived = false;

        while (!fileNameReceived) {
            theSocketII.receive(thePacketIII);
            String fileName = new String(thePacketIII.getData(), 0, thePacketIII.getLength());
            file = new File(directory, fileName);

            fileOutputStream = new FileOutputStream(file);
            System.out.println("RECEIVING FILE: " + file.getName());

            byte[] ack = fileName.getBytes();
            DatagramPacket ackPacket = new DatagramPacket(ack, ack.length, thePacketIII.getAddress(), thePacketIII.getPort());
            theSocketII.send(ackPacket);
            System.out.println("ACKNOWLEDGMENT FOR FILENAME SENT");

            fileNameReceived = true;
        }

        while (true) {
            Random random = new Random();
            theSocketII.receive(thePacketIII);

            if (thePacketIII.getLength() == 0) { // the end moment
                System.out.println("END PACKET");
                break;
            }

            // extract offset
            byte[] data = thePacketIII.getData();
            int offset = ((data[0] & 0xFF) << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);

            if (random.nextDouble() < packetLossRate) {
                System.out.println("[DATA RECEPTION]: " + offset + " | " + (thePacketIII.getLength() - 4) + " | DISCARDED");
                continue; // simulate packet loss
            }

            fileOutputStream.write(data, 4, thePacketIII.getLength() - 4);
            System.out.println("[DATA RECEPTION]: " + offset + " | " + (thePacketIII.getLength() - 4) + " | OK");

            byte[] ack = String.valueOf(offset).getBytes();
            DatagramPacket ackPacket = new DatagramPacket(ack, ack.length, thePacketIII.getAddress(), thePacketIII.getPort());
            theSocketII.send(ackPacket);
        }

        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
        theSocketII.close();
        System.out.println("[COMPLETE]");
    }
}
