package gui.player;

import gui.QuestionPanel;
import gui.WITMFilter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import model.Game;
import model.Player;
import model.Questionnaire;
import model.Result;
import model.Round;

public class QuestionnaireApplication {

	private JFrame frmWidmQuestionnaire;

	private final JFileChooser fc = new JFileChooser();

	private JList roundList;
	private Round round;
	private ArrayList<Player> playersLeftToTest;

	private Player chosen;

	private BufferedImage image;

	private Result workingResult;

	private ArrayList<QuestionPanel> qPanels;

	private File witmFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(
							UIManager.getSystemLookAndFeelClassName());
					//When a round has been selected,
					QuestionnaireApplication window = new QuestionnaireApplication();
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
	public QuestionnaireApplication() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWidmQuestionnaire = new JFrame();
		frmWidmQuestionnaire.setTitle("WidM - Questionnaire");
		frmWidmQuestionnaire.setBounds(100, 100, 450, 450);
		//frmWidmQuestionnaire.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmWidmQuestionnaire.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set icon

		image = null;
		try {
			image = ImageIO.read(
					frmWidmQuestionnaire.getClass().getResource("/icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frmWidmQuestionnaire.setIconImage(image);



		loadFile();
		roundSelectionStep();
	}

	private void loadFile() {
		fc.setFileFilter(new WITMFilter());
		int retVal = fc.showOpenDialog(null);
		if(retVal == JFileChooser.APPROVE_OPTION) {
			witmFile = fc.getSelectedFile();
			Game.loadInstance(witmFile);
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
					randomNameStep();
				}
			}
		});
		frmWidmQuestionnaire.getContentPane().add(btnLaad, BorderLayout.EAST);

		JLabel label = new JLabel(" ");
		frmWidmQuestionnaire.getContentPane().add(label, BorderLayout.NORTH);

		JLabel label_1 = new JLabel(" ");
		frmWidmQuestionnaire.getContentPane().add(label_1, BorderLayout.SOUTH);
	}

	private void randomNameStep() {
		frmWidmQuestionnaire.dispose();
		frmWidmQuestionnaire = new JFrame();
		frmWidmQuestionnaire.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frmWidmQuestionnaire.setUndecorated(true);
		frmWidmQuestionnaire.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmWidmQuestionnaire.setIconImage(image);
		frmWidmQuestionnaire.setLayout(new FlowLayout());
		frmWidmQuestionnaire.setVisible(true);
		frmWidmQuestionnaire.toFront();

		Random r = new Random();
		if(playersLeftToTest.size() == 0) {
			done();
		}
		else {
			int randomIndex = r.nextInt(playersLeftToTest.size());
			chosen = playersLeftToTest.get(randomIndex);
			JLabel nextPlayer = new JLabel("De volgende speler is: " + chosen);
			nextPlayer.setFont(new Font("Tahoma", Font.BOLD, 28));
			frmWidmQuestionnaire.add(nextPlayer);

			JButton btnStart = new JButton("Ik ben " + chosen + ", laten we beginnen.");
			btnStart.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					startQuestionnaire();

				}
			});
			frmWidmQuestionnaire.add(btnStart);
		}
	}

	private void startQuestionnaire() {
		frmWidmQuestionnaire.dispose();
		frmWidmQuestionnaire = new JFrame();
		frmWidmQuestionnaire.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frmWidmQuestionnaire.setUndecorated(true);
		frmWidmQuestionnaire.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmWidmQuestionnaire.setIconImage(image);
		SpringLayout layout = new SpringLayout();
		Container contentPane = frmWidmQuestionnaire.getContentPane();
		contentPane.setLayout(layout);

		workingResult = new Result(chosen,round);

		JPanel container = new JPanel();
		Toolkit tk = Toolkit.getDefaultToolkit();


		container.setLayout(new GridLayout(0,1));
		Questionnaire q = round.getQuestionnaire();
		qPanels = new ArrayList<QuestionPanel>();
		for(int i=0;i<q.size();i++) {
			String question = q.getQuestion(i);
			int weight = q.getWeight(i);
			QuestionPanel qp = new QuestionPanel(question,weight);
			qPanels.add(qp);
			container.add(qp);
		}
		JScrollPane questionPane = new JScrollPane(container);
		questionPane.getVerticalScrollBar().setUnitIncrement(16);

		JButton doneButton = new JButton("Ik ben klaar met de vragenlijst!");
		doneButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Object[] options = {"OK",
				"Nee, ik ben nog niet klaar."};
				int n = JOptionPane.showOptionDialog(frmWidmQuestionnaire,
						"Druk op OK als je klaar bent.",
						"Ik ben klaar",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE,
						null,     //do not use a custom Icon
						options,  //the titles of buttons
						options[1]); //default button title
				if(n == 0) {
					workingResult.endNow();
					// Verwerk de antwoorden
					ArrayList<String> answers = new ArrayList<String>();
					for(QuestionPanel qp: qPanels) {
						answers.add(qp.getAnswer());
					}
					workingResult.setAnswers(answers);
					chosen.setResult(round, workingResult);
					//Opslaan
					Game.saveInstance(witmFile);
					//Tempsolution om hier af te komen.
					//System.exit(0);
					playersLeftToTest.remove(chosen);
					//Kies een volgende speler uit
					randomNameStep();
				}
			}
		});

		frmWidmQuestionnaire.add(questionPane);
		frmWidmQuestionnaire.add(doneButton);


		layout.putConstraint(SpringLayout.NORTH, questionPane, 30, SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, questionPane, -80, SpringLayout.SOUTH, contentPane);
		Spring springWidth = Spring.constant(800);
		layout.putConstraint(SpringLayout.EAST, questionPane, springWidth, SpringLayout.WEST, questionPane);
		layout.putConstraint(SpringLayout.WEST, questionPane, (tk.getScreenSize().width - 800)/2, SpringLayout.WEST, contentPane);

		layout.putConstraint(SpringLayout.SOUTH, doneButton, -20, SpringLayout.SOUTH, contentPane);
		layout.putConstraint(SpringLayout.EAST, doneButton, 0, SpringLayout.EAST, questionPane);

		frmWidmQuestionnaire.setVisible(true);
		frmWidmQuestionnaire.toFront();
		workingResult.startNow();
	}

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

}
