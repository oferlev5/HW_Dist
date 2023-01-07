import java.util.*;


public class Node implements Runnable {

    public int id;
    public int numOfNodes;
    public HashMap<Integer,double[]> neighboursPorts;
    public double[][] matrix;



    public Node(int numOfNodes,String line) {
        this.numOfNodes = numOfNodes;
        this.matrix =  new double[numOfNodes][numOfNodes];
        this.initMatrix();
        this.neighboursPorts = new HashMap<>();
        String[] splittedLine = line.split(" ");
        this.id = Integer.parseInt(splittedLine[0]);
        for (int i = 1; i < splittedLine.length; i += 4) {
            double[] values = {Double.parseDouble(splittedLine[i + 2]), Double.parseDouble(splittedLine[i + 3])};
            int key = Integer.parseInt(splittedLine[i]);
            double weight = Double.parseDouble(splittedLine[i + 1]);
            this.matrix[this.id -1][key-1] = weight;
            this.matrix[key -1][this.id-1] = weight;
            this.neighboursPorts.put(key, values);


        }
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
