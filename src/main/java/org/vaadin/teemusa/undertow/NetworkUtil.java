package org.vaadin.teemusa.undertow;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

/**
 * Helper class for network related utilities.
 */
class NetworkUtil {

    private static final int RANGE_START = 50000;
    private static Random random = new Random();

    /**
     * Finds out a suitable hostname for a server to use from outside.
     * 
     * @return hostname where the server should be accessible
     */
    public static String getDeploymentHostname() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nwInterface = interfaces.nextElement();
                if (!nwInterface.isUp() || nwInterface.isLoopback()
                        || nwInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = nwInterface
                        .getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isLoopbackAddress()) {
                        continue;
                    }
                    if (address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Could not enumerate ");
        }

        throw new RuntimeException("No compatible ip address found.");
    }

    /**
     * Gets a random port in range {@code [50 000, 60 000)}. The returned port
     * has been tested to be available for the server to use.
     * 
     * @return random free port
     */
    public static int getRandomPort() {
        int port;
        do {
            port = random.nextInt(10000) + RANGE_START;
        } while (!available(port));
        return port;
    }

    /**
     * Helper method for testing if a port is available for use.
     * 
     * @param port
     *            the port to test
     * @return {@code true} if port is available; {@code false} if not
     */
    private static boolean available(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

}
