package sdt.cn.store.xmpp.handler;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

import gnu.inet.encoding.Stringprep;
import gnu.inet.encoding.StringprepException;
import sdt.cn.store.xmpp.UnauthenticatedException;
import sdt.cn.store.xmpp.UnauthorizedException;
import sdt.cn.store.xmpp.auth.AuthManager;
import sdt.cn.store.xmpp.auth.AuthToken;
import sdt.cn.store.xmpp.session.ClientSession;
import sdt.cn.store.xmpp.session.Session;
import sdt.cn.store.xmpp.session.SessionManager;

public class IQAuthHandler extends IQHandler {
	private static final String NAMESPACE = "jabber:iq:auth";

	private Element probeResponse;

	public IQAuthHandler() {
		probeResponse = DocumentHelper.createElement(QName.get("query",
				NAMESPACE));
		probeResponse.addElement("username");
		if (AuthManager.isPlainSupported()) {
			probeResponse.addElement("password");
		}
		if (AuthManager.isDigestSupported()) {
			probeResponse.addElement("digest");
		}
		probeResponse.addElement("resource");
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		log.info("IQAuthHandler.handleIQ");
		IQ reply = null;
		ClientSession session = SessionManager.getInstance().getSession(packet.getFrom());
		if (session == null) {
			log.error("Session not found for key " + packet.getFrom());
			reply = IQ.createResultIQ(packet);
			reply.setChildElement(packet.getChildElement().createCopy());
			reply.setError(PacketError.Condition.internal_server_error);
			return reply;
		}
		
		try {
			System.out.println("packet::"+packet.toXML());
			System.out.println("packet::"+packet.getType());
			Element iq = packet.getElement();
			Element query = iq.element("query");
			Element queryResponse = probeResponse.createCopy();
			if (IQ.Type.get == packet.getType()) {
				// get query
				String username = query.elementText("username");
				if (username != null) {
					queryResponse.element("username").setText(username);
				}
				reply = IQ.createResultIQ(packet);
				reply.setChildElement(queryResponse);
				if (session.getStatus() != Session.STATUS_AUTHENTICATED) {
					reply.setTo((JID) null);
				}
			}else {
				// set query
				String resource = query.elementText("resource");
				String username = query.elementText("username");
				String password = query.elementText("password");
				String digest = null;
				if (query.element("digest") != null) {
					digest = query.elementText("digest").toLowerCase();
				}
				// Verify the resource
				if (resource != null) {
					try {
						resource = JID.resourceprep(resource);
					} catch (StringprepException e) {
						throw new UnauthorizedException("Invalid resource: "+ resource, e);
					}
				} else {
					throw new IllegalArgumentException("Invalid resource (empty or null).");
				}
				// Verify the username
				if (username == null || username.trim().length() == 0) {
					throw new UnauthorizedException(
							"Invalid username (empty or null).");
				}
				try {
					Stringprep.nodeprep(username);
				} catch (StringprepException e) {
					throw new UnauthorizedException("Invalid username: "
							+ username, e);
				}
				username = username.toLowerCase();
				// Verify that username and password are correct
				AuthToken token = null;
				if (password != null && AuthManager.isPlainSupported()) {
					token = AuthManager.authenticate(username, password);
				} else if (digest != null && AuthManager.isDigestSupported()) {
					token = AuthManager.authenticate(username, session.getStreamID().toString(), digest);
				}

				if (token == null) {
					throw new UnauthenticatedException();
				}

				// Set the session authenticated successfully
				session.setAuthToken(token, resource);
				packet.setFrom(session.getAddress());
				reply = IQ.createResultIQ(packet);
			}
		}catch (Exception ex) {
			log.error(ex);
			reply = IQ.createResultIQ(packet);
			reply.setChildElement(packet.getChildElement().createCopy());
			if (ex instanceof IllegalArgumentException) {
				reply.setError(PacketError.Condition.not_acceptable);
			} else if (ex instanceof UnauthorizedException) {
				reply.setError(PacketError.Condition.not_authorized);
			} else if (ex instanceof UnauthenticatedException) {
				reply.setError(PacketError.Condition.not_authorized);
			} else {
				reply.setError(PacketError.Condition.internal_server_error);
			}
		}
		if(reply!=null) {
			session.deliver(reply);
		}
		return null;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

}
