package gui.gm;

import gui.LayoutHelper;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import model.Game;
import model.Player;

public class RekojDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public RekojDialog(HashMap<Player,Integer> rekojsOld, CorrectionPanel cp) {
		this.corpanel = cp;
		this.texts = new HashMap<Player,JTextField>();
		Container contentPane = getContentPane();
		SpringLayout sl = new SpringLayout();
		contentPane.setLayout(sl);
		setTitle("Rekojs");
		if(rekojsOld == null) {
			rekojsOld = new HashMap<Player,Integer>();
		}
		this.rekojs = rekojsOld;
		
		for(Player p:Game.getInstance().getPlayers()) {
			JLabel pLabel = new JLabel(p.toString(), JLabel.TRAILING);
			contentPane.add(pLabel);
			JTextField textField = new JTextField(10);
			if(this.rekojs.containsKey(p)) {
				textField.setText(this.rekojs.get(p).toString());
			}
			texts.put(p, textField);
			pLabel.setLabelFor(textField);
		    contentPane.add(textField);
		}
		JButton doneButton = new JButton("Klaar");
		doneButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(Player p : texts.keySet()) {
					JTextField tf = texts.get(p);
					int num = 0;
					try {
						num =Integer.parseInt(tf.getText());
					}
					catch(Exception e) {}
					rekojs.put(p, new Integer(num));
				}
				corpanel.updateRekojs(rekojs);
				dispose();
			}
		});
		JLabel dummy = new JLabel(" ");
		contentPane.add(dummy);
		dummy.setLabelFor(doneButton);
		contentPane.add(doneButton);
		//Lay out the panel.
		LayoutHelper.makeCompactGrid(contentPane,
		                                Game.getInstance().getPlayers().size()+1, 2, //rows, cols
		                                6, 6,        //initX, initY
		                                6, 6);       //xPad, yPad
		this.setContentPane(contentPane);
		this.setResizable(false);
		this.pack();
	}
	
	private CorrectionPanel corpanel;
	private HashMap<Player,Integer> rekojs;
	private HashMap<Player,JTextField> texts;
}
