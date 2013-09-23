package hc.fcl.server;

import java.util.Date;

public class Connections {
	private String ip;
	private String uri;
	private Date timestamp;
	private long received;
	private long sent;
	private long speed;

	public Connections(String ip, String uri, Date timestamp, long received, long sent, long speed) {
		setIp(ip);
		setUri(uri);
		setTimestamp(timestamp);
		setReceived(received);
		setSent(sent);
		setSpeed(speed);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public long getReceived() {
		return received;
	}

	public void setReceived(long received) {
		this.received = received;
	}

	public long getSent() {
		return sent;
	}

	public void setSent(long sent) {
		this.sent = sent;
	}

	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}
	
	public String toString () {
		return getIp() + " " + getUri() + " " + getTimestamp().toString() + " " + getReceived() + " " + getSent() + " " + getSpeed();
	}

}
