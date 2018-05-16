package sdt.cn.store.xmpp.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

import gnu.inet.encoding.StringprepException;
import sdt.cn.store.bean.User;
import sdt.cn.store.service.UserExistsException;
import sdt.cn.store.service.UserNotFoundException;
import sdt.cn.store.service.UserService;
import sdt.cn.store.xmpp.ServiceLocator;
import sdt.cn.store.xmpp.UnauthorizedException;
import sdt.cn.store.xmpp.session.ClientSession;
import sdt.cn.store.xmpp.session.Session;
import sdt.cn.store.xmpp.session.SessionManager;

public class IQRegisterHandler extends IQHandler{

	private final Log log = LogFactory.getLog(getClass());

	private static final String NAMESPACE = "jabber:iq:register";

	private UserService userService;

	private Element probeResponse;


	public IQRegisterHandler() {
		userService = ServiceLocator.getUserService();
		probeResponse = DocumentHelper.createElement(QName.get("query",NAMESPACE));
		probeResponse.addElement("username");
		probeResponse.addElement("password");
		probeResponse.addElement("email");
		probeResponse.addElement("name");
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		log.info("IQRegisterHandler::handleIQ");

		IQ reply = null;
		log.info("packet::"+packet.toXML());
		ClientSession session = SessionManager.getInstance().getSession(packet.getFrom());

		if (session == null) {
			reply = IQ.createResultIQ(packet);
			reply.setChildElement(packet.getChildElement().createCopy());
			reply.setError(PacketError.Condition.internal_server_error);
			return reply;
		}

		if (IQ.Type.get.equals(packet.getType())) {
			reply = IQ.createResultIQ(packet);
			if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
				log.info("STATUS_AUTHENTICATED");
			}else {
				reply.setTo((JID) null);
				reply.setChildElement(probeResponse.createCopy());
			}
		}else if (IQ.Type.set.equals(packet.getType())) { 
			Element query = packet.getChildElement();
			if (query.element("remove") != null) {
				if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
					// TODO
				} else {
					throw new UnauthorizedException();
				}
			} else {
				try {
					String username = query.elementText("username");
					String password = query.elementText("password");
					String email = query.elementText("email");
					String name = query.elementText("name");
					log.info("username::"+username);
					log.info("password::"+password);

					// Deny registration of users with no password
					if (password == null || password.trim().length() == 0) {
						reply = IQ.createResultIQ(packet);
						reply.setChildElement(packet.getChildElement().createCopy());
						reply.setError(PacketError.Condition.not_acceptable);
						return reply;
					}

					if (email != null && email.matches("\\s*")) {
						email = null;
					}

					if (name != null && name.matches("\\s*")) {
						name = null;
					}

					int status=session.getStatus();
					log.info("session status:"+status);
					User user;
					if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
						user = userService.getUser(session.getUsername());
					} else {
						user = new User();
					}
					user.setUsername(username);
					user.setPassword(password);
					user.setEmail(email);
					user.setName(name);
					log.info("user::"+user.toString());
					userService.saveUser(user);
					reply = IQ.createResultIQ(packet);
				} catch (Exception ex) {
					log.error(ex);
					reply = IQ.createResultIQ(packet);
					reply.setChildElement(packet.getChildElement().createCopy());
					if (ex instanceof UserExistsException) {
						reply.setError(PacketError.Condition.conflict);
					} else if (ex instanceof UserNotFoundException) {
						reply.setError(PacketError.Condition.bad_request);
					} else if (ex instanceof StringprepException) {
						reply.setError(PacketError.Condition.jid_malformed);
					} else if (ex instanceof IllegalArgumentException) {
						reply.setError(PacketError.Condition.not_acceptable);
					} else {
						reply.setError(PacketError.Condition.internal_server_error);
					}
				}
			}
		}

		return reply;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

}
