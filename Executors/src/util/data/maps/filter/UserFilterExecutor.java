package util.data.maps.filter;

import util.data.maps.UserList;
import util.sys.Executor;

public class UserFilterExecutor extends Executor<UserListFilterProcessor, UserList, UserList> {
	public UserFilterExecutor() {
		super("ulfilter", 24, UserListFilterProcessor.class, UserList.class, UserList.class);
	}
	public static void main(String[] args) {
		UserFilterExecutor e = new UserFilterExecutor();
		e.initializeFromCmdLine(args);
		e.run();
	}
}
