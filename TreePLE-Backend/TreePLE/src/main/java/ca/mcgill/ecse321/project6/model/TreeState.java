/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.26.1-f40f105-3613 modeling language!*/

package ca.mcgill.ecse321.project6.model;

// line 101 "../../../../../treeple.ump"
public class TreeState
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum Status { Projected, Planted, CutDown }
  public enum Health { Healthy, Diseased, Dead }
  public enum Mark { MarkedToPlant, MarkedToCutDown, None }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TreeState Attributes
  private Status status;
  private Health health;
  private Mark mark;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TreeState()
  {
    resetStatus();
    resetHealth();
    resetMark();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setStatus(Status aStatus)
  {
    boolean wasSet = false;
    status = aStatus;
    wasSet = true;
    return wasSet;
  }

  public boolean resetStatus()
  {
    boolean wasReset = false;
    status = getDefaultStatus();
    wasReset = true;
    return wasReset;
  }

  public boolean setHealth(Health aHealth)
  {
    boolean wasSet = false;
    health = aHealth;
    wasSet = true;
    return wasSet;
  }

  public boolean resetHealth()
  {
    boolean wasReset = false;
    health = getDefaultHealth();
    wasReset = true;
    return wasReset;
  }

  public boolean setMark(Mark aMark)
  {
    boolean wasSet = false;
    mark = aMark;
    wasSet = true;
    return wasSet;
  }

  public boolean resetMark()
  {
    boolean wasReset = false;
    mark = getDefaultMark();
    wasReset = true;
    return wasReset;
  }

  public Status getStatus()
  {
    return status;
  }

  public Status getDefaultStatus()
  {
    return Status.Planted;
  }

  public Health getHealth()
  {
    return health;
  }

  public Health getDefaultHealth()
  {
    return Health.Healthy;
  }

  public Mark getMark()
  {
    return mark;
  }

  public Mark getDefaultMark()
  {
    return Mark.None;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "status" + "=" + (getStatus() != null ? !getStatus().equals(this)  ? getStatus().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "health" + "=" + (getHealth() != null ? !getHealth().equals(this)  ? getHealth().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "mark" + "=" + (getMark() != null ? !getMark().equals(this)  ? getMark().toString().replaceAll("  ","    ") : "this" : "null");
  }
}