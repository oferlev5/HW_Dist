import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ExManager {
    private final String path;
    private int num_of_nodes=0;
    public HashMap<Integer, Node> Nodes;
    public ArrayList<Thread> Threads = new ArrayList<>();
    // your code here

    public ExManager(String path) {
        this.path = path;
        this.Nodes = new HashMap<>();
        // your code here
    }

    public Node get_node(int id) {
        return this.Nodes.get(id);
        // your code here
    }

    public int getNum_of_nodes() {
        return this.num_of_nodes;
    }

    public void update_edge(int id1, int id2, double weight) {
        Node node1 = get_node(id1);
        Node node2 = get_node(id2);
        node1.updateWeight(id2,weight);
        node2.updateWeight(id1,weight);
        //your code here
    }

    public void read_txt() throws FileNotFoundException {
        // your code here
        Scanner scanner = new Scanner(new File(this.path));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("stop"))
                break;
            else if(!line.contains(" ")) {
                this.num_of_nodes = Integer.parseInt(line);
            }
            else {
                Node newNode = new Node(this.num_of_nodes, line);
                this.Nodes.put(newNode.id, newNode);


            }

        }
    }

    public void start() {
        boolean isFirstRound = true;
        for (Node n: this.Nodes.values()
        ) {
            if (!n.threads.isEmpty()){
                isFirstRound = false;
                break;
            }

        }
        if (!isFirstRound) {
            this.terminate();
        }
        Node.latch = new CountDownLatch(this.num_of_nodes);
        for (Node n: this.Nodes.values()
        ) {
            n.createServerSockets();
            n.notRecievedFrom = new HashSet<>();
            for (int i = 1; i < this.num_of_nodes + 1; i++) {
                n.notRecievedFrom.add(i);
//                Node.lockCounter = 0;
//                Node.lock =  new Object();
            }
            n.notRecievedFrom.remove(n.id);
            n.resetCounter();

        }
//        for (Node n: this.Nodes.values()
//             ) {
//            if (!n.threads.isEmpty()) {
//                for (Thread t: n.threads
//                     ) {
//                    t.interrupt();
//
//                }
////                n.closeThread = true;
////                boolean AllThreadAreClosed = false;
//
//            }
//
//        }


        for (Node value : this.Nodes.values()
        ) {
            try {
                Thread thread = new Thread(value);
                thread.start();
//                System.out.println("created new thread " + thread.getName());
                this.Threads.add(thread);


            } catch (Exception e) {
//                e.printStackTrace();
            }

        }
        for (Thread t : this.Threads
        ) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        boolean toContinue = false;
        boolean fromBreak = false;
        while (!toContinue) {
            for (Node n: this.Nodes.values()
            ) {
                if (!n.notRecievedFrom.isEmpty()) {
                    fromBreak = true;
                }

            }
            toContinue = !fromBreak;
            fromBreak = false;
//            System.out.println("not received messages from all nodes");
        }
    }

    // your code here


    public void terminate() {
        ArrayList<Thread> threadsToClose = new ArrayList<>();
        for (Node node: this.Nodes.values()
        ) {
            for (ServerSocket s: node.serverSockets
            ) {
                try {
//                    System.out.println("closing....");
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            for (Thread t: node.threads
            ) {
                t.interrupt();
                threadsToClose.add(t);
//                while (t.isAlive()) {
//                }
//                System.out.println(t.isAlive());

            }
        }
//        while (!threadsToClose.isEmpty()) {
//            System.out.println("got here");
//            threadsToClose.removeIf(t -> !t.isAlive());
//        }

//        System.out.println("all threads are dead");
    }
}
