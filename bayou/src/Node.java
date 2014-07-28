import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Node extends Thread {

	int port;
	String procid;
	int basePort;
	
	String type;
	
	ConcurrentLinkedQueue<Message> msgQueue;
	
	MessageListener listener;
	
	public Node(String id, int p, int base){
		procid = id;
		port = p;
		basePort = base;
		
		msgQueue = new ConcurrentLinkedQueue<Message>();
		
		listener = new MessageListener(msgQueue, port, procid);
	}
	
	public boolean sendMsg(String id, Message m){
		
		//sending message to network process instead of actual process.
		m.dest = id;
				
		Socket s;
		OutputStream os;
		ObjectOutputStream oos;
		try{
			s = new Socket("localhost",basePort-1);
			
			os = s.getOutputStream();
			oos = new ObjectOutputStream(os);
			
			oos.writeObject(m);
			
			oos.close();
			os.close();
			s.close();
		
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void print(String str){
		System.out.println(type + " "+procid+": " + str);
	/*	try{
			throw(new Exception());
		} catch(Exception e){
			e.printStackTrace();
		} */
		
	}
	
}
