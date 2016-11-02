package shared.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	public Connection (Socket socket) {
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Object o) {
		try {
			oos.writeObject(o);
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ObjectOutputStream getOutputStream() {
		return oos;
	}
	
	public ObjectInputStream getInputStream() {
		return ois;
	}
	
	public Object receive() {
		try {
			return ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Socket getSocket() {
		return socket;
	}
}
