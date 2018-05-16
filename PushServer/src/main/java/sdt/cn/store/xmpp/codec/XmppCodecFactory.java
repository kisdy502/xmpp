package sdt.cn.store.xmpp.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class XmppCodecFactory implements ProtocolCodecFactory{

	private final XmppEncoder encoder;
	private final XmppDecoder decoder;

	public XmppCodecFactory() {
		encoder = new XmppEncoder();
		decoder = new XmppDecoder();
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

}
