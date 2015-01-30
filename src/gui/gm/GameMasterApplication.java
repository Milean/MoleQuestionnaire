package gui.gm;

import gui.WITMFilter;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import model.Game;

public class GameMasterApplication {

	private static GameMasterApplication gApp;
	
	private JFrame frmWieIsDe;
	private final JFileChooser fc = new JFileChooser();
	private PlayerPanel playerPanel;
	private RoundPanel roundPanel;
	private CorrectionPanel correctionPanel;
	private RoundResultPanel roundResultPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(
				            UIManager.getSystemLookAndFeelClassName());
					gApp = new GameMasterApplication();
					gApp.frmWieIsDe.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GameMasterApplication() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWieIsDe = new JFrame();
		frmWieIsDe.setResizable(false);
		frmWieIsDe.setTitle("Wie is de Mol - Beheer");
		frmWieIsDe.setBounds(100, 100, 800, 623);
		frmWieIsDe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWieIsDe.getContentPane().setLayout(null);
		
		FileFilter ff = new WITMFilter();
		fc.setFileFilter(ff);
		
		// Set icon
        	
		BufferedImage image = null;
		try {
		image = ImageIO.read(
				frmWieIsDe.getClass().getResource("/icon.png"));
		} catch (IOException e) {
		e.printStackTrace();
		}
		frmWieIsDe.setIconImage(image);
		
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 794, 572);
		frmWieIsDe.getContentPane().add(tabbedPane);
				
		playerPanel = new PlayerPanel();
		tabbedPane.addTab("Spelersbeheer", null, playerPanel, null);
		
		roundPanel = new RoundPanel();
		tabbedPane.addTab("Rondes", null, roundPanel, null);

		correctionPanel = new CorrectionPanel();
		tabbedPane.addTab("Beoordelen", null, correctionPanel, null);
				
		roundResultPanel = new RoundResultPanel();
		tabbedPane.addTab("Resultaten", null, roundResultPanel, null);
		
		JMenuBar menuBar = new JMenuBar();
		frmWieIsDe.setJMenuBar(menuBar);
		
		JMenu mnBestand = new JMenu("Bestand");
		menuBar.add(mnBestand);
		
		JMenuItem mntmOpslaan = new JMenuItem("Opslaan");
		mntmOpslaan.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				save();				
			}
		});
		mnBestand.add(mntmOpslaan);
		
		JMenuItem mntmLaden = new JMenuItem("Laden");
		mntmLaden.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Create a file chooser
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					Game.loadInstance(fc.getSelectedFile());
				}
				GameMasterApplication.reinit();
			}
		});
		mnBestand.add(mntmLaden);
		
		JMenuItem mntmAfsluiten = new JMenuItem("Afsluiten");
		mntmAfsluiten.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				
			}
		});
		mnBestand.add(mntmAfsluiten);
	}
	
	private void save() {
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File to = fc.getSelectedFile();
			Game.saveInstance(to);
		}
	}
	
	public static void reinit() {
		gApp.playerPanel.reinit();
		gApp.roundPanel.reinit();
		gApp.correctionPanel.reinit();
		gApp.roundResultPanel.reinit();
	}
}
