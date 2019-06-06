/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.26.1-f40f105-3613 modeling language!*/

package ca.mcgill.ecse321.project6.model;

// line 72 "../../../../../treeple.ump"
public class User
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum UserType { Resident, Municipal }

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static int nextId = 1;

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //User Attributes
  private String name;
  private String passwordHash;
  private UserType type;

  //Autounique Attributes
  private int id;

  //User Associations
  private Address address;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public User(String aName, String aPasswordHash, UserType aType)
  {
    name = aName;
    passwordHash = aPasswordHash;
    type = aType;
    id = nextId++;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setPasswordHash(String aPasswordHash)
  {
    boolean wasSet = false;
    passwordHash = aPasswordHash;
    wasSet = true;
    return wasSet;
  }

  public boolean setType(UserType aType)
  {
    boolean wasSet = false;
    type = aType;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public String getPasswordHash()
  {
    return passwordHash;
  }

  public UserType getType()
  {
    return type;
  }

  public int getId()
  {
    return id;
  }

  public Address getAddress()
  {
    return address;
  }

  public boolean hasAddress()
  {
    boolean has = address != null;
    return has;
  }

  public boolean setAddress(Address aNewAddress)
  {
    boolean wasSet = false;
    address = aNewAddress;
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    address = null;
  }


  public String toString()
  {
    return super.toString() + "["+
            "id" + ":" + getId()+ "," +
            "name" + ":" + getName()+ "," +
            "passwordHash" + ":" + getPasswordHash()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "type" + "=" + (getType() != null ? !getType().equals(this)  ? getType().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "address = "+(getAddress()!=null?Integer.toHexString(System.identityHashCode(getAddress())):"null");
  }
}