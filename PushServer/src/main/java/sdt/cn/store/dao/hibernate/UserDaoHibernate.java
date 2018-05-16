package sdt.cn.store.dao.hibernate;

import java.util.Date;

import java.util.List;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import sdt.cn.store.bean.User;
import sdt.cn.store.dao.UserDao;
import sdt.cn.store.service.UserNotFoundException;
public class UserDaoHibernate extends HibernateDaoSupport implements UserDao{
	
	@Override
	public User getUser(Long id) {
		return (User) getHibernateTemplate().get(User.class, id);
	}

	public User saveUser(User user) {
		getHibernateTemplate().saveOrUpdate(user);
		getHibernateTemplate().flush();
		return user;
	}

	public void removeUser(Long id) {
		getHibernateTemplate().delete(getUser(id));
	}

	public boolean exists(Long id) {
		User user = (User) getHibernateTemplate().get(User.class, id);
		return user != null;
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsers() {
		return (List<User>) getHibernateTemplate().find(
				"from User u order by u.createdDate desc");
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsersFromCreatedDate(Date createDate) {
		return (List<User>) getHibernateTemplate()
				.find("from User u where u.createdDate >= ? order by u.createdDate desc",
						createDate);
	}

	@SuppressWarnings("unchecked")
	public User getUserByUsername(String username) throws UserNotFoundException {
		List users = getHibernateTemplate().find("from User where username=?",
				username);
		if (users == null || users.isEmpty()) {
			throw new UserNotFoundException("User '" + username + "' not found");
		} else {
			return (User) users.get(0);
		}
	}

}
