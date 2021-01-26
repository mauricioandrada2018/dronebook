import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ProxySIL extends Thread {
	
	private Socket sock;
	private DatagramSocket udpSock;
	private String hostname;
	private int remotePort = -1;
	
	public ProxySIL(String hostname, int verbose) {
		super();
		this.hostname = hostname;
	}


	public void run() {
		
		if (hostname == null)
			hostname = "localhost";
		
		try {
			
			sock = new Socket(hostname, 51001);
			System.out.println("Success connecting to proxy server...");
			
			udpSock = new DatagramSocket(14550);
			
			(new OBCToServer()).start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private class ServerToOBC extends Thread {
		
		private int remotePort;

		public ServerToOBC(int remotePort) {
			super();
			this.remotePort = remotePort;
		}
		
		public void run() {
			
			try {
								
				BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
				byte[] buffer = new byte[200];
				
				while (true) {
					
					int size = bis.read(buffer);
					
					if (size > 0) {
						
						DatagramPacket dp = new DatagramPacket(buffer,0,size,InetAddress.getByName("localhost"),remotePort);
						udpSock.send(dp);
						
					}
									
					
					synchronized(this) {
						
						try {
							wait(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}			
		}						
	}
	
	private class OBCToServer extends Thread {
		
		public void run() {
		
			try {
				
				BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());
				byte[] buffer = new byte[200];
				
				while (true) {

					DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
					udpSock.receive(dp);
					
					if (remotePort < 0) {
						
						remotePort = dp.getPort();
						(new ServerToOBC(remotePort)).start();
						
					}
					
					bos.write(buffer,0,dp.getLength());
					bos.flush();
					
					synchronized(this) {
						
						try {
							wait(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}		
	}
}
