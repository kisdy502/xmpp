package sdt.cn.store.xmpp.push;

import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

import sdt.cn.store.bean.Notification;
import sdt.cn.store.bean.User;
import sdt.cn.store.service.NotificationService;
import sdt.cn.store.service.UserService;
import sdt.cn.store.xmpp.ServiceLocator;
import sdt.cn.store.xmpp.session.ClientSession;
import sdt.cn.store.xmpp.session.SessionManager;

public class NotificationManager {
	private static final String NOTIFICATION_NAMESPACE = "androidpn:iq:notification";

	private final Log log = LogFactory.getLog(getClass());

	private SessionManager sessionManager;
	private NotificationService notificationService;
	private UserService userService;



	public NotificationManager() {
		sessionManager = SessionManager.getInstance();
		notificationService = ServiceLocator.getNotificationService();
		userService = ServiceLocator.getUserService();
	}

	public void sendBroadcast(String apiKey, String title, String message, String uri) {
		log.info("sendBroadcast()...");
		List<User> users = userService.getUsers();
		for (User user : users) {
			Random random = new Random();
			String id = Integer.toHexString(random.nextInt());
			IQ notificationIQ = createNotificationIQ(id,apiKey, title, message, uri);
			ClientSession session = sessionManager.getSession(user.getUsername());
			if(session != null&&session.getPresence().isAvailable()){
				notificationIQ.setTo(session.getAddress());
				session.deliver(notificationIQ);
			}
			saveNotification(apiKey, user.getUsername(), title, message, uri, id);
		}

	}

	private void saveNotification(String apiKey, String username, String title, String message, String uri, String uuid) {
		Notification notification = new Notification();
		notification.setApiKey(apiKey);
		notification.setUri(uri);
		notification.setMessage(message);
		notification.setUsername(username);
		notification.setTitle(title);
		notification.setUuid(uuid);
		notificationService.saveNotification(notification);
	}

	private IQ createNotificationIQ(String id, String apiKey, String title, String message, String uri) {
		Element notification = DocumentHelper.createElement(QName.get(
				"notification", NOTIFICATION_NAMESPACE));
		notification.addElement("id").setText(id);
		notification.addElement("apiKey").setText(apiKey);
		notification.addElement("title").setText(title);
		notification.addElement("message").setText(message);
		notification.addElement("uri").setText(uri);

		IQ iq = new IQ();
		iq.setType(IQ.Type.set);
		iq.setChildElement(notification);

		return iq;
	}

	public void sendNotifcationToUser(String apiKey, String username, String title, String message, String uri,
			boolean b) {
		Random random = new Random();
		String id = Integer.toHexString(random.nextInt());
		IQ notificationIQ = createNotificationIQ(id,apiKey, title, message, uri);
		ClientSession session = sessionManager.getSession(username);
		if (session != null) {
			if (session.getPresence().isAvailable()) {
				notificationIQ.setTo(session.getAddress());
				session.deliver(notificationIQ);
			}
		}else {
			System.out.println("no user online");
		}
	}

}
