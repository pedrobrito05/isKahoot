package iskahoot.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket client;
    public Connection(String ip, int port) throws IOException {
        client = new Socket(ip , port);
        out=new ObjectOutputStream(client.getOutputStream());
        in=new ObjectInputStream ( client.getInputStream());
    }
    public Connection(Socket socket) throws IOException {
        this.client=socket;
        this.out=new ObjectOutputStream(socket.getOutputStream());
        this.in= new ObjectInputStream(socket.getInputStream());
    }

    public synchronized void send(Answer answer) throws IOException {
        out.writeObject(answer);
        out.flush();
    }
    public synchronized void send(String string) throws IOException {
        out.writeObject(string);
        out.flush();
    }
    public synchronized void send(Question question) throws IOException {
        out.writeObject(question);
        out.flush();
    }

    public Object receive() throws IOException, ClassNotFoundException {
        return in.readObject();
    }
    public void close() throws IOException {
        out.close();
        in.close();
        client.close ();
    }

}
