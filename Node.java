import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.*;


public class Node implements Runnable {

    public int id;
    public int numOfNodes;
    public HashMap<Integer,int[]> portsById;
    public HashMap<Integer,Integer> IdByListeningPorts;
    public HashMap<Integer,Integer> idByBroadcastingPorts;
    public ArrayList<ServerSocket> serverSockets;
    public StringBuilder Message;

    public double[][] matrix;


    public Node(int numOfNodes,String line) {
        this.Message = new StringBuilder();
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
            this.Message.append(this.createString(this.id, key, weight)).append(" ");
        }
        this.serverSockets = new ArrayList<>();
        this.createServerSockets();
        this.Message.append(this.numOfNodes);


    }
    public String createString(int node1, int node2, double w){
        String s = Integer. toString(node1) + " " + Integer.toString(node2)+ " " + Double.toString(w);
        return s;
    }

    public void createServerSockets() {
        for ( Integer port: this.IdByListeningPorts.keySet()
             ) {
            try {
                ServerSocket s = new ServerSocket(port);
                this.serverSockets.add(s);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    public void initMatrix(){
        for (int i=0; i<this.numOfNodes;i++){
            for (int j=0; j<this.numOfNodes;j++){
                this.matrix[i][j] = -1;
            }
        }

    }
    public synchronized void updateMatrix(String[] data) {
        for(int i = 0; i<data.length-1;i+=3) {
            int node1 = Integer.parseInt(data[i]);
            int node2 = Integer.parseInt(data[i+1]);
            double w = Double.parseDouble(data[i+2]);
            this.matrix[node1 -1][node2-1] = w;
            this.matrix[node2 -1][node1-1] = w;
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

    public void updateMessage(int neighbourId,double w) {
        String[] splittedMessage = String.valueOf(this.Message).split(" ");
        for (int i = 1; i< splittedMessage.length-1;i+=3) {
            if (Integer.parseInt(splittedMessage[1]) == neighbourId) {
                splittedMessage[i+1] = String.valueOf(w);
            }
        }
        splittedMessage[splittedMessage.length-1] = String.valueOf(this.numOfNodes);
        String updatedData = String.join(" ", splittedMessage);
        this.Message = new StringBuilder(updatedData);
    }


    public void updateWeight(int neighbourId, double w){
        this.matrix[this.id-1][neighbourId-1] = w;
        this.matrix[neighbourId-1][this.id-1] = w;
        this.updateMessage(neighbourId, w);


    }




    public void run(){
        for (ServerSocket ss: this.serverSockets
             ) {
            new Thread(() -> {
                try {
                    while (true) {
                        // Accept incoming connections
                        Socket clientSocket = ss.accept();
                        // Read data from the client socket
                        DataInputStream dis=new DataInputStream(clientSocket.getInputStream());
                        String data =dis.readUTF();
                        String[] words = data.split(" ");
                        this.updateMatrix(words);
                        int hopCounter = Integer.parseInt(words[words.length-1]);
                        words[words.length-1] = String.valueOf(hopCounter-1);
                        String updatedData = String.join(" ", words);

                        // Send the data to all of the other sockets
                        int Port = ss.getLocalPort();
                        int sender = this.IdByListeningPorts.get(Port);
                        int senderPort = this.portsById.get(sender)[0];

                        //send the data to the other server sockets
                        if (hopCounter > 0) {
                            for (int port:idByBroadcastingPorts.keySet()
                            ) {
                                if (port != senderPort) {
                                    Socket s= new Socket("localhost",port);
                                    DataOutputStream dout=new DataOutputStream(s.getOutputStream());
                                    dout.writeUTF(updatedData);
                                    dout.flush();
                                    dout.close();
                                    s.close();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();


                    }
        this.sendFirstMessage();




        }
    public void sendFirstMessage(){
        try {
            for (int port: idByBroadcastingPorts.keySet()
                 ) {
                Socket s= new Socket("localhost",port);
                DataOutputStream dout=new DataOutputStream(s.getOutputStream());
                dout.writeUTF(String.valueOf(this.Message));
                dout.flush();
                dout.close();
                s.close();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    }




