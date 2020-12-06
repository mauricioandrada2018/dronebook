
public class Logger {
	
	void log(String comment, byte[] bytes, int size) {
		
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < size; i++) {
		

			sb.append(Byte.toString(bytes[i])).append(" ");
		}

		System.out.println(comment + " = " + sb.toString());
		System.out.println("size = "+size);
	}

}
