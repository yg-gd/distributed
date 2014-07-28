import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Client extends Node {

	String connectedServ;
	
	
	
	public Client(String id, int port, int base){
		super(id, port, base);
		
		type = "Client";
	}
	@Override
	public void run() {
		
		listener.start();
		
		print("I'm alive");
		Message msg;
		
		while(true){
			//test();
			
			msg = msgQueue.poll();
			while(msg == null){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg = msgQueue.poll();
			}
			if(connectedServ == null){
				print("not connected to a server");
				continue;
			}
			msgSwitch(msg);
			
		}
		
		//AddMessage amsg = new AddMessage(procid, "Dan", "is dumb");
		//sendMsg(connectedServ, amsg);
		//ReadMessage rmsg = new ReadMessage(procid, "Dan");
		
	}
	
	public void msgSwitch(Message msg){
		
		switch(msg.type){
		case ADD: 	
		case DELETE:
		case EDIT:add(msg);
					break;
		
		default:
			print(msg.type + " not supported");
		}
		
		
	}
	
	public void add(Message msg){
		msg.procid = procid;
		sendMsg( connectedServ, msg); 
	}
	
	public void test(){
		Message m = new Message(procid);
		
		m.type = MessageType.DEFAULT;
		
		sendMsg(connectedServ, m);
	}

}
