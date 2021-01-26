import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Proxy extends Thread {

	private Socket to, from;
	private boolean log;
	private String comment;

	public void setLog(String comment, boolean log) {

		this.log = log;
		this.comment = comment;
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
				
				if (log)
					System.out.println(comment + "received size = "+size);

				if (size > 0) {

					if (log)
						System.out.println(comment + Arrays.toString(Arrays.copyOfRange(buffer, 0, size - 1)));
					
					bos.write(buffer, 0, size);
					bos.flush();

				}

				synchronized (this) {

					try {
						wait(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
