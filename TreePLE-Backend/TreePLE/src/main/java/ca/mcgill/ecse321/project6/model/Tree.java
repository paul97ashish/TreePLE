/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.26.1-f40f105-3613 modeling language!*/

package ca.mcgill.ecse321.project6.model;
import java.util.*;
import java.sql.Date;

// line 58 "../../../../../treeple.ump"
public class Tree
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum LandType { Residential, Institutional, Park, Municipal }
  public enum Municipality { AhuntsicCartierville, Anjou, CoteDesNeigesNotreDamedeGrace, LIleBizardSainteGenevieve, LaSalle, Lachine, LePlateauMontRoyal, SudOuest, MercierHochelagaMaisonneuve, MontrealNord, Outremont, PierrefondsRoxboro, RivieredesPrairiesPointeauxTrembles, RosemontLaPetitePatrie, SaintLaurent, SaintLeonard, Verdun, VilleMarie, VilleraySaintMichelParcExtension, BaiedUrfe, Beaconsfield, CoteSaintLuc, DollardDesOrmeaux, Dorval, Hampstead, Kirkland, MontrealEst, MontrealOuest, MontRoyal, PointeClaire, SainteAnnedeBellevue, Senneville, Westmount }

  //------------------------
  // STATIC VARIABLES
  //------------------------

  public static int nextId = 1;

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Tree Attributes
  private double latitude;
  private double longitude;
  private double heightMeters;
  private double canopyDiameterMeters;
  private String species;
  private Municipality municipality;
  private LandType land;
  private Comparator<TreeHistory> entriesPriority;

  //Autounique Attributes
  private int id;

  //Tree Associations
  private TreeState state;
  private List<TreeHistory> entries;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Tree(double aLatitude, double aLongitude, double aHeightMeters, double aCanopyDiameterMeters, String aSpecies, Municipality aMunicipality, LandType aLand, TreeState aState)
  {
    latitude = aLatitude;
    longitude = aLongitude;
    heightMeters = aHeightMeters;
    canopyDiameterMeters = aCanopyDiameterMeters;
    species = aSpecies;
    municipality = aMunicipality;
    land = aLand;
    entriesPriority = 
      new Comparator<TreeHistory>(){
        @Override
        public int compare(TreeHistory arg0, TreeHistory arg1)
        {
          return ((Integer)arg0.getId()).compareTo(
                 ((Integer)arg1.getId()));
        }
      };
    id = nextId++;
    if (!setState(aState))
    {
      throw new RuntimeException("Unable to create Tree due to aState");
    }
    entries = new ArrayList<TreeHistory>();
  }

  //------------------------
  // INTERFACE
  //------------------------

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

  public boolean setSpecies(String aSpecies)
  {
    boolean wasSet = false;
    species = aSpecies;
    wasSet = true;
    return wasSet;
  }

  public boolean setMunicipality(Municipality aMunicipality)
  {
    boolean wasSet = false;
    municipality = aMunicipality;
    wasSet = true;
    return wasSet;
  }

  public boolean setLand(LandType aLand)
  {
    boolean wasSet = false;
    land = aLand;
    wasSet = true;
    return wasSet;
  }

  public boolean setEntriesPriority(Comparator<TreeHistory> aEntriesPriority)
  {
    boolean wasSet = false;
    entriesPriority = aEntriesPriority;
    wasSet = true;
    return wasSet;
  }

  public double getLatitude()
  {
    return latitude;
  }

  public double getLongitude()
  {
    return longitude;
  }

  public double getHeightMeters()
  {
    return heightMeters;
  }

  public double getCanopyDiameterMeters()
  {
    return canopyDiameterMeters;
  }

  public String getSpecies()
  {
    return species;
  }

  public Municipality getMunicipality()
  {
    return municipality;
  }

  public LandType getLand()
  {
    return land;
  }

  public Comparator<TreeHistory> getEntriesPriority()
  {
    return entriesPriority;
  }

  public int getId()
  {
    return id;
  }

  public TreeState getState()
  {
    return state;
  }

  public TreeHistory getEntry(int index)
  {
    TreeHistory aEntry = entries.get(index);
    return aEntry;
  }

  public List<TreeHistory> getEntries()
  {
    List<TreeHistory> newEntries = Collections.unmodifiableList(entries);
    return newEntries;
  }

  public int numberOfEntries()
  {
    int number = entries.size();
    return number;
  }

  public boolean hasEntries()
  {
    boolean has = entries.size() > 0;
    return has;
  }

  public int indexOfEntry(TreeHistory aEntry)
  {
    int index = entries.indexOf(aEntry);
    return index;
  }

  public boolean setState(TreeState aNewState)
  {
    boolean wasSet = false;
    if (aNewState != null)
    {
      state = aNewState;
      wasSet = true;
    }
    return wasSet;
  }

  public static int minimumNumberOfEntries()
  {
    return 0;
  }

  public boolean addEntry(TreeHistory aEntry)
  {
    boolean wasAdded = false;
    if (entries.contains(aEntry)) { return false; }
    entries.add(aEntry);
    wasAdded = true;
    if(wasAdded)
        Collections.sort(entries, entriesPriority);
    
    return wasAdded;
  }

  public boolean removeEntry(TreeHistory aEntry)
  {
    boolean wasRemoved = false;
    if (entries.contains(aEntry))
    {
      entries.remove(aEntry);
      wasRemoved = true;
    }
    return wasRemoved;
  }


  public void delete()
  {
    state = null;
    entries.clear();
  }


  public String toString()
  {
    return super.toString() + "["+
            "id" + ":" + getId()+ "," +
            "latitude" + ":" + getLatitude()+ "," +
            "longitude" + ":" + getLongitude()+ "," +
            "heightMeters" + ":" + getHeightMeters()+ "," +
            "canopyDiameterMeters" + ":" + getCanopyDiameterMeters()+ "," +
            "species" + ":" + getSpecies()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "municipality" + "=" + (getMunicipality() != null ? !getMunicipality().equals(this)  ? getMunicipality().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "land" + "=" + (getLand() != null ? !getLand().equals(this)  ? getLand().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "entriesPriority" + "=" + (getEntriesPriority() != null ? !getEntriesPriority().equals(this)  ? getEntriesPriority().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "state = "+(getState()!=null?Integer.toHexString(System.identityHashCode(getState())):"null");
  }
}