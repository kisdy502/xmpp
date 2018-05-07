package sdt.cn.store.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Xmpp {

	private final Log log=	LogFactory.getLog("Xmpp");

	private ApplicationContext context;

	private static Xmpp instance;

	public static Xmpp getInstance(){
		if(instance==null) {
			instance=new Xmpp();
		}
		return instance;
	}

	public Xmpp(){
		System.out.println("xmpp init");
		log.debug("xmpp init");
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
}
