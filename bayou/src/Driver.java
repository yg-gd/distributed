import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;


public class Driver {

	static String procid = "bossman";
	
	static String primary = "primary";
	
	static int basePort = 5755;
	
	static int freePort = basePort;
	
	static int numServ = 3;
	static int numClient = 2;
	
	static ArrayList<Server> servs = new ArrayList<Server>();
	static ArrayList<Client> clients = new ArrayList<Client>();
	static ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> network = new ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>>();
	static ConcurrentHashMap<String, Integer> portLookUp = new ConcurrentHashMap<String, Integer>();
	static DriverMessageListener listener;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		listener = new DriverMessageListener(procid, basePort-1, basePort, network,portLookUp);
		

		listener.start();
		/*
		
		
		
		for(int i = 0; i < numServ; ++i){
			Server serv = new Server(i,basePort + i, basePort, network);
			servs.add(serv);
			serv.start();
		}
		
		for(int i = 0; i < numClient; ++i){
			Client client = new Client(numServ + i, basePort + numServ + i, basePort);
			clients.add(client);
			client.start();
		} */
		
		Server primaryServ = new Server(primary, freePort++, basePort, true);
		
		servs.add(primaryServ);
		portLookUp.put(primary, basePort);
		ConcurrentHashMap<String, Boolean> buddys = new ConcurrentHashMap<String, Boolean>();
		buddys.put(primary, true);
		network.put(primary, buddys);
		primaryServ.start();
		
		
		printDirections();
		
		Scanner sc = new Scanner(System.in);
		
