package gui.gm;

import gui.LayoutHelper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import model.Game;
import model.Player;
import model.Status;

public class PlayerPanel extends JPanel {
	private JTextField textField;
	private JTree tree;
	
	private Player curPlayer = null;
	private JComboBox comboBox;

	/**
	 * Create the panel.
	 */
	public PlayerPanel() {
		initialize();
	}

	public void initialize() {
		SpringLayout sl_playerPanel = new SpringLayout();
		this.setLayout(sl_playerPanel);
		
		tree = new JTree();
		fillTree();
		tree.setBorder(LayoutHelper.GrayBorder);
		sl_playerPanel.putConstraint(SpringLayout.SOUTH, tree, -10, SpringLayout.SOUTH, this);
		sl_playerPanel.putConstraint(SpringLayout.NORTH, tree, 10, SpringLayout.NORTH, this);
		sl_playerPanel.putConstraint(SpringLayout.WEST, tree, 10, SpringLayout.WEST, this);
		sl_playerPanel.putConstraint(SpringLayout.EAST, tree, 150, SpringLayout.WEST, this);
		
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if(selPath != null) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
					if(node != null) {
						Object userObject = node.getUserObject();

						if(selRow != -1 && userObject != null) {
							if(e.getClickCount() == 2) {
								if(userObject instanceof Player) {
									setPlayer((Player)userObject);
								}
							}
						}
					}
				}
			}
		};
		tree.addMouseListener(ml);
		
		this.add(tree);
		
		JButton btnNieuweSpeler = new JButton("Nieuwe speler");
		sl_playerPanel.putConstraint(SpringLayout.NORTH, btnNieuweSpeler, 0, SpringLayout.NORTH, tree);
		sl_playerPanel.putConstraint(SpringLayout.WEST, btnNieuweSpeler, 6, SpringLayout.EAST, tree);
		btnNieuweSpeler.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setPlayer(null);
				
			}
		});
		add(btnNieuweSpeler);
		
		JLabel lblNaam = new JLabel("Naam:");
		sl_playerPanel.putConstraint(SpringLayout.WEST, lblNaam, 6, SpringLayout.EAST, tree);
		sl_playerPanel.putConstraint(SpringLayout.NORTH, lblNaam, 50, SpringLayout.SOUTH, btnNieuweSpeler);
		add(lblNaam);
		
		JLabel lblStatus = new JLabel("Status:");
		sl_playerPanel.putConstraint(SpringLayout.NORTH, lblStatus, 6, SpringLayout.SOUTH, lblNaam);
		sl_playerPanel.putConstraint(SpringLayout.WEST, lblStatus, 6, SpringLayout.EAST, tree);
		add(lblStatus);
		
		textField = new JTextField();
		sl_playerPanel.putConstraint(SpringLayout.NORTH, textField, -3, SpringLayout.NORTH, lblNaam);
		sl_playerPanel.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.EAST, lblNaam);
		sl_playerPanel.putConstraint(SpringLayout.EAST, textField, 100, SpringLayout.WEST, textField);
		add(textField);
		
		comboBox = new JComboBox();
		sl_playerPanel.putConstraint(SpringLayout.NORTH, comboBox, 3, SpringLayout.SOUTH, textField);
		sl_playerPanel.putConstraint(SpringLayout.WEST, comboBox, 0, SpringLayout.WEST, textField);
		sl_playerPanel.putConstraint(SpringLayout.EAST, comboBox, 0, SpringLayout.EAST, textField);
		comboBox.setModel(new DefaultComboBoxModel(Status.values()));
		add(comboBox);
		
		JButton btnOpslaan = new JButton("Opslaan");
		sl_playerPanel.putConstraint(SpringLayout.EAST, btnOpslaan, 0, SpringLayout.EAST, comboBox);
		sl_playerPanel.putConstraint(SpringLayout.NORTH, btnOpslaan, 6, SpringLayout.SOUTH, comboBox);
		add(btnOpslaan);
		btnOpslaan.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
				
			}
		});
		
		JButton btnDelete = new JButton("Verwijder");
		sl_playerPanel.putConstraint(SpringLayout.EAST, btnDelete, 0, SpringLayout.EAST, btnOpslaan);
		sl_playerPanel.putConstraint(SpringLayout.NORTH, btnDelete, 6, SpringLayout.SOUTH, btnOpslaan);
		add(btnDelete);
		btnDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				delete();				
			}
		});
		
		textField.setColumns(10);
	}
	
	private void fillTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		DefaultTreeModel model = new DefaultTreeModel(root);
		model.setRoot(root);
		
		DefaultMutableTreeNode gm = new DefaultMutableTreeNode("Spelleiding");
		root.add(gm);
		DefaultMutableTreeNode pl = new DefaultMutableTreeNode("Spelers");
		root.add(pl);
		DefaultMutableTreeNode out = new DefaultMutableTreeNode("Uitgeschakeld");
		root.add(out);
		DefaultMutableTreeNode mole = new DefaultMutableTreeNode("De Mol");
		root.add(mole);

		Game game = Game.getInstance();
		mole.add(new DefaultMutableTreeNode(game.getMole()));
		
		for(Player p : game.getPlayers()) {
			DefaultMutableTreeNode addTo = null;
			switch(p.getStatus()) {
			case GAME_MASTER:
				addTo = gm;
				break;
			case ELIMINATED:
				addTo = out;
				break;
			default:
				addTo = pl;
			}
			addTo.add(new DefaultMutableTreeNode(p));
		}

		tree.setModel(model);
		tree.setRootVisible(false);
		
		for(int i=0;i<tree.getRowCount() -1 ; i++) {
			tree.expandRow(i);
		}
	}
	
	/*
	 * Opslaan
	 */
	private void save() {
		if(curPlayer == null) {
			curPlayer = new Player(textField.getText(),(Status) comboBox.getSelectedItem());
			Game.getInstance().getPlayers().add(curPlayer);
		}
		else {
			curPlayer.setStatus((Status) comboBox.getSelectedItem());
			curPlayer.setName(textField.getText());
		}
		if(curPlayer.isMole()) {
			Game.getInstance().setMole(curPlayer);
		}
		GameMasterApplication.reinit();
	}
	
	private void setPlayer(Player p) {
		this.curPlayer = p;
		if(p != null) {
			this.textField.setText(p.getName());
			this.comboBox.setSelectedItem(p.getStatus());			
		}
		else {
			this.textField.setText("");
			this.comboBox.setSelectedItem(Status.PLAYING);
		}
	}
	
	private void delete() {
		Game.getInstance().getPlayers().remove(curPlayer);
		reinit();
	}
	
	protected void reinit() {
		fillTree();
		setPlayer(null);
	}
}
