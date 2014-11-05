package com.jerabi.ssdp.demo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPMulicastTest {
	final private static int port = 1337;
	final private static String address = "239.255.255.250"; 
	private static InetAddress mcastGroup = null;
	private static MulticastSocket mcastSocket = null;

	public static void sender() throws IOException{
		String msg = "Hello";
		System.out.println("Sending `"+msg+"` to "+mcastGroup.getHostAddress()+":"+port);
		DatagramPacket udpMsg = new DatagramPacket(msg.getBytes(), msg.length(), mcastGroup, port);
		mcastSocket.send(udpMsg);
	}
	
	public static void receiver() throws IOException {
		byte[] buf = new byte[1000];
		mcastSocket.joinGroup(mcastGroup);
	 	DatagramPacket recv = new DatagramPacket(buf, buf.length);
	 	System.out.println("Listening on "+mcastSocket.getLocalSocketAddress());
	 	mcastSocket.receive(recv);
		mcastSocket.leaveGroup(mcastGroup);
	 	System.out.println("I got: "+ new String(recv.getData()));
	 }
	
	public static void main(String[] args) throws IOException{
		mcastGroup = InetAddress.getByName(address);
		mcastSocket = new MulticastSocket(port);
		mcastSocket.setLoopbackMode(false);
		System.out.println("Multicast loopback is "+mcastSocket.getLoopbackMode());
		if(args.length > 0){
			System.out.println("I'm a sender.");
			sender();
		} else {
			System.out.println("I'm a recevier.");
			receiver();
		}
		mcastSocket.close();
	}

}
