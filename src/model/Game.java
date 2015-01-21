package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.thoughtworks.xstream.XStream;

/**
 * This is the main-class for a Game of Who is the Mole. 
 * It will contain a list of Questionnaires, a list of Rounds and a list of Players.
 * Besides that, it shall contain one Player, being the mole.
 * @author Jeff
 *
 */
public class Game {
	/**
	 * We'll use a singleton principle for this class, as there's no need to have a second game.
	 */
	private static Game gameInstance;
	public static Game getInstance() {
		if(gameInstance == null) {
			gameInstance = new Game();
		}
		return gameInstance;
	}
	
	public static Game loadInstance(File file) {
		XStream xstream = new XStream();
		xstream.setMode(XStream.ID_REFERENCES);
		try {
			FileInputStream fis = new FileInputStream(file);
			gameInstance = (Game) xstream.fromXML(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			JOptionPane.showMessageDialog(null, "DEBUG eA loadInstance");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			JOptionPane.showMessageDialog(null, "DEBUG eB loadInstance");
			e.printStackTrace();
		} catch ( Exception e ) {
//			JOptionPane.showMessageDialog(null, "DEBUG eC loadInstance:\n"+e.getMessage());
			e.printStackTrace();
		}
		//When done with magic, return the instance.
		return Game.getInstance();
	}

	public static void saveInstance(File to) {
		XStream xstream = new XStream();
		xstream.setMode(XStream.ID_REFERENCES);
		String xml = xstream.toXML(Game.getInstance());
		try {
			FileWriter fw = new FileWriter(to);
			fw.write(xml);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<Player> players;
	private List<Round> rounds;
	private Player mole;
	
	private Game() {
		this.players = new ArrayList<Player>();
		this.rounds = new ArrayList<Round>();
		this.mole = null;
	}
		
	/**
	 * @return the mole
	 */
	public Player getMole() {
		return mole;
	}

	/**
	 * @param mole the mole to set
	 */
	public void setMole(Player mole) {
		this.mole = mole;
	}

	/**
	 * @return the players
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * @return the rounds
	 */
	public List<Round> getRounds() {
		return rounds;
	}
	
}
