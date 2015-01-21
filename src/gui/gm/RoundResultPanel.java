package gui.gm;

import gui.LayoutHelper;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import model.Game;
import model.Player;
import model.Result;
import model.Round;

public class RoundResultPanel extends JPanel {

	private JList roundList;
	private SpringLayout springLayout;
	private Round curRound;
	private JPanel results;
	private JLabel title;
	/**
	 * Create the panel.
	 */
	public RoundResultPanel() {
		springLayout = new SpringLayout();
		setLayout(springLayout);
		
		roundList = new JList();
		roundList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fillRoundList();
		roundList.setBorder(LayoutHelper.GrayBorder);
		springLayout.putConstraint(SpringLayout.NORTH, roundList, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, roundList, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, roundList, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, roundList, 150, SpringLayout.WEST, this);
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = roundList.locationToIndex(e.getPoint());
				if(e.getClickCount() == 2) {
					ListModel dlm = roundList.getModel();
					if(index < dlm.getSize() && index >= 0) {
						Round clicked = (Round) dlm.getElementAt(index);
						setRound(clicked);
					}
				}
			}
		};
		roundList.addMouseListener(ml);
		add(roundList);
		
		title = new JLabel("Uitslag van ronde:");
		springLayout.putConstraint(SpringLayout.NORTH, title, 0, SpringLayout.NORTH, roundList);	
		
		results = new JPanel();
		results.setLayout(new SpringLayout());
		springLayout.putConstraint(SpringLayout.NORTH, results, 6, SpringLayout.SOUTH, title);
		springLayout.putConstraint(SpringLayout.SOUTH, results, 0, SpringLayout.SOUTH, roundList);
		springLayout.putConstraint(SpringLayout.WEST, results, 6, SpringLayout.EAST, roundList);
		springLayout.putConstraint(SpringLayout.EAST, results, -6, SpringLayout.EAST, this);
		add(results);
		setRound(null);
	}
	
	public void setRound(Round r) {
		this.curRound = r;
		if(r == null) {
			results.removeAll();
			return;
		}
		results.removeAll();
		results.setVisible(false);
		ArrayList<Result> res = new ArrayList<Result>();
		for(Player p:r.getPlayers()) {
			res.add(p.getResult(r));
		}
		title.setText("Uitslag van ronde: " +r);
		Collections.sort(res);
		JLabel naam = new JLabel("<HTML><U>Speler</U></HTML>");
		results.add(naam);
		JLabel vragen = new JLabel("<HTML><U>Vragen</U></HTML>");
		results.add(vragen);
		JLabel jokers = new JLabel("<HTML><U>Jokers</U></HTML>");
		results.add(jokers);
		JLabel rekojs = new JLabel("<HTML><U>Rekojs</U></HTML>");
		results.add(rekojs);
		JLabel score = new JLabel("<HTML><U>Score</U></HTML>");
		results.add(score);
		JLabel tijd = new JLabel("<HTML><U>Tijd</U></HTML>");
		results.add(tijd);
		for(Result result : res) {
			naam = new JLabel("<HTML><I><B>"+result.getPlayer().getName()+"</B></I></HTML>");
			results.add(naam);
			int numRekojs = 0;
			
			for(Integer i : result.getRekojs().values()) {
				numRekojs += i.intValue();
			}
			int numJokers = result.getJokers();
			int numScore = result.score();
			int numQuestionsCorrect = numScore - numJokers + numRekojs;
			vragen = new JLabel(Integer.toString(numQuestionsCorrect));
			results.add(vragen);
			jokers = new JLabel(Integer.toString(numJokers));
			results.add(jokers);
			rekojs = new JLabel(Integer.toString(numRekojs));
			results.add(rekojs);
			score = new JLabel("<HTML><B>"+Integer.toString(numScore)+"</B></HTML>");
			results.add(score);
			tijd = new JLabel(Long.toString(result.duration()));
			results.add(tijd);
		}
		LayoutHelper.makeCompactGrid(results,
                r.getPlayers().size()+1, 6, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		results.setVisible(true);
	}
	
	protected void reinit() {
		fillRoundList();
		setRound(null);		
	}
	
	private void fillRoundList() {
		DefaultListModel lModel = new DefaultListModel();
		for(Round r : Game.getInstance().getRounds()) {
			lModel.addElement(r);
		}
		roundList.setModel(lModel);
	}
	
}
