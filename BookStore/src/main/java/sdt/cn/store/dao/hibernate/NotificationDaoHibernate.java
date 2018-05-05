package sdt.cn.store.dao.hibernate;

import java.util.List;

import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import sdt.cn.store.bean.Notification;
import sdt.cn.store.dao.NotificationDao;

@SuppressWarnings("deprecation")
public class NotificationDaoHibernate extends HibernateDaoSupport implements NotificationDao {

	public void saveNotification(Notification notification) {
		getHibernateTemplate().saveOrUpdate(notification);
		getHibernateTemplate().flush();
		
	}

	public List<Notification> findNotificationsByUsername(String username) {
		@SuppressWarnings({ "deprecation", "unchecked" })
		List<Notification> list = (List<Notification>) getHibernateTemplate().find("from Notification n where n.username=?",username);
		if(list!=null&&list.size()>0){
			return list;
		}else {
			return null;
		}
	}

	public void deleteNotification(Notification notification) {
		getHibernateTemplate().delete(notification);
		
	}

	public void deleteNotificationByUuid(String UUID) {
		@SuppressWarnings({ "deprecation", "unchecked" })
		List<Notification> list = (List<Notification>) getHibernateTemplate().find("from Notification where uuid=?",UUID);
		if(list!=null&&list.size()>0){
			getHibernateTemplate().delete(list.get(0));
		}
		
	}

}
