package gui.gm;

import gui.LayoutHelper;
import gui.QuestionPanel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import model.Game;
import model.Player;
import model.Questionnaire;
import model.Result;
import model.Round;

public class CorrectionPanel extends JPanel {
	private static final long serialVersionUID = -5327981885427787956L;
	private JTree tree;
	private JScrollPane scrollPane;
	private JLabel scoreLbl;

	private Result curResult;
	private JLabel playerLabel;
	private JLabel molLabel;
	private ArrayList<CorrectionQuestionPanel> cqps;
	private JTextField jokerFld;
	private JButton rekojBtn;
	private JButton saveButton;
	private JButton recalcRoundButton;
	private Round selectedRound;

	/*
	 * http://download.oracle.com/javase/7/docs/api/javax/swing/JTree.html <- dubbelclick listener
	 */


	public CorrectionPanel() {
		SpringLayout springLayout = new SpringLayout();
		this.setLayout(springLayout);

		cqps = new ArrayList<CorrectionQuestionPanel>();

		tree = new JTree();
		tree.setBorder(LayoutHelper.GrayBorder);
		tree.setRootVisible(false);
		springLayout.putConstraint(SpringLayout.NORTH, tree, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, tree, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, tree, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, tree, 150, SpringLayout.WEST, this);
		add(tree);

		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if(selPath != null) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
					if(node != null) {
						Object userObject = node.getUserObject();
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
						Object parent = parentNode.getUserObject();
						if(selRow != -1 && userObject != null) {
							if(e.getClickCount() == 2) {
								if(userObject instanceof Player && parent instanceof Round) {
									setPlayer((Player)userObject,(Round)parent);
								}
								
								// Bit of code to let you determine the results of the whole round at 
								// once, without having to recalculate for every single player manually.
								if(userObject instanceof Round){
									selectedRound = (Round) userObject;
									recalcRoundButton.setEnabled(true);
								}
								else{
									selectedRound = null;
									recalcRoundButton.setEnabled(false);
								}
							}
						}
					}
				}
			}
		};
		tree.addMouseListener(ml);


		scoreLbl = new JLabel("Score: 0");
		springLayout.putConstraint(SpringLayout.SOUTH, scoreLbl, 0, SpringLayout.SOUTH, tree);
		springLayout.putConstraint(SpringLayout.WEST, scoreLbl, 6, SpringLayout.EAST, tree);
		this.add(scoreLbl);

		JLabel jokerLbl = new JLabel("Jokers:");
		springLayout.putConstraint(SpringLayout.SOUTH, jokerLbl, 0, SpringLayout.SOUTH, scoreLbl);
		springLayout.putConstraint(SpringLayout.WEST, jokerLbl, 6, SpringLayout.EAST, scoreLbl);
		this.add(jokerLbl);

		jokerFld = new JTextField("0");
		springLayout.putConstraint(SpringLayout.SOUTH, jokerFld, 0, SpringLayout.SOUTH, jokerLbl);
		springLayout.putConstraint(SpringLayout.WEST, jokerFld, 6, SpringLayout.EAST, jokerLbl);
		springLayout.putConstraint(SpringLayout.EAST, jokerFld, 20, SpringLayout.WEST, jokerFld);
		this.add(jokerFld);

		rekojBtn = new JButton("Rekojs");
		springLayout.putConstraint(SpringLayout.SOUTH, rekojBtn, 0, SpringLayout.SOUTH, jokerFld);
		springLayout.putConstraint(SpringLayout.WEST, rekojBtn, 6, SpringLayout.EAST, jokerFld);
		rekojBtn.setEnabled(false);
		final CorrectionPanel parent = this;
		rekojBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					RekojDialog dialog = new RekojDialog(curResult.getRekojs(),parent);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		this.add(rekojBtn);

		saveButton = new JButton("Deze speler herberekenen (en opslaan)");
		springLayout.putConstraint(SpringLayout.SOUTH, saveButton, 0, SpringLayout.SOUTH, rekojBtn);
		springLayout.putConstraint(SpringLayout.WEST, saveButton, 6, SpringLayout.EAST, rekojBtn);
		saveButton.setEnabled(false);
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				recalculate(); //Gezien de werking hier is dit voldoende.
				
			}
		});
		add(saveButton);

		recalcRoundButton = new JButton("Deze ronde (her)berekenen");
		springLayout.putConstraint(SpringLayout.SOUTH, recalcRoundButton, 0, SpringLayout.SOUTH, rekojBtn);
		springLayout.putConstraint(SpringLayout.WEST, recalcRoundButton, 6, SpringLayout.EAST, saveButton);
		recalcRoundButton.setEnabled(false);
		recalcRoundButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				recalculateRound();
			}
		});
		add(recalcRoundButton);

		playerLabel = new JLabel("Speler: ");
		springLayout.putConstraint(SpringLayout.NORTH, playerLabel, 0, SpringLayout.NORTH, tree);
		springLayout.putConstraint(SpringLayout.WEST, playerLabel, 6, SpringLayout.EAST, tree);
		add(playerLabel);

		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 3, SpringLayout.SOUTH, playerLabel);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 6, SpringLayout.EAST, tree);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -3, SpringLayout.NORTH, saveButton);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, this);
		add(scrollPane);

		molLabel = new JLabel("Mol: "+Game.getInstance().getMole());
		springLayout.putConstraint(SpringLayout.NORTH, molLabel, 0, SpringLayout.NORTH, tree);
		springLayout.putConstraint(SpringLayout.EAST, molLabel, 0, SpringLayout.EAST, scrollPane);
		add(molLabel);
	}

	protected void setPlayer(Player player,Round round) {
		saveButton.setEnabled(true);
		rekojBtn.setEnabled(true);
		playerLabel.setText("Speler: " + player);
		this.curResult = player.getResult(round);
		jokerFld.setText(""+curResult.getJokers());
		Result moleResult = Game.getInstance().getMole().getResult(round);
		JPanel corrections = new JPanel();
		corrections.setLayout(new GridLayout(0,2));
		Questionnaire questionnaire = round.getQuestionnaire();
		cqps = new ArrayList<CorrectionQuestionPanel>();
		List<Boolean> ratings = curResult.getRating();
		for(int i = 0; i < questionnaire.size();i++) {
			String question = questionnaire.getQuestion(i);
			Boolean correct = null;
			if(ratings.size()>i) {
				correct = ratings.get(i);
			}
			boolean c = false;
			if(correct != null){
				c = correct.booleanValue();
			}
			else{
				c = moleResult.getAnswers().get(i).equals(curResult.getAnswers().get(i));
			}
			CorrectionQuestionPanel cqp = new CorrectionQuestionPanel(question,questionnaire.getWeight(i),
					curResult.getAnswers().get(i),this);
			cqps.add(cqp);
			cqp.setCorrect(c);

			corrections.add(cqp);

			//Nu het antwoord van de mol er nog bij.
			QuestionPanel mqp = new QuestionPanel(question,questionnaire.getWeight(i),moleResult.getAnswers().get(i));
			mqp.setEditable(false);
			corrections.add(mqp);
		}
		scrollPane.setViewportView(corrections);
	}

	private void fillTree() {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root Node");
		TreeModel treeModel = new DefaultTreeModel(rootNode);
		for(Round r : Game.getInstance().getRounds()) {
			DefaultMutableTreeNode rn = new DefaultMutableTreeNode(r);
			rootNode.add(rn);
			for(Player p : r.getPlayers()) {
				DefaultMutableTreeNode pn = new DefaultMutableTreeNode(p);
				rn.add(pn);
			}
		}
		tree.setModel(treeModel);
	}
	
	private void recalculateRound(){
		for(Player p : selectedRound.getPlayers()){
			setPlayer(p,selectedRound);
			recalculate();
		}
		scrollPane.setViewportView(null);
		saveButton.setEnabled(false);
		rekojBtn.setEnabled(false);
		playerLabel.setText("Speler: ");
		jokerFld.setText("0");
		JOptionPane.showMessageDialog(this, "Alle resultaten van ronde "+selectedRound.getRoundNumber()+" zijn (her)berekend.");
	}

	/**
	 * Recalculate the score and update the Score label.
	 */
	public void recalculate() {
		int score = 0;

		if(curResult != null) {
			ArrayList<Boolean> rating = new ArrayList<Boolean>();
			for(int i = 0; i<cqps.size();i++) {
				rating.add(new Boolean(cqps.get(i).getCorrect()));
			}
			curResult.setRating(rating);

			int jokers = 0;
			try {
				jokers = Integer.parseInt(jokerFld.getText());
			}catch(Exception e) {}
			curResult.setJokers(new Integer(jokers));
			score = curResult.score();
		}
		scoreLbl.setText("Score: " + score);
	}

	protected void reinit() {
		this.fillTree();
		this.curResult = null;
		molLabel.setText("Mol: " + Game.getInstance().getMole());
		recalculate();
	}

	public void updateRekojs(HashMap<Player, Integer> rekojs) {
		for(Player player:rekojs.keySet()) {
			Integer amount = rekojs.get(player);
			curResult.setRekojs(player, amount);
		}
		recalculate();
	}
}
