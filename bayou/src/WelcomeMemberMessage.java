import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class WelcomeMemberMessage extends Message implements Serializable {

	int localVersion;
	int lastCSN;
	
	ArrayList<String> otherServs;
	
	HashMap<String, String> database;
	ArrayList<LogEntry> cLog;
	ArrayList<LogEntry> uLog;
	HashMap<String,Integer> versionVector;
	HashMap<String,Integer> commitVector;
	
	public WelcomeMemberMessage(String id, 
									int localVersion,
									int CSN,
									ArrayList<String> otherServs,
									HashMap<String,String> database, 
									ArrayList<LogEntry> cLog,
									ArrayList<LogEntry> uLog,
									HashMap<String,Integer> versionVector,
									HashMap<String,Integer> commitVector) {
		super(id);
		
		this.type = MessageType.WELCOMEMEMBER;
		
		this.localVersion = localVersion;
		this.lastCSN = CSN;
		this.otherServs = otherServs;
		this.database = database;
		this.cLog = cLog;
		this.uLog = uLog;
		this.versionVector = versionVector;
		this.commitVector = commitVector;
	}

}
