import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private Proxy obcToQgc = new Proxy();
	private Proxy qgcToObc = new Proxy();

	public static void main(String[] args) {

		new Server();
	}
	
	public Server() {
		
		(new QGCThread()).start();
		(new OBCThread()).start();

	}
	
	private class QGCThread extends Thread {
				
		public void run() {
			
			try {
				
				System.out.println("QGC started");
				
				ServerSocket ss = new ServerSocket(54321);
				Socket qgcSocket = ss.accept();	
				
				ss.close();
				
				System.out.println("QGC connected: IP = "+qgcSocket.getInetAddress()+" port = "+qgcSocket.getPort());
				
				qgcToObc.setFrom(qgcSocket);
				obcToQgc.setTo(qgcSocket);
				
			} catch (IOException e) {

				e.printStackTrace();
			}			
		}
	}
	
	private class OBCThread extends Thread {
		
		public void run() {
			
			try {
				
				System.out.println("OBC started");
				
				ServerSocket ss = new ServerSocket(51001);
				Socket obcSocket = ss.accept();
				
				ss.close();
				
				System.out.println("On-board computer connected: IP = "+obcSocket.getInetAddress()+" port = "+obcSocket.getPort());
				
				qgcToObc.setLog(true);
							
				qgcToObc.setTo(obcSocket);
				obcToQgc.setFrom(obcSocket);
				
				
			} catch (IOException e) {

				e.printStackTrace();
			}
			
		}
	}

}
