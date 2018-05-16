package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class SSLClient {

	private static String CLIENT_KEY_STORE = getPath();
	private static String CLIENT_KEY_STORE_PASSWORD = "456456";

	private static String getPath() {
		String default_path="cert"+ File.separator + "client_ks";
		String source_path=	SSLServer.class.getResource("/").getPath();
		String full_path=source_path+File.separator+default_path;
		System.out.println("SSLClient::"+full_path);
		return full_path;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("javax.net.ssl.trustStore", CLIENT_KEY_STORE);
		System.setProperty("javax.net.debug", "ssl,handshake");
		SSLClient client = new SSLClient();
		Socket s = client.clientWithoutCert(true);
		PrintWriter writer = new PrintWriter(s.getOutputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		writer.println("hello I am client!");
		writer.flush();
		System.out.println("from server:"+reader.readLine());
		s.close();
	}

	private Socket clientWithoutCert() throws Exception {
		SocketFactory sf = SSLSocketFactory.getDefault();
		Socket s = sf.createSocket("localhost", 8443);
		return s;
	}
	
	//客户端出示证书
	private Socket clientWithoutCert(boolean authed) throws Exception {
		SSLContext context = SSLContext.getInstance("TLS");
		KeyStore ks = KeyStore.getInstance("jceks");
		ks.load(new FileInputStream(CLIENT_KEY_STORE), null);
        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
        kf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());
        context.init(kf.getKeyManagers(), null, null);
        SocketFactory factory = context.getSocketFactory();
        Socket s = factory.createSocket("localhost", 8443);
        return s;
	}
}
