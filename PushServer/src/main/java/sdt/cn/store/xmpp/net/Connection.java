package sdt.cn.store.xmpp.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.dom4j.io.OutputFormat;
import org.jivesoftware.util.XMLWriter;
import org.xmpp.packet.Packet;

import sdt.cn.store.xmpp.session.Session;
import sdt.cn.store.xmpp.ssl.SSLConfig;
import sdt.cn.store.xmpp.ssl.SSLKeyManagerFactory;
import sdt.cn.store.xmpp.ssl.SSLTrustManagerFactory;

/**
 * 表示一个连接
 * @author Administrator
 *
 */
public class Connection {
	private final Log log=	LogFactory.getLog("Connection");

	@SuppressWarnings("unchecked")
	private static ThreadLocal encoder = new ThreadLocalEncoder();

	private String language = null;
	private int majorVersion = 1;
	private int minorVersion = 0;

	private IoSession ioSession;

	private Session session;

	private boolean closed;

	private TLSPolicy tlsPolicy = TLSPolicy.required; 					 //默认需要SSL验证

	public Connection(IoSession session) {
		ioSession=session;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setXMPPVersion(int majorVersion, int minorVersion) {
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

	public TLSPolicy getTlsPolicy() {
		return tlsPolicy;
	}

	public void init(Session session) {
		this.session = session;
	}

	public void close() {
		boolean closedSuccessfully = false;
		synchronized (this) {
			if(!isClosed()) {
				try {
					deliverRawText("</stream:stream>", false);
				} catch (Exception e) {
					// Ignore
				}
				if (session != null) {
					session.setStatus(Session.STATUS_CLOSED);
				}
				ioSession.close(false);
				closed = true;
				closedSuccessfully = true;
			}
		}

		if (closedSuccessfully) {
			notifyCloseListeners();
		}else {
			log.debug("非安全关闭");
		}
	}
	
	public String getHostAddress() throws UnknownHostException {
		return ((InetSocketAddress) ioSession.getRemoteAddress()).getAddress()
				.getHostAddress();
	}

	public boolean isClosed() {
		if (ioSession == null) {
			return closed;
		}
		return session.getStatus() == Session.STATUS_CLOSED;
	}

	public void deliverRawText(String text) {
		deliverRawText(text,true);
	}

	private void deliverRawText(String text, boolean asynchronous) {
		System.out.println("send::"+text);
		if (!isClosed()) {
			IoBuffer buffer = IoBuffer.allocate(text.length());
			buffer.setAutoExpand(true);
			boolean errorDelivering = false;
			try {
				buffer.put(text.getBytes("UTF-8"));
				buffer.flip();
				if (asynchronous) {
					ioSession.write(buffer);
				}else {
					// Send stanza and wait for ACK
					boolean ok = ioSession.write(buffer).awaitUninterruptibly(2000);
					if (!ok) {
						System.out.println("No ACK was received when sending stanza to: "+ this.toString());
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				errorDelivering = true;
			}
			// Close the connection if delivering text fails
			if (errorDelivering && asynchronous) {
				close();
			}
		}else {
			System.out.println("session has closed");
		}
	}

	private void notifyCloseListeners() {
		if (closeListener != null) {
			try {
				closeListener.onConnectionClose(session);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Enumeration of possible TLS policies required to interact with the server.
	 */
	public enum TLSPolicy {
		required, optional, disabled
	}

	public enum ClientAuth {
		disabled,wanted,needed
	}

	private ConnectionCloseListener closeListener;

	public void registerCloseListener(ConnectionCloseListener listener) {
		log.info("registerCloseListener");
		if (closeListener != null) {
			throw new IllegalStateException("Close listener already configured");
		}
		if (isClosed()) {
			listener.onConnectionClose(session);
		} else {
			closeListener = listener;
		}
	}

	public void startTLS(ClientAuth policy) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		log.info("startTLS()...");
		KeyStore ksKeys = SSLConfig.getKeyStore();
		System.out.println("ksKeys  is null::"+(ksKeys==null));
		String keypass = SSLConfig.getKeyPassword();
		System.out.println("keypass::"+keypass);

		KeyStore ksTrust = SSLConfig.getc2sTrustStore();
		String trustpass = SSLConfig.getc2sTrustPassword();
		System.out.println("trustpass::"+trustpass);

		KeyManager[] km = SSLKeyManagerFactory.getKeyManagers(ksKeys, keypass);
		TrustManager[] tm = SSLTrustManagerFactory.getTrustManagers(ksTrust,trustpass);

		SSLContext tlsContext = SSLContext.getInstance("TLS");
		tlsContext.init(km, tm, null);

		SslFilter filter = new SslFilter(tlsContext);
		ioSession.getFilterChain().addFirst("tls", filter);
		//ioSession.getFilterChain().addBefore("executor", "tls", filter);
		ioSession.setAttribute(SslFilter.DISABLE_ENCRYPTION_ONCE, Boolean.TRUE);

		deliverRawText("<proceed xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>");

	}

	public void deliver(Packet packet) {
		String xml=packet.toXML();
		deliverRawText(xml,false);
	}


	private static class ThreadLocalEncoder extends ThreadLocal<CharsetEncoder> {
		protected CharsetEncoder initialValue() {
			return Charset.forName("UTF-8").newEncoder();
		}
	}

}
