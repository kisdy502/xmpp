package sdt.cn.store.xmpp.session;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;

import sdt.cn.store.service.UserNotFoundException;
import sdt.cn.store.xmpp.auth.AuthToken;
import sdt.cn.store.xmpp.net.Connection;

public class ClientSession extends Session {

	private static final String ETHERX_NAMESPACE = "http://etherx.jabber.org/streams";

	private Presence presence;

	private AuthToken authToken;

	private boolean initialized;

	private boolean wasAvailable = false;

	public ClientSession(String serverName, Connection connection,
			String streamID) {
		super(serverName, connection, streamID);
		presence = new Presence(Presence.Type.unavailable);
	}

	/**
	 * Returns the authentication token associated with this session.
	 * 
	 * @return the authentication token
	 */
	public AuthToken getAuthToken() {
		return authToken;
	}

	public void setAuthToken(AuthToken authToken, String resource) {
		setAddress(new JID(authToken.getUsername(), getServerName(), resource));
		this.authToken = authToken;
		setStatus(Session.STATUS_AUTHENTICATED);
		// Add session to the session manager
		SessionManager.getInstance().addSession(this);
	}


	public static ClientSession createSession(String serverName,
			Connection connection, XmlPullParser xpp) throws XmlPullParserException {
		if (!xpp.getName().equals("stream")) {
			throw new XmlPullParserException("Bad opening tag (not stream)");
		}
		if (!xpp.getNamespace(xpp.getPrefix()).equals(ETHERX_NAMESPACE)) {
			throw new XmlPullParserException("Stream not in correct namespace");
		}
		String language = "en";
		for (int i = 0; i < xpp.getAttributeCount(); i++) {
			if ("lang".equals(xpp.getAttributeName(i))) {
				language = xpp.getAttributeValue(i);
			}
		}

		connection.setLanguage(language);
		connection.setXMPPVersion(MAJOR_VERSION, MINOR_VERSION);

		ClientSession session = SessionManager.getInstance()
				.createClientSession(connection);

		StringBuilder sb = new StringBuilder(200);
		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<stream:stream ");
		sb
		.append("xmlns:stream=\"http://etherx.jabber.org/streams\" xmlns=\"jabber:client\" from=\"");
		sb.append(serverName);
		sb.append("\" id=\"");
		sb.append(session.getStreamID());
		sb.append("\" xml:lang=\"");
		sb.append(language);
		sb.append("\" version=\"");
		sb.append(MAJOR_VERSION).append(".").append(MINOR_VERSION);
		sb.append("\">");
		connection.deliverRawText(sb.toString());

		sb = new StringBuilder();
		sb.append("<stream:features>");
		if (connection.getTlsPolicy() != Connection.TLSPolicy.disabled) {
			sb.append("<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\">");
			if (connection.getTlsPolicy() == Connection.TLSPolicy.required) {
				sb.append("<required/>");
			}
			sb.append("</starttls>");
		}

		String specificFeatures = session.getAvailableStreamFeatures();
		if (specificFeatures != null) {
			sb.append(specificFeatures);
		}
		sb.append("</stream:features>");

		connection.deliverRawText(sb.toString());
		return session;

	}

	public String getAvailableStreamFeatures() {
		StringBuilder sb = new StringBuilder();
		if (getAuthToken() == null) {
			// Supports Non-SASL Authentication            
			sb.append("<auth xmlns=\"http://jabber.org/features/iq-auth\"/>");
			// Supports In-Band Registration
			sb.append("<register xmlns=\"http://jabber.org/features/iq-register\"/>");
		}else {
			sb.append("<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"/>");
			sb.append("<session xmlns=\"urn:ietf:params:xml:ns:xmpp-session\"/>");
		}
		return sb.toString();
	}

	public String getUsername() throws UserNotFoundException {
		if (authToken == null) {
			throw new UserNotFoundException();
		}
		return getAddress().getNode();
	}

	public void setPresence(Presence presence) {
		Presence oldPresence = this.presence;
		this.presence = presence;
		if (oldPresence.isAvailable() && !this.presence.isAvailable()) {
			setInitialized(false);  //离线
		}else  if (!oldPresence.isAvailable() && this.presence.isAvailable()) {
			wasAvailable = true;//在线了
		}
	}

	public void setInitialized(boolean b) {
		this.initialized=b;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public Presence getPresence() {
		return presence;
	}

}
