import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


public class DriverMessageListener extends Thread {
	int port;
	String procid;
	int basePort;
	boolean networkOn;
	
	ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> network;
	ConcurrentHashMap<String, Integer> portLookUp;
	Vector<String> isolated;
	
	public DriverMessageListener(String id, int p, int base, ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> net, ConcurrentHashMap<String, Integer> plu){
		port = p;
		System.out.println("Network: Accepting on port " +p);
		procid = id;
		basePort = base;
		network = net;
		portLookUp = plu;
		networkOn = true;
		
		isolated = new Vector<String>();
	}
	
	public void run(){
		try{
			ServerSocket ss = new ServerSocket(port);
			while(true){
				Socket s = ss.accept();
				InputStream is = s.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				
				Message m = (Message)ois.readObject();
								
				if(	m.procid.equals("bossman")
					||
						(	
						networkOn && 
						network.get(m.procid) != null && 
						network.get(m.procid).get(m.dest) == true && 
						!isolated.contains(m.dest) && 
						!isolated.contains(m.procid)
						)  
					){
					
				//	System.out.println("Network: To " + m.dest + " From " + m.procid + " of type " + m.type);
					sendMsg(m.dest, m);
				}
				
				ois.close();
				is.close();
				s.close();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean sendMsg(String id, Message m){
		
		//sending message to network process instead of actual process.
		
		int port = portLookUp.get(id);
		Socket s;
		OutputStream os;
		ObjectOutputStream oos;
		try{
			s = new Socket("localhost",port);
			
			os = s.getOutputStream();
			oos = new ObjectOutputStream(os);
			
			oos.writeObject(m);
			
			oos.close();
			os.close();
			s.close();
		
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	private void print(String str){
		System.out.println("Network: " + str);
	}
}
