import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class test {

    public static void main(String[] args) throws IOException, InterruptedException {
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
        Runnable task = () -> {
            // Code to be executed by the thread
            while (!Thread.interrupted()) {
                System.out.println("king");
            }
        };
        Thread newThread = new Thread(task);
        newThread.start();
        TimeUnit.SECONDS.sleep(5);
        newThread.interrupt();


    }

    }
