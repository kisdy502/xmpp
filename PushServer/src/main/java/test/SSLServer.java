package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;


public class SSLServer  extends Thread {

	private Socket socket;	

	public SSLServer(Socket socket) {
		System.out.println("client address:"+socket.getRemoteSocketAddress().toString());
		this.socket=socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			String data = reader.readLine();
			System.out.println("from client:"+data);
			writer.println("HelloIamServer");
			writer.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {

		}
	}

	private static String getPath() {
		String default_path="cert"+ File.separator + "server_ks";
		String source_path=	SSLServer.class.getResource("/").getPath();
		String full_path=source_path+File.separator+default_path;
		System.out.println("SSLServer::"+full_path);
		return full_path;
	}


	private static String SERVER_KEY_STORE = getPath();
	private static String SERVER_KEY_STORE_PASSWORD = "123123";

	public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, CertificateException,
	FileNotFoundException, IOException, KeyManagementException, UnrecoverableKeyException {
		System.setProperty("javax.net.ssl.trustStore", SERVER_KEY_STORE);
		System.setProperty("javax.net.debug", "ssl,handshake");
		SSLContext context = SSLContext.getInstance("TLS");
		KeyStore ks = KeyStore.getInstance("jceks");
		ks.load(new FileInputStream(SERVER_KEY_STORE), null);
		KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
		kf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());
		context.init(kf.getKeyManagers(), null, null);

		ServerSocketFactory factory = context.getServerSocketFactory();
		ServerSocket _socket = factory.createServerSocket(8443);
		((SSLServerSocket) _socket).setNeedClientAuth(true); 					 //false时，不需要客户端出示证书

		while (true) {
			new SSLServer(_socket.accept()).start();
		}
	}
}
