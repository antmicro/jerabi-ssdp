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
import com.jerabi.ssdp.util.SSDPConstants;

public class SSDPControlPoint {
	private static final Logger s_logger = Logger.getLogger(SSDPServer.class.getName());
	private static Handler logHandle = null;
	public SSDPControler controler = null;
		
	public void start() throws Exception {
		// will dispatch the responses from the handlers to the listener
		logHandle = new FileHandler("log");
		s_logger.addHandler(logHandle);
		s_logger.setLevel(Level.FINE);
		s_logger.fine("Creating controller");
		controler = new SSDPControler();
		controler.setPeriodicSenderEnabled(false);
		controler.setDiscoverSenderEnabled(false);
		controler.setMulticastListenerEnabled(true);
		
		controler.addMessageHandler(new SSDPDefaultMessageHandler());
		controler.getMulticastListener().setSSDPResponseHandler(new SSDPDefaultResponseHandler(controler));
		controler.getDiscoverSender().setSSDPResponseHandler(new SSDPDiscoverResponseHandler(controler));
		
		s_logger.fine("Starting controller");
    	controler.start();
	}
	
	public void stop() throws Exception {
		controler.stop();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		SSDPControlPoint server = new SSDPControlPoint();
    	server.start();
		
	}
}
