package ca.mcgill.ecse321.project6.dto;

import java.util.List;

import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.Tree.LandType;
import ca.mcgill.ecse321.project6.model.Tree.Municipality;
import ca.mcgill.ecse321.project6.model.TreeHistory;
import ca.mcgill.ecse321.project6.model.TreeState;

public class TreeDto {
	private int id;
	private double latitude;
	private double longitude;
	private double heightMeters;
	private double canopyDiameterMeters;
	private String species;
	private LandType land;
	private Municipality municipality;
	private List<TreeHistory> entries;
	private TreeState state;
	
	public TreeDto() {
	}
	
	public TreeDto(int id, double latitude, double longitude, double heightMeters,
			double canopyDiameterMeters, String species, LandType land, Municipality municipality,
			List<TreeHistory> entries, TreeState state) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.heightMeters = heightMeters;
		this.canopyDiameterMeters = canopyDiameterMeters;
		this.species = species;
		this.land = land;
		this.municipality = municipality;
		this.entries = entries;
		this.state = state;
	}
	
	public int getId() {
		return id;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public double getHeightMeters() {
		return heightMeters;
	}
	
	public double getCanopyDiameterMeters() {
		return canopyDiameterMeters;
	}
	
	public String getSpecies() {
		return species;
	}
	
	public LandType getLandType() {
		return land;
	}
	
	public Municipality getMunicipality() {
		return municipality;
	}
	
	public List<TreeHistory> getHistory() {
		return entries;
	}
	
	public TreeState getState() {
		return state;
	}
	
	public static TreeDto fromDomainObject(Tree t) {
		return new TreeDto(t.getId(), t.getLatitude(), t.getLongitude(), t.getHeightMeters(),
				t.getCanopyDiameterMeters(), t.getSpecies(), t.getLand(), t.getMunicipality(), t.getEntries(),
				t.getState());
	}
}
