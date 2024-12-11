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
        int bufferSize = 1024;
        fileInputStream.read(fileBytes);
        fileInputStream.close();

        //1024/fileBytes
        //file-1024, 
        //check if fileBytes<1024
        //get mod, and iterate until mod + 1
        //within iteration, send DatagramPacket

        byte[] buff = new byte[bufferSize];
        DatagramSocket theSocket = new DatagramSocket(); 
        InetAddress ipAddress = InetAddress.getByName(recvHost);

        

    }
}
