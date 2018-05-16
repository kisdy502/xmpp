package sdt.cn.store.xmpp.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SSLTrustManagerFactory {

	private static final Log log = LogFactory.getLog(SSLTrustManagerFactory.class);

	public static TrustManager[] getTrustManagers(KeyStore truststore,
			String trustpass) {
		TrustManager[] trustManagers;
		try {
			if (truststore == null) {
				trustManagers = null;
			} else {
				TrustManagerFactory trustFactory = TrustManagerFactory
						.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				if (trustpass == null) {
					trustpass = SSLConfig.getc2sTrustPassword();
				}

				trustFactory.init(truststore);

				trustManagers = trustFactory.getTrustManagers();
			}
		} catch (KeyStoreException e) {
			trustManagers = null;
			log.error("SSLTrustManagerFactory startup problem.", e);
		} catch (NoSuchAlgorithmException e) {
			trustManagers = null;
			log.error("SSLTrustManagerFactory startup problem.", e);
		}
		return trustManagers;
	}

}
