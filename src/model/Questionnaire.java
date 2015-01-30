package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Questionnaire class will hold anything necessary to generate a questionnaire to test our test-subjects,
 * namely: The questions and their weights. Therefor, it is basically just a delegator.
 * @author Jeff
 *
 */
public class Questionnaire {
	/*
	 * Variables
	 */
	private List<String> questions;
	private List<List<String>> answers;
	private Map<String,Integer> weights;
	
	public Questionnaire() {
		init();
	}
	
	protected void init() {
		this.questions = new ArrayList<String>();
		this.weights = new HashMap<String, Integer>();
		this.answers = new ArrayList<List<String>>();
	}

	/*
	 * Delegation methods
	 */
	/*
	public void add(int index, String element, List<String> options) {
		questions.add(index, element);
		answers.add(index, options);
	}

	public boolean add(String e, List<String> options) {
		answers.add(options);
		return questions.add(e);
	}

	public boolean addAll(Collection<? extends String> c, Collection<? extends List<String>> o) {
		answers.addAll(o);
		return questions.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends String> c, Collection<? extends List<String>> o) {
		answers.addAll(o);
		return questions.addAll(index, c);
	}

	public void setWeight(int index, int weight) {
		this.setWeight(index, new Integer(weight));
	}
	
	public void setWeight(int index, Integer weight) {
		this.weights.put(getQuestion(index), weight);
	}
	
	public boolean isEmpty() {
		return questions.isEmpty();
	}

	public Iterator<String> iterator() {
		return questions.iterator();
	}
	
	public Iterator<List<String>> answerIterator() {
		return answers.iterator();
	}

	public String remove(int index) {
		answers.remove(index);
		return questions.remove(index);
	}

	public String set(int index, String element, List<String> options) {
		answers.set(index, options);
		return questions.set(index, element);
	}

	*/
	
	public Integer getWeight(int index) {
		return this.getWeight(this.getQuestion(index));
	}
	
	public Integer getWeight(String question) {
		return this.weights.get(question);
	}

	public String getQuestion(int index) {
		return questions.get(index);
	}

	public List<String> getAllQuestions(){
		return this.questions;
	}

	public List<String> getAnswers(int index) {
		return answers.get(index);
	}
	
	public List<List<String>> getAllAnswers(){
		return this.answers;
	}
	
	public int size() {
		return questions.size();
	}

	/**
	 * Parse a string to make a questionnaire.
	 * @param content the string to parse.
	 */
	public void parse(String content) {
		//First, empty the questionnaire.
		init();
		//Then, parse
		String question = null;
		List<String> answerList = null;
		Integer weight;

		//pre-generate the pattern to be used for question matching
		String sPattern = "(\\d?)\\s*(.*)[\n\r]*";//Find all lines with a single number, any number of whitespaces and then a question.
		Pattern pattern = Pattern.compile(sPattern);

		Scanner s = new Scanner(content);
		while(s.hasNextLine()){

			//trim every input to remove leading and trailing spaces
			String ln = s.nextLine().trim();
			
			if(ln.length()==0){
				//empty line - continue with next line
				//do nothing
				System.out.println("Empty line found");
			}
			else if(ln.startsWith("-")){
				//begint met een streepje, dus een antwoord bij de huidige vraag.
				System.out.println("Answer found");

				//verwijder het streepje en haal overbodige spaties weg.
				String answer = ln.substring(1,ln.length()).trim();

				answerList.add(answer);
				System.out.println("Parsed the following line: \"" + ln + "\" into:\nAnswer: " + answer);
			}
			else{
				//begint met iets anders. Sluit de huidige vraag af en begin een nieuwe.
				Matcher matcher = pattern.matcher(ln);
				System.out.println("parsing line: "+ln);
				if(matcher.find()){
					try {
						weight = Integer.parseInt(matcher.group(1));
					}
					catch(NumberFormatException e) { //If something goes wrong, the weight must be 1.
						weight = new Integer(1);
					}
					question = matcher.group(2);
					if(!question.equals("")) {
						//add all answers for the previous question, only if there was one.
						if(answerList != null){
							this.answers.add(answerList);
						}
						
						//initialize the next question
						answerList = new ArrayList<String>();
						this.questions.add(question);
						this.weights.put(question, weight);
						System.out.println("Parsed the following line: \"" + matcher.group() + "\" into:" +
								"\nWeight: " + weight +
								" Question: " + question);
					}
				}
				else{
					System.out.println("No valid question found");
				}
			}
		}
		if(answerList != null){
			this.answers.add(answerList);
		}

		
/*		Matcher matcher = pattern.matcher(content);
		while(matcher.find()) {
			try {
				weight = Integer.parseInt(matcher.group(1));
			}
			catch(NumberFormatException e) { //If something goes wrong, the weight must be 1.
				weight = new Integer(1);
			}
			question = matcher.group(2);
			if(!question.equals("")) {
				this.questions.add(question);
				this.weights.put(question, weight);
				System.out.println("Parsed the following line: \"" + matcher.group() + "\" into:" +
						"\nWeight: " + weight +
						" Question: " + question);
			}
		}
*/
	}
	
	public String toString() {
		String output = "";
		for(int i = 0 ; i < this.questions.size(); i++) {
			output += this.getWeight(i) + " " + this.getQuestion(i) + "\n";
			List<String> answerList = this.answers.get(i);
			for(int j = 0; j < answerList.size(); j++){
				output += "- "+answerList.get(j) + "\n";
			}
			output += "\n";
		}
		return output;
	}
}
