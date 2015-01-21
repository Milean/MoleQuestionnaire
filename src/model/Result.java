package model;

import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A Result will contain the results of one Questionnaire entered by one Player.
 * Besides the answers to the questions, the Result-class will also carry 
 * a rating, as imposed by a GameMaster. Important is the time taken to enter the questionnaire, as it is a tiebreaker.
 * Results need to refer back to the player they belong to, so the results can be set to all correct if the player is the mole.
 * Results also do need to refer to the Round they were entered in, so the weights for the questions can be found.
 * @author Jeff
 *
 */
public class Result implements Comparable<Result>, Serializable {
	/**
	 * It is required that the variables answers and rating are of equal length. 
	 * Even more so, it is required that they are of the same length as the Questionnaire has a number of questions.
	 */
	private List<String> answers;
	private List<Boolean> rating; //null-value means unrated.
	private Player player;
	private Round round;
	private Date start;
	private Date end;
	private int jokers; //The amount of jokers used by this player in this round
	private HashMap<Player,Integer> rekojs; //The amount of rekojs used on this player in this round. Requires usage of Integer for the Map.
	
	public Result(Player player, Round round) {
		this.answers = new ArrayList<String>();
		this.jokers = 0;
		this.rekojs = new HashMap<Player,Integer>();
		this.player = player;
		this.round = round;
		this.rating = new ArrayList<Boolean>();
	}

	/*
	 * Getters and setters
	 */
	
	public List<Boolean> getRating() {
		return this.rating;
	}

	public void setRating(List<Boolean> rating) {
		this.rating = rating;
		if(this.player.isMole()) { //If the result belongs to the mole, everything is correct!
			this.rating.clear();
			for(Boolean val : rating) {
				this.rating.add(new Boolean(true));
			}
		}
	}

	public List<String> getAnswers() {
		return this.answers;
	}
	
	public void setAnswers(List<String> answers) {
		this.answers = answers;
		//Set ratings to all 'null' values.
		this.rating.clear();
		for(String answer : answers) {
			this.rating.add(null);
		}
	}

	public Date getStart() {
		return this.start;
	}

	public Date getEnd() {
		return this.end;
	}

	public int getJokers() {
		return this.jokers;
	}

	public void setJokers(Integer amount) {
		this.jokers = amount;
	}
	
	/*
	 * Delegates and functionals
	 */
		
	public Date startNow() {
		this.start = new Date();
		return getStart();
	}
	
	public Date endNow() {
		this.end = new Date();
		return getEnd();
	}
	
	/**
	 * This method returns the amount taken to get to the result.
	 * @return 	The time taken iff both start and end are known
	 * 			The busy time iff only the start is known
	 * 			0 iff start is unknown.
	 */
	public long duration() {
		if(this.start != null) {
			if(this.end != null) {
				return this.end.getTime() - this.start.getTime();
			}
			return (new Date()).getTime() - this.start.getTime();
		}
		return 0;
	}
	
	/**
	 * Set an amount of rekojs to be used against this result
	 * @param player - the player playing the rekojs
	 * @param amount - the amount of rekojs being played
	 * @return the new amount of rekojs.
	 */
	public Integer setRekojs(Player player, Integer amount) {
		this.rekojs.put(player, amount);
		return amount; //Note that this now represents the new amount!
	}
	
	public HashMap<Player,Integer> getRekojs() {
		return this.rekojs;
	}
	
	/**
	 * Gives the score for this result, taken by the amount of correct questions 
	 * plus the amount of jokers used, minus the amount of rekojs used
	 * Note that if this player is the mole, the score will always be equal to the amount of questions asked.
	 * @return the score for this result
	 */
	public int score() {
		int score = 0;
		if(this.rating != null) {
			for(int i = 0;i<this.rating.size();i++) {
				boolean correct = this.player.isMole() 
				|| this.rating.get(i) != null && this.rating.get(i).equals(new Boolean(true));
				Boolean isCorrect = new Boolean(correct);
				if(isCorrect != null && isCorrect.booleanValue()) {
					score += this.round.getQuestionnaire().getWeight(i).intValue();
				}
			}
		}
		score += jokers;
		for(Integer numRekojs:rekojs.values()){
			score -= numRekojs.intValue();
		}
		return score;
	}
	
	/**
	 * Comparable methods
	 */
	
	public int compareTo(Result other) {
		Result rOther = (Result) other;
		if(this.player.isMole()) {
			return 1;
		}
		if(rOther.player.isMole()) {
			return -1;
		}
		if(this.score() - rOther.score() == 0) {
			//use time!
			return (int) (rOther.duration() - this.duration());
		}
		return this.score() - rOther.score();
	}

	public Player getPlayer() {
		// TODO Auto-generated method stub
		return this.player;
	}
	
}
