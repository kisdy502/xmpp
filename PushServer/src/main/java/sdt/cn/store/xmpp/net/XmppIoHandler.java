package sdt.cn.store.xmpp.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.dom4j.io.XMPPPacketReader;
import org.jivesoftware.openfire.net.MXParser;
import org.jivesoftware.openfire.nio.XMLLightweightParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import sdt.cn.store.xmpp.Xmpp;

public class XmppIoHandler implements IoHandler {

	public static final String XML_PARSER = "XML_PARSER";
	private static final String CONNECTION = "CONNECTION";
	private static final String STANZA_HANDLER = "STANZA_HANDLER";
	private String serverName;

	private static XmlPullParserFactory factory = null;
	private static Map<Integer, XMPPPacketReader> parsers = new ConcurrentHashMap<Integer, XMPPPacketReader>();

	public XmppIoHandler() {
		super();
		System.out.println("XmppIoHandler init");
		serverName=Xmpp.getInstance().getServerName();
		try {
			factory = XmlPullParserFactory.newInstance(
					MXParser.class.getName(), null);
			factory.setNamespaceAware(true);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("sessionCreated");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("sessionOpened");
		System.out.println("remote:"+session.getRemoteAddress());
		XMLLightweightParser parser = new XMLLightweightParser("UTF-8");
		session.setAttribute(XML_PARSER, parser);
		Connection connection = new Connection(session);
		session.setAttribute(CONNECTION, connection);
		session.setAttribute(STANZA_HANDLER, new StanzaHandler(serverName,
				connection));
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("sessionClosed");
		Connection connection = (Connection) session.getAttribute(CONNECTION);
		connection.close();
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		System.out.println("sessionIdle:"+status);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		StanzaHandler handler = (StanzaHandler) session.getAttribute(STANZA_HANDLER);
		int hashCode = Thread.currentThread().hashCode();
		XMPPPacketReader parser = parsers.get(hashCode);
		if (parser == null) {
			parser = new XMPPPacketReader();
			parser.setXPPFactory(factory);
			parsers.put(hashCode, parser);
		}
		try {
			handler.process((String) message, parser);
		} catch (Exception e) {
			e.printStackTrace();
			Connection connection = (Connection) session.getAttribute(CONNECTION);
			connection.close();
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("messageSent:"+message);
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		System.out.println("inputClosed:");
		session.close(false);

	}

}
