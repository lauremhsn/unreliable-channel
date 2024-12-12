import java.net.*;
import java.io.*;
import java.util.*;

public class RUDPDestination{ //java RUDPDestination -p <recvPort>
    public static void main(String[] args) throws Exception{
        int recvPort = 0;
        for (int i = 0; i<args.length; i++){
            if (args[i].equals("-p")){
                recvPort = Integer.parseInt(args[i+1]);
            }
        }
        DatagramSocket theSocketII = new DatagramSocket(recvPort);
        byte[] buff = new byte[1024];
        DatagramPacket thePacketIII = new DatagramPacket(buff, buff.length);

        FileOutputStream fileOutputStream = null;
        File file = null;
        
        double packetLost = 0.3; //30% chance of packet loss simulation
        File directory = new File("filesReceived");
        if (!directory.exists()) {
            directory.mkdirs(); //create if it doesnt exist
        }

        boolean fileName = false; //to check if we received file name

        while(true){
            Random random = new Random();
            theSocketII.receive(thePacketIII);
            System.out.println("[DATA RECEIVED]: Packet received - Length: " + thePacketIII.getLength());

            if (thePacketIII.getLength() == 0){
                System.out.println("END PACKET");
                break;//this is endPacket
            }

            if (random.nextDouble()<packetLost){
                System.out.println("[DATA RECEPTION]: " + thePacketIII.getOffset() + " | " + thePacketIII.getLength() + " | DISCARDED");
                continue; //lose the packet - oops
            }

            String packetStuff = new String(thePacketIII.getData(), 0, thePacketIII.getLength());
            
            if (!fileName){
                System.out.println("Received filename: " + packetStuff);
                file = new File(directory, packetStuff);
                try{
                    fileOutputStream = new FileOutputStream(file);
                    System.out.println("RECEIVING FILE: " + file.getName());
                }
                catch (FileNotFoundException e){
                    System.err.println("File creation failed: " + e.getMessage());
                    break;
                }
            }
            else{
                fileOutputStream.write(thePacketIII.getData(), 0, thePacketIII.getLength());
                System.out.println("[DATA RECEPTION]: " + thePacketIII.getOffset() + " | " + thePacketIII.getLength() + " | OK");
            }

            String ackStatus = "" + thePacketIII.getOffset();
            byte[] ack = ackStatus.getBytes();
            DatagramPacket thePacketIV = new DatagramPacket(ack, ack.length, thePacketIII.getAddress(), thePacketIII.getPort());
            theSocketII.send(thePacketIV);
            System.out.println("SENDING ACKNOWLEDGMENT " + ackStatus);

            if (thePacketIII.getLength()<1024) {
                break;//this is the last packet
            }
        }
        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
        theSocketII.close();

        System.out.println("[COMPLETE]");
    }
}
