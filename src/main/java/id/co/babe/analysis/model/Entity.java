package id.co.babe.analysis.model;

public class Entity {
	public static final int type_person = 1;
	public static final int type_orginization = 2;
	public static final int type_event = 3;
	public static final int type_place = 4;
	public static final int type_unknow = 0;
			
	
	
	
	public String name;
	public double occFreq;
	public int entityType;
	
	public Entity(String name, double occFreq, int entityType) {
		this.name = name;
		this.occFreq = occFreq;
		this.entityType = entityType;
	}

}
