package sdt.cn.store.xmpp.session;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;

import sdt.cn.store.xmpp.Xmpp;
import sdt.cn.store.xmpp.net.Connection;
import sdt.cn.store.xmpp.net.ConnectionCloseListener;



public class SessionManager {

	private static final Log log = LogFactory.getLog(SessionManager.class);

	private static final String RESOURCE_NAME = "AndroidpnClient";

	private String serverName;

	public static SessionManager getInstance() {
		return SmHolder.instance;
	}

	public SessionManager() {
		super();
		serverName=Xmpp.getInstance().getServerName();
	}

	private Map<String, ClientSession> preAuthSessions = new ConcurrentHashMap<String, ClientSession>();
	private Map<String, ClientSession> clientSessions = new ConcurrentHashMap<String, ClientSession>();

	private ClientSessionListener clientSessionListener = new ClientSessionListener();

	private final AtomicInteger connectionsCounter = new AtomicInteger(0);


	private static final class SmHolder{
		private final static SessionManager instance=new SessionManager();
	}

	public void addSession(ClientSession session) {
		preAuthSessions.remove(session.getStreamID().toString());
		clientSessions.put(session.getAddress().toString(), session);
	}


	public ClientSession createClientSession(Connection conn) {
		if (serverName == null) {
			throw new IllegalStateException("Server not initialized");
		}
		Random random = new Random();
		String streamId = Integer.toHexString(random.nextInt());
		ClientSession session = new ClientSession(serverName, conn, streamId);
		conn.init(session);
		conn.registerCloseListener(clientSessionListener);

		// Add to pre-authenticated sessions
		preAuthSessions.put(session.getAddress().getResource(), session);

		connectionsCounter.incrementAndGet();
		return session;
	}

	public boolean removeSession(ClientSession session) {
		if (session == null || serverName == null) {
			return false;
		}
		JID fullJID = session.getAddress();
		System.out.println("fullJID::"+fullJID.toString());
		log.debug("fullJID::"+fullJID.toString());

		// Remove the session from list
		boolean clientRemoved = clientSessions.remove(fullJID.toString()) != null;
		boolean preAuthRemoved = (preAuthSessions.remove(fullJID.getResource()) != null);

		// Decrement the counter of user sessions
		if (clientRemoved || preAuthRemoved) {
			connectionsCounter.decrementAndGet();
			return true;
		}
		return false;
	}

	public ClientSession getSession(JID from) {
		if (from == null || serverName == null
				|| !serverName.equals(from.getDomain())) {
			return null;
		}
		log.info("from::"+from.toString());
		if (from.getResource() != null) {
			ClientSession session = preAuthSessions.get(from.getResource());
			if (session != null) {
				return session;
			}
		}
		if (from.getResource() == null || from.getNode() == null) {
			return null;
		}
		return clientSessions.get(from.toString());

	}

	public ClientSession getSession(String username) {
		// return getSession(new JID(username, serverName, null, true));
		return getSession(new JID(username, serverName, RESOURCE_NAME, true));
	}

	public Collection<ClientSession> getSessions() {
		return clientSessions.values();
	}


	private class ClientSessionListener implements ConnectionCloseListener {

		public void onConnectionClose(Object handback) {
			try {
				log.debug("session closed:callback");
				ClientSession session = (ClientSession) handback;
				removeSession(session);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
