package sdt.cn.store.xmpp.auth;

import sdt.cn.store.util.Config;

public class AuthToken {
	private String username;

	private String domain;

	/**
	 * Constucts a new AuthToken with the specified JID.
	 * 
	 * @param jid the username or bare JID
	 */
	public AuthToken(String jid) {
		if (jid == null) {
			 this.domain = Config.getString("xmpp.domain");
			return;
		}
		int index = jid.indexOf("@");
		if (index > -1) {
			this.username = jid.substring(0, index);
			this.domain = jid.substring(index + 1);
		} else {
			this.username = jid;
			this.domain = Config.getString("xmpp.domain");
		}
	}

	/**
	 * Returns the username.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Returns the domain.
	 * 
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}
	
	@Override
	public String toString() {
		return domain+username;
	}
}
