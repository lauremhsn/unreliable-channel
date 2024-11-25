import java.net.*;

public class Client { //SENDER RECEIVER PORT, EX: A B PORT
    public static void main (String [] args) throws Exception{
        String msgSender = args[0];
        String msgReceiver = args[1];
        InetAddress IP = InetAddress.getLocalHost();
        int port = Integer.parseInt(args[2]);

        int seq = 0;

        DatagramSocket theSocket = new DatagramSocket();

        sendPackets(msgSender, msgReceiver, seq, port, IP, theSocket); //Sender

        sendPackets(msgReceiver, msgSender, seq, port, IP, theSocket); //Receiver

        
        String endMess = "END";
        byte[] endArr = endMess.getBytes();
        DatagramPacket lastPacket = new DatagramPacket(endArr, endArr.length, IP, port);
        theSocket.send(lastPacket);

        theSocket.close();
    }

    private static void sendPackets(String msgSender, String msgReceiver, int seq, int port, InetAddress IP, DatagramSocket theSocket) throws Exception{
        for (int i = 0; i<30; ++i){
            String s = msgSender + " " + msgReceiver + " " + seq;
            seq = 1-seq;

            byte[] aBuff = s.getBytes();
            DatagramPacket thePacket = new DatagramPacket(aBuff, aBuff.length, IP, port);
            
            Thread.sleep(500);
            
            theSocket.send(thePacket);
        }
    }
}
