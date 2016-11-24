package util.data.dsv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.data.maps.UserList;
import util.sys.DataType;

public class UserConfusionCSV extends ConfusionCSV<String> {

	private final Map<String, List<String>> userMap = new HashMap<String, List<String>>();
	private final Set<String> newKeys = new HashSet<String>();
	public UserConfusionCSV(String[] blargs) {
		super(blargs);
	}
	public UserConfusionCSV(boolean symm) {
		super(symm);
	}
	public UserConfusionCSV() {
		super(true);
	}
	private static final long serialVersionUID = 1423511210223449087L;

	public UserConfusionCSV(ConfusionCSV<String> csv) {
		super(csv);
	}
	@Override
	public DataType deepCopy() {
		return new UserConfusionCSV(this);
	}	
	
	@Override
	public int getNumFixedArgs() {
		return 0;
	}
	public void uploadUserList(UserList ul) {
		for (String key : ul.keySet()) {
			newKeys.add(key);
			if (!userMap.containsKey(key)) {
				userMap.put(key, new ArrayList<String>());
			}
			userMap.get(key).addAll(ul.get(key));
		}
	}
	@Override
	public synchronized void absorb(ConfusionCSV<String> other) {
		if (other instanceof UserConfusionCSV) {
			UserConfusionCSV uo = (UserConfusionCSV)other;
			for (String key : uo.userMap.keySet()) {
				if (this.userMap.containsKey(key)) {
					this.userMap.get(key).addAll(uo.userMap.get(key));
				}
				else {
					this.userMap.put(key, uo.userMap.get(key));
					newKeys.add(key);
				}
			}
		}
		super.absorb(other);
	}
	public void computeConfusion() {
		for (String key : newKeys) {
			computeConfusionHelp(key);
		}
		newKeys.clear();
	}
	private void computeConfusionHelp(String sub) {
		for (String othersubreddit : userMap.keySet()) {
			for (String user : userMap.get(sub)) {
				if (userMap.get(othersubreddit).contains(user)) {
					//append to their confusion
					int updateValue = this.containsKey(sub, othersubreddit) ? this.get(sub, othersubreddit)+1 : 1;
					this.put(sub, othersubreddit, updateValue);
				}
			}
		}
	}
}
