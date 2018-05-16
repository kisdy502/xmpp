package sdt.cn.store.xmpp.presence;

import sdt.cn.store.bean.User;
import sdt.cn.store.xmpp.session.SessionManager;

public class PresenceManager {

	public static PresenceManager getInstance() {
		return Holder.instance;
	}

	public boolean isAvailable(User user) {
		System.out.println("isAvailable::"+user.toString());
		return SessionManager.getInstance().getSession(user.getUsername()) != null;
	}

	private final static class Holder{
		private final static PresenceManager instance=new PresenceManager();
	}

}
