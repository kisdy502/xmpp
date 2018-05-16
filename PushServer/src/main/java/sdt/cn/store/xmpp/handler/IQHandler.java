package sdt.cn.store.xmpp.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

import sdt.cn.store.xmpp.UnauthorizedException;
import sdt.cn.store.xmpp.router.PacketDeliverer;
import sdt.cn.store.xmpp.session.SessionManager;

public abstract class IQHandler {
	
	protected final Log log = LogFactory.getLog(getClass());

	public IQHandler() {
		super();
	}
	
    public void process(Packet packet) {
    	System.out.println("IQHandler.process");
        IQ iq = (IQ) packet;
        try {
            IQ reply = handleIQ(iq);
            if (reply != null) {
                PacketDeliverer.deliver(reply);
            }
        } catch (UnauthorizedException e) {
        	if (iq != null) {
        		 IQ response = IQ.createResultIQ(iq);
        		 response.setChildElement(iq.getChildElement().createCopy());
                 response.setError(PacketError.Condition.not_authorized);
                 SessionManager.getInstance().getSession(iq.getFrom()).process(response);
        	}
        }catch (Exception e) {
			log.info("服务器内部错误"+e.getMessage());
		}
    }
    
    public abstract IQ handleIQ(IQ packet) throws UnauthorizedException;
    
    
    public abstract String getNamespace();
}
