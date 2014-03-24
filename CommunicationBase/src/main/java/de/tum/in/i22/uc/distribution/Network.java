package de.tum.in.i22.uc.distribution;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Florian Kelbert
 *
 */
public class Network {

	public static final String IP_UNSPEC = "unspec";

	public static final Set<String> SUPPORTED_SOCKET_FAMILIES = new HashSet<String>(
			Arrays.asList("AF_INET", "AF_UNIX"));

	public static final Set<String> LOCAL_IP_ADDRESSES = new HashSet<String>(
			Arrays.asList("127.0.0.1", "localhost",
					"0000:0000:0000:0000:0000:0000:0000:0001", "::1"));
	protected static final Logger _logger = LoggerFactory.getLogger(Network.class);



	/**
	 * Returns this machines IP addresses, IPv4, IPv6, or both -- depending on the
	 * specified parameter.
	 *
	 * @param clazz Specifies which IP addresses to return: Inet4Address.class (IPv4), Inet6Address.class (IPv6), or InetAddress.class (both)
	 * @return the set of IP addresses, possibly empty. Never null.
	 */
	public static <T extends InetAddress> Set<T> getInetAddresses(Class<T> clazz) {
		Set<T> result = new HashSet<T>();

		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return result;
		}

		if (interfaces != null) {
			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				try {
					if (!current.isUp() || current.isLoopback()
							|| current.isVirtual()) {
						continue;
					}
				} catch (SocketException e) {
				}

				Enumeration<InetAddress> addresses = current.getInetAddresses();

				if (addresses != null) {
					while (addresses.hasMoreElements()) {
						InetAddress current_addr = addresses.nextElement();
						if (current_addr.isLoopbackAddress()) {
							continue;
						}

						try {
							T addr = clazz.cast(current_addr);
							result.add(addr);
						}
						catch (ClassCastException e) {
						}
					}
				}
			}
		}

		return result;
	}
}