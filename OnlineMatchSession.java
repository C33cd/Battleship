import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class OnlineMatchSession implements Closeable {
    public enum Mode {
        HOST,
        CLIENT
    }

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Mode mode;
    private final String roomCode;
    private volatile boolean closed;

    public OnlineMatchSession(Socket socket, Mode mode, String roomCode) throws IOException {
        this.socket = socket;
        this.mode = mode;
        this.roomCode = roomCode;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        this.closed = false;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isHost() {
        return mode == Mode.HOST;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public boolean isOpen() {
        return !closed && socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void sendLine(String line) {
        if (!isOpen()) {
            return;
        }

        writer.println(line);
    }

    public String readLine() throws IOException {
        if (!isOpen()) {
            return null;
        }

        return reader.readLine();
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }

        closed = true;
        try {
            reader.close();
        } catch (IOException ignored) {
        }
        writer.close();
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}