import java.net.*;
import java.io.*;

public class RUDPSource {
    public static void main(String[] args) throws Exception {
        // arg[0] - String "-r"
        // arg[1] - String recvHost:int recvPort
        // arg[2] - String "-f"
        // arg[3] - String filename

        String recvHost = "";
        int recvPort = 0;
        String filename = "";

        // Parse command-line arguments
        for (int i = 0; i<args.length; ++i) {
            if ("-r".equals(args[i])) {
                String[] recvInfo = args[i + 1].split(":");
                recvHost = recvInfo[0];
                recvPort = Integer.parseInt(recvInfo[1]);
            }
            if ("-f".equals(args[i])) {
                filename = args[i + 1];
            }
        }

        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File does not exist in current directory.");
            return;
        }

        byte[] fileBytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(fileBytes);

        int bufferSize = 1024; // Each packet has a max of 1024 bytes
        int packetNum = (int) Math.ceil((double) fileBytes.length / bufferSize);

        System.out.println("Number of Packets: " + packetNum);

        DatagramSocket theSocket = new DatagramSocket();
        InetAddress ipAddress = InetAddress.getByName(recvHost);

        theSocket.setSoTimeout(2500); // Timeout interval

        // send filename
        System.out.println("Sending filename: " + filename);
        DatagramPacket filenamePacket = new DatagramPacket(filename.getBytes(), filename.length(), ipAddress, recvPort);
        theSocket.send(filenamePacket);

        byte[] buffer = new byte[bufferSize + 4]; // 4 bytes 4 offset

        for (int i = 0; i < packetNum; i++) {
            int start = i * bufferSize;
            int length = Math.min(bufferSize, fileBytes.length - start);

            // offset (start) as the first 4 bytes of the packet
            buffer[0] = (byte) (start >> 24);
            buffer[1] = (byte) (start >> 16);
            buffer[2] = (byte) (start >> 8);
            buffer[3] = (byte) (start);

            // put file data in buffer
            System.arraycopy(fileBytes, start, buffer, 4, length);

            DatagramPacket thePacket = new DatagramPacket(buffer, length + 4, ipAddress, recvPort);
            boolean ackReceived = false;

            while (!ackReceived) {
                theSocket.send(thePacket);
                System.out.println("[DATA TRANSMISSION]: " + start + " | " + length);

                try {
                    byte[] ackBuffer = new byte[16];
                    DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                    theSocket.receive(ackPacket);
                
                    // check ACK
                    String receivedAck = new String(ackPacket.getData(), 0, ackPacket.getLength()).trim();
                    
                    // discared non-number ACKs
                    if (receivedAck.matches("\\d+")) { 
                        if (Integer.parseInt(receivedAck) == start) {
                            ackReceived = true;
                            System.out.println("[ACK CONFIRMED]: " + start);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    System.err.println("[DATA RE-TRANSMISSION]: " + start + " | " + length);
                    theSocket.send(thePacket);
                }
            }
        }

        // empty packet as an end signal
        DatagramPacket endPacket = new DatagramPacket(new byte[0], 0, ipAddress, recvPort);
        theSocket.send(endPacket);

        System.out.println("[COMPLETE]");
        fileInputStream.close();
        theSocket.close();
    }
}
