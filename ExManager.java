import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
//        if (!this.Threads.isEmpty()) {
//            for (Thread thread : Threads
//            ) {
////                System.out.println("stopped  thread " + thread.getName());
//                thread.interrupt();
////                System.out.println(thread.isAlive());
//            }
//
//        }
//        this.Threads = new ArrayList<>();


        for (Node value : this.Nodes.values()
        ) {
            try {
                Thread thread = new Thread(value);
                thread.start();
//                System.out.println("created new thread " + thread.getName());
                this.Threads.add(thread);


            } catch (Exception e) {
                e.printStackTrace();
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
    }

    // your code here


    public void terminate() {
        for (Thread t: this.Threads
             ) {
            t.interrupt();

        }
    }
}
