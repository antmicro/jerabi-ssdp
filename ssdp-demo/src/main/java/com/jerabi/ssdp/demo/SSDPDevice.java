package com.jerabi.ssdp.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jerabi.ssdp.SSDPControler;
import com.jerabi.ssdp.handler.SSDPDefaultResponseHandler;
import com.jerabi.ssdp.handler.SSDPDefaultMessageHandler;
import com.jerabi.ssdp.handler.SSDPDiscoverResponseHandler;
import com.jerabi.ssdp.message.AliveMessage;
import com.jerabi.ssdp.message.ServiceInfo;
import com.jerabi.ssdp.message.ISSDPMessage;
import com.jerabi.ssdp.message.USNInfo;
import com.jerabi.ssdp.message.helper.SSDPMessageHelper;
import com.jerabi.ssdp.sender.SSDPPeriodicMessageSender;
import com.jerabi.ssdp.util.SSDPContants;
import com.jerabi.ssdp.util.UUIDGenerator;

public class SSDPDevice {

	private static final String uuid = UUIDGenerator.getUUID();
	private static final String descriptionUri = "http://192.168.1.3/desc.xml";
	private static final Logger s_logger = Logger.getLogger(SSDPServer.class.getName());
	private static Handler logHandler = null;
	public SSDPControler controler = null;
	
	
	public void start() throws Exception {
		// will dispatch the responses from the handlers to the listener
		logHandler = new FileHandler("Device.log");
		logHandler.setLevel(Level.ALL);
		s_logger.addHandler(logHandler);
		s_logger.setLevel(Level.ALL);
		controler = new SSDPControler();
				
		controler.setPeriodicSenderEnabled(true);
		controler.setDiscoverSenderEnabled(false);
		controler.setMulticastListenerEnabled(false);
		controler.addMessageHandler(new SSDPDefaultMessageHandler());
		controler.getMulticastListener().setSSDPResponseHandler(new SSDPDefaultResponseHandler(controler));
		controler.getDiscoverSender().setSSDPResponseHandler(new SSDPDiscoverResponseHandler(controler));
		
		controler.setPeriodicMessageSender(new SSDPPeriodicMessageSender(controler, SSDPContants.DEFAULT_IP, SSDPContants.DEFAULT_PORT) {
			
			@Override
			public List<ISSDPMessage> getSSDPMessagesToSend() {
				List<ISSDPMessage> list = new ArrayList<ISSDPMessage>();
				
				for (ServiceInfo deviceInfo : controler.getServiceInfoList()) {
				
					AliveMessage message = SSDPMessageHelper.createSSDPAliveMessage(deviceInfo);
					
					message.setCacheControl("max-age=1800");
					message.setServer("Linux/3.16 UPnP/1.0 RootDeviceMock/0.1");
					
					list.add(message);
				}
				
				return list;
			}
		});
		
		controler.getPeriodicMessageSender().setDelay(30000);

		// add devices
		controler.getServiceInfoList().add(new ServiceInfo(SSDPContants.DEFAULT_IP, SSDPContants.DEFAULT_PORT, "upnp:rootdevice", descriptionUri, new USNInfo(uuid, "upnp:rootdevice")));
//		controler.getServiceInfoList().add(new ServiceInfo(SSDPContants.DEFAULT_IP, SSDPContants.DEFAULT_PORT, "urn:schemas-upnp-org:service:ConnectionManager:1", descriptionUri, new USNInfo("9dcf6222-fc4b-33eb-bf49-e54643b4f416","schemas-upnp-org:service:ConnectionManager:1")));
//		controler.getServiceInfoList().add(new ServiceInfo(SSDPContants.DEFAULT_IP, SSDPContants.DEFAULT_PORT, "urn:schemas-upnp-org:service:ContentDirectory:1", descriptionUri, new USNInfo("9dcf6222-fc4b-33eb-bf49-e54643b4f416","schemas-upnp-org:service:ContentDirectory:1")));
//		controler.getServiceInfoList().add(new ServiceInfo(SSDPContants.DEFAULT_IP, SSDPContants.DEFAULT_PORT, "urn:schemas-upnp-org:device:MediaServer:1", descriptionUri, new USNInfo("9dcf6222-fc4b-33eb-bf49-e54643b4f416","schemas-upnp-org:device:MediaServer:1")));

		
		
		s_logger.fine("Starting an SSDP Device");
		
    	controler.start();
	}
	
	public void stop() throws Exception {
		controler.stop();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		SSDPDevice server = new SSDPDevice();
    	server.start();
		
	}
}
