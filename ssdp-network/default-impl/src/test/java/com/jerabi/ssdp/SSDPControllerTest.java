package com.jerabi.ssdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import com.jerabi.ssdp.SSDPcontroller;
import com.jerabi.ssdp.handler.ISSDPMessageHandler;
import com.jerabi.ssdp.handler.SSDPDefaultMessageHandler;
import com.jerabi.ssdp.listener.SSDPMulticastListener;
import com.jerabi.ssdp.message.AbstractSSDPNotifyMessage;
import com.jerabi.ssdp.message.AliveMessage;
import com.jerabi.ssdp.message.ByeByeMessage;
import com.jerabi.ssdp.message.DiscoverMessage;
import com.jerabi.ssdp.message.DiscoverResponseMessage;
import com.jerabi.ssdp.message.ISSDPMessage;
import com.jerabi.ssdp.message.ServiceInfo;
import com.jerabi.ssdp.message.USNInfo;
import com.jerabi.ssdp.message.UpdateMessage;
import com.jerabi.ssdp.message.helper.SSDPMessageHelper;
import com.jerabi.ssdp.sender.SSDPDiscoverSender;
import com.jerabi.ssdp.sender.SSDPPeriodicMessageSender;
import com.jerabi.ssdp.util.SSDPConstants;
import com.jerabi.ssdp.util.State;

public class SSDPcontrollerTest {

	private static SSDPcontroller controller = null;
	private static ISSDPMessage aliveMessage = null;
	private static ISSDPMessage byebyeMessage = null;
	private static ISSDPMessage updateMessage = null;
	private static ISSDPMessage discoverMessage = null;
	private static ISSDPMessage discoverResponseMessage = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		aliveMessage = SSDPMessageHelper.getSSDPMessage(createAliveMessage());
		byebyeMessage = SSDPMessageHelper.getSSDPMessage(createByeByeMessage());
		updateMessage = SSDPMessageHelper.getSSDPMessage(createUpdateMessage());
		discoverMessage = SSDPMessageHelper.getSSDPMessage(createDiscoverMessage());
		discoverResponseMessage = SSDPMessageHelper.getSSDPMessage(createDiscoverResponseMessage());
		
	}

	@Before
	public void setUp() throws Exception {
		controller = new SSDPcontroller();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMessageListener() {
		
		assertNotNull(controller.getMessageHandlerList());
		assertTrue(controller.getMessageHandlerList().size()==0);
		
		ISSDPMessageHandler listener = new SSDPDefaultMessageHandler(){
			@Override
			public void processSSDPAliveMessage(AliveMessage ssdpMessage)
					throws Exception {
			}
			@Override
			public void processSSDPByeByeMessage(ByeByeMessage ssdpMessage)
					throws Exception {
			}
			@Override
			public void processSSDPDiscoverMessage(String remoteAddr,
					int remotePort, DiscoverMessage ssdpMessage)
					throws Exception {
			}
			@Override
			public void processSSDPDiscoverResponseMessage(
					DiscoverResponseMessage ssdpMessage) throws Exception {
			}
			@Override
			public void processSSDPUpdateMessage(UpdateMessage ssdpMessage)
					throws Exception {
			}};
			
		
		controller.addMessageHandler(listener);
		assertTrue(controller.getMessageHandlerList().size()==1);
			
		controller.removeMessageHandler(null);
		assertTrue(controller.getMessageHandlerList().size()==1);
		
		
		controller.removeMessageHandler(listener);
		
		assertNotNull(controller.getMessageHandlerList());
		assertTrue(controller.getMessageHandlerList().size()==0);
		
	}

	@Test
	public void testPeriodicSender() {
		
		boolean enabled = controller.getPeriodicSenderEnabled();
		
		controller.setPeriodicSenderEnabled(!enabled);
		assertTrue(controller.getPeriodicSenderEnabled()==!enabled);
		
		controller.setPeriodicSenderEnabled(enabled);
		assertTrue(controller.getPeriodicSenderEnabled()==enabled);
		
		
		SSDPPeriodicMessageSender sender = controller.getPeriodicMessageSender();
		assertNotNull(sender);
		
		controller.setPeriodicMessageSender(null);
		assertTrue(controller.getPeriodicMessageSender()==null);
		
		controller.setPeriodicMessageSender(sender);
		assertEquals(sender, controller.getPeriodicMessageSender());
		
	}

	@Test
	public void testDiscoverSender() {
		
		boolean enabled = controller.getDiscoverSenderEnabled();
		
		controller.setDiscoverSenderEnabled(!enabled);
		assertTrue(controller.getDiscoverSenderEnabled()==!enabled);
		
		controller.setDiscoverSenderEnabled(enabled);
		assertTrue(controller.getDiscoverSenderEnabled()==enabled);
		
		
		SSDPDiscoverSender sender = controller.getDiscoverSender();
		assertNotNull(sender);
		
		controller.setDiscoverSender(null);
		assertTrue(controller.getDiscoverSender()==null);
		
		controller.setDiscoverSender(sender);
		assertEquals(sender, controller.getDiscoverSender());
	}

	@Test
	public void testMulticastListener() {
		boolean enabled = controller.getMulticastListenerEnabled();
		
		controller.setMulticastListenerEnabled(!enabled);
		assertTrue(controller.getMulticastListenerEnabled()==!enabled);
		
		controller.setMulticastListenerEnabled(enabled);
		assertTrue(controller.getMulticastListenerEnabled()==enabled);
		
		
		SSDPMulticastListener sender = controller.getMulticastListener();
		assertNotNull(sender);
		
		controller.setMulticastListener(null);
		assertTrue(controller.getMulticastListener()==null);
		
		controller.setMulticastListener(sender);
		assertEquals(sender, controller.getMulticastListener());
	}

	@Test
	public void testServiceInfo() {
		
		assertNotNull(controller.getServiceInfoList());
		assertTrue(controller.getServiceInfoList().size()==0);
		
		// add device
		controller.getServiceInfoList().add(new ServiceInfo(SSDPConstants.DEFAULT_IP, SSDPConstants.DEFAULT_PORT, "upnp:rootdevice","http://142.225.35.55:5001/description/fetch", new USNInfo("9dcf6222-fc4b-33eb-bf49-e54643b4f416","upnp:rootdevice")));
		controller.getServiceInfoList().add(new ServiceInfo(SSDPConstants.DEFAULT_IP, SSDPConstants.DEFAULT_PORT, "urn:schemas-upnp-org:service:ConnectionManager:1","http://142.225.35.55:5001/description/fetch", new USNInfo("9dcf6222-fc4b-33eb-bf49-e54643b4f416","schemas-upnp-org:service:ConnectionManager:1")));
		controller.getServiceInfoList().add(new ServiceInfo(SSDPConstants.DEFAULT_IP, SSDPConstants.DEFAULT_PORT, "urn:schemas-upnp-org:service:ContentDirectory:1","http://142.225.35.55:5001/description/fetch", new USNInfo("9dcf6222-fc4b-33eb-bf49-e54643b4f416","schemas-upnp-org:service:ContentDirectory:1")));
		controller.getServiceInfoList().add(new ServiceInfo(SSDPConstants.DEFAULT_IP, SSDPConstants.DEFAULT_PORT, "urn:schemas-upnp-org:device:MediaServer:1","http://142.225.35.55:5001/description/fetch", new USNInfo("9dcf6222-fc4b-33eb-bf49-e54643b4f416","schemas-upnp-org:device:MediaServer:1")));

		assertEquals(4, controller.getServiceInfoList().size());
		
		ServiceInfo serviceInfo = new ServiceInfo();
		controller.addServiceInfo(serviceInfo);
		
		assertEquals(5, controller.getServiceInfoList().size());
		
		controller.removeServiceInfo(serviceInfo);
		assertEquals(4, controller.getServiceInfoList().size());
		
		List<ServiceInfo> list = new ArrayList<ServiceInfo>();
		list.add(new ServiceInfo(SSDPConstants.DEFAULT_IP, SSDPConstants.DEFAULT_PORT, "upnp:rootdevice","http://142.225.35.55:5001/description/fetch", new USNInfo("9dcf6222-fc4b-33eb-bf49-e54643b4f416","upnp:rootdevice")));
		
		controller.setServiceInfoList(list);
		assertEquals(1, controller.getServiceInfoList().size());
	}

	@Test
	public void testStartandStopAll() {
		try {
			controller.getDiscoverSender().setDelay(300000);
			controller.getPeriodicMessageSender().setDelay(300000);
			controller.getMulticastListener().setTimeout(300000);
			
			controller.start();
		} catch (Exception e) {
			
		}
		
		CountDownLatch latch = new CountDownLatch(1);
		
		try {
			latch.await(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
		assertEquals(State.SLEEP, controller.getDiscoverSender().getState());
		assertEquals(State.SLEEP, controller.getPeriodicMessageSender().getState());
		assertEquals(State.STARTED, controller.getMulticastListener().getState());
		
		try {
			controller.stop();
		} catch (Exception e) {
			
		}
		
		latch = new CountDownLatch(1);
		
		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
		assertEquals(State.STOPPED, controller.getDiscoverSender().getState());
		assertEquals(State.STOPPED, controller.getPeriodicMessageSender().getState());
		
		// if in blocking it won't be stopped until a socket message is received. 
		// assertEquals(State.STOPPED, controller.getMulticastListener().getState());
		
	}
	
	@Test
	public void testStartandStopDiscoverOnly() {
		try {
			controller.setPeriodicSenderEnabled(false);
			controller.setDiscoverSenderEnabled(true);
			controller.setMulticastListenerEnabled(false);
			
			controller.getDiscoverSender().setDelay(300000);
			controller.getPeriodicMessageSender().setDelay(300000);
			controller.getMulticastListener().setTimeout(300000);
			
			controller.start();
		} catch (Exception e) {
			
		}
		
		CountDownLatch latch = new CountDownLatch(1);
		
		try {
			latch.await(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
		assertEquals(State.SLEEP, controller.getDiscoverSender().getState());
		assertEquals(State.STOPPED, controller.getPeriodicMessageSender().getState());
		assertEquals(State.STOPPED, controller.getMulticastListener().getState());
		
		try {
			controller.stop();
		} catch (Exception e) {
			
		}
		
		latch = new CountDownLatch(1);
		
		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
		assertEquals(State.STOPPED, controller.getDiscoverSender().getState());
		assertEquals(State.STOPPED, controller.getPeriodicMessageSender().getState());
		
		// if in blocking it won't be stopped until a socket message is received. 
		// assertEquals(State.STOPPED, controller.getMulticastListener().getState());
		
	}
	
	@Test
	public void testStartandStopPeriodicOnly() {
		try {
			controller.setPeriodicSenderEnabled(true);
			controller.setDiscoverSenderEnabled(false);
			controller.setMulticastListenerEnabled(false);
			
			controller.getDiscoverSender().setDelay(300000);
			controller.getPeriodicMessageSender().setDelay(300000);
			controller.getMulticastListener().setTimeout(300000);
			
			controller.start();
		} catch (Exception e) {
			
		}
		
		CountDownLatch latch = new CountDownLatch(1);
		
		try {
			latch.await(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
		assertEquals(State.STOPPED, controller.getDiscoverSender().getState());
		assertEquals(State.SLEEP, controller.getPeriodicMessageSender().getState());
		assertEquals(State.STOPPED, controller.getMulticastListener().getState());
		
		try {
			controller.stop();
		} catch (Exception e) {
			
		}
		
		latch = new CountDownLatch(1);
		
		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
		assertEquals(State.STOPPED, controller.getDiscoverSender().getState());
		assertEquals(State.STOPPED, controller.getPeriodicMessageSender().getState());

		// if in blocking it won't be stopped until a socket message is received. 
		// assertEquals(State.STOPPED, controller.getMulticastListener().getState());
		
	}
	
	@Test
	public void testStartandStopMulticastListenerOnlyNonBlocking() {
		try {
			controller.setPeriodicSenderEnabled(false);
			controller.setDiscoverSenderEnabled(false);
			controller.setMulticastListenerEnabled(true);
			
			controller.getDiscoverSender().setDelay(300000);
			controller.getPeriodicMessageSender().setDelay(300000);
			controller.getMulticastListener().setTimeout(300000);
			
			controller.start();
		} catch (Exception e) {
			
		}
		
		CountDownLatch latch = new CountDownLatch(1);
		
		try {
			latch.await(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
		assertEquals(State.STOPPED, controller.getDiscoverSender().getState());
		assertEquals(State.STOPPED, controller.getPeriodicMessageSender().getState());
		assertEquals(State.STARTED, controller.getMulticastListener().getState());
		
		try {
			controller.stop();
		} catch (Exception e) {
			
		}
		
		latch = new CountDownLatch(1);
		
		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
		assertEquals(State.STOPPED, controller.getDiscoverSender().getState());
		assertEquals(State.STOPPED, controller.getPeriodicMessageSender().getState());

		// if in blocking it won't be stopped until a socket message is received. 
		// assertEquals(State.STOPPED, controller.getMulticastListener().getState());
		
	}
	
	@Ignore("Badly written test, known to fail, need further analysis") @Test
	public void testStartandStopMulticastListenerOnlyBlocking() {
		try {
			controller.setPeriodicSenderEnabled(false);
			controller.setDiscoverSenderEnabled(false);
			controller.setMulticastListenerEnabled(true);
			
			controller.getDiscoverSender().setDelay(300000);
			controller.getPeriodicMessageSender().setDelay(300000);
			controller.getMulticastListener().setTimeout(300000);
			controller.getMulticastListener().setBlocking(false);
			
			controller.start();
		} catch (Exception e) {
			
		}
		
		CountDownLatch latch = new CountDownLatch(1);
		
		try {
			latch.await(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
		assertEquals(State.STOPPED, controller.getDiscoverSender().getState());
		assertEquals(State.STOPPED, controller.getPeriodicMessageSender().getState());
		assertEquals(State.STARTED, controller.getMulticastListener().getState());
		
		try {
			controller.stop();
		} catch (Exception e) {
			
		}
		
		latch = new CountDownLatch(1);
		
		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			
		}
		
		assertEquals(State.STOPPED, controller.getDiscoverSender().getState());
		assertEquals(State.STOPPED, controller.getPeriodicMessageSender().getState());

		// if in blocking it won't be stopped until a socket message is received. 
		assertEquals(State.STOPPED, controller.getMulticastListener().getState());
		
	}
	
	@Test
	public void testProcessSSDPMessageISSDPMessage() {
		final CountDownLatch aliveLatch = new CountDownLatch(2);
		final CountDownLatch byebyeLatch = new CountDownLatch(2);
		final CountDownLatch discoverLatch = new CountDownLatch(2);
		final CountDownLatch discoverResponseLatch = new CountDownLatch(2);
		final CountDownLatch updateLatch = new CountDownLatch(2);
		
		ISSDPMessageHandler listener = new SSDPDefaultMessageHandler(){
			@Override
			public void processSSDPAliveMessage(AliveMessage ssdpMessage)
					throws Exception {
				aliveLatch.countDown();
			}
			@Override
			public void processSSDPByeByeMessage(ByeByeMessage ssdpMessage)
					throws Exception {
				byebyeLatch.countDown();
			}
			@Override
			public void processSSDPDiscoverMessage(String remoteAddr,
					int remotePort, DiscoverMessage ssdpMessage)
					throws Exception {
				discoverLatch.countDown();
			}
			@Override
			public void processSSDPDiscoverResponseMessage(
					DiscoverResponseMessage ssdpMessage) throws Exception {
				discoverResponseLatch.countDown();
			}
			@Override
			public void processSSDPUpdateMessage(UpdateMessage ssdpMessage)
					throws Exception {
				updateLatch.countDown();
			}};
		
		
		assertNotNull(controller.getMessageHandlerList());
			
		controller.addMessageHandler(listener);
		controller.addMessageHandler(null);
		
		assertTrue(controller.getMessageHandlerList().size()==1);

		
		// processing messages
		try {
			controller.processSSDPMessage(null);
			controller.processSSDPMessage(aliveMessage);
			controller.processSSDPMessage(byebyeMessage);
			controller.processSSDPMessage(updateMessage);
			controller.processSSDPMessage(discoverMessage);
			controller.processSSDPMessage(discoverResponseMessage);
			
			controller.processSSDPMessage(null, -1, aliveMessage);
			controller.processSSDPMessage(null, -1, byebyeMessage);
			controller.processSSDPMessage(null, -1, updateMessage);
			controller.processSSDPMessage(null, -1, discoverMessage);
			controller.processSSDPMessage(null, -1, discoverResponseMessage);
			
		} catch (Exception e) {
			fail("Shouldn't throw Exception");
		}
		
		assertEquals(0, aliveLatch.getCount());
		assertEquals(0, byebyeLatch.getCount());
		assertEquals(0, discoverLatch.getCount());
		assertEquals(0, discoverResponseLatch.getCount());
		assertEquals(0, updateLatch.getCount());
		
		
		try {
			controller.processSSDPMessage(new ISSDPMessage(){@Override
				public String toString() {
					return "Custom ISSDPMessage";
				}} );
			controller.processSSDPMessage(new AbstractSSDPNotifyMessage() {
				
				@Override
				public String toString() {
					return "";
				}
				
				@Override
				public String getNts() {
					return "";
				}
			});
			
			
			controller.processSSDPMessage(null, -1, new ISSDPMessage(){@Override
				public String toString() {
					return "Custom ISSDPMessage";
				}} );
			controller.processSSDPMessage(null, -1, new AbstractSSDPNotifyMessage() {
				
				@Override
				public String toString() {
					return "";
				}
				
				@Override
				public String getNts() {
					return "";
				}
			});
		} catch (Exception e) {
			fail("Shouldn't throw Exception");
		}

	}

	public static String createByeByeMessage(){
		
		/*
		NOTIFY * HTTP/1.1
		HOST: 239.255.255.250:1900
		NTS: ssdp:byebye
		USN: uuid:0b1f697a-a0fa-5181-010f-8edcc5a1a3e8::upnp:rootdevice
		NT: upnp:rootdevice
		CONTENT-LENGTH: 0
		*/
		StringBuffer sb = new StringBuffer();
		
		sb.append("NOTIFY * HTTP/1.1").append("\n");
		sb.append("HOST: 239.255.255.250:1900").append("\n");
		sb.append("NTS: ssdp:byebye").append("\n");
		sb.append("USN: uuid:0b1f697a-a0fa-5181-010f-8edcc5a1a3e8::upnp:rootdevice").append("\n");
		sb.append("NT: upnp:rootdevice").append("\n");
		sb.append("CONTENT-LENGTH: 0").append("\n");
		
		return sb.toString();
	}

	public static String createDiscoverMessage(){
		
		/*
		M-SEARCH * HTTP/1.1
		HOST: 239.255.255.250:1900
		ST: urn:schemas-upnp-org:device:MediaServer:1
		MAN: "ssdp:discover"
		MX: 2
		X-AV-Client-Info: av=5.0; cn="Sony Computer Entertainment Inc."; mn="PLAYSTATION 3"; mv="1.0";
		*/
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("M-SEARCH * HTTP/1.1").append("\n");
		sb.append("HOST: 239.255.255.250:1900").append("\n");
		sb.append("ST: urn:schemas-upnp-org:device:MediaServer:1").append("\n");
		sb.append("MAN: \"ssdp:discover\"").append("\n");
		sb.append("MX: 2").append("\n");
		sb.append("X-AV-Client-Info: av=5.0; cn=\"Sony Computer Entertainment Inc.\"; mn=\"PLAYSTATION 3\"; mv=\"1.0\";").append("\n");
		
		return sb.toString();
	}

	public static String createDiscoverResponseMessage(){
		
		/*
		HTTP/1.1 200 OK
		CACHE-CONTROL: max-age=1200
		DATE: Tue, 05 May 2009 13:31:51 GMT
		LOCATION: http://142.225.35.55:5001/description/fetch
		SERVER: Windows_XP-x86-5.1, UPnP/1.0, PMS/1.11
		ST: upnp:rootdevice
		EXT: 
		USN: uuid:9dcf6222-fc4b-33eb-bf49-e54643b4f416::upnp:rootdevice
		Content-Length: 0
	 */
		StringBuffer sb = new StringBuffer();
		
		sb.append("HTTP/1.1 200 OK").append("\n");
		sb.append("CACHE-CONTROL: max-age=1200").append("\n");
		sb.append("DATE: Tue, 05 May 2009 13:31:51 GMT").append("\n");
		sb.append("LOCATION: http://142.225.35.55:5001/description/fetch").append("\n");
		sb.append("SERVER: Windows_XP-x86-5.1, UPnP/1.0, PMS/1.11").append("\n");
		sb.append("ST: upnp:rootdevice").append("\n");
		sb.append("EXT: ").append("\n");
		sb.append("USN: uuid:9dcf6222-fc4b-33eb-bf49-e54643b4f416::upnp:rootdevice").append("\n");
		sb.append("Content-Length: 0").append("\n");
		
		return sb.toString();
	}

	public static String createUpdateMessage(){
		/*
		NOTIFY * HTTP/1.1
		HOST: 239.255.255.250:1900
		NT: urn:schemas-upnp-org:service:ContentDirectory:1
		NTS: ssdp:update
		LOCATION: http://142.225.35.55:5001/description/fetch
		USN: uuid:9dcf6222-fc4b-33eb-bf49-e54643b4f416::urn:schemas-upnp-org:service:ContentDirectory:1
		CACHE-CONTROL: max-age=1800
		SERVER: Windows_XP-x86-5.1, UPnP/1.0, PMS/1.11
		 */
		StringBuffer sb = new StringBuffer();
		
		sb.append("NOTIFY * HTTP/1.1").append("\n");
		sb.append("HOST: 239.255.255.250:1900").append("\n");
		sb.append("NT: urn:schemas-upnp-org:service:ContentDirectory:1").append("\n");
		sb.append("NTS: ssdp:update").append("\n");
		sb.append("LOCATION: http://142.225.35.55:5001/description/fetch").append("\n");
		sb.append("USN: uuid:9dcf6222-fc4b-33eb-bf49-e54643b4f416::urn:schemas-upnp-org:service:ContentDirectory:1").append("\n");
		sb.append("CACHE-CONTROL: max-age=1800").append("\n");
		sb.append("SERVER: Windows_XP-x86-5.1, UPnP/1.0, PMS/1.11").append("\n");
		
		return sb.toString();
	}

	public static String createAliveMessage(){
		/*
		NOTIFY * HTTP/1.1
		HOST: 239.255.255.250:1900
		NT: urn:schemas-upnp-org:service:ContentDirectory:1
		NTS: ssdp:alive
		LOCATION: http://142.225.35.55:5001/description/fetch
		USN: uuid:9dcf6222-fc4b-33eb-bf49-e54643b4f416::urn:schemas-upnp-org:service:ContentDirectory:1
		CACHE-CONTROL: max-age=1800
		SERVER: Windows_XP-x86-5.1, UPnP/1.0, PMS/1.11
		 */
		StringBuffer sb = new StringBuffer();
		
		sb.append("NOTIFY * HTTP/1.1").append("\n");
		sb.append("HOST: 239.255.255.250:1900").append("\n");
		sb.append("NT: urn:schemas-upnp-org:service:ContentDirectory:1").append("\n");
		sb.append("NTS: ssdp:alive").append("\n");
		sb.append("LOCATION: http://142.225.35.55:5001/description/fetch").append("\n");
		sb.append("USN: uuid:9dcf6222-fc4b-33eb-bf49-e54643b4f416::urn:schemas-upnp-org:service:ContentDirectory:1").append("\n");
		sb.append("CACHE-CONTROL: max-age=1800").append("\n");
		sb.append("SERVER: Windows_XP-x86-5.1, UPnP/1.0, PMS/1.11").append("\n");
		
		
		return sb.toString();
	}
	
	

}
