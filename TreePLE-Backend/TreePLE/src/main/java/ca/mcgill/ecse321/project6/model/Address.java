/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.26.1-f40f105-3613 modeling language!*/

package ca.mcgill.ecse321.project6.model;

// line 81 "../../../../../treeple.ump"
public class Address
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Address Attributes
  private int streetNumber;
  private String streetName;
  private String postalCode;
  private double latitude;
  private double longitude;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Address(int aStreetNumber, String aStreetName, String aPostalCode, double aLatitude, double aLongitude)
  {
    streetNumber = aStreetNumber;
    streetName = aStreetName;
    postalCode = aPostalCode;
    latitude = aLatitude;
    longitude = aLongitude;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setStreetNumber(int aStreetNumber)
  {
    boolean wasSet = false;
    streetNumber = aStreetNumber;
    wasSet = true;
    return wasSet;
  }

  public boolean setStreetName(String aStreetName)
  {
    boolean wasSet = false;
    streetName = aStreetName;
    wasSet = true;
    return wasSet;
  }

  public boolean setPostalCode(String aPostalCode)
  {
    boolean wasSet = false;
    postalCode = aPostalCode;
    wasSet = true;
    return wasSet;
  }

  public boolean setLatitude(double aLatitude)
  {
    boolean wasSet = false;
    latitude = aLatitude;
    wasSet = true;
    return wasSet;
  }

  public boolean setLongitude(double aLongitude)
  {
    boolean wasSet = false;
    longitude = aLongitude;
    wasSet = true;
    return wasSet;
  }

  public int getStreetNumber()
  {
    return streetNumber;
  }

  public String getStreetName()
  {
    return streetName;
  }

  public String getPostalCode()
  {
    return postalCode;
  }

  public double getLatitude()
  {
    return latitude;
  }

  public double getLongitude()
  {
    return longitude;
  }

  public void delete()
  {}

  // line 89 "../../../../../treeple.ump"
  public boolean equals(Object obj){
    if(obj.getClass() == this.getClass()) {
      Address a = (Address) obj;
      return (a.getStreetNumber() == streetNumber &&
        a.getStreetName().equals(streetName) &&
        a.getPostalCode().equals(postalCode) &&
        a.getLatitude() == latitude &&
        a.getLongitude() == longitude);
    }
    return false;
  }


  public String toString()
  {
    return super.toString() + "["+
            "streetNumber" + ":" + getStreetNumber()+ "," +
            "streetName" + ":" + getStreetName()+ "," +
            "postalCode" + ":" + getPostalCode()+ "," +
            "latitude" + ":" + getLatitude()+ "," +
            "longitude" + ":" + getLongitude()+ "]";
  }
}