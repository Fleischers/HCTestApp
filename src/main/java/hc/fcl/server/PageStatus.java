package hc.fcl.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class PageStatus {
	private Statistics statistics;
	
	PageStatus (Statistics statistics) {
		this.statistics = statistics;
	}

	public ByteBuf getHTML() {
		
		return Unpooled.copiedBuffer(
				"<html><head><title>Status</title></head>"
				+ "<body>"
				+ "<h2>Status page</h2>"
				+ "<h3>Open connections: " + statistics.getCurrentOpenConnections() + "</h3>"
				+ "<h3>Total amount of requests: "+ statistics.getAmountOfRequests() + "</h3>" 
				+ "<h3>Unique IP requests counter: " +statistics.getUniqueIp() + "</h3>" 
				+ "<h3>Requests statistics: </h3>" 
				+"<table border><tr><td>IP</td><td>Request count</td><td>Time last query</td></tr>" 
				+ createIPTable() 	
				+ "</table> " 
				+ "<h3>Redirects: </h3>" + "<table border> <tr>  <td>Redirect url</td><td>Count</td>  </tr> " 
				+ createRedirectTable()  + "</table>"
				+ "<h3>Log: " + "</h3>" 
				+ "<table border>"
				+ "<tr><td> src_ip </td> <td> URI </td> <td> timestamp </td><td> sent_bytes </td> " 
				+ "<td> received_bytes </td><td> speed(bytes/sec) </td> </tr>"
				+ createLogTable(statistics.getConnectionsLog()) + "</table>"
				+ "</body></html>", CharsetUtil.US_ASCII);
	}
	
	private String createRedirectTable() {
		String table ="";
		Iterator<Map.Entry<String, Integer>> iterator = statistics.getIteratorOfRedirectsTable();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> element = iterator.next();
            table += "<tr><td>" + element.getKey() + " </td><td> " 
            		+ element.getValue() + " </td></tr>";
        }
        
        return table;
	}

	private String createIPTable() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Iterator<Map.Entry<String, Object[]>> iterator = statistics.getIteratorOfIpTable();
        String table = "";
        while (iterator.hasNext()) {
            Map.Entry<String, Object[]> element = iterator.next();
            table += "<tr><td> " + element.getKey() + " </td><td> " + element.getValue()[0] 
            		+ " </td><td> " + dateFormat.format(element.getValue()[1]) + " </td></tr>";
        }
        
        return table;
	}

	public ByteBuf getConnectionsHTML() {
		
		return Unpooled.copiedBuffer(
				"<html><head><title>Connections</title></head>"
				+ "<body>"
				+ "<h2>Connections page</h2>"
				+ "<h3>Log:</h3>" + statistics.getConnectionsFullLog() + "<br/>"
				+ "</body></html>", CharsetUtil.US_ASCII);
	}
	
	 private String createLogTable(List<Connections> connectionsLog) {
	        String s = new String();
	        if (connectionsLog.size() >= 16) {
	            for(int i = connectionsLog.size()-16; i < connectionsLog.size(); i++) {
	                s+= doTable(i, connectionsLog);
	            }
	        }
	        else {
	            for(int i = 0; i < connectionsLog.size(); i++) {
	            	s+=doTable(i, connectionsLog);
	            }
	                
	        }
	        return s;
	    }

	

	private String doTable(int i, List<Connections> cL) {
		StringBuffer sb = new StringBuffer();
		sb.append("<tr><td>");
        sb.append(cL.get(i).getIp()); //.substring(1, cL.get(i).getIp().lastIndexOf(':')));
        sb.append("</td><td>");
        sb.append(cL.get(i).getUri());
        sb.append("</td><td>");
        sb.append(cL.get(i).getTimestamp());
        sb.append("</td><td>");
        sb.append(cL.get(i).getSent());
        sb.append("</td><td>");
        sb.append(cL.get(i).getReceived());
        sb.append("</td><td>");
        sb.append(cL.get(i).getSpeed());
        sb.append("</td></tr>");
        
        return sb.toString();
		
	}

}
