package gui.gm;

import gui.LayoutHelper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import model.Game;
import model.Player;
import model.Questionnaire;
import model.Round;

public class RoundPanel extends JPanel {
	private static final long serialVersionUID = 5319445064094707042L;
	private JTextField numPlayers;
	private JList inList;
	private JList outList;
	private JTextArea txtrVraagPer;
	private JList roundList;
	private SpringLayout springLayout;
	
	private Round curRound;
	private JTextField roundNum;

	/**
	 * Create the panel.
	 */
	public RoundPanel() {
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
		
		
		
		JButton btnNieuweRonde = new JButton("Nieuwe ronde");
		springLayout.putConstraint(SpringLayout.NORTH, btnNieuweRonde, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnNieuweRonde, 6, SpringLayout.EAST, roundList);
		btnNieuweRonde.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setRound(null);
				
			}
		});
		add(btnNieuweRonde);
		
		JLabel lblRoundNum = new JLabel("Ronde nummer:");
		springLayout.putConstraint(SpringLayout.WEST, lblRoundNum, 6, SpringLayout.EAST, roundList);
		add(lblRoundNum);
		
		roundNum = new JTextField("0");
		springLayout.putConstraint(SpringLayout.WEST, roundNum, 6, SpringLayout.EAST, lblRoundNum);
		springLayout.putConstraint(SpringLayout.NORTH, lblRoundNum, 3, SpringLayout.NORTH, roundNum);
		springLayout.putConstraint(SpringLayout.NORTH, roundNum, 6, SpringLayout.SOUTH, btnNieuweRonde);
		add(roundNum);
		roundNum.setColumns(10);
		
		JLabel lblAantalSpelers = new JLabel("Aantal spelers:");
		springLayout.putConstraint(SpringLayout.WEST, lblAantalSpelers, 6, SpringLayout.EAST, roundNum);
		add(lblAantalSpelers);
		
		numPlayers = new JTextField("0");
		springLayout.putConstraint(SpringLayout.WEST, numPlayers, 6, SpringLayout.EAST, lblAantalSpelers);
		springLayout.putConstraint(SpringLayout.NORTH, lblAantalSpelers, 3, SpringLayout.NORTH, numPlayers);
		springLayout.putConstraint(SpringLayout.NORTH, numPlayers, 6, SpringLayout.SOUTH, btnNieuweRonde);
		add(numPlayers);
		numPlayers.setColumns(10);
		
		JButton btnOpslaan = new JButton("Opslaan");
		springLayout.putConstraint(SpringLayout.WEST, btnOpslaan, 6, SpringLayout.EAST, roundList);
		springLayout.putConstraint(SpringLayout.SOUTH, btnOpslaan, -10, SpringLayout.SOUTH, this);
		btnOpslaan.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveRound();				
			}
		});
		add(btnOpslaan);
		
		JLabel lblVragen = new JLabel("Vragen:");
		springLayout.putConstraint(SpringLayout.NORTH, lblVragen, 6, SpringLayout.SOUTH, lblAantalSpelers);
		springLayout.putConstraint(SpringLayout.WEST, lblVragen, 6, SpringLayout.EAST, roundList);
		add(lblVragen);
		
		txtrVraagPer = new JTextArea();
		txtrVraagPer.setWrapStyleWord(true);
		JScrollPane vraagPane = new JScrollPane(txtrVraagPer);
		vraagPane.getVerticalScrollBar().setUnitIncrement(16);
		springLayout.putConstraint(SpringLayout.NORTH, vraagPane, 6, SpringLayout.SOUTH, lblVragen);
		springLayout.putConstraint(SpringLayout.WEST, vraagPane, 6, SpringLayout.EAST, roundList);
		springLayout.putConstraint(SpringLayout.SOUTH, vraagPane, -6, SpringLayout.NORTH, btnOpslaan);
		springLayout.putConstraint(SpringLayout.EAST, vraagPane, 350, SpringLayout.WEST, vraagPane);
		txtrVraagPer.setLineWrap(true);
		txtrVraagPer.setText("Elke vraag en antwoord begint op een nieuwe regel.\n" +
							"Een antwoord begint met een streepje -, en een nieuwe vraag is de eerste regel zonder streepje.\n" +
							"Alle vragen hebben een standaard-gewicht van 1.\n" +
							"Als je een ander gewicht aan een vraag wilt geven, zet dan een positief getal voor de vraag, dat is dan het gewicht.");
		add(vraagPane);
		
		int lWidth = 100;
		
		outList = new JList();
		outList.setBorder(LayoutHelper.GrayBorder);
		springLayout.putConstraint(SpringLayout.WEST, outList, 6, SpringLayout.EAST, vraagPane);
		springLayout.putConstraint(SpringLayout.EAST, outList, lWidth, SpringLayout.WEST, outList);
		springLayout.putConstraint(SpringLayout.SOUTH, outList, -10, SpringLayout.SOUTH, this);
		add(outList);
		
		JLabel lblSpelers = new JLabel("Spelers OUT:");
		springLayout.putConstraint(SpringLayout.WEST, lblSpelers, 0, SpringLayout.WEST, outList);
		springLayout.putConstraint(SpringLayout.NORTH, outList, 3, SpringLayout.SOUTH, lblSpelers);
		springLayout.putConstraint(SpringLayout.NORTH, lblSpelers, 6, SpringLayout.SOUTH, numPlayers);
		add(lblSpelers);
		
		inList = new JList();
		inList.setBorder(LayoutHelper.GrayBorder);
		springLayout.putConstraint(SpringLayout.EAST, inList, -5, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.WEST, inList, -lWidth, SpringLayout.EAST, inList);
		springLayout.putConstraint(SpringLayout.NORTH, inList, 0, SpringLayout.NORTH, outList);
		springLayout.putConstraint(SpringLayout.SOUTH, inList, 0, SpringLayout.SOUTH, outList);		
		add(inList);
		
		JLabel lblSpelers2 = new JLabel("Spelers IN:");
		springLayout.putConstraint(SpringLayout.WEST, lblSpelers2, 0, SpringLayout.WEST, inList);
		springLayout.putConstraint(SpringLayout.SOUTH, lblSpelers2, 0, SpringLayout.SOUTH, lblSpelers);
		springLayout.putConstraint(SpringLayout.NORTH, lblSpelers2, 0, SpringLayout.NORTH, lblSpelers);
		add(lblSpelers2);
		
		JButton addButton = new JButton("-->");
		springLayout.putConstraint(SpringLayout.WEST, addButton, 3, SpringLayout.EAST, outList);
		springLayout.putConstraint(SpringLayout.EAST, addButton, -3, SpringLayout.WEST, inList);
		springLayout.putConstraint(SpringLayout.NORTH, addButton, 15, SpringLayout.NORTH, outList);
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] moving = outList.getSelectedValues();
				DefaultListModel inModel = (DefaultListModel) inList.getModel();
				DefaultListModel outModel = (DefaultListModel) outList.getModel();
				for(Object o : moving) {
					Player p = (Player) o;
					outModel.removeElement(p);
					inModel.addElement(p);
				}
			}
		});
		add(addButton);
		
		JButton remButton = new JButton("<--");
		springLayout.putConstraint(SpringLayout.WEST, remButton, 0, SpringLayout.WEST, addButton);
		springLayout.putConstraint(SpringLayout.EAST, remButton, 0, SpringLayout.EAST, addButton);
		springLayout.putConstraint(SpringLayout.NORTH, remButton, 5, SpringLayout.SOUTH, addButton);
		remButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] moving = inList.getSelectedValues();
				DefaultListModel inModel = (DefaultListModel) inList.getModel();
				DefaultListModel outModel = (DefaultListModel) outList.getModel();
				for(Object o : moving) {
					Player p = (Player) o;
					inModel.removeElement(p);
					outModel.addElement(p);
				}
			}
		});
		add(remButton);
	}
	
	protected void saveRound() {
		int roundNum = Integer.parseInt(this.roundNum.getText());
		int numPlayers = Integer.parseInt(this.numPlayers.getText());
		ArrayList<Player> players = new ArrayList<Player>();
		for(int i = 0; i < inList.getModel().getSize() ; i++) {
			players.add((Player) inList.getModel().getElementAt(i));
		}
		Questionnaire quest = new Questionnaire();
		quest.parse(this.txtrVraagPer.getText());
		if(curRound == null) {
			curRound = new Round(roundNum, numPlayers);
			Game.getInstance().getRounds().add(curRound);
		}
		curRound.setNumPlayers(numPlayers);
		curRound.setRoundNumber(roundNum);
		curRound.setPlayers(players);
		curRound.setQuestionnaire(quest);		
		
		GameMasterApplication.reinit();
	}
	
	private void fillRoundList() {
		DefaultListModel lModel = new DefaultListModel();
		for(Round r : Game.getInstance().getRounds()) {
			lModel.addElement(r);
		}
		roundList.setModel(lModel);
	}
	
	private void fillInOutLists() {
		DefaultListModel inModel = new DefaultListModel();
		DefaultListModel outModel = new DefaultListModel();
		inList.setModel(inModel);
		outList.setModel(outModel);
		for(Player p : Game.getInstance().getPlayers()) {
			if(this.curRound != null && this.curRound.getPlayers().contains(p)) {
				inModel.addElement(p);
			}
			else {
				outModel.addElement(p);
			}
		}
	}

	protected void reinit() {
		fillRoundList();
		setRound(null);		
	}
	
	public void setRound(Round r) {
		this.curRound = r;
		if(r == null) {
			txtrVraagPer.setText("Elke vraag en antwoord begint op een nieuwe regel.\n" +
							"Een antwoord begint met een streepje -, en een nieuwe vraag is de eerste regel zonder streepje.\n" +
							"Alle vragen hebben een standaard-gewicht van 1.\n" +
							"Als je een ander gewicht aan een vraag wilt geven, zet dan een positief getal voor de vraag, dat is dan het gewicht.");
			this.numPlayers.setText("0");
			this.roundNum.setText("0");
		}
		else {
			txtrVraagPer.setText(r.getQuestionnaire().toString());
			this.numPlayers.setText(Integer.toString(r.getNumPlayers()));
			this.roundNum.setText(Integer.toString(r.getRoundNumber()));
		}		
		fillInOutLists();
	}
}
