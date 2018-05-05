package sdt.cn.store.xmpp;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Xmpp {
	private ApplicationContext context;
	public static Xmpp getInstance(){
		return XmppHolder.instance;
	}
	public Xmpp(){
		start();
	}


	private void start() {
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
	}
	private final static class XmppHolder{
		private final static Xmpp instance=new Xmpp();
	}

	public Object getBean(String beanName) {
		return context.getBean(beanName);
	}
}
