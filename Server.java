import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;
public class Server {
    public int counter = 0;

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        ServerSocket Anat = new ServerSocket(9999);
        new Thread(() -> {
            try {
                while (true) {
                    System.out.println("created server anat");
                    // Accept incoming connections
                    Socket clientSocket = Anat.accept();
                    server.counter+=1;
                    DataInputStream dis=new DataInputStream(clientSocket.getInputStream());
                    String  str=(String)dis.readUTF();
                    System.out.println("message= "+str + server.counter);


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        final int NUM_SOCKETS = 20;

        // Create the server sockets
        ServerSocket[] sockets = new ServerSocket[NUM_SOCKETS];
        for (int i = 0; i < NUM_SOCKETS; i++) {
            sockets[i] = new ServerSocket(10000 + i);
        }

        // Start a thread for each socket to handle incoming connections
        for (int i = 0; i < NUM_SOCKETS; i++) {
            int socketNum = i;
            new Thread(() -> {
                try {
                    while (true) {
                        System.out.println("created server");
                        // Accept incoming connections
                        Socket clientSocket = sockets[socketNum].accept();

                        // Read data from the client socket
                        DataInputStream dis=new DataInputStream(clientSocket.getInputStream());
                        String str=(String)dis.readUTF();
                        // update matrix
                        // Send the data to all of the other sockets

                        //send the data to socket anat
                        Socket s=new Socket("localhost",9999);
                        DataOutputStream dout=new DataOutputStream(s.getOutputStream());
                        dout.writeUTF("(2,4) weight:5");
                        dout.flush();
                        dout.close();
                        s.close();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}