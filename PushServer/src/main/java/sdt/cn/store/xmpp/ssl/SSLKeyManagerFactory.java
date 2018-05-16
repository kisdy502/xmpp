package sdt.cn.store.xmpp.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SSLKeyManagerFactory {

	private static final Log log = LogFactory.getLog(SSLKeyManagerFactory.class);

	public static KeyManager[] getKeyManagers(String storeType,
			String keystore, String keypass) throws NoSuchAlgorithmException,
	KeyStoreException, IOException, CertificateException,
	UnrecoverableKeyException {
		KeyManager[] keyManagers;
		if (keystore == null) {
			keyManagers = null;
		} else {
			if (keypass == null) {
				keypass = "";
			}
			KeyStore keyStore = KeyStore.getInstance(storeType);
			keyStore.load(new FileInputStream(keystore), keypass.toCharArray());

			KeyManagerFactory keyFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyFactory.init(keyStore, keypass.toCharArray());
			keyManagers = keyFactory.getKeyManagers();
		}
		return keyManagers;
	}


	public static KeyManager[] getKeyManagers(KeyStore keystore, String keypass) {
		KeyManager[] keyManagers;
		try {
			if (keystore == null) {
				keyManagers = null;
			} else {
				KeyManagerFactory keyFactory = KeyManagerFactory
						.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				if (keypass == null) {
					keypass = SSLConfig.getKeyPassword();
				}

				keyFactory.init(keystore, keypass.toCharArray());
				keyManagers = keyFactory.getKeyManagers();
			}
		} catch (KeyStoreException e) {
			keyManagers = null;
			log.error("SSLKeyManagerFactory startup problem.", e);
		} catch (NoSuchAlgorithmException e) {
			keyManagers = null;
			log.error("SSLKeyManagerFactory startup problem.", e);
		} catch (UnrecoverableKeyException e) {
			keyManagers = null;
			log.error("SSLKeyManagerFactory startup problem.", e);
		}
		return keyManagers;
	}

}
