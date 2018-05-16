package sdt.cn.store.xmpp.session;


import java.net.UnknownHostException;
import java.util.Date;

import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

import sdt.cn.store.xmpp.net.Connection;

public abstract class Session {

	SessionManager sessionManager;

	public static final int MAJOR_VERSION = 1;
	public static final int MINOR_VERSION = 0;

	/** 
	 * The session status when closed 
	 */
	public static final int STATUS_CLOSED = 0;

	/**
	 * The session status when connected
	 */
	public static final int STATUS_CONNECTED = 1;

	/**
	 * The session status when authenticated
	 */
	public static final int STATUS_AUTHENTICATED = 2;

	protected Connection connection;

	private String serverName;

	private int status = STATUS_CONNECTED;

	private String streamID;

	private JID address;

    private long startDate = System.currentTimeMillis();
    
	private long lastActiveDate;

	private long clientPacketCount = 0;

	private long serverPacketCount = 0;
	
    public String getServerName() {
        return serverName;
    }

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status=status;

	}

	public Session(String serverName, Connection conn, String streamID) {
		this.connection = conn;
		this.sessionManager = SessionManager.getInstance();
		this.serverName = serverName;
		this.streamID = streamID;
		this.address = new JID(null, serverName, streamID, true);
	}

	public String getStreamID() {
		return streamID;
	}

	public JID getAddress() {
		return address;
	}

    public void setAddress(JID address) {
        this.address = address;
    }
    
    public String getHostAddress() throws UnknownHostException {
        return connection.getHostAddress();
    }
    
    public Date getCreationDate() {
        return new Date(startDate);
    }

	public void close() {
		if (connection != null) {
			connection.close();
		}
	}

	public abstract String getAvailableStreamFeatures() ;


	public void process(Packet packet) {
		try {
			deliver(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deliver(Packet packet) {
		if (connection != null && !connection.isClosed()) {
			connection.deliver(packet);
		}
	}

	public void incrementServerPacketCount() {
		serverPacketCount++;
		lastActiveDate = System.currentTimeMillis();
	}


	public void incrementClientPacketCount() {
		clientPacketCount++;
		lastActiveDate = System.currentTimeMillis();
	}



}

