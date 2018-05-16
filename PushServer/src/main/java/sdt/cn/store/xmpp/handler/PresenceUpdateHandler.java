package sdt.cn.store.xmpp.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;

import sdt.cn.store.service.NotificationService;
import sdt.cn.store.xmpp.ServiceLocator;
import sdt.cn.store.xmpp.router.PacketDeliverer;
import sdt.cn.store.xmpp.session.ClientSession;
import sdt.cn.store.xmpp.session.Session;
import sdt.cn.store.xmpp.session.SessionManager;

public class PresenceUpdateHandler {

	protected final Log log = LogFactory.getLog(getClass());

	protected NotificationService notificationService;
	
	public PresenceUpdateHandler() {
		notificationService=ServiceLocator.getNotificationService();
	}

	public void process(Packet packet) {
		System.out.println("PresenceUpdateHandler.process");
		ClientSession session = SessionManager.getInstance().getSession(packet.getFrom());

		Presence presence = (Presence) packet;
		Presence.Type type = presence.getType();

		if (type == null) { 				// null == available
			if (session != null && session.getStatus() == Session.STATUS_CLOSED) {
				log.warn("Rejected available presence: " + presence + " - " + session);
				return;
			}

			if (session != null) {
				session.setPresence(presence);
				if (!session.isInitialized()) {
					session.setInitialized(true);
				}
			}
		} else if (Presence.Type.unavailable == type) {
			  if (session != null) {
                  session.setPresence(presence);
              }
		}else {
			presence = presence.createCopy();
		    if (session != null) {
		    	presence.setFrom(new JID(null, session.getServerName(),
                        null, true));
                presence.setTo(session.getAddress());
		    }else {
		    	 JID sender = presence.getFrom();
                 presence.setFrom(presence.getTo());
                 presence.setTo(sender);
		    }
		    presence.setError(PacketError.Condition.bad_request);
            PacketDeliverer.deliver(presence);
		}
		
		
	}

}
