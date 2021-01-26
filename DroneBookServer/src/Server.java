import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private Proxy obcToQgc = new Proxy();
	private Proxy qgcToObc = new Proxy();
	private String logSelection = "";

	public static void main(String[] args) {

		Server server = new Server();
		if (args.length > 0)
			server.logSelection = args[0];
	}
	
	public Server() {
		
		(new OBCThread()).start();
		(new QGCThread()).start();

	}
	
	private class QGCThread extends Thread {
				
		public void run() {
			
			try {
				
				System.out.println("QGC started");
				
				ServerSocket ss = new ServerSocket(54321);
				Socket qgcSocket = ss.accept();	
								
				System.out.println("QGC connected: IP = "+qgcSocket.getInetAddress()+" port = "+qgcSocket.getPort());
				
				if (logSelection.equals("qgc"))
					qgcToObc.setLog("QGC to OBC: ",true);
				
				qgcToObc.setFrom(qgcSocket);
				obcToQgc.setTo(qgcSocket);
				
				ss.close();

				
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
				
				System.out.println("On-board computer connected: IP = "+obcSocket.getInetAddress()+" port = "+obcSocket.getPort());

				if (logSelection.equals("obc"))
					obcToQgc.setLog("OBC to QGC: ",true);
		
				qgcToObc.setTo(obcSocket);
				obcToQgc.setFrom(obcSocket);
				
				ss.close();
				
				
			} catch (IOException e) {

				e.printStackTrace();
			}
			
		}
	}

}
