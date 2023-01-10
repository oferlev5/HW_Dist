import java.util.*;


public class Node implements Runnable {

    public int id;
    public int numOfNodes;
    public HashMap<Integer,int[]> portsById;
    public HashMap<Integer,Integer> IdByListeningPorts;
    public HashMap<Integer,Integer> idByBroadcastingPorts;

    public double[][] matrix;



    public Node(int numOfNodes,String line) {
        this.numOfNodes = numOfNodes;
        this.matrix =  new double[numOfNodes][numOfNodes];
        this.initMatrix();
        this.portsById = new HashMap<>();
        this.IdByListeningPorts = new HashMap<>();
        this.idByBroadcastingPorts = new HashMap<>();
        String[] splittedLine = line.split(" ");
        this.id = Integer.parseInt(splittedLine[0]);
        for (int i = 1; i < splittedLine.length; i += 4) {
            int[] values = {Integer.parseInt(splittedLine[i + 2]), Integer.parseInt(splittedLine[i + 3])};
            int key = Integer.parseInt(splittedLine[i]);
            double weight = Double.parseDouble(splittedLine[i + 1]);
            this.matrix[this.id -1][key-1] = weight;
            this.matrix[key -1][this.id-1] = weight;
            this.portsById.put(key, values);
            this.IdByListeningPorts.put(values[1], key);
            this.idByBroadcastingPorts.put(values[0],key);


        }
    }

    public void createServerSockets(HashMap<Integer,Integer> idByListeningPorts) {


    }

    public void initMatrix(){
        for (int i=0; i<this.numOfNodes;i++){
            for (int j=0; j<this.numOfNodes;j++){
                this.matrix[i][j] = -1;
            }
        }

    }

    public void print_graph() {
        // Loop through all rows
        double[][] mat = this.matrix;
        // Loop through all elements of current row
        for (double[] row : matrix) {
            for (double element : row) {
                System.out.print(element + " ");
            }
            System.out.println(); // Print a line break after each

        }

    }
    public void updateWeight(int neighbourId, double w){
        this.matrix[this.id-1][neighbourId-1] = w;
        this.matrix[neighbourId-1][this.id-1] = w;

    }




    public void run(){


    }



}
