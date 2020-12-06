import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

public class Proxy extends Thread {
	
	private Socket to, from;
	private boolean log;
	private Logger logger = new Logger();
	
	public void setLog(boolean log) {
		
		this.log = log;
	}
	
	public void setTo(Socket to) {
		this.to = to;
		
		if (to != null && from != null)
			start();
	}

	public void setFrom(Socket from) {
		this.from = from;
		
		if (to != null && from != null)
			start();
	}
	
	public void run() {
		
		try {
			
			BufferedInputStream bis = new BufferedInputStream(from.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(to.getOutputStream());
			
			
			byte[] buffer = new byte[200];
			
			while (true) {
				
				int size = bis.read(buffer);
				
				if (size > 0) {
					
					bos.write(buffer,0,size);
					bos.flush();
				}
				
				if (log) {
					
					logger.log("data = ", buffer, size);
					
				}
					
				
				synchronized(this) {
					
					wait(1);
				}
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		

	}

}
