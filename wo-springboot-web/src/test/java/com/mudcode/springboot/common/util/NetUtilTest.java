package com.mudcode.springboot.common.util;

import com.mudcode.springboot.common.encoder.HexEncoder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

class NetUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(NetUtilTest.class);

    @Test
    void inetSocketAddressTest() {
        String str = "mudcode.com";
        InetSocketAddress address = new InetSocketAddress(str, 0);
        boolean unResolved = address.isUnresolved();
        System.out.println(unResolved);

        System.out.println(address.getAddress().getHostAddress());
    }

    /**
     * Regular expression that matches proxies that are to be trusted.
     *
     * @see ServerProperties.Tomcat.Remoteip
     */
    boolean isPrivateIp(String ip) {
        String internalProxies = "10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|" // 10/8
                + "192\\.168\\.\\d{1,3}\\.\\d{1,3}|" // 192.168/16
                + "169\\.254\\.\\d{1,3}\\.\\d{1,3}|" // 169.254/16
                + "127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|" // 127/8
                + "100\\.6[4-9]{1}\\.\\d{1,3}\\.\\d{1,3}|" // 100.64.0.0/10
                + "100\\.[7-9]{1}\\d{1}\\.\\d{1,3}\\.\\d{1,3}|" // 100.64.0.0/10
                + "100\\.1[0-1]{1}\\d{1}\\.\\d{1,3}\\.\\d{1,3}|" // 100.64.0.0/10
                + "100\\.12[0-7]{1}\\.\\d{1,3}\\.\\d{1,3}|" // 100.64.0.0/10
                + "172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|" // 172.16/12
                + "172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|" // 172.16/12
                + "172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|" // 172.16/12
                + "0:0:0:0:0:0:0:1|::1";
        Pattern internalProxiesPattern = Pattern.compile(internalProxies);
        return internalProxiesPattern.matcher(ip).matches();
    }

    @Test
    void privateIpTest() {
        System.out.println(isPrivateIp("127.0.0.1"));
        System.out.println(isPrivateIp("192.168.50.19"));
        System.out.println(isPrivateIp("172.16.32.27"));
        System.out.println(isPrivateIp("10.128.2.67"));
        System.out.println(isPrivateIp("169.254.221.117"));
        System.out.println(isPrivateIp("39.105.78.27"));
    }

    String produceMacHex() {
        NetworkInterface candidate = null;
        try {
            for (Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface
                    .getNetworkInterfaces(); interfaceEnumeration.hasMoreElements(); ) {
                NetworkInterface anInterface = interfaceEnumeration.nextElement();
                logger.trace("index: {}, displayName: {}", anInterface.getIndex(), anInterface.getDisplayName());

                if (!anInterface.isUp()) {
                    continue;
                }
                if (anInterface.isLoopback()) {
                    continue;
                }
                if (anInterface.isPointToPoint()) {
                    continue;
                }
                if (anInterface.isVirtual()) {
                    continue;
                }

                boolean hasNonLoopbackAddress = false;
                for (Enumeration<InetAddress> addrs = anInterface.getInetAddresses(); addrs.hasMoreElements(); ) {
                    InetAddress address = addrs.nextElement();
                    if (address.isLoopbackAddress()) {
                        continue;
                    }
                    if (address.isAnyLocalAddress()) {
                        continue;
                    }
                    if (address.isLinkLocalAddress()) {
                        continue;
                    }
                    hasNonLoopbackAddress = true;
                    logger.trace("index: {}, displayName: {}, address: {}", anInterface.getIndex(),
                            anInterface.getDisplayName(), address.getHostAddress());
                    break;
                }
                if (!hasNonLoopbackAddress) {
                    continue;
                }

                if (candidate == null) {
                    candidate = anInterface;
                } else {
                    candidate = anInterface.getIndex() < candidate.getIndex() ? anInterface : candidate;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (candidate == null) {
            throw new IllegalArgumentException("fail to get mac address");
        }

        try {
            String macHex = HexEncoder.toHexDigits(candidate.getHardwareAddress());
            logger.trace("index: {}, displayName: {}, macHex: {}", candidate.getIndex(), candidate.getDisplayName(),
                    macHex);
            return macHex;
        } catch (Exception e) {
            throw new IllegalArgumentException("fail to get mac address: " + e.getMessage(), e);
        }
    }

    @Test
    void produceMacHexTest() {
        System.out.println(produceMacHex());
    }

}
