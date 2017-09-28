package server;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

/**
 * Created by User on 28.09.2017.
 */
public class ConnectionHelper {

    private ServerSocket server;
    private boolean close = false;

    public ConnectionHelper(int port) {
        System.out.println("port: " + port + "; ip: " + getIpAddress());
        connectServerSocket(port);
    }

    private void connectServerSocket(int port) {
        new Thread(() -> {
            try {
                server = new ServerSocket();
                server.bind(new InetSocketAddress("0.0.0.0", port));

                while (!close) {
                    new ClientSocket(server.accept()).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = enumeration.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    InetAddress inetAddress = inetAddressEnumeration
                            .nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "{"
                                + inetAddress.getHostAddress() + "}";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip.replace("}{", "},{");
    }

    private class ClientSocket extends Thread {

        private Socket socket;

        private ClientSocket(Socket socket) {
            this.socket = socket;
            try {
                socket.setKeepAlive(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            try {

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                String readeLine;

                while ((readeLine = reader.readLine()) != null) {
                    System.out.println(readeLine);
                    writer.println("thx for your message: " + readeLine);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
