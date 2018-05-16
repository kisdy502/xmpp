package sdt.cn.store.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;

import org.springframework.dao.DataIntegrityViolationException;

import sdt.cn.store.bean.User;
import sdt.cn.store.dao.UserDao;
import sdt.cn.store.service.UserExistsException;
import sdt.cn.store.service.UserNotFoundException;
import sdt.cn.store.service.UserService;

public class UserServiceImpl implements UserService{
	
	private UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public User getUser(String userId) {
		return userDao.getUser(new Long(userId));
	}

	public List<User> getUsers() {
		return userDao.getUsers();
	}

	@Override
	public List<User> getUsersFromCreatedDate(Date createDate) {
		return null;
	}

	@Override
	public User saveUser(User user) throws UserExistsException {
		try {
			return userDao.saveUser(user);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			throw new UserExistsException("User '" + user.getUsername()	+ "' already exists!");
		}catch (EntityExistsException e) {
			e.printStackTrace();
			throw new UserExistsException("User '" + user.getUsername()	+ "' already exists!");
		}
	}
	@Override
	public User getUserByUsername(String username) throws UserNotFoundException {
		return (User) userDao.getUserByUsername(username);
	}

	@Override
	public void removeUser(Long userId) {
		userDao.removeUser(userId);

	}



}
