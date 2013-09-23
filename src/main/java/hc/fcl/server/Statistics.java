package hc.fcl.server;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;

public class Statistics {
	private String ip = "";
	private int CurrentOpenConnections;
	private int AmountOfRequests;
	private HashSet<String> ClientIP;
	private HashMap<String, Object[]> ClientIPTable;
	private HashMap<String, Integer> RedirectsTable;
	private List<Connections> connectionsLog; 
	
	Statistics () {
		CurrentOpenConnections = 0;
		AmountOfRequests = 0;
		ClientIP = new HashSet<String>();
		ClientIPTable = new HashMap<String, Object[]>();
		connectionsLog = new LinkedList<Connections>();
		RedirectsTable = new HashMap<String, Integer>();
	}

	public void addCurrentOpenConnections() {
		CurrentOpenConnections++;
		addAmountOfRequests();
	}

	public void substractCurrentOpenConnections() {
		CurrentOpenConnections--;		
	}
	
	public int getCurrentOpenConnections() {
		return CurrentOpenConnections;
	}

	public void addRedirects(String Url) {
		if (RedirectsTable.containsKey(Url)) {
			RedirectsTable.put(Url, RedirectsTable.get(Url) + 1);
        } else {
            RedirectsTable.put(Url, 1);
        }
	}

	public void addClientIP(ChannelHandlerContext ctx) {
		ip = ctx.channel().remoteAddress().toString();
		ip=ip.substring(1, ip.lastIndexOf(':'));
		ClientIP.add(ip);
		
		
		if (ClientIPTable.containsKey(ip)) {
			ClientIPTable.get(ip)[0] = (Integer) ClientIPTable.get(ip)[0] + 1;
			ClientIPTable.get(ip)[1] = new Date();
		}
		else {
			Object tbl[] = new Object[2];
			tbl[0] = 1;
			tbl[1] = new Date();
			ClientIPTable.put(ip, tbl);
		}
	}

	public int getAmountOfRequests() {
		return AmountOfRequests;
	}

	public void addAmountOfRequests() {
		AmountOfRequests++;
	}

	public Object getClientIP() {
		return ip;
	}
	
	public int getUniqueIp() {
		return ClientIP.size();
	}

	public void addLog(Connections connections) {
		connectionsLog.add(connections);
		
	}
	
	public String getConnectionsFullLog() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < connectionsLog.size(); i++) {
			sb.append(connectionsLog.get(i).toString());
			sb.append("<br/>");
		}
		
		 
		 return sb.toString();
	}

	public List<Connections> getConnectionsLog() {
		return connectionsLog;
	}
	
	public Iterator<Map.Entry<String, Object[]>> getIteratorOfIpTable() {
        return ClientIPTable.entrySet().iterator();
    }

    public Iterator<Map.Entry<String, Integer>> getIteratorOfRedirectsTable() {
        return RedirectsTable.entrySet().iterator();
    }

}
