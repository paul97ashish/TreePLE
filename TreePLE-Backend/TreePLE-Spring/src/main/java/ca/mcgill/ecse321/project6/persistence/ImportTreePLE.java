package ca.mcgill.ecse321.project6.persistence;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;

import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.TreeHistory;
import ca.mcgill.ecse321.project6.model.TreeState;
import ca.mcgill.ecse321.project6.model.User;

public class ImportTreePLE {
	
	private final static String nad83csrsparams = "+proj=tmerc +lat_0=0 +lon_0=-73.5 +k=0.9999 +x_0=304800 +y_0=0 +ellps=GRS80 +units=m +no_defs";
	// read data from tab separated CSV following MTL format
	public static List<Tree> importFromMtlCSV(String filename) throws IOException {
		List<Tree> list = new ArrayList<Tree>();
		BufferedReader reader = null;
		CRSFactory factory = new CRSFactory();
		CoordinateReferenceSystem nad83, world;
		BasicCoordinateTransform transform;
		nad83 = factory.createFromParameters("NAD83 (CSRS)", nad83csrsparams);
		world = factory.createFromName("EPSG:4326");

		transform = new BasicCoordinateTransform(nad83, world);
		try {
			reader = new BufferedReader(new FileReader(filename));
			reader.readLine(); //first line is just labels. safe to ignore.
			String line = null;
			User dummyUser = new User("Unknown", "dummyhash", User.UserType.Municipal);
			while((line = reader.readLine()) != null) {
				Tree t = null;
				String[] fields = line.split("\t");
				Tree.Municipality mun = getArrondissementByNumber(Integer.parseInt(fields[1]));
				Tree.LandType land;
				if(fields[21].length() > 0) {
					land = Tree.LandType.Park;
				} else if (fields[0] == "C") {
					land = Tree.LandType.Municipal;
				} else {
					land = Tree.LandType.Residential;
				}
				String species = fields[13];
				
				//try to estimate age
				String plantDate = "";
				if(fields[16].length() > 0) {
					plantDate = fields[16];	//recorded plant date
				} else {
					plantDate = fields[15];	//last update, at least this old
				}				
				
				double nad83x = Double.parseDouble(fields[8]);
				double nad83y = Double.parseDouble(fields[9]);
				ProjCoordinate nad83coord = new ProjCoordinate(nad83x, nad83y);
				ProjCoordinate lonlat = new ProjCoordinate();
				
				lonlat = transform.transform(nad83coord, lonlat);
				
				// currently no way to get height or diameter
				t = new Tree(lonlat.x, lonlat.y, 0, 0, species, mun, land, new TreeState());
				if(plantDate.length() > 0) {
					Date planted;
					SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
					try {
						planted = new Date(df.parse(plantDate).getTime());
						TreeHistory th = new TreeHistory(planted, 0, 0, dummyUser);
						t.addEntry(th);
					} catch (ParseException e) {
						// just keep going
					}

				}
				list.add(t);
			}
		} catch (FileNotFoundException e) {
			list.clear();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			list.clear();
			return list;
		} finally {
			if (reader != null) reader.close();
		}
		return list;
	}
	
	private static Tree.Municipality getArrondissementByNumber(int num) {
		Tree.Municipality m = null;
		switch(num) {
		case 1:
			m = Tree.Municipality.AhuntsicCartierville;
			break;
		case 2:
			m = Tree.Municipality.VilleraySaintMichelParcExtension;
			break;
		case 3:
			m = Tree.Municipality.RosemontLaPetitePatrie;
			break;
		case 4:
			m = Tree.Municipality.MercierHochelagaMaisonneuve;
			break;
		case 5:
			m = Tree.Municipality.LePlateauMontRoyal;
			break;
		case 6:
			m = Tree.Municipality.VilleMarie;
			break;
		case 7:
			m = Tree.Municipality.CoteDesNeigesNotreDamedeGrace;
			break;
		case 8:
			m = Tree.Municipality.SudOuest;
			break;
		case 9:
			m = Tree.Municipality.RivieredesPrairiesPointeauxTrembles;
			break;
		case 10:
			m = Tree.Municipality.Anjou;
			break;
		case 11:
			m = Tree.Municipality.MontrealNord;
			break;
		case 12:
			m = Tree.Municipality.SaintLeonard;
			break;
		case 13:
			m = Tree.Municipality.LaSalle;
			break;
		case 15:
			m = Tree.Municipality.Outremont;
			break;
		case 16:
			m = Tree.Municipality.Verdun;
			break;
		case 24:	// technically also includes Lachine
			m = Tree.Municipality.LIleBizardSainteGenevieve;
			break;
		case 25:
			m = Tree.Municipality.PierrefondsRoxboro;
			break;
		case 26:
			m = Tree.Municipality.SaintLaurent;
			break;
		}
		return m;
	}

}
