import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MessageListener extends Thread {
	
	ConcurrentLinkedQueue<Message> msgQueue;
	int port;
	String procid;
	
	public MessageListener(ConcurrentLinkedQueue<Message> q, int p, String id){
		msgQueue = q;
		port = p;
		procid = id;
	}
	
	public void run(){
		System.out.println(procid+ "listening on " + port);
		try{
			ServerSocket ss = new ServerSocket(port);
			while(true){
				Socket s = ss.accept();
				InputStream is = s.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				
				Message m = (Message)ois.readObject();
				
				msgQueue.add(m);
				
				ois.close();
				is.close();
				s.close();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
