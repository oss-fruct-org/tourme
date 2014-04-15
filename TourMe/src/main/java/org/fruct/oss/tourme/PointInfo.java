package org.fruct.oss.tourme;

/*
 * Small class to represent point information
 * Used both in YandexPoints module and WikiLocation
 */
public class PointInfo {
	// Both classes
	public String title;
	public String latitude;
	public String longitude;
    public String category;
	
	// WikiLocation specific
	public String type = null;	
	public String url = null;
	//public String mobileurl = null;
	public String distance = null;
	
	// Yandex specific
	public String info = null;	
}
