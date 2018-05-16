package sdt.cn.store.xmpp.router;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;

import sdt.cn.store.xmpp.handler.PresenceUpdateHandler;
import sdt.cn.store.xmpp.session.ClientSession;
import sdt.cn.store.xmpp.session.Session;
import sdt.cn.store.xmpp.session.SessionManager;

public class PresenceRouter {
	
	private final Log log = LogFactory.getLog(getClass());

	private PresenceUpdateHandler presenceUpdateHandler;

	public PresenceRouter() {
		presenceUpdateHandler = new PresenceUpdateHandler();
	}

	public void route(Presence packet) {
		if (packet == null) {
			throw new NullPointerException();
		}
		ClientSession session = SessionManager.getInstance().getSession(packet.getFrom());

		if (session == null || session.getStatus() != Session.STATUS_CONNECTED) {
			handle(packet);
		} else {
			packet.setTo(session.getAddress());
			packet.setFrom((JID) null);
			packet.setError(PacketError.Condition.not_authorized);
			session.process(packet);
		}
	}

	private void handle(Presence packet) {
		Presence.Type type = packet.getType();
		if (type == null || Presence.Type.unavailable == type) {
			presenceUpdateHandler.process(packet);
		} else {
			log.warn("Unknown presence type");
		}
	}
}
