package gui.player;

import gui.WITMFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import model.Game;
import model.Player;
import model.Result;
import model.Round;

public class QuestionnaireApplicationMP {

	// config variables
	private static final Font QUESTIONFONT = new Font("Comic Sans MS", Font.BOLD, 30);
	private static final Color QUESTIONCOLOR = new Color(137,16,33);
	private static final Font ANSWERFONT = new Font("Comic Sans MS", Font.BOLD, 20);
	private static final Font BUTTONFONT = new Font("Comic Sans MS", Font.BOLD, 20);
	private static final Color ANSWERCOLOR = new Color(23,48,70);
	private static final Color BACKGROUNDCOLOR = new Color(228,215,171);

	private static final int ANSWERLIMIT = 6;
	
	// Fields used for initialisation
	private final JFileChooser fc = new JFileChooser();
	private File witmFile;
	private BufferedImage image;
	

	// Fields used for the general application, and background data
	private JFrame frmWidmQuestionnaire;
	private JList roundList;
	private Round round;
	private ArrayList<Player> playersLeftToTest;
	private Player chosen;
	private Result workingResult;
	private List<String> workingAnswers;

	// Fields for the full-screen where players fill out the questionnaire.
	private JPanel questionPane;
	private JPanel answersPane;
	private JLabel moleLogo;
	private JLabel currentQuestion;
	private List<String> questions;
	private List<List<String>> answers;
	private int current;
	private ActionListener answerListener; 
	private boolean showConfirmation;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(
							UIManager.getSystemLookAndFeelClassName());

					QuestionnaireApplicationMP window = new QuestionnaireApplicationMP();
					window.frmWidmQuestionnaire.setVisible(true);
					window.frmWidmQuestionnaire.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public QuestionnaireApplicationMP() {
		initialize();
	}


	/*
	 ********************************************************
	 * PART 1: Methods for the file- and round selection.	*
	 * 														*
	 * These methods are not called for the answering of	*
	 * questions in the full-screen multiple-choice screen.	*
	 ********************************************************
	 */
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWidmQuestionnaire = new JFrame();
		frmWidmQuestionnaire.setTitle("WidM - Questionnaire");
		frmWidmQuestionnaire.setBounds(100, 100, 450, 450);
		frmWidmQuestionnaire.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set icon

		image = null;
		try {
			//Mole Logo is 267x250 pixels
			moleLogo = new JLabel();
//			moleLogo.setIcon(new ImageIcon("img/genie.jpg"));
			moleLogo.setIcon(new ImageIcon(frmWidmQuestionnaire.getClass().getResource("/amerika.jpg")));

			//set icon of the frame
			image = ImageIO.read(
					frmWidmQuestionnaire.getClass().getResource("/icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frmWidmQuestionnaire.setIconImage(image);
		frmWidmQuestionnaire.setVisible(true);
		
		loadFile();
		frmWidmQuestionnaire.setVisible(false);
		frmWidmQuestionnaire.setVisible(true);
	}

	private void loadFile() {
		fc.setFileFilter(new WITMFilter());
		int retVal = fc.showOpenDialog(null);
		if(retVal == JFileChooser.APPROVE_OPTION) {
			witmFile = fc.getSelectedFile();
			Game.loadInstance(witmFile);
			roundSelectionStep();
		}
	}
	
	private void roundSelectionStep() {
		JLabel lblRonde = new JLabel("Ronde:");
		frmWidmQuestionnaire.getContentPane().add(lblRonde, BorderLayout.WEST);

		roundList = new JList();
		roundList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DefaultListModel lm = new DefaultListModel();
		for(Round r : Game.getInstance().getRounds()) {
			lm.addElement(r);
		}
		roundList.setModel(lm);
		frmWidmQuestionnaire.getContentPane().add(roundList, BorderLayout.CENTER);

		JButton btnLaad = new JButton("Laad");
		btnLaad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				round = (Round) roundList.getSelectedValue();
				if(round!=null) {
					playersLeftToTest = new ArrayList<Player>(round.getPlayers());
					
					questions = round.getQuestionnaire().getAllQuestions();
					answers = round.getQuestionnaire().getAllAnswers();

					startQuestionnaire();
				}
			}
		});
		frmWidmQuestionnaire.getContentPane().add(btnLaad, BorderLayout.EAST);

		JLabel label = new JLabel(" ");
		frmWidmQuestionnaire.getContentPane().add(label, BorderLayout.NORTH);

		JLabel label_1 = new JLabel(" ");
		frmWidmQuestionnaire.getContentPane().add(label_1, BorderLayout.SOUTH);
		
