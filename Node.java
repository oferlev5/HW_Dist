import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;


public class Node implements Runnable {

    public int id;
    public int numOfNodes;
    public HashMap<Integer,int[]> portsById;
    public HashMap<Integer,Integer> IdByListeningPorts;
    public HashMap<Integer,Integer> idByBroadcastingPorts;
    public ArrayList<ServerSocket> serverSockets;
    public StringBuilder Message;
    public ArrayList<Thread> threads;
    HashSet<Integer> notRecievedFrom;
    public static CountDownLatch latch;
    public int portCounter;

    public double[][] matrix;


    public Node(int numOfNodes,String line) {
        this.threads = new ArrayList<>();
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
//        this.createServerSockets();
        this.Message.append(this.numOfNodes);


    }
    public String createString(int node1, int node2, double w){
        String s = Integer. toString(node1) + " " + Integer.toString(node2)+ " " + Double.toString(w);
        return s;
    }

    public synchronized void updateSet(String toRemove) {
        this.notRecievedFrom.remove(Integer.parseInt(toRemove));
    }

    public synchronized void resetCounter(){
        this.portCounter = this.IdByListeningPorts.keySet().toArray().length;
    }

    public synchronized void updateCounter(){
        this.portCounter--;
    }

    public synchronized int getCounter(){
        return this.portCounter;
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
        for (int i = 0; i <this.matrix.length; i++) {
            {
                for (int j = 0; j <this.matrix[0].length; j++) {
//                System.out.print(this.matrix[i][j]);
                    if (j != this.matrix[0].length -1) {
                        System.out.print(this.matrix[i][j] +", ");
                    }
                    else {
                        System.out.print(this.matrix[i][j]);
                        System.out.println();

                    }

                }
            }
//            System.out.println(); // Print a line break after each

        }

    }

    public void updateMessage(int neighbourId,double w) {
        String[] splittedMessage = String.valueOf(this.Message).split(" ");
        for (int i = 1; i< splittedMessage.length-1;i+=3) {
            if (Integer.parseInt(splittedMessage[i]) == neighbourId) {
                splittedMessage[i+1] = String.valueOf(w);
            }
        }
        splittedMessage[splittedMessage.length-1] = String.valueOf(this.numOfNodes);
        String updatedData = String.join(" ", splittedMessage);
        this.Message = new StringBuilder(updatedData);
//        System.out.println("the message is " + this.Message);
    }


    public void updateWeight(int neighbourId, double w){
        this.matrix[this.id-1][neighbourId-1] = w;
        this.matrix[neighbourId-1][this.id-1] = w;
        this.updateMessage(neighbourId, w);


    }




    public void run(){
//        if (!this.threads.isEmpty()) {
//            this.closeThread = true;
////            boolean AllThreadClosed = false;
//            for (Thread t: this.threads
//                 ) {
//                System.out.println(t.isAlive());
//
//            }
//            for (Thread t: threads
//                 ) {
//                t.interrupt();
//
//            }
//        }
        this.threads = new ArrayList<>();
        for (ServerSocket ss: this.serverSockets
        ) {
            Thread t = new Thread(() -> {
                try {
                    boolean firstRound = true;
                    while (!Thread.currentThread().isInterrupted()) {
                        // Accept incoming connections
                        if (ss.isClosed()) {
                            continue;
                        }
                        else {
                            if(firstRound) {
                                firstRound = false;
                                this.updateCounter();
//                                System.out.println("Node " + this.id + " | Counter has been updated to " + this.getCounter());
                            }

                            Socket clientSocket = ss.accept();

                            // Read data from the client socket
                            DataInputStream dis=new DataInputStream(clientSocket.getInputStream());
                            String data =dis.readUTF();
                            String[] words = data.split(" ");
                            boolean forwardMesaage = false;
                            if (this.notRecievedFrom.contains(Integer.parseInt(words[0]))) {
                                this.updateMatrix(words);
                                this.updateSet(words[0]);
                                forwardMesaage = true;
                            }

//                        this.updateMatrix(words);
                            int hopCounter = Integer.parseInt(words[words.length-1]);
                            words[words.length-1] = String.valueOf(hopCounter-1);
                            String updatedData = String.join(" ", words);

                            // Send the data to all of the other sockets
                            int Port = ss.getLocalPort();
                            int sender = this.IdByListeningPorts.get(Port);
                            int senderPort = this.portsById.get(sender)[0];

                            //send the data to the other server sockets
                            if (hopCounter > 0 && forwardMesaage) {
                                this.sendMessage(updatedData, senderPort);
//                            System.out.println("sent message to neigh");
//                            for (int port:idByBroadcastingPorts.keySet()
//                            ) {
//                                if (port != senderPort) {
//                                    Socket s = new Socket("localhost", port);
//                                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
//                                    dout.writeUTF(updatedData);
//                                    dout.flush();
//                                    dout.close();
//                                    s.close();
//
//                                }
//
//                            }
                            }
                        }
                    }

                } catch (IOException e) {
//                    e.printStackTrace();
                }

            });
            t.start();
            this.threads.add(t);


        }
        while (this.getCounter() >0) {

        }
//        System.out.println("Node" + this.id + " | ports counter has been gone to 0");
        try {
            latch.countDown();
//            System.out.println("Node" + this.id + " | downed the latch by one");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        for (Thread t: this.threads
//             ) {
//            t.start();

//        }
        this.sendFirstMessage();


    }
    public void sendFirstMessage() {
//        System.out.println("Node" + this.id + " | start sending first message");
        try {
            for (int port : this.idByBroadcastingPorts.keySet()
            ) {
                Socket s = new Socket("localhost", port);
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                dout.writeUTF(String.valueOf(this.Message));
                dout.flush();
                dout.close();
                s.close();
//                System.out.println("Node" + this.id + " | message was sent to port " + port);
            }
//                System.out.println("reached false ");
        } catch (ConnectException e) {
            System.out.println("anat");


        } catch (IOException ee) {
//            ee.printStackTrace();
        }

//        for (int port : idByBroadcastingPorts.keySet()
//        ) {
//            try {
//                Socket s = new Socket("localhost", port);
//                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
//                dout.writeUTF(String.valueOf(this.Message));
//                dout.flush();
//                dout.close();
//                s.close();
//
//            } catch (ConnectException ignored) {
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }


    }

//    public void sendFirstMessage(){
//        try {
//            for (int port: idByBroadcastingPorts.keySet()
//                 ) {
//                Socket s= new Socket("localhost",port);
//                DataOutputStream dout=new DataOutputStream(s.getOutputStream());
//                dout.writeUTF(String.valueOf(this.Message));
//                dout.flush();
//                dout.close();
//                s.close();
//
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    public  void sendMessage(String M, int senderPort) {
        try {
            for (int port : idByBroadcastingPorts.keySet()
            ) {
                if (port != senderPort) {
                    Socket s = new Socket("localhost", port);
                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                    dout.writeUTF(M);
                    dout.flush();
                    dout.close();
                    s.close();
                }
            }
//                System.out.println("reached false ");
        } catch (ConnectException e) {
//            System.out.println("ofer");


        } catch (IOException ee) {
//            ee.printStackTrace();
        }



//        for (int port : idByBroadcastingPorts.keySet()
//        ) {
//            if (port != senderPort) {
//                try {
//                    Socket s = new Socket("localhost", port);
//                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
//                    dout.writeUTF(M);
//                    dout.flush();
//                    dout.close();
//                    s.close();
//
//                } catch (ConnectException ignored) {
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        }
    }


}




