package sdt.cn.store.xmpp.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.jivesoftware.openfire.nio.XMLLightweightParser;

import sdt.cn.store.xmpp.net.XmppIoHandler;

/**
 * 解码
 * @author Administrator
 *
 */
public class XmppDecoder extends CumulativeProtocolDecoder{

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		System.out.println("doDecode::");
		XMLLightweightParser parser = (XMLLightweightParser) session
				.getAttribute(XmppIoHandler.XML_PARSER);
		parser.read(in);
		if (parser.areThereMsgs()) {
			for (String stanza : parser.getMsgs()) {
				System.out.println(stanza);
				out.write(stanza);
			}
		}

		System.out.println("doDecode finish");
		return !in.hasRemaining();
	}
}
