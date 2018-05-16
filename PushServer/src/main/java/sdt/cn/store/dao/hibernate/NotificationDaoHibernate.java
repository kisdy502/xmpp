package sdt.cn.store.dao.hibernate;

import java.util.List;

import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import sdt.cn.store.bean.Notification;
import sdt.cn.store.dao.NotificationDao;

public class NotificationDaoHibernate extends HibernateDaoSupport implements NotificationDao {

	public void saveNotification(Notification notification) {
		getHibernateTemplate().saveOrUpdate(notification);
		getHibernateTemplate().flush();

	}

	@SuppressWarnings("unchecked")
	public List<Notification> findNotificationsByUsername(String username) {
		List<Notification> list = (List<Notification>) getHibernateTemplate().find("from Notification n where n.username=?",username);
		return list;
	}

	public void deleteNotification(Notification notification) {
		getHibernateTemplate().delete(notification);
	}

	@SuppressWarnings("unchecked")
	public void deleteNotificationByUuid(String UUID) {
		List<Notification> list = (List<Notification>) getHibernateTemplate().find("from Notification where uuid=?",UUID);
		if(list!=null&&list.size()>0){
			getHibernateTemplate().delete(list.get(0));
		}

	}

}
