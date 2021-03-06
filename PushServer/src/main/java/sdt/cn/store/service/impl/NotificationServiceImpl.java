package sdt.cn.store.service.impl;

import java.util.List;

import sdt.cn.store.bean.Notification;
import sdt.cn.store.dao.NotificationDao;
import sdt.cn.store.service.NotificationService;

public class NotificationServiceImpl implements NotificationService {

	public NotificationDao notificationDao;

	public void saveNotification(Notification notification) {
		notificationDao.saveNotification(notification);
	}

	public List<Notification> findNotificationsByUsername(String username) {
		return notificationDao.findNotificationsByUsername(username);
	}

	public void deleteNotification(Notification notification) {
		notificationDao.deleteNotification(notification);
	}

	public NotificationDao getNotificationDao() {
		return notificationDao;
	}

	public void setNotificationDao(NotificationDao notificationDao) {
		this.notificationDao = notificationDao;
	}

	public void deleteNotification(String uuid) {
		notificationDao.deleteNotificationByUuid(uuid);
	}



}
