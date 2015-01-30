package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Round is one single round in the game and is to be used for practical manners.
 * A Round needs to have a round-number and an amount of players; besides having a list of players.
 * Checking results for a certain round can be done by invoking the Players.
 * A Round has exactly 1 Questionnaire.
 * @author Jeff
 *
 */
public class Round implements Comparable<Object>, Serializable {
	private static final long serialVersionUID = 690037075919438118L;
	private int roundNumber; 
	private int numPlayers;
	private List<Player> players;
	private Questionnaire questionnaire;
	private boolean finished;
	
	public Round(int roundNumber, int numPlayers){
		this.roundNumber = roundNumber;
		this.numPlayers = numPlayers;
		this.players = new ArrayList<Player>();
		this.questionnaire = new Questionnaire();
		this.finished = false;
	}

	/*
	 * Getters and setters
	 */
	
	public int getRoundNumber() {
		return this.roundNumber;
	}

	public void setRoundNumber(int roundNumber) {
		this.roundNumber = roundNumber;
	}

	public int getNumPlayers() {
		return this.numPlayers;
	}

	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}

	public List<Player> getPlayers() {
		return this.players;
	}

	public Questionnaire getQuestionnaire() {
		return this.questionnaire;
	}
	
	public void finish() {
		this.finished = true;
	}
	
	public boolean isFinished() {
		return this.finished;
	}
	
	public int compareTo(Object other) {
		if(other instanceof Round) {
			Round rOther = (Round) other;
			if(this.roundNumber - rOther.roundNumber == 0) {
				return this.numPlayers - rOther.numPlayers;
			}
			return this.roundNumber - rOther.roundNumber;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numPlayers;
		result = prime * result + roundNumber;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Round)) {
			return false;
		}
		Round other = (Round) obj;
		if (numPlayers != other.numPlayers) {
			return false;
		}
		if (roundNumber != other.roundNumber) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ronde " + roundNumber + "; " + numPlayers + " spelers";
	}

	public void setPlayers(ArrayList<Player> players2) {
		this.players = players2;		
	}

	public void setQuestionnaire(Questionnaire quest) {
		this.questionnaire = quest;		
	}
	
	
	
}