		frmWidmQuestionnaire.repaint();
	}

	/**
	 * Called when the application is finished, to return to a state where the user can exit.
	 */
	private void done() {
		frmWidmQuestionnaire.dispose();
		frmWidmQuestionnaire = new JFrame();
		frmWidmQuestionnaire.setBounds(100,100,450,450);
		frmWidmQuestionnaire.setUndecorated(false);
		frmWidmQuestionnaire.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWidmQuestionnaire.setIconImage(image);
		frmWidmQuestionnaire.setVisible(true);
		frmWidmQuestionnaire.toFront();
		frmWidmQuestionnaire.add(new JLabel("We zijn er klaar mee. Druk op het kruisje om af te sluiten."));
	}

	
	/*
	 ************************************************************
	 * PART 2: Methods for the multiple-choice questionnaire	*
	 * 															*
	 * These methods are ony used when the players are filling	*
	 * out the questionnaire. 									*
	 ************************************************************
	 */
	
	private void startQuestionnaire() {
		frmWidmQuestionnaire.dispose();
		frmWidmQuestionnaire = new JFrame();

		// Creates one actionlistener for all answer buttons.
		answerListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				answerGiven(e.getActionCommand());
			}
		};
		
		frmWidmQuestionnaire.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frmWidmQuestionnaire.setUndecorated(true);
		frmWidmQuestionnaire.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmWidmQuestionnaire.setIconImage(image);
		
		Container contentpane = frmWidmQuestionnaire.getContentPane();
		contentpane.setLayout(new BorderLayout());
		contentpane.setBackground(BACKGROUNDCOLOR);
		
		questionPane = new JPanel();
		questionPane.setLayout(new BorderLayout());
		questionPane.setBackground(BACKGROUNDCOLOR);
		frmWidmQuestionnaire.add(questionPane, BorderLayout.NORTH);

		questionPane.add(moleLogo, BorderLayout.WEST);
		
		currentQuestion = new JLabel("");
		currentQuestion.setForeground(QUESTIONCOLOR);
		currentQuestion.setFont(QUESTIONFONT);
		questionPane.add(currentQuestion, BorderLayout.CENTER);
		
		answersPane = new JPanel();
		answersPane.setBackground(BACKGROUNDCOLOR);
		frmWidmQuestionnaire.add(answersPane, BorderLayout.CENTER);
		
		frmWidmQuestionnaire.setVisible(true);
		frmWidmQuestionnaire.toFront();
		
		playerSelect();

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
		frmWidmQuestionnaire.repaint();
		
		String nextPlayer = null;
		chosen = null;
		showConfirmation = false;

		if(playersLeftToTest.size() == 0) {
			done();
			return;
		}
		
		while(chosen == null){
			nextPlayer = JOptionPane.showInputDialog(frmWidmQuestionnaire, "Voer je naam in...", "Aanmelden", JOptionPane.OK_OPTION);
			if(nextPlayer == null){
				// do nothing
			}
			else if(nextPlayer.equalsIgnoreCase("done")){
				done();
				break;
			}
			else{
				for(Player p : playersLeftToTest){
					if(p.getName().equalsIgnoreCase(nextPlayer)){
						chosen = p;
						playersLeftToTest.remove(p);
						break;
					}
				}
				
				if(chosen == null){
					JOptionPane.showMessageDialog(frmWidmQuestionnaire, "Ongeldige speler, of je bent al geweest.");
				}
				else{
					String[] options = {"Niet vragen om bevestiging en direct door", "Mij eerst vragen om bevestiging, en dan pas door."};
					int optionChosen = JOptionPane.showOptionDialog(frmWidmQuestionnaire, "Wat moet er gebeuren als je een antwoord aanklikt?", "Wil je je antwoorden expliciet bevestigen?", 0, JOptionPane.QUESTION_MESSAGE, null, options, null);
					if(optionChosen > 0){
						showConfirmation = true;
					}
					JOptionPane.showMessageDialog(frmWidmQuestionnaire, "OK "+chosen.getName()+", je zult *"+(showConfirmation ? "wel" : "niet")+"* gevraagd worden om je antwoorden te bevestigen. \n\nJe tijd gaat in zodra je op OK klikt.");
					workingResult = new Result(chosen,round);
					workingAnswers = new ArrayList<String>();
					workingResult.startNow();
					nextQuestion();
				}
			}

		}
	}


	/**
	 * Retrieves and displays the next question. If there is no next question, the 
	 * questionnaire will end and go back to the player selecting screen.
	 */
	private void nextQuestion(){
		current++;
		
		if(current>=0 && current<questions.size()){
			String question = questions.get(current);
			List<String> answerList = answers.get(current);
			
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
				text.setBounds(70, 20 + 70*(j%ANSWERLIMIT), 500, 50);
				panels[j/ANSWERLIMIT].add(text);
			}
			
		}
		else{
			workingResult.endNow();

			//save workingResult
			workingResult.setAnswers(workingAnswers);
			chosen.setResult(round, workingResult);
			//Opslaan
			Game.saveInstance(witmFile);

			JOptionPane.showMessageDialog(frmWidmQuestionnaire, chosen.getName()+", je bent klaar met de questionnaire.");
			playerSelect();
			//System.out.println("Error: probeerde vraag "+i+" in te stellen.");
		}
		frmWidmQuestionnaire.repaint();
	}
	
	/**
	 * Asks the player to confirm their given answer. If confirmed, 
	 * processes the answer and moves to the next question.
	 *  
	 * @param answer The given answer for the current question.
	 */
	private void answerGiven(String answer){
		if(showConfirmation){
			int confirm = JOptionPane.showConfirmDialog(frmWidmQuestionnaire, "Bevestig je antwoord: "+answer, "Bevestig je antwoord.", JOptionPane.YES_NO_OPTION);
			if(confirm == JOptionPane.OK_OPTION){
				workingAnswers.add(answer);
				nextQuestion();
			}
		}
		else{
			workingAnswers.add(answer);
			nextQuestion();
		}
	}

}
