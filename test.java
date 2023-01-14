import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class test {

    public static void main(String[] args) throws IOException {
//        ExManager manager = new ExManager("input_1.txt");
//        manager.read_txt();
//        System.out.println(manager.getNum_of_nodes());
//        manager.update_edge(1,2,10);
//        Node node = manager.get_node(1);
//        node.print_graph();
//        System.out.println(node.portsById);
//        System.out.println(node.idByBroadcastingPorts);
//        System.out.println(node.IdByListeningPorts);
        Node node = new Node(5,"1 4 8.9 6060 13821 3 7.5 19068 6327");
        System.out.println((node.Message));

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



    }

    }
