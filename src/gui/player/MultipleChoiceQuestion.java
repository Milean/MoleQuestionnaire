package gui.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MultipleChoiceQuestion extends JFrame{
	private static final long serialVersionUID = 7745057817623902217L;

	private static final Font QUESTIONFONT = new Font("Comic Sans MS", Font.BOLD, 30);
	private static final Color QUESTIONCOLOR = Color.RED;
	private static final Font ANSWERFONT = new Font("Comic Sans MS", Font.BOLD, 20);
	private static final Font BUTTONFONT = new Font("Comic Sans MS", Font.BOLD, 20);
	private static final Color ANSWERCOLOR = new Color(0.9f, 0.85f, 0.4f);
	private static final Color BACKGROUNDCOLOR = Color.BLACK;

	private static final int ANSWERLIMIT = 8;
	
	private JPanel questionPane;
	private JPanel answersPane;
	private JLabel moleLogo;
	private JLabel currentQuestion;
	private ArrayList<String> questions;
	private ArrayList<ArrayList<String>> answers;
	private int current;
	private String player;
	private BufferedImage image;
	private ActionListener answerListener; 

	public MultipleChoiceQuestion(ArrayList<String> questionList, ArrayList<ArrayList<String>> answerList){
		questions = questionList;
		answers = answerList;
		
		initGUI();
		playerSelect();
	}
	
	/**
	 * Builds the GUI for the multiple choice questions.
	 */
	private void initGUI(){
		image = null;
		
		// Creates one actionlistener for all answer buttons.
		answerListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				answerGiven(e.getActionCommand());
			}
		};
		
		
		try {
			image = ImageIO.read(this.getClass().getResource("/icon.png"));
			
			//Mole Logo is 267x250 pixels
			
			moleLogo = new JLabel();
			moleLogo.setIcon(new ImageIcon("img/mollogo.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setIconImage(image);
		Container contentpane = this.getContentPane();
		contentpane.setLayout(new BorderLayout());
		contentpane.setBackground(BACKGROUNDCOLOR);
		
		questionPane = new JPanel();
		questionPane.setLayout(new BorderLayout());
		questionPane.setBackground(BACKGROUNDCOLOR);
		this.add(questionPane, BorderLayout.NORTH);

		questionPane.add(moleLogo, BorderLayout.WEST);
		
		currentQuestion = new JLabel("");
		currentQuestion.setForeground(QUESTIONCOLOR);
		currentQuestion.setFont(QUESTIONFONT);
		questionPane.add(currentQuestion, BorderLayout.CENTER);
		
		answersPane = new JPanel();
		answersPane.setBackground(BACKGROUNDCOLOR);
		this.add(answersPane, BorderLayout.CENTER);
		
		/*
		 * ButtonPanel tijdelijk, om er zelf doorheen te kunnen klikken
		 */
		JPanel buttonPanel = new JPanel();
		JButton init = new JButton("next");
		init.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextQuestion();
			}
		});
		buttonPanel.add(init);
		
		JButton exit = new JButton("exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		buttonPanel.add(exit);
		
		this.add(buttonPanel, BorderLayout.SOUTH);
		/*
		 * Einde ButtonPanel tijdelijk
		 */
		
		this.setVisible(true);
		this.toFront();
	}
	
	/**
	 * Retrieves and displays the next question. If there is no next question, the 
	 * questionnaire will end and go back to the player selecting screen.
	 */
	private void nextQuestion(){
		current++;
		
		if(current>=0 && current<questions.size()){
			String question = questions.get(current);
			ArrayList<String> answerList = answers.get(current);
			
			currentQuestion.setText(String.format("<html><div style=\"width:%dpx;\">%s</div><html>", 700, (current+1)+".  "+question));
			
			int amount = answerList.size();
			int columns = amount/ANSWERLIMIT + (amount%ANSWERLIMIT == 0 ? 0 : 1);

			answersPane.removeAll();
			answersPane.setLayout(new GridLayout(1, columns));

			JPanel[] panels = new JPanel[columns];
			for(int j = 0; j<panels.length; j++){
				panels[j] = new JPanel();
				panels[j].setBackground(BACKGROUNDCOLOR);
				panels[j].setLayout(null);
				answersPane.add(panels[j]);
			}
			
			for(int j = 0; j<amount; j++){
				JButton number = new JButton(""+(j+1));
				number.setBounds(20, 20 + 70*(j%ANSWERLIMIT), 50, 50);
				number.setFont(BUTTONFONT);
				number.setMargin(new Insets(0, 0, 0, 0));
				//number.setForeground(ANSWERCOLOR);
				number.setActionCommand(answerList.get(j));
				number.addActionListener(answerListener);
				panels[j/ANSWERLIMIT].add(number);
				
				JLabel text = new JLabel("  "+answerList.get(j));
				text.setFont(ANSWERFONT);
				text.setForeground(ANSWERCOLOR);
				text.setBounds(70, 20 + 70*(j%ANSWERLIMIT), 300, 50);
				panels[j/ANSWERLIMIT].add(text);
			}
			
		}
		else{
			JOptionPane.showMessageDialog(this, player+", je bent klaar met de questionnaire.");
			playerSelect();
			//System.out.println("Error: probeerde vraag "+i+" in te stellen.");
		}
		repaint();
	}
	
	/**
	 * Asks the player to confirm their given answer. If confirmed, 
	 * processes the answer and moves to the next question.
	 *  
	 * @param answer The given answer for the current question.
	 */
	private void answerGiven(String answer){
		int confirm = JOptionPane.showConfirmDialog(this, player+", je hebt het volgende antwoord: "+answer, "Bevestig je antwoord.", JOptionPane.YES_NO_OPTION);
		if(confirm == JOptionPane.OK_OPTION){
			System.out.println("Gekozen optie: "+answer);
			nextQuestion();
		}
	}
	/**
	 * Sets the starting screen for the next player, where they can enter 
	 * their name. This method only ends when the next player has entered 
	 * his name, or when 'done' is presented to end the questionnaire.
	 */
	private void playerSelect(){
		current = -1;
		currentQuestion.setText("");
		answersPane.removeAll();
		repaint();
		
		String nextPlayer = null;
		while(nextPlayer == null || nextPlayer.equals("")){
			nextPlayer = JOptionPane.showInputDialog(this, "Voer je naam in...", "Aanmelden", JOptionPane.OK_OPTION);
		}
		if(nextPlayer.equalsIgnoreCase("done")){
			System.exit(0);
		}
		else{
			player = nextPlayer;
			nextQuestion();
		}
		
	}
	
	
	public static void main(String[] args){
		ArrayList<String> q = new ArrayList<String>();
		ArrayList<ArrayList<String>> a = new ArrayList<ArrayList<String>>();

		q.add("Testvraag 1");
		ArrayList<String> a1 = new ArrayList<String>();
		a1.add("T1 Antwoord 1");
		a1.add("T1 Antwoord 2");
		a1.add("T1 Antwoord 3");
		a1.add("T1 Antwoord 4");
		a1.add("T1 Antwoord 5");
		
		q.add("Because a JLabel's background is transparent, there is no effect from using the setBackground method. To make a new background, you need to create a JPanel with the appropriate color and put the label on that. For example");
		ArrayList<String> a2 = new ArrayList<String>();
		a2.add("T2 Antwoord 1");
		a2.add("T2 Antwoord 2");
		a2.add("T2 Antwoord 3");
		a2.add("T2 Antwoord 4");
		a2.add("T2 Antwoord 5");
		
		q.add("Testvraag 3");
		ArrayList<String> a3 = new ArrayList<String>();
		a3.add("T3 Antwoord 1");
		a3.add("T3 Antwoord 2");
		a3.add("T3 Antwoord 3");
		a3.add("T3 Antwoord 4");
		a3.add("T3 Antwoord 5");
		a3.add("T3 Antwoord 6");
		a3.add("T3 Antwoord 7");
		a3.add("T3 Antwoord 8");
		a3.add("T3 Antwoord 9");
		a3.add("T3 Antwoord 10");
		a3.add("T3 Antwoord 11");
		a3.add("T3 Antwoord 12");
		a3.add("T3 Antwoord 13");

		a.add(a1);
		a.add(a2);
		a.add(a3);
		
		new MultipleChoiceQuestion(q,a);
	}

}
