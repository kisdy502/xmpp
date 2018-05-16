package sdt.cn.store.xmpp.router;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

import sdt.cn.store.xmpp.PacketException;
import sdt.cn.store.xmpp.session.ClientSession;
import sdt.cn.store.xmpp.session.SessionManager;

public class PacketDeliverer {
	
	private static final Log log = LogFactory.getLog(PacketDeliverer.class);

	public static void deliver(Packet packet) throws PacketException {
		if (packet == null) {
			throw new PacketException("Packet was null");
		}
		JID recipient = packet.getTo();
		log.info("PacketDeliverer::"+recipient.toString());
		if (recipient != null) {
			ClientSession clientSession = SessionManager.getInstance()
					.getSession(recipient);
			if (clientSession != null) {
				clientSession.deliver(packet);
			}
		}
	}
}
