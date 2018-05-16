package sdt.cn.store.xmpp.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import sdt.cn.store.service.UserNotFoundException;
import sdt.cn.store.xmpp.ServiceLocator;
import sdt.cn.store.xmpp.UnauthenticatedException;
import sdt.cn.store.xmpp.Xmpp;

public class AuthManager {
	private static final Object DIGEST_LOCK = new Object();

	private static MessageDigest digest;

	static {
		try {
			digest = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static boolean isPlainSupported() {
		return true;
	}

	public static boolean isDigestSupported() {
		return true;
	}

	public static AuthToken authenticate(String username, String password) throws UnauthenticatedException {
		if (username == null || password == null) {
			throw new UnauthenticatedException();
		}
		username = username.trim().toLowerCase();
		if (username.contains("@")) {
			int index = username.indexOf("@");
			String domain = username.substring(index + 1);
			if (domain.equals(Xmpp.getInstance().getServerName())) {
				username = username.substring(0, index);
			} else {
				throw new UnauthenticatedException();
			}
		}
		try {
			if (!password.equals(getPassword(username))) {
				throw new UnauthenticatedException();
			}
		} catch (UserNotFoundException unfe) {
			throw new UnauthenticatedException();
		}
		return new AuthToken(username);
	}

	private static String getPassword(String username) throws UserNotFoundException {
		return ServiceLocator.getUserService().getUserByUsername(username).getPassword();
	}

	public static AuthToken authenticate(String username, String token, String digest) throws UnauthenticatedException {
		if (username == null || token == null || digest == null) {
			throw new UnauthenticatedException();
		}
		username = username.trim().toLowerCase();
		if (username.contains("@")) {
			int index = username.indexOf("@");
			String domain = username.substring(index + 1);
			if (domain.equals(Xmpp.getInstance().getServerName())) {
				username = username.substring(0, index);
			} else {
				throw new UnauthenticatedException();
			}
		}
		try {
			String password = getPassword(username);
			String anticipatedDigest = createDigest(token, password);
			if (!digest.equalsIgnoreCase(anticipatedDigest)) {
				throw new UnauthenticatedException();
			}
		} catch (UserNotFoundException e) {
			throw new UnauthenticatedException();
		}
		return new AuthToken(username);
	}

	private static String createDigest(String token, String password) {
		synchronized (DIGEST_LOCK) {
			digest.update(token.getBytes());
			return Hex.encodeHexString(digest.digest(password.getBytes()));
		}
	}

}
