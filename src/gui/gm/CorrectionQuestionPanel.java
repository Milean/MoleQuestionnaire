package gui.gm;

import gui.LayoutHelper;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CorrectionQuestionPanel extends JPanel {

	private JToggleButton correctButton;
	private JToggleButton wrongButton;
	private CorrectionPanel parent = null;

	private boolean correct;
	private String answer;
	private int weight;
	private JTextArea answerComponent;
	private String question;
	private JLabel qField;

	public CorrectionQuestionPanel(String question, int weight, String answer, CorrectionPanel parent){
		this.answer = answer;
		this.weight = weight;
		this.answerComponent = new JTextArea(answer);
		this.answerComponent.setWrapStyleWord(true);
		this.answerComponent.setBorder(LayoutHelper.GrayBorder);
		this.answerComponent.setRows(4);
		this.answerComponent.setEditable(false);
		this.question = question;
		this.qField = new JLabel("<HTML>" + 
				question + "<BR>Deze vraag telt " + 
				weight + " keer mee.</HTML>");
		this.setParent(parent);
		this.setLayout(new GridLayout(2,2));

		BufferedImage goedImg = null;
		try {
			goedImg = ImageIO.read(
					this.getClass().getResource("/goed.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Icon correctIcon = new ImageIcon(goedImg);
		this.correctButton = new JToggleButton(correctIcon);
		this.correctButton.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				setCorrect(correctButton.getModel().isSelected());

			}
		});
		BufferedImage wrongImg = null;
		try {
			wrongImg = ImageIO.read(
					this.getClass().getResource("/fout.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Icon wrongIcon = new ImageIcon(wrongImg);
		this.wrongButton = new JToggleButton(wrongIcon);
		this.wrongButton.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				setCorrect(!wrongButton.getModel().isSelected());

			}
		});
		build();
	}

	public void build() {
		this.add(this.qField);
		this.add(this.correctButton);
		this.add(this.answerComponent);
		this.add(this.wrongButton);
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
		correctButton.setSelected(correct);
		wrongButton.setSelected(!correct);
		this.parent.recalculate();
	}

	public boolean getCorrect() {
		return this.correct;
	}

	public void setParent(CorrectionPanel parent) {
		this.parent = parent;
	}
}
