import java.io.Serializable;
import java.util.HashMap;


public class AntiEntropyReq extends Message implements Serializable {

	HashMap<String,Integer> versionVector;
	int lastCSN;
	boolean first;
	
	public AntiEntropyReq(String id, HashMap<String,Integer> vec, int csn, boolean f) {
		super(id);
		
		type = MessageType.ANTIENTROPYREQ;
		versionVector = vec;
		lastCSN = csn;
		
		first = f;
		// TODO Auto-generated constructor stub
	}

}
