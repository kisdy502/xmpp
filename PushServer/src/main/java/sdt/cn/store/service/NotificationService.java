package sdt.cn.store.service;

import java.util.List;

import sdt.cn.store.bean.Notification;


public interface NotificationService {

	public void saveNotification(Notification notification);
	
	public List<Notification> findNotificationsByUsername(String username);
	
	public void deleteNotification(Notification notification);
	
	public void deleteNotification(String uuid);
}
