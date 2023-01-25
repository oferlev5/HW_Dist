import java.io.*;
import java.io.IOException;
import java.net.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;


public class Node implements Runnable {

    public int id;
    public int numOfNodes;
    public HashMap<Integer,int[]> portsById;
    public HashMap<Integer,Integer> IdByListeningPorts;
    public HashMap<Integer,Integer> idByBroadcastingPorts;
    public ArrayList<ServerSocket> serverSockets;
    public Pair<Integer,HashMap<Integer,HashMap<Integer,Double>>> Message;
    public ArrayList<Thread> threads;
    HashSet<Integer> notRecievedFrom;
    public static CountDownLatch latch;
    public int portCounter;

    public double[][] matrix;


    public Node(int numOfNodes,String line) {
        this.threads = new ArrayList<>();
        this.numOfNodes = numOfNodes;
        this.matrix = new double[numOfNodes][numOfNodes];
        this.initMatrix();
        this.portsById = new HashMap<>();
        this.IdByListeningPorts = new HashMap<>();
        this.idByBroadcastingPorts = new HashMap<>();
        String[] splittedLine = line.split(" ");
        this.id = Integer.parseInt(splittedLine[0]);
        this.createEmptyMessage();
        for (int i = 1; i < splittedLine.length; i += 4) {
            int[] values = {Integer.parseInt(splittedLine[i + 2]), Integer.parseInt(splittedLine[i + 3])};
            int key = Integer.parseInt(splittedLine[i]);
            double weight = Double.parseDouble(splittedLine[i + 1]);
            this.matrix[this.id - 1][key - 1] = weight;
            this.matrix[key - 1][this.id - 1] = weight;
            this.portsById.put(key, values);
            this.IdByListeningPorts.put(values[1], key);
            this.idByBroadcastingPorts.put(values[0], key);
            this.createMessage(key, weight);
            this.serverSockets = new ArrayList<>();
//        this.createServerSockets();


        }
//        System.out.println(this.Message);
    }

    public void createEmptyMessage(){
        HashMap<Integer,HashMap<Integer,Double>> data = new HashMap<>();
        HashMap<Integer,Double> values = new HashMap<>();
        data.put(this.id,values);
        this.Message = new Pair<>(this.numOfNodes,data);

    }


    public void createMessage(int nKey,double w){
        HashMap<Integer, HashMap<Integer, Double>> data = this.Message.getValue();
        HashMap<Integer, Double> innerMap= data.get(this.id);
        innerMap.put(nKey, w);

    }

    public synchronized void updateSet(Integer toRemove) {
        this.notRecievedFrom.remove(toRemove);
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
//                System.out.println("node with id "+ this.id + "| server socket with port " + port + " created");
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
    public synchronized void updateMatrix(HashMap<Integer,Double> data, int messageOwner) {
        for (int neighbour: data.keySet()
        ) {
            double w = data.get(neighbour);
            this.matrix[messageOwner -1][neighbour-1] = w;
            this.matrix[neighbour -1][messageOwner-1] = w;


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
        HashMap<Integer, HashMap<Integer, Double>> data = this.Message.getValue();
        HashMap<Integer, Double> innerMap= data.get(this.id);
        innerMap.put(neighbourId, w);
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
                Instant timestamp;
                timestamp = Instant.now();
//                System.out.println(timestamp + " | node " + this.id + "|" +" opened a new thread for listening for port " +ss.getLocalPort());
                try {
                    boolean firstRound = true;
                    while (!ss.isClosed()) {
                        // Accept incoming connections
                        if (ss.isClosed()) {
                            timestamp = Instant.now();
//                            System.out.println(timestamp + " | node " + this.id + "|" +" thread for listening for port " +ss.getLocalPort() +" encounterd closed socket");

                            continue;
                        }
                        else {
                            if(firstRound) {
                                firstRound = false;
                                this.updateCounter();
//                                System.out.println("Node " + this.id + " | Counter has been updated to " + this.getCounter());
                            }
                            timestamp = Instant.now();
//                            System.out.println(timestamp + " | node " + this.id + "|" +"  thread for listening for port " +ss.getLocalPort() +" before accept");

                            Socket clientSocket = ss.accept();
                            timestamp = Instant.now();
//                            System.out.println(timestamp + " | node " + this.id + "|" +"  thread for listening for port " +ss.getLocalPort() +" after accept");

                            // Read data from the client socket
                            ObjectInputStream dis=new ObjectInputStream(clientSocket.getInputStream());
                            try {
                                Pair<Integer,HashMap<Integer,HashMap<Integer,Double>>> receivedPair = (Pair<Integer,HashMap<Integer,HashMap<Integer,Double>>>)
                                        dis.readObject();
                                HashMap<Integer, HashMap<Integer,Double>> data = receivedPair.getValue();
                                int messagerOwner = (int) data.keySet().toArray()[0];
                                HashMap<Integer,Double> innerData = data.get(messagerOwner);
                                boolean forwardMesaage = false;
                                if (this.notRecievedFrom.contains(messagerOwner)) {
                                    this.updateMatrix(innerData,messagerOwner);
                                    this.updateSet(messagerOwner);
                                    forwardMesaage = true;

                                }
                                int hopCounter = receivedPair.getKey();
                                receivedPair.setKey(hopCounter-1);

                                // Send the data to all of the other sockets

                                int Port = ss.getLocalPort();
                                int sender = this.IdByListeningPorts.get(Port);
                                int senderPort = this.portsById.get(sender)[0];

                                if (hopCounter > 0 && forwardMesaage) {
                                    this.sendMessage(receivedPair, senderPort);
                                }

                                if (this.notRecievedFrom.isEmpty()) {
                                    ss.close();
                                }

                            }catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }



//                        this.updateMatrix(words);





                            //send the data to the other server sockets

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

                } catch (IOException e) {
//                    e.printStackTrace();
                }
                timestamp = Instant.now();
//                System.out.println(timestamp + " | node " + this.id + "|" +" thread of port " +ss.getLocalPort() + " is finished");


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
                ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());
                dout.writeObject(this.Message);
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

    public  void sendMessage(Pair<Integer, HashMap<Integer,HashMap<Integer,Double>>> M, int senderPort) {
        try {
            for (int port : idByBroadcastingPorts.keySet()
            ) {
                if (port != senderPort) {
                    Socket s = new Socket("localhost", port);
                    ObjectOutputStream dout = new ObjectOutputStream(s.getOutputStream());
                    dout.writeObject(M);
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




