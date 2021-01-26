public class Client {
	
	public static void main(String[] args) {
		
		String hostname = "localhost";
		String iface = "sil";
		int verbose = 0;
				
		if (args.length == 0 || args.length % 2 != 0) {
			
			System.out.println("Error: wrong number of options");
			printUsage();
			return;
		}
		
		int length = (args.length < 6)?args.length:6;
		
		for (int i = 0; i < length; i +=2) {
			
			switch (args[i]) {
			
			case "-i":
				
				iface = args[i+1];
				
				break;
				
			case "-h":
				
				hostname = args[i+1];
				
				break;
				
			case "-v":
				
				verbose = Integer.valueOf(args[i + 1]);
				
				break;
				
			default:
				System.out.println("Error: incorrect parameters");
				printUsage();

			}			
		}
		
		new Client(hostname,iface, verbose);
	}

	public Client(String hostName, String iface, int verbose) {
		
		if (iface.equals("sil"))
			(new ProxySIL(hostName, verbose)).start();
		else if (iface.equals("obc"))
			(new ProxyOBC(hostName, verbose)).start();

	}
	
	private static void printUsage() {
		
		System.out.println("px4client [-h host] [-v 1|2] -i obc|sil");
		System.out.println("-h:	server host (default localhost)");
		System.out.println("-i:	interface (on-board computer or simulator (default simulator)");
		System.out.println("-v:	enable verbose mode; higher numbers enable higher logs");

		
	}
}
