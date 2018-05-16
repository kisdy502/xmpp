package sdt.cn.store.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Xmpp {

	private final Log log=	LogFactory.getLog("Xmpp");

	private ApplicationContext context;

	private String serverName;

	public static Xmpp getInstance(){
		return XmppHolder.instance;
	}

	public Xmpp(){
		System.out.println("xmpp init");
		log.debug("xmpp init");
		serverName="127.0.0.1";
		start();
	}


	private void start() {
		log.info("start");
		System.out.println("xmpp start");
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
	}

	private final static class XmppHolder{
		private final static Xmpp instance=new Xmpp();
	}

	public Object getBean(String beanName) {
		return context.getBean(beanName);
	}

	public String getServerName() {
		return serverName;
	}


}
