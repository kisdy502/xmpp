package sdt.cn.store.dao;

import java.util.List;

import sdt.cn.store.bean.Notification;



public interface NotificationDao {
	public void saveNotification(Notification notification);
	
	public List<Notification> findNotificationsByUsername(String username);
	
	public void deleteNotification(Notification notification);
	
	public void deleteNotificationByUuid(String UUID);
}
