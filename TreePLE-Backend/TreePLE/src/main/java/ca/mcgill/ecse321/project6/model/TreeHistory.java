/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.26.1-f40f105-3613 modeling language!*/

package ca.mcgill.ecse321.project6.model;
import java.sql.Date;

// line 49 "../../../../../treeple.ump"
public class TreeHistory
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static int nextId = 1;

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TreeHistory Attributes
  private Date recordedOn;
  private double heightMeters;
  private double canopyDiameterMeters;

  //Autounique Attributes
  private int id;

  //TreeHistory Associations
  private User reporter;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TreeHistory(Date aRecordedOn, double aHeightMeters, double aCanopyDiameterMeters, User aReporter)
  {
    recordedOn = aRecordedOn;
    heightMeters = aHeightMeters;
    canopyDiameterMeters = aCanopyDiameterMeters;
    id = nextId++;
    if (!setReporter(aReporter))
    {
      throw new RuntimeException("Unable to create TreeHistory due to aReporter");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setRecordedOn(Date aRecordedOn)
  {
    boolean wasSet = false;
    recordedOn = aRecordedOn;
    wasSet = true;
    return wasSet;
  }

  public boolean setHeightMeters(double aHeightMeters)
  {
    boolean wasSet = false;
    heightMeters = aHeightMeters;
    wasSet = true;
    return wasSet;
  }

  public boolean setCanopyDiameterMeters(double aCanopyDiameterMeters)
  {
    boolean wasSet = false;
    canopyDiameterMeters = aCanopyDiameterMeters;
    wasSet = true;
    return wasSet;
  }

  public Date getRecordedOn()
  {
    return recordedOn;
  }

  public double getHeightMeters()
  {
    return heightMeters;
  }

  public double getCanopyDiameterMeters()
  {
    return canopyDiameterMeters;
  }

  public int getId()
  {
    return id;
  }

  public User getReporter()
  {
    return reporter;
  }

  public boolean setReporter(User aNewReporter)
  {
    boolean wasSet = false;
    if (aNewReporter != null)
    {
      reporter = aNewReporter;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    reporter = null;
  }


  public String toString()
  {
    return super.toString() + "["+
            "id" + ":" + getId()+ "," +
            "heightMeters" + ":" + getHeightMeters()+ "," +
            "canopyDiameterMeters" + ":" + getCanopyDiameterMeters()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "recordedOn" + "=" + (getRecordedOn() != null ? !getRecordedOn().equals(this)  ? getRecordedOn().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "reporter = "+(getReporter()!=null?Integer.toHexString(System.identityHashCode(getReporter())):"null");
  }
}