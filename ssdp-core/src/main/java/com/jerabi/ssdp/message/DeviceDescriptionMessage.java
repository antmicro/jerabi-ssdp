package com.jerabi.ssdp.message;

/**
 * A simple interface that is meant to gather messages that contain 
 * device identifiers and descriptors. E.g: LOCATION, USN, etc. fields. 
 * 
 * Its purpose is to unify the handling of such messages.
 * @author lkucharski@antmicro.com
 *
 */
public interface DeviceDescriptionMessage {

	/**
	 * Returns the URL for retrieving more information about the device.  
	 * The device most respond to this URL.
	 * 
	 * @return descriptor location
	 */
	String getLocation();
	String getUsn();
}
