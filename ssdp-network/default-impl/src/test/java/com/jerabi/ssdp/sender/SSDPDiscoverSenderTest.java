package com.jerabi.ssdp.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import com.jerabi.ssdp.handler.SSDPDiscoverResponseHandler;
import com.jerabi.ssdp.network.impl.MulticastListener;
import com.jerabi.ssdp.network.impl.UDPSender;
import com.jerabi.ssdp.network.impl.UDPServer;
import com.jerabi.ssdp.sender.SSDPDiscoverSender;
import com.jerabi.ssdp.util.SSDPConstants;

public class SSDPDiscoverSenderTest {

	private static SSDPDiscoverSender discoverSender = null;
	private static SSDPDiscoverSender discoverSender2 = null;
	private static ExecutorService threadPool = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
    	discoverSender = new SSDPDiscoverSender(null, SSDPConstants.DEFAULT_IP, SSDPConstants.DEFAULT_PORT);
    	discoverSender2 = new SSDPDiscoverSender(null, SSDPConstants.DEFAULT_IP, SSDPConstants.DEFAULT_PORT, SSDPConstants.DEFAULT_DELAY);
    	
    	threadPool = Executors.newFixedThreadPool(5);

		threadPool.execute(discoverSender);
		threadPool.execute(discoverSender2);

		CountDownLatch latch = new CountDownLatch(1);
		latch.await(1, TimeUnit.SECONDS);
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSendMessageISSDPMessage() {
		
		try {
			discoverSender.sendMessage("DiscoverMessage");
			
		} catch (Exception e) {
			fail("Shouldn't throw Exception");
		}
		
	} 

	@Test
	public void testGetSSDPMessagesToSend() {
		assertNotNull(discoverSender.getSSDPMessagesToSend());
		assertTrue(discoverSender.getSSDPMessagesToSend().size()>0);
	}
	
	@Test
	public void testInvalidPort() {
		
		boolean exception = false;
		
		try { 
			SSDPDiscoverSender sender1 = new SSDPDiscoverSender(null, SSDPConstants.DEFAULT_IP, -1);
			sender1.sendMessage("invalid port");
		} catch(Exception e){
			exception = true;
		}
		
		assertTrue(exception);
    	
		exception = false;
		try {
			SSDPDiscoverSender sender1 = new SSDPDiscoverSender(null, SSDPConstants.DEFAULT_IP, -1, SSDPConstants.DEFAULT_DELAY);
			sender1.sendMessage("invalid port");
		} catch(Exception e){
			exception = true;
		}
		
		assertTrue(exception);
    	
	}
	
	@Ignore("Badly written test, known to fail, need further analysis") @Test
	/*
	 * to complete this test, we need to have a listener that will response to this M_SEARCH
	 */
	public void testSendNull() {
		
		// this is a wrong assumption! The test below actually sets the response handler.
		assertNull(discoverSender.getSSDPResponseHandler());
		
		discoverSender.setSSDPResponseHandler(new SSDPDiscoverResponseHandler(null){
			@Override
			public void handle(String message) throws Exception {
			}
		});
		
		assertNotNull(discoverSender.getSSDPResponseHandler());
		
		try {
			discoverSender.sendMessage(null);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Shouldn't throw Exception");
		}
		
	}

	@Ignore("Badly written test, known to fail, need further analysis") @Test
	/*
	 * to complete this test, we need to have a listener that will response to this M_SEARCH
	 */
	public void testSSDPHandler() {
		
		MulticastListener listener = null;
		
		try {
			listener = new MulticastListener(SSDPConstants.DEFAULT_PORT, new SSDPDiscoverResponseHandler(null) {
				
				@Override
				public void handle(String remoteAddr, int remotePort, String message)
						throws Exception {
					UDPSender sender = new UDPSender();
					sender.sendMessage(message, InetAddress.getByName(remoteAddr), remotePort, null, 1);
				}
			});
			
			listener.joinGroup(InetAddress.getByName(SSDPConstants.DEFAULT_IP));
			
		} catch(Exception e){
			
		}
		
		Thread t1 = new Thread(new UDPServer(listener, false));
		t1.start();
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		discoverSender.setSSDPResponseHandler(new SSDPDiscoverResponseHandler(null){
			@Override
			public void handle(String message) throws Exception {
				if(message!=null && message.equals("testSetSSDPHandler")){
					latch.countDown();
				}
			}
		});
		
		assertNotNull(discoverSender.getSSDPResponseHandler());
		
		try {
			discoverSender.sendMessage("testSetSSDPHandler");
				
		} catch (Exception e) {
			fail("Shouldn't throw Exception");
		}
		
		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		
		assertEquals(0, latch.getCount());
		
		t1.interrupt();
		fail("Need a DiscoverResponseServer");
	}
	
}