		while(true){
			
			String command = sc.nextLine();
			
			String[] comArgs = command.split(" ");
			
			driverSwitch(comArgs);
			
		}
		

	}
	
	public static void printDirections(){
		print("Welcome to Bayou");
		print("Supported commands are:");
		print("startClient i j");
		print("clientDisconnect i");
		print("clientReconnect i j");
		print("pause");
		print("continue");
		print("printLog i");
		print("printLog");
		print("isolate i");
		print("reconnect i");
		print("breakConnection i j");
		print("recoverConnection i j");
		print("join i");
		print("leave i");
		print("Client i add song URL");
		print("Client i delete song");
		print("Client i edit song URL");
		System.out.println();
	}
	
	public static void driverSwitch(String[] args){
		
		switch (args[0]) {

		case "startClient":
			startClient(args[1], args[2]);
			break;
		case "clientDisconnect":
			disconnectClient(args[1]);
			break;
		case "clientReconnect":
			reconnectClient(args[1], args[2]);
			break;
		case "pause":
			pause();
			break;
		case "continue":
		case "resume":
			resume();
			break;
		case "printLog":
			printLog(args);
			break;
		case "isolate":
			changeState(args[1], false);
			break;
		case "reconnect":
			changeState(args[1], true);
			break;
		case "breakConnection":
			editConnect(args[1], args[2], false);
			break;
		case "recoverConnection":
			editConnect(args[1], args[2], true);
			break;
		case "join":
			join(args[1]);
			break;
		case "leave":
			leave(args[1]);
			break;
		case "Client":
			client(args);
			break;
		default:
			print("that isn't a command");
		}
	}
	
	public static void client(String[] args){
		if(findNode(args[1],clients) == null){ 
			print("that client doesn't exist");
			return;
		}
		Message msg; 
		switch(args[2]){
		case "add":	
			msg = new AddMessage(procid,args[3],args[4]);
			break;
		case "delete":
			msg = new AddMessage(procid,args[3],"");
			break;
		case "edit":
			msg = new AddMessage(procid,args[3],args[4]);
			break;
		default:
			print("that isn't a command");
			return;
		}
		sendMsg(args[1],msg);
	}
	
	public static void leave(String i){

		if(i.equals(procid)){
			print("You're not the bossman!");
			return;
		}
		if(findNode(i,servs) == null){ 
			print("that server doesn't exist");
			return;
		}
		
		Message m = new Message(procid);
		m.type = MessageType.IAMLEAVING;
		sendMsg(i,m);
		
	}
	
	public static void join(String i){
		
		if(i.equals(procid)){
			print("You're not the bossman!");
			return;
		}
		
		if(findNode(i,servs) != null){ 
			print("that server already exist");
			return;
		}
		
		String member = null;
		for(Server s : servs){
			if(!listener.isolated.contains(s.procid)){
				member = s.procid;
				break;
			}
		}
		
		if(member == null){
			print("There is no non-isolated member of the system to welcome " + i);
			return;
		}
		
		
		Server serv = new Server(i,freePort++,basePort, false);
		servs.add(serv);
		portLookUp.put(i, freePort-1);
		ConcurrentHashMap<String, Boolean> buddys = new ConcurrentHashMap<String, Boolean>();
		
		for(Server s : servs){
			buddys.put(s.procid, true);
		}
		for(Client c : clients){
			buddys.put(c.procid, true);
		}
		for( Object o : network.values()){
			ConcurrentHashMap<String, Boolean> pam = (ConcurrentHashMap<String, Boolean>)o;
			pam.put(i, true);
		}
		network.put(i, buddys);
		
		serv.start();
		
		EditMembershipMessage m = new EditMembershipMessage(procid, i, true);
		
		sendMsg(member, m);
	}
	
	
	public static void editConnect(String i, String j, boolean b){
		
		if(network.get(i) == null){
			print(i +" not in the system");
			return;
		}
		if(network.get(j) == null){
			print(j +" not in the system");
			return;
		}
		
		network.get(i).put(j, b);
		network.get(j).put(i, b);
	}
	
	public static void changeState(String i, Boolean b){
		
		ConcurrentHashMap<String, Boolean> map = network.get(i);
		
		if(map == null){
			print(i + "is not is system");
			return;
		}
		
		for(Object o : map.keySet()){
			String k = (String)o;
			map.put(k, b);
		}
		
		for( Object o : network.values()){
			ConcurrentHashMap<String, Boolean> pam = (ConcurrentHashMap<String, Boolean>)o;
			pam.put(i, b);
		}
		
		if(b){
			listener.isolated.remove(i);
		} else {
			listener.isolated.add(i);
		}
	}
	
	public static void printLog(String[] i){
		if(i.length == 1){
			for(Server s: servs){
				printLog(s.procid);
			}
		} else{
			printLog(i[1]);
		}
	}
	
	public static void printLog(String i){
		PrintLogMessage m = new PrintLogMessage(procid);
		m.type = MessageType.PRINTLOG;
		
		sendMsg(i, m);
	}
	
	public static void resume(){
		listener.networkOn = true;
	}
	
	public static void pause(){
		listener.networkOn = false;
	}
	
	public static void reconnectClient(String i, String j){
		
		Client c = (Client)findNode(i, clients);
		
		String serv = c.connectedServ;
		
		
		if(c == null){
			print( i + " does not exist");
			return;
		}
		if(findNode(j, servs) == null){
			print(j + " does not exist");
			return;
		}
		
		if(serv != null && network.get(i).get(serv) && network.get(serv).get(i)){
			print(i + " and " + j + " are already connected");
			return;
		}
		
		c.connectedServ = j;
		
		editConnect(i,j,true);
		
		return;
	}
	
	public static void disconnectClient(String i){
		
		Client c = (Client)findNode(i, clients);
		String serv;
		
		
		if(c == null){
			print("Client does not exist");
			return;
		}
		
		if(c.connectedServ == null){
			print("Client was not connected to any server");
			return;
		}
		
		serv = c.connectedServ;
		//c.connectedServ = null;
		
		editConnect(i, serv, false);
		
		return;
	}
	
	public static void startClient(String i, String j){
	
		if(i.equals(procid)){
			print("You're not the bossman!");
			return;
		}
		
		if(findNode(j,servs) == null){ 
			print("that server doesn't exist");
			return;
		}
		
		if(findNode(i,clients) != null){ // strings are not clients
			print("that client is already up");
			return;
		}
		
		int port = freePort++;
		
		Client client = new Client(i, port, basePort);
		
		portLookUp.put(i, port);
		
		clients.add(client);
		
		ConcurrentHashMap<String, Boolean> buddys = new ConcurrentHashMap<String, Boolean>();
		
		for(Server s : servs){
			buddys.put(s.procid, true);
		}
		for(Client c : clients){
			buddys.put(c.procid, true);
		}
		for( Object o : network.values()){
			ConcurrentHashMap<String, Boolean> pam = (ConcurrentHashMap<String, Boolean>)o;
			pam.put(i, true);
		}
		
		network.put(i, buddys);
		
		client.connectedServ = j;
		
		client.start();
		
	}
	
	public static Node findNode(String j, ArrayList<? extends Node> list){
		for(Node n: list )
		{
			if(n.procid.equals(j))
				return n;
		}
		return null;
	}

	
	public static void print(String str){
		System.out.println("Driver: "+str);
	}
	
	public static void sendMsg(String id, Message m){
		
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
		}
	}

}
