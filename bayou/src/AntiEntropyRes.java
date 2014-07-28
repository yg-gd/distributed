import java.io.Serializable;
import java.util.ArrayList;


public class AntiEntropyRes extends Message implements Serializable {

	ArrayList<LogEntry> diffcLog;
	ArrayList<LogEntry> diffuLog;
	boolean goodbye;
	boolean primary;
	
	public AntiEntropyRes(String id, ArrayList<LogEntry> clog, ArrayList<LogEntry> ulog, boolean b, boolean p) {
		super(id);
		
		type = MessageType.ANTIENTROPYRES;
		diffcLog = clog;
		diffuLog = ulog;
		goodbye = b;
		primary = p;
		// TODO Auto-generated constructor stub
	}

}
