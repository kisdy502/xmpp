package sdt.cn.store.xmpp.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class XmppEncoder implements ProtocolEncoder{

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		System.out.println("encode");
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		System.out.println("XmppEncoder dispose");
	}

}
