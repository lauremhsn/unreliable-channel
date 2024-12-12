//a reliable source node (i.e. a sending node) program 
import java.util.*;
import java.net.*;
import java.io.*;

public class RUDPSource{
    public static void main (String [] args) throws Exception{
        //arg[0] - String "-r"
        //arg[1] - String recvHost:
        //arg[2] - int recvPort
        //arg[3] - String "-f"
        //arg[4] - String filename

        for (int i = 0; i<= args.length; i++){
            if (args[0] == "-r"){
                //split
                //get recvHost
                //get recvPort
            }
            if (args[0] == "-f"){
                //get filename
            }
        }

        String recvHost = "";
        int recvPort = 0;
        String filename = "";
        File file = new File("filename.txt");
        byte[] fileBytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(fileBytes);
        
        int bufferSize = 1024;
        //Every packet a max of 1024 bytes
        int packetNum = (int) Math.ceil(fileBytes.length/bufferSize);
        if (fileBytes.length<1024){
            //something
        }

        byte[] buff = new byte[bufferSize];
        DatagramSocket theSocket = new DatagramSocket(); 
        InetAddress ipAddress = InetAddress.getByName(recvHost);

        int timeOut = 2500;
        theSocket.setSoTimeout(timeOut);

        for (int i = 0; i<packetNum; i++){
            int start = i*bufferSize;
            int length = Math.min(1024, fileBytes.length-start); //just to double check last packet
            //fill out buff
            //for (int j = 0; j<=buffLength; j++){
            //    buff[j] = fileBytes[i];
            //}
            fileInputStream.read(buff);

            DatagramPacket thePacket = new DatagramPacket(buff, buff.length, ipAddress, recvPort);
            theSocket.send(thePacket);
            System.out.println("[DATA TRANSMISSION]: " + start + " | " + length);

            //acknowledgments 
            boolean ack = false;
            byte[] buffII = new byte[256];
            DatagramPacket thPacketII = new DatagramPacket(buffII, buffII.length); 
            while (!ack){
                try{
                    theSocket.receive(thPacketII);
                    String ackStatus = new String(thPacketII.getData(), 0, thPacketII.getLength());
                /*    if (ackStatus.equals("smth")){
                        ack = true;
                        //OK, DISCARDED
                    System.out.println("Acknowledgement Received: " + ackStatus);
                    }
                */    
                }
                catch (SocketTimeoutException e){
                    System.err.println("[DATA RE-TRANSMISSION]: " + start + " | " + length);
                    theSocket.send(thePacket);
                    
                }
            }

            
        }

        fileInputStream.close();
    }

}
