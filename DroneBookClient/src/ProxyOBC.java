import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.fazecast.jSerialComm.SerialPort;

public class ProxyOBC extends Thread {

	private SerialPort serialPort;
	private Socket sock;
	private String hostname;
	
	public ProxyOBC(String hostname) {
		super();
		this.hostname = hostname;
	}
	
	public void run() {
		
		if (hostname == null)
			hostname = "localhost";
		
		try {
			
			sock = new Socket(hostname, 51001);
			serialPort = initSerialPort();
			
			if (serialPort != null && serialPort.openPort()) {
				
				(new SerialToServer()).start();
				(new ServerToSerial()).start();
				
			}
				
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		
	}
	
	private class SerialToServer extends Thread {
		
		public void run() {
			
			try {
				
				BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());
				byte[] buffer = new byte[200];
				
				while (true) {
					
					int size = serialPort.readBytes(buffer, buffer.length);
					bos.write(buffer,0,size);
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
	
	private class ServerToSerial extends Thread {
		
		public void run() {
			
			try {
				
				BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
				byte[] buffer = new byte[200];
				
				while (true) {
					
					int size = bis.read(buffer);
					
					int auxSize = 0;
					
					while (auxSize < size) {
						
						auxSize += serialPort.writeBytes(buffer, size-auxSize, auxSize);	
						
					}
					
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

	private SerialPort initSerialPort() {
	
		SerialPort serialPort = SerialPort.getCommPort("/dev/ttyAMA0");
		
		if (serialPort != null) {
			
			serialPort.setComPortParameters(115200, 
					8, 
					SerialPort.ONE_STOP_BIT, 
					SerialPort.NO_PARITY);
			serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING,
					0, 
					0);
			
			
		}
		
		return serialPort;
	}
}
