package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The Player-class will be the place for to keep a name and possible 
 * some other information about the individual players, including GameMasters
 * This class will also hold the Results for a player. 
 * @author Jeff
 *
 */
public class Player implements Serializable {
	private String name;
	private Map<Round,Result> results;
	private Status status;

	public Player(String name, Status status) {
		this.name = name;
		this.status = status;
		results = new HashMap<Round,Result>();
	}
	
	/*
	 * Getters and setters
	 */
	
	public String getName() {
		return this.name;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/*
	 * Delegates and functionals
	 */
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setResult(Round round, Result result) {
		this.results.put(round, result);
	}
	
	public Result getResult(Round round) {
		if(this.results.containsKey(round)) {
			return this.results.get(round);
		}
		return null;
	}
	
	public boolean isMole() {
		return this.status == Status.MOLE;
	}

	
	/*
	 * Obligatory functions, auto-implemented by Eclipse
	 */
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Player)) {
			return false;
		}
		Player other = (Player) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (!status.equals(other.status)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	
}