package util.listdata.filter;

import java.io.File;

import filter.Filter;
import util.listdata.UserList;
import util.sys.FileProcessor;


public class UserListFilterProcessor extends FileProcessor<UserList, UserList> {
	private final Filter filter;
	public UserListFilterProcessor() {
		super();
		this.filter = null;
	}
	public UserListFilterProcessor(String inp, String out, String[] filterArgs) {
		super(inp, out, new UserList());
		filter = Filter.getFilter(filterArgs);
	}
	@Override
	public int getNumFixedArgs() {
		return 0;
	}
	@Override
	public boolean hasNArgs() {
		return true;
	}
	@Override
	public String getConstructionErrorMsg() {
		return "Please specify one or more fully qualified filters or nicknames: "+Filter.getKnownFilters();
	}

	@Override
	public UserList getNextData() {
		File f = super.getNextFile();
		if ( f == null) {
			return null;
		}
		return UserList.createFromFile(f);
	}

	@Override
	public void reduce(UserList data) {
		synchronized (processAggregate) {
			for (String key : data.keySet()) {
				processAggregate.put(key, data.get(key));
			}
		}
	}

	@Override
	public void map(UserList dataIn, UserList workerAgg) {
		filter.filter(dataIn);
		for (String key : dataIn.keySet()) {
			workerAgg.put(key, dataIn.get(key));
		}
	}
	

}
