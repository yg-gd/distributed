import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Server extends Node {

	boolean primary;
	boolean retiring;
	boolean dead;
	
	Random rand;
	
	int localVersion;
	int lastCSN;
	
	ArrayList<String> otherServs;
	
	HashMap<String, String> database;
	ArrayList<LogEntry> cLog;
	ArrayList<LogEntry> uLog;
	HashMap<String,Integer> versionVector;
	HashMap<String,Integer> commitVector;
	
	public Server(String id, int port, int base, boolean b){
		
		super(id, port, base);
		
		primary = b;
		lastCSN = 0;
		
		type = "Server";
		
		localVersion = 0;
		retiring = false;
		dead = false;
		
		otherServs = new ArrayList<String>();
		
		
		database = new HashMap<String, String>();
		cLog = new ArrayList<LogEntry>();
		uLog = new ArrayList<LogEntry>();
		versionVector = new HashMap<String,Integer>();
		commitVector = new HashMap<String,Integer>();
		
		rand = new Random();
	}
	
	
	public void run() {
		
		listener.start();
		
		LogEntry e = new LogEntry("Dan", "is dumb",procid,++localVersion);
		if(primary){
			e.csn = ++lastCSN;
			cLog.add(e);
			database.put(e.song, e.url);
		} else {
			uLog.add(e);
		}
		if(versionVector.get(e.originServ) == null){
			versionVector.put(e.originServ, e.timestamp);
		}
		versionVector.put(e.originServ,max(versionVector.get(e.originServ), e.timestamp));
		
		otherServs.add(procid);
		
		msgSwitch();

	}
	
	public void msgSwitch(){
		
		while(!dead){
			
			Message msg;
			
			msg = msgQueue.poll();
			while(msg == null){
				try{
					Thread.sleep(2000);
				} catch (Exception e){
					System.err.print(e.getStackTrace());
				}
				if(otherServs.size() > 1){
					int random;
					do{
						random = rand.nextInt(otherServs.size());
					} while(procid.equals(otherServs.get(random)));
					requestAntiEntropy(otherServs.get(random), true);
				}
				msg = msgQueue.poll();
				
			}
			
			
		//	print("Got a message from " + msg.procid);
			
			if(!msg.procid.equals("bossman") && 
					!otherServs.contains(msg.procid) &&
					msg.type != MessageType.ADD){
				otherServs.add(msg.procid);
				versionVector.put(msg.procid, -1);
				commitVector.put(msg.procid, -1);
			}
			
			switch (msg.type) {
			case READ:
				processReadMsg(msg);
				break;
			case ADD:
				processAddMsg(msg);
				break;
			case ANTIENTROPYREQ:
				processAntiEntropyReq(msg);
				break;
			case ANTIENTROPYRES:
				processAntiEntropyRes(msg);
				break;
			case PRINTLOG:
				printLog();
				break;
			case EDITMEMBERSHIP:
				editMembership(msg);
				break;
			case WELCOMEMEMBER:
				acceptWelcome(msg);
				break;
			case IAMLEAVING:
				retire();
				break;
			case DEATHACK:
				leave();
				break;
			case DEFAULT:
				break;
			default:
				break;
			}
		}
		
		/*if(otherServs.size() > 1){
			int random;
			do{
				random = rand.nextInt(otherServs.size());
			} while(procid.equals(otherServs.get(random)));
			requestAntiEntropy(otherServs.get(random), true);
		} */
	}
	
	public void leave(){
		if(retiring){
			print("Killing myself!!");
			dead = true;
		}
	}
	
	public void requestAntiEntropy(String other, boolean first){
				
		AntiEntropyReq req = new AntiEntropyReq(procid, versionVector, lastCSN, first);
		
		sendMsg(other, req);
	}
	
	public void retire(){
		
		//if primary blah
		
		
		MemberLogEntry e = new MemberLogEntry(procid,false,procid, ++localVersion);
		versionVector.put(procid, localVersion);
		
		if(primary){
			e.csn = ++lastCSN;
			cLog.add(e);
			database.put(e.song, e.url);
		} else {
			uLog.add(e);
		}
		if(versionVector.get(e.originServ) == null){
			versionVector.put(e.originServ, e.timestamp);
		}
		versionVector.put(e.originServ,max(versionVector.get(e.originServ), e.timestamp));
		
		retiring = true;
	}
	
	public void acceptWelcome(Message m){
		
		if(retiring){
			return;
		}
		
		WelcomeMemberMessage msg = (WelcomeMemberMessage)m;
		
		localVersion = msg.localVersion;
		lastCSN = msg.lastCSN;
		otherServs = msg.otherServs;
		database = msg.database;
		cLog = msg.cLog;
		uLog = msg.uLog;
		versionVector = msg.versionVector;
		commitVector = msg.commitVector;
		
	}
	
	public void editMembership(Message msg){
		EditMembershipMessage emsg = (EditMembershipMessage)msg;
		if(emsg.joining){
			welcomeMember(emsg);
		}
	}
	
	public void welcomeMember(EditMembershipMessage msg){
		
		MemberLogEntry e = new MemberLogEntry(msg.newproc, true, procid, ++localVersion);
	
		versionVector.put(procid, localVersion);
		versionVector.put(msg.newproc, localVersion);
		
		if(primary){
			e.csn = ++lastCSN;
			cLog.add(e);
			otherServs.add(((MemberLogEntry) e).member);
		} else {
			uLog.add(e);
		}
		if(versionVector.get(e.originServ) == null){
			versionVector.put(e.originServ, e.timestamp);
		}
		versionVector.put(e.originServ,max(versionVector.get(e.originServ), e.timestamp));
		
		WelcomeMemberMessage m = new WelcomeMemberMessage(procid,
															lastCSN,
															localVersion,
															otherServs,
															database,
															cLog,
															uLog,
															versionVector,
															commitVector);
		
		sendMsg(msg.newproc, m);
		
	}
	
	public void printLog(){
		
		for(LogEntry e : cLog){
			print(e.toString());
		}
		for(LogEntry e : uLog){
			print(e.toString());
		}
	}
	
	public void processAntiEntropyReq(Message msg){
		
		AntiEntropyReq req = (AntiEntropyReq)msg;
		
		HashMap<String,Integer> otherVector = req.versionVector;
		
		ArrayList<LogEntry> diffcLog = new ArrayList<LogEntry>();
		ArrayList<LogEntry> diffuLog = new ArrayList<LogEntry>();
		if(req.lastCSN < lastCSN){
			for(int i = req.lastCSN; i < cLog.size(); i++){
				diffcLog.add(cLog.get(i));
			}
		}
		
		for(LogEntry e : uLog){
			if(otherVector.get(e.originServ) == null || otherVector.get(e.originServ) < e.timestamp){
				diffuLog.add(e);
			}
		}
		
		AntiEntropyRes res = new AntiEntropyRes(procid, diffcLog, diffuLog, retiring, primary);
		
		sendMsg(req.procid, res);
		
		if(req.first){
			requestAntiEntropy(req.procid, false);
		}
	}
	
	public void processAntiEntropyRes(Message msg){
		
		if(retiring){
			return;
		}
		
		AntiEntropyRes res = (AntiEntropyRes)msg;
		
		ArrayList<LogEntry> diffcLog = res.diffcLog;
		ArrayList<LogEntry> diffuLog = res.diffuLog;
		
		for(LogEntry e : diffcLog){
			print("We got csn " + e.csn );
			if(e.csn > lastCSN){
				assert(e.csn == lastCSN+1);
				uLog.remove(e);
				cLog.add(e);
				lastCSN = e.csn;
				print("New CSN "+ lastCSN);
				if(versionVector.get(e.originServ) == null){
					versionVector.put(e.originServ, e.timestamp);
				}
				versionVector.put(e.originServ,max(versionVector.get(e.originServ), e.timestamp));
				if(e instanceof MemberLogEntry){
					if(((MemberLogEntry) e).joining){
						otherServs.add(((MemberLogEntry) e).member);
					} else {
						otherServs.remove(((MemberLogEntry) e).member);
					}
				} else {
					database.put(e.song, e.url);
				}
			}
		}
		if(primary){
			for(LogEntry e : diffuLog){
				if(!cLog.contains(e)){
					e.csn = ++lastCSN;
					print("New CSN "+ lastCSN);
					cLog.add(e);
					if(versionVector.get(e.originServ) == null){
						versionVector.put(e.originServ, e.timestamp);
					}
					versionVector.put(e.originServ,max(versionVector.get(e.originServ), e.timestamp));
					if(e instanceof MemberLogEntry){
						if(((MemberLogEntry) e).joining){
							otherServs.add(((MemberLogEntry) e).member);
						} else {
							otherServs.remove(((MemberLogEntry) e).member);
						}
					} else {
						database.put(e.song, e.url);
					}
				}
			}
		
		} else {
			for(LogEntry e : diffuLog){
				if(!uLog.contains(e)){
					uLog.add(e);
					if(versionVector.get(e.originServ) == null){
						versionVector.put(e.originServ, e.timestamp);
					}
					versionVector.put(e.originServ,max(versionVector.get(e.originServ), e.timestamp));
				}
			}	
		}
		if(res.goodbye){
			primary = res.primary;
			Message m = new Message(procid);
			m.type = MessageType.DEATHACK;
			sendMsg(res.procid, m);
		}
	}
	
	public void processReadMsg(Message msg){
		
		if(retiring){
			AckMessage nack = new AckMessage(procid, false);
			sendMsg(msg.procid, nack);
			return;
		}
		
		//adsfgadgagagafgadfgsfdgsfgbaf
		
		ReadMessage rmsg = (ReadMessage)msg;
		
		String url = database.get(rmsg.song);
		
		if(url == null){
			System.out.println("Error: Could not find entry for " + rmsg.song);
		} else {
			System.out.println("The entry for " + rmsg.song + " is " + url);
		}
		
	}
	
	public void processAddMsg(Message msg){
		
		if(retiring){
			AckMessage nack = new AckMessage(procid, false);
			sendMsg(msg.procid, nack);
			return;
		}
		
		AddMessage amsg = (AddMessage)msg;
		
		LogEntry e = new LogEntry(amsg.song, amsg.url, procid, ++localVersion);
		
		if(primary){
			e.csn = ++lastCSN;
			cLog.add(e);
			database.put(amsg.song, amsg.url);
		} else {
			uLog.add(e);
		}
		if(versionVector.get(e.originServ) == null){
			versionVector.put(e.originServ, e.timestamp);
		}
		versionVector.put(e.originServ,max(versionVector.get(e.originServ), e.timestamp));
		
		
	}
	
	public int max(int i, int j){
		return i < j ? j : i;
	}

}
