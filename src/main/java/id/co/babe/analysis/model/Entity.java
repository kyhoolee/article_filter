package id.co.babe.analysis.model;

public class Entity {
	public String name;
	public int occFreq;
	public int entityType;
	
	public Entity(String name, int occFreq, int entityType) {
		this.name = name;
		this.occFreq = occFreq;
		this.entityType = entityType;
	}

}
