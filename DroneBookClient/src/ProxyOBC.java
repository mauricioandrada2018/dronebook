import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import com.fazecast.jSerialComm.SerialPort;

public class ProxyOBC extends Thread {

	private SerialPort serialPort;
	private Socket sock;
	private String hostname;
	private boolean verbose;
	
	public ProxyOBC(String hostname, boolean verbose) {
		super();
		this.hostname = hostname;
		this.verbose = verbose;
	}
	
	public void run() {
		
		if (hostname == null)
			hostname = "localhost";
		
		try {
			
			if (verbose) System.out.println("Connecting to server..."+hostname);
			
			while(true) {
				
				try {
					
					sock = new Socket(hostname, 51001);
					break;
					
				} catch (Exception e) {
					if (verbose) System.out.println("Failed to connect to server "+hostname+"... Retrying in 1 second...");
				}

				synchronized(this) {
					
					try {
						wait(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if (verbose) {
				System.out.println("Success connecting to proxy server...");	
			}
			
			if (verbose) System.out.println("Connecting to serial port...");

			serialPort = initSerialPort();
			
			if (serialPort != null && serialPort.openPort()) {
				
				if (verbose) {
					System.out.println("Success connecting to serial port...");
				}
				
				(new SerialToServer()).start();
				(new ServerToSerial()).start();
				
			} else {
				
				if (verbose) {
					System.out.println("Failed connecting to serial port. Make sure Pixhawk is powered, serial port is correctly configured in the RPi 4 and cables are correctly connected");
				}
			}
				
			
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (Exception e) {
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
					
					if (size > 0) {
						
						if (verbose) System.out.println("data from serial: "+Arrays.toString(Arrays.copyOfRange(buffer, 0, size-1)));
						
						bos.write(buffer,0,size);
						bos.flush();
						
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
	
	private class ServerToSerial extends Thread {
		
		public void run() {
			
			try {
				
				BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
				byte[] buffer = new byte[200];
				
				while (true) {
					
					int size = bis.read(buffer);
					
					if (size > 0) {
						
						if (verbose) System.out.println("data from server: "+Arrays.toString(Arrays.copyOfRange(buffer, 0, size-1)));
						
						int auxSize = 0;
						
						while (auxSize < size) {
							
							auxSize += serialPort.writeBytes(buffer, size-auxSize, auxSize);	
							
						}
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

	private SerialPort initSerialPort() throws Exception {
	
		SerialPort serialPort = null;
				
			serialPort = SerialPort.getCommPort("/dev/ttyS0");
			
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
