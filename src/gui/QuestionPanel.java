package gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class QuestionPanel extends JPanel {
	
	private static final long serialVersionUID = -4849500401937042786L;
	protected String question;
	protected int weight;
	protected String answer = "";
	
	protected JLabel qField;
	
	protected JTextArea answerComponent;
	
	/**
	 * Create the panel.
	 */
	public QuestionPanel(String question, int weight, String answer) {
		this.answer = answer;
		this.weight = weight;
		this.answerComponent = new JTextArea(answer);
		this.answerComponent.setWrapStyleWord(true);
		this.answerComponent.setBorder(LayoutHelper.GrayBorder);
		this.answerComponent.setRows(4);
		this.question = question;
		this.qField = new JLabel("<HTML>" + 
				question + "<BR>Deze vraag telt " + 
				weight + " keer mee.</HTML>");
		this.setLayout(new GridLayout(0, 1));
		this.build();
	}
	
	public QuestionPanel(String question, int weight) {
		this(question, weight, ""); //Default answer should be none.
	}
	
	public QuestionPanel(String question) {
		this(question,1); //Default weight to show: 1
	}
	
	public void build() {
		this.add(this.qField);
		this.add(this.answerComponent);
	}
	
	public void setEditable(boolean edit) {
		this.answerComponent.setEditable(edit);
	}
	
	public String getAnswer() {
		return answerComponent.getText();
	}
}
