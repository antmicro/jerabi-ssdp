package com.jerabi.ssdp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;
import org.junit.Assume;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jerabi.ssdp.util.UUIDGenerator;

public class UUIDGeneratorTest {
	private static NetworkInterface iface = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		iface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
		byte[] mac = iface.getHardwareAddress();
		Assume.assumeFalse("Could not obtain mac address for testing.", mac == null);
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetUUID() {
		
		Map<String,String> map = new HashMap<String,String>();
		
		for(int i=0;i<100;i++){
			String uuid = UUIDGenerator.getUUID();
			if(map.containsKey(uuid)){
				fail("UUID already exist");
			}
			map.put(uuid, uuid);
		}

	}
	
	@Test
	public void testGetUUIDWithMAC() {
		
		String initialUUID = null;
		
		try {
			initialUUID = UUIDGenerator.getUUID(iface);
		} catch (Exception e) {
			fail("Got getUUID exception: "+e.getMessage());
		}
		
		for(int i=0;i<100;i++){
			String uuid = null;
			try {
				uuid = UUIDGenerator.getUUID(iface);
			} catch (Exception e) {
				fail("Got getUUID exception: "+e.getMessage());
			}
			
			assertNotNull(uuid);
			assertEquals(initialUUID, uuid);
		}
		
	}
	
	@Test
	public void testGetUUIDWithNullNetworkInterface() {
		
		String initialUUID = null;
		
		try {
			initialUUID = UUIDGenerator.getUUID(null);
		} catch (Exception e) {
			fail("Should not throws Exception");
		}
		
		assertNull(initialUUID);
		
	}

}
