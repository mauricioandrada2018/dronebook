/*******************************************************************************
 * Copyright 2021 Mauricio Andrada
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
