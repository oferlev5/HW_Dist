import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class test {

    public static void main(String[] args) throws IOException, InterruptedException, FileNotFoundException {
//        ExManager manager = new ExManager("input_1.txt");
//        manager.read_txt();
//        System.out.println(manager.getNum_of_nodes());
//        manager.update_edge(1,2,10);
//        Node node = manager.get_node(1);
//        node.print_graph();
//        System.out.println(node.portsById);
//        System.out.println(node.idByBroadcastingPorts);
//        System.out.println(node.IdByListeningPorts);
//        Node node = new Node(5,"1 4 8.9 6060 13821 3 7.5 19068 6327");
//        System.out.println((node.Message));

//        ServerSocket[] sockets = new ServerSocket[20];
//        for (int i = 0; i < 20; i++) {
//            sockets[i] = new ServerSocket(10000 + i);
//
//        }
//        for (int i = 0; i < 20; i++) {
//            System.out.println("trying to accept requests in socket number" + i);
//            Socket clientSocket = sockets[i].accept();
//
//        }
//        System.out.println("i finished");
//        ServerSocket Ofer = new ServerSocket(5678);
//        System.out.println(Ofer.getLocalPort());
//        Socket clientSocket = Ofer.accept();
//        String s = Integer. toString(1) + " " + Integer.toString(2)+ " " + Double.toString(3.4);
//        System.out.println(s);
//        ServerSocket s= new ServerSocket(4567);
//        new Thread(() -> {
//            boolean is = true;
//            while (is){
//                try {
//                    Socket se = new Socket("localhost", 4567);
//                    DataOutputStream dout = new DataOutputStream(se.getOutputStream());
//                    dout.writeUTF("Asdasd");
//                    dout.flush();
//                    dout.close();
//                    s.close();
//                    is = false;
//
//                } catch (ConnectException ee) {
//                    System.out.println("here");
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }}).start();
//
//        new Thread(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                Socket r = s.accept();
//                System.out.println("accepted req");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//        Runnable task = () -> {
//            // Code to be executed by the thread
//            while (!Thread.interrupted()) {
//                System.out.println("king");
//            }
//        };
//        Thread newThread = new Thread(task);
//        newThread.start();
//        TimeUnit.SECONDS.sleep(5);
//        newThread.interrupt();
//        String[] paths = {"input_2.txt"};
//        Thread t = new Thread(() -> {
//            for (int i = 0; i <1000000 ; i++) {
//                System.out.println(i);
//
//            }
//        });
//        t.start();
//        TimeUnit.SECONDS.sleep(1);
//        while (true){
//            System.out.println(t.isAlive());
//        }
        ServerSocket ss= new ServerSocket(4567);
        Thread t = new Thread(() -> {
            try {
                boolean firstRound = true;
                while (!Thread.currentThread().isInterrupted()) {
                    // Accept incoming connection
                        Socket clientSocket = ss.accept();

                        // Read data from the client socket
                        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                    try {
                        Pair<Integer,HashMap<Integer,HashMap<Integer,Double>>> receivedMap = (Pair<Integer,HashMap<Integer,HashMap<Integer,Double>>>)
                                ois.readObject();
                        HashMap<Integer, HashMap<Integer,Double>> data = receivedMap.getValue();
                       Set<Integer> MessagerOwner = data.keySet();
                        System.out.println(MessagerOwner);

                    }catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
//
//                        String[] words = data.split(" ");
//                        this.updateMatrix(words);
                        //send the data to the other server sockets

                        }

                } catch (IOException e) {
                e.printStackTrace();
            }

        });
        t.start();

        int hopcounter = 5;
        HashMap<Integer,HashMap<Integer,Double>> ofer = new HashMap<>();
        HashMap<Integer,Double> values = new HashMap<>();
        values.put(4,5.9);
        ofer.put(1,values);
        Pair<Integer,HashMap<Integer,HashMap<Integer,Double>>> anat = new Pair<>(hopcounter,ofer);
        Socket s = new Socket("localhost", 4567);
        ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());
        dout.writeObject(anat);
        dout.flush();
        dout.close();
        s.close();




    }

    }
