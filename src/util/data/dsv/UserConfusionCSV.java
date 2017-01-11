package util.data.dsv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.data.maps.UserList;
import util.sys.DataType;

public class UserConfusionCSV extends ConfusionCSV<String> {

	
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
	public static UserConfusionCSV fromFile(File f) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(f.toPath());
		} catch (IOException e) {
			System.err.println("error reading file: "+f);
			e.printStackTrace();
			System.exit(1);
		}
		if (lines.size() == 0) {
			throw new IllegalArgumentException("Empty file can't be UserConfusionCSV");
		}
		UserConfusionCSV ucc = new UserConfusionCSV();
		String header = lines.get(0);
		String[] subreds = header.split(",");
		for (int i = 1; i < lines.size(); i++) {
			String[] confusion = lines.get(i).split(",");
			String sub1 = confusion[0];
			if (confusion.length != subreds.length) {
				System.err.println(lines.get(i));
				throw new RuntimeException("logical error, confusion length is "+confusion.length+" but subreds length is "+subreds.length);
			}
			for (int j = 1; j < subreds.length; j++) {
				ucc.put(sub1, subreds[j], Integer.parseInt(confusion[j]));
			}
		}
		return ucc;
	}
	
	@Override
	public int getNumFixedArgs() {
		return 0;
	}
	
	//these are just for intermediate computations when uploading a userlist
	private final Map<String, List<String>> userMap = new HashMap<String, List<String>>();
	private final Set<String> newKeys = new HashSet<String>();
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
