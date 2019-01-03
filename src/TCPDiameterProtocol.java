import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import sun.misc.IOUtils;
import sun.plugin2.message.Message;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TCPDiameterProtocol {

    private static final int PORT = 3868;


    public static void main(String[] args) throws Exception {
//        Socket serversocket = new Socket("127.0.0.1", 3876);
//        ServerSocket serverSocket2 = new ServerSocket(PORT2);
        //serversocket.bind(new InetSocketAddress("127.0.0.1", 3876));
//        System.out.println("Second server is connected");
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("First server starting");
        try {
            while (true) {
                new ThreadConector(serverSocket.accept()).start();
                System.out.println("First server is up and running");

            }
        } finally {
            serverSocket.close();
        }
    }

    public static class ThreadConector extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private Socket serversocket;


        public ThreadConector(Socket socket) throws IOException {
            this.clientSocket = socket;
            Socket serversocket = new Socket();
            serversocket.bind(new InetSocketAddress("127.0.0.2", 3876));
            System.out.println("Second server is connected");
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                DataOutputStream outinfo = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                DataInputStream ininfo = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                InputStream in = clientSocket.getInputStream();
                byte[] bytes = new byte[32];
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int count;
                while ((count = in.read(bytes)) > 0) {
                    //modify here
                    buffer.write(bytes, 0, count);
                }
                byte[] a = buffer.toByteArray();
                System.out.println(Arrays.toString(a));

                System.out.println(new String(bytes, 0));
                System.out.println("Test for 5: " + a[5]);
                System.out.println("Test for 6: " + a[6]);
                System.out.println("Test for 7: " + a[7]);
                ByteBuffer bb = ByteBuffer.wrap(new byte[] {0,a[5],a[6],a[7]});
                int intValue = bb.getInt();
                System.out.println(intValue);
                System.out.println("Capabilities-Exchange-Answer - CEA");

                //sending message to another server
                String returnMessage;
                try
                {
                    String numberInIntFormat = new String(bytes, 0);
                    returnMessage = numberInIntFormat;
                }
                catch(NumberFormatException e)
                {
                    //Input was not a number. Sending proper message back to client.
                    returnMessage = "Verificati inca o data mesajul \n";

                }
                System.out.println(returnMessage);
                //Sending the response back to the server.
                OutputStream os = serversocket.getOutputStream();
                System.out.println(os);
                OutputStreamWriter osw = new OutputStreamWriter(os);
                System.out.println(osw);
                BufferedWriter bw = new BufferedWriter(osw);
//                bw.write(returnMessage);
//                System.out.println("Message sent to the client is "+ returnMessage);
//                bw.flush();

            } catch (IOException e) {
                System.out.println(e);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                }
            }
        }

    }

}