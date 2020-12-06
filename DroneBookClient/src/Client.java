public class Client {
	
	public static void main(String[] args) {
		
		String hostname = "localhost";
		String iface = "sil";
		
		printUsage();
		
		if (args.length % 2 != 0) {
			
			System.out.println("Error: wrong number of options");
			return;
		}
		
		int length = (args.length < 4)?args.length:4;
		
		for (int i = 0; i < length; i +=2) {
			
			switch (args[i]) {
			
			case "-i":
				
				iface = args[i+1];
				
				break;
				
			case "-h":
				
				hostname = args[i+1];
				
				break;
				
			default:
				System.out.println("Error: incorrect parameters");

			}			
		}
		
		new Client(hostname,iface);
	}

	public Client(String hostName, String iface) {
		
		if (iface.equals("sil"))
			(new ProxySIL(hostName)).start();
		else if (iface.equals("obc"))
			(new ProxyOBC(hostName)).start();

	}
	
	private static void printUsage() {
		
		System.out.println("px4client [-h host] -i obc|sil");
		System.out.println("-h:	server host (default localhost)");
		System.out.println("-i:	interface (on-board computer or simulator (default simulator)");
		
	}
}
