import java.io.*;
import java.net.*;
public class Client {

    public static void main(String[] args) {
        try{
            for (int i = 0; i < 20; i++) {
                Socket s=new Socket("localhost",10000 + i);
                DataOutputStream dout=new DataOutputStream(s.getOutputStream());
                dout.writeUTF("(2,3) weight:5");
                dout.flush();
                dout.close();
                s.close();
            }
//            Socket s=new Socket("localhost",9999);
//            DataOutputStream dout=new DataOutputStream(s.getOutputStream());
//            dout.writeUTF("(2,3) weight:5");
//            dout.flush();
//            dout.close();
//            s.close();

        }catch(Exception e){System.out.println(e);}
    }
}