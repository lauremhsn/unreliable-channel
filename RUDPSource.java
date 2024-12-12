//a reliable source node (i.e. a sending node) program 
import java.net.*;
import java.io.*;

public class RUDPSource{
    public static void main (String [] args) throws Exception{
        //arg[0] - String "-r"
        //arg[1] - String recvHost:int recvPort
        //arg[2] - String "-f" 
        //arg[3] - String filename

        String recvHost = "";
        int recvPort = 0;
        String filename = "";

        for (int i = 0; i<args.length; i++){
            if ("-r".equals(args[i])){
                String[] recvInfo = args[i + 1].split(":");//split
                recvHost = recvInfo[0];//get recvHost
                recvPort = Integer.parseInt(recvInfo[1]);//get recvPort
            }
            if ("-f".equals(args[i])){
                filename = args[i + 1];//get filename
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
        
        int bufferSize = 1024;
        //Every packet a max of 1024 bytes
        int packetNum = (int) Math.ceil((double) fileBytes.length/bufferSize);

        byte[] buff = new byte[bufferSize];
        DatagramSocket theSocket = new DatagramSocket(); 
        InetAddress ipAddress = InetAddress.getByName(recvHost);

        int timeOut = 2500;
        theSocket.setSoTimeout(timeOut);

        System.out.println("Sending filename: " + filename);
        DatagramPacket filenamePacket = new DatagramPacket(filename.getBytes(), filename.length(), ipAddress, recvPort);
        theSocket.send(filenamePacket);

        for (int i = 0; i<packetNum; i++){
            System.out.println("in for-loop");
            int start = i*bufferSize;
            System.out.println("Start: " + start);
            int length = Math.min(1024, fileBytes.length-start); //just to double check last packet
            System.out.println("Length: " + length);
            //fill out buff
            for (int j = 0; j<length; j++){
                buff[j] = fileBytes[start + j];
            }
            if (length == 0) {
                break; //no more stuff to read
            }

            DatagramPacket thePacket = new DatagramPacket(buff, length, ipAddress, recvPort);
            theSocket.send(thePacket);
            System.out.println("[DATA TRANSMISSION]: " + start + " | " + length);

            //acknowledgments 
            boolean ack = false;
         
            while (!ack){
                System.out.println("in ack-loop");
                try{
                    byte[] buffII = new byte[16];
                    DatagramPacket thPacketII = new DatagramPacket(buffII, buffII.length);
                    theSocket.receive(thPacketII);
                    String ackStatus = new String(thPacketII.getData(), 0, thPacketII.getLength());
                    System.out.println("ACKNOWLEDGING: " + ackStatus + "/" + packetNum + " PACKETS");
                    if (ackStatus.equals(""+start)){
                        ack = true;
                        System.out.println("[ACK CONFIRMED]: " + start);
                    }  
                    //else{
                    //    System.out.println("idk saraha");
                    //}
                }
                catch (SocketTimeoutException e){
                    System.err.println("[DATA RE-TRANSMISSION]: " + start + " | " + length);
                    theSocket.send(thePacket);
                }
            }

            
        }
        byte[] endSignal = new byte[0];
        DatagramPacket endPacket = new DatagramPacket(endSignal, endSignal.length, ipAddress, recvPort);
        theSocket.send(endPacket);

        System.out.println("[COMPLETE]");
        fileInputStream.close();
        theSocket.close();
    }

}
