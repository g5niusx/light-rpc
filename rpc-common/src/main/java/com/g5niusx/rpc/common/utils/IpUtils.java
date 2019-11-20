package com.g5niusx.rpc.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 本机系统信息
 */
@Slf4j
public final class IpUtils {


    private IpUtils() {
    }

    /**
     * 获取本机ip
     *
     * @return ip
     */
    public static String getSystemIp() {
        //从配置文件读取
        InetAddress systemLocalIp = getSystemLocalIp();
        if (systemLocalIp != null) {
            return systemLocalIp.getHostAddress();
        }
        return null;
    }


    private static InetAddress getSystemLocalIp() {
        InetAddress inetAddress;
        try {
            // 针对window系统
            if (isWindows()) {
                inetAddress = getWinLocalIp();
                // 针对linux系统
            } else {
                inetAddress = getUnixLocalIp();
            }
        } catch (SocketException e) {
            return null;
        }
        return inetAddress;
    }

    private static boolean isWindows() {
        String os = getSystemOSName().toLowerCase();
        // windows
        return os.contains("win");
    }

    /**
     * 获取FTP的配置操作系统
     *
     * @return 操作系统名称
     */
    private static String getSystemOSName() {
        // 获得系统属性集
        Properties props = System.getProperties();
        // 操作系统名称
        return props.getProperty("os.name");
    }

    /**
     * 获取window 本地ip地址
     *
     * @return ip对象
     */
    private static InetAddress getWinLocalIp() {
        InetAddress inet = null;
        try {
            inet = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("获取host异常", e);
        }
        return inet;
    }

    /**
     * 可能多多个ip地址只获取一个ip地址 获取Linux 本地IP地址
     *
     * @return ip
     */
    private static InetAddress getUnixLocalIp() throws SocketException {
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress                   ip;
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface         ni        = netInterfaces.nextElement();
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = addresses.nextElement();
                if (ip instanceof Inet4Address) {
                    return ip;
                }
            }
        }
        return null;
    }
}
