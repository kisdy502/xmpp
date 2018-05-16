package sdt.cn.store.xmpp.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

import sdt.cn.store.xmpp.handler.IQAuthHandler;
import sdt.cn.store.xmpp.handler.IQHandler;
import sdt.cn.store.xmpp.handler.IQRegisterHandler;
import sdt.cn.store.xmpp.session.ClientSession;
import sdt.cn.store.xmpp.session.Session;
import sdt.cn.store.xmpp.session.SessionManager;

public class IQRouter {

	private final Log log = LogFactory.getLog(getClass());

	private List<IQHandler> iqHandlers = new ArrayList<IQHandler>();

	private Map<String, IQHandler> namespace2Handlers = new ConcurrentHashMap<String, IQHandler>();

	public IQRouter() {
		iqHandlers.add(new IQRegisterHandler());
		iqHandlers.add(new IQAuthHandler());
	}


	private void sendErrorPacket(IQ originalPacket,
			PacketError.Condition condition) {
		if (IQ.Type.error == originalPacket.getType()) {
			log.error("Cannot reply an IQ error to another IQ error: "
					+ originalPacket);
			return;
		}
		IQ reply = IQ.createResultIQ(originalPacket);
		reply.setChildElement(originalPacket.getChildElement().createCopy());
		reply.setError(condition);
		try {
			PacketDeliverer.deliver(reply);
		} catch (Exception e) {
			// Ignore
		}
	}

	/**
	 * Routes the Message packet.
	 * 
	 * @param packet the packet to route
	 */
	public void route(IQ packet) {
		if (packet == null) {
			throw new NullPointerException();
		}
		JID sender = packet.getFrom();
		ClientSession session = SessionManager.getInstance().getSession(sender);
		if (session == null
				|| session.getStatus() == Session.STATUS_AUTHENTICATED
				|| ("jabber:iq:auth".equals(packet.getChildElement()
						.getNamespaceURI())
						|| "jabber:iq:register".equals(packet.getChildElement()
								.getNamespaceURI()) || "urn:ietf:params:xml:ns:xmpp-bind"
						.equals(packet.getChildElement().getNamespaceURI()))) {
			handle(packet);
		} else {
			IQ reply = IQ.createResultIQ(packet);
			reply.setChildElement(packet.getChildElement().createCopy());
			reply.setError(PacketError.Condition.not_authorized);
			session.process(reply);
		}
	}


	private void handle(IQ packet) {
		Element childElement = packet.getChildElement();
		String namespace = null;
		if (childElement != null) {
			namespace = childElement.getNamespaceURI();
		}
		if (namespace == null) {
			if (packet.getType() != IQ.Type.result
					&& packet.getType() != IQ.Type.error) {
				log.warn("Unknown packet " + packet);
			}
		} else {
			IQHandler handler = getHandler(namespace);
			if (handler == null) {
				sendErrorPacket(packet,	PacketError.Condition.service_unavailable);
			} else {
				handler.process(packet);
			}
		}
	}

	public void addHandler(IQHandler handler) {
		if (iqHandlers.contains(handler)) {
			throw new IllegalArgumentException(
					"IQHandler already provided by the server");
		}
		namespace2Handlers.put(handler.getNamespace(), handler);
	}

	public void removeHandler(IQHandler handler) {
		if (iqHandlers.contains(handler)) {
			throw new IllegalArgumentException(
					"Cannot remove an IQHandler provided by the server");
		}
		namespace2Handlers.remove(handler.getNamespace());
	}

	private IQHandler getHandler(String namespace) {
		IQHandler handler = namespace2Handlers.get(namespace);
		if (handler == null) {
			for (IQHandler handlerCandidate : iqHandlers) {
				if (namespace.equalsIgnoreCase(handlerCandidate.getNamespace())) {
					handler = handlerCandidate;
					namespace2Handlers.put(namespace, handler);
					break;
				}
			}
		}
		return handler;
	}


}
