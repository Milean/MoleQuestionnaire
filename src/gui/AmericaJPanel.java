package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AmericaJPanel extends JPanel{

	private Image background = Toolkit.getDefaultToolkit().createImage("mol_achtergrond_2.png");
	private static final long serialVersionUID = 1L;

	public AmericaJPanel(){
		super();
		System.out.println(background.toString());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, null);
	}
}
