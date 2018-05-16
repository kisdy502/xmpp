package sdt.cn.store.xmpp.router;

import org.xmpp.packet.Message;

public class MessageRouter {

	public MessageRouter() {
		
	}

	public void route(Message packet) {
		throw new RuntimeException("Please implement this!");
	}
}
