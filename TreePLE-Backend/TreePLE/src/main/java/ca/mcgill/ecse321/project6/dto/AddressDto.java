package ca.mcgill.ecse321.project6.dto;

import ca.mcgill.ecse321.project6.model.Address;

public class AddressDto {
	private int streetNumber;
	private String streetName;
	private String postalCode;
	private double latitude;
	private double longitude;
	
	public AddressDto() {
	}
	
	public AddressDto(int streetNumber, String streetName, String postalCode, double latitude, double longitude) {
		this.streetNumber = streetNumber;
		this.streetName = streetName;
		this.postalCode = postalCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public int getStreetNumber() {
		return streetNumber;
	}
	
	public String getStreetName() {
		return streetName;
	}
	
	public String getPostalCode() {
		return postalCode;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public static AddressDto fromDomainObject(Address address) {
		return new AddressDto(address.getStreetNumber(), address.getStreetName(), address.getPostalCode(), address.getLatitude(), address.getLongitude());
	}
}
