import org.awaitility.Awaitility;
import org.example.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {

    @Test
    public void testClientPresence() throws IOException {
        Server server = new Server();
        server.start();

        InputStream resources = getClass().getClassLoader().getResourceAsStream("properties.yaml");
        Properties properties = new Properties();
        properties.load(resources);
        if (resources != null) {
            resources.close();
        }

        InetAddress inetAddress = InetAddress.getByName(properties.getProperty("host"));
        Socket socket = new Socket(inetAddress, Integer.parseInt(properties.getProperty("port")));
        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
        writer.write("Regina");
        writer.flush();
        Awaitility.await().atMost(5L, TimeUnit.SECONDS).until(() -> !Server.clients.isEmpty());
        assertEquals(1, Server.clients.size());

    }
}
