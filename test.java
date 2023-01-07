import java.io.FileNotFoundException;

public class test {

    public static void main(String[] args) throws FileNotFoundException {
        ExManager manager = new ExManager("input_1.txt");
        manager.read_txt();
        System.out.println(manager.getNum_of_nodes());
        manager.update_edge(1,2,10);
        Node node = manager.get_node(1);
        node.print_graph();

    }

    }
