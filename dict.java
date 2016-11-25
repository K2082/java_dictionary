import java.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

class wordGood {
	String word;
	int bGood;
	int jGood;
	int yGood;
	wordGood(String s, int b, int j, int y) {
		word = s;
		bGood = b;
		jGood = j;
		yGood = y;
	}
	void setWord(String s) {
		word = s;
	}
}

public class dict {
	static boolean baiduVisible = false;
	static boolean jinshanVisible = false;
	static boolean youdaoVisible = false;
	static int numOfBaiduGood;
	static int numOfYoudaoGood;
	static int numOfJinshanGood;
	
	public static void main(String[] args) throws Exception {		
		ArrayList wordlist = new ArrayList();
		
		DataSave d = new DataSave();
		
		//the whole frame
		JFrame frame = new JFrame("Dictionary");
		frame.setSize(550, 400);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//place to input words and click button
		JPanel inputArea = new JPanel();
		JButton search = new JButton("search");
		JTextField userInput = new JTextField(20);
		inputArea.add(userInput, BorderLayout.EAST);
		inputArea.add(search, BorderLayout.WEST);	
		
		//place to choose website
		JCheckBox chooseBaidu = new JCheckBox("baidu", false);
		JCheckBox chooseJinshan = new JCheckBox("jinshan", false);
		JCheckBox chooseYoudao = new JCheckBox("youdao", false);
		JPanel choose = new JPanel();
		choose.add(chooseBaidu);
		choose.add(chooseJinshan);
		choose.add(chooseYoudao);
		inputArea.add(choose, BorderLayout.SOUTH);
		
		//place to show three meanings
		JPanel meaning = new JPanel();
		
		JPanel baidu = new JPanel();
		JButton baiduGood = new JButton("good");
		JTextArea baiduMeaning = new JTextArea("Baidu: ");
		baiduMeaning.setBackground(new Color(240,240,240));
		baiduMeaning.setEditable(false);
		baidu.add(baiduMeaning, BorderLayout.NORTH);
		baidu.add(baiduGood, BorderLayout.SOUTH);
		
		JPanel youdao = new JPanel();
		JButton youdaoGood = new JButton("good");
		JTextArea youdaoMeaning = new JTextArea("Youdao: ");
		youdaoMeaning.setBackground(new Color(240,240,240));
		youdaoMeaning.setEditable(false);
		youdao.add(youdaoMeaning, BorderLayout.NORTH);
		youdao.add(youdaoGood, BorderLayout.SOUTH);
		
		JPanel jinshan = new JPanel();
		JButton jinshanGood = new JButton("good");
		JTextArea jinshanMeaning = new JTextArea("Jinshan: ");
		jinshanMeaning.setBackground(new Color(240,240,240));
		jinshanMeaning.setEditable(false);
		jinshan.add(jinshanMeaning, BorderLayout.NORTH);
		jinshan.add(jinshanGood, BorderLayout.SOUTH);
		
		meaning.add(baidu);
		meaning.add(jinshan);
		meaning.add(youdao);
		
		//add to main frame
		frame.add(inputArea, BorderLayout.NORTH);
		frame.add(meaning, BorderLayout.CENTER);
		
		frame.setVisible(true);
		
		//show meaning by clicking
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				String newWord = userInput.getText();
				String bMeaning = newWord;	//baidu meaning
				String jMeaning = newWord;	//jinshan meaning
				String yMeaning = newWord;	//youdao meaning
				
				/**********************
				TODO: find the three meanings on each website
				and the time of being clicked
				baidu: http://dict.youdao.com/w/eng/(the word)
				jinshan: http://www.iciba.com/(the word)
				youdao: http://dict.youdao.com/w/eng/(the word)
				***********************/
				
				if (numOfBaiduGood >= numOfJinshanGood) {
					if (numOfJinshanGood >= numOfYoudaoGood){
						//baidu > jinshan > youdao
						meaning.add(baidu);
						meaning.add(jinshan);
						meaning.add(youdao);
					}
					else {
						if (numOfBaiduGood >= numOfYoudaoGood) {
							// baidu > youdao > jinshan
							meaning.add(baidu);
							meaning.add(youdao);
							meaning.add(jinshan);
						}
						else {
							// youdao > baidu > jinshan
							meaning.add(youdao);
							meaning.add(baidu);
							meaning.add(jinshan);
						}
					}
				}
				else {
					if (numOfYoudaoGood >= numOfJinshanGood){
						//youdao > jinshan > baidu
						meaning.add(youdao);
						meaning.add(jinshan);
						meaning.add(baidu);
					}
					else {
						if (numOfBaiduGood >= numOfYoudaoGood) {
							//jinshan > baidu > youdao
							meaning.add(jinshan);
							meaning.add(baidu);
							meaning.add(youdao);
						}
						else {
							// jinshan > youdao > baidu
							meaning.add(jinshan);
							meaning.add(youdao);
							meaning.add(baidu);
						}
					}
				}
				
				if (chooseBaidu.isSelected() ) {
					baiduMeaning.setText("Baidu:\n" + bMeaning);
				}
				else {
					baiduMeaning.setText("");
				}
				if (chooseJinshan.isSelected() ) {
					jinshanMeaning.setText("Jinshan:\n" + jMeaning);
				}
				else {
					jinshanMeaning.setText("");
				}
				if (chooseYoudao.isSelected() ){
					youdaoMeaning.setText("Youdao:\n" + yMeaning);
				}
				else {
					youdaoMeaning.setText("");
				}
			}
		} );
		
		baiduGood.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				numOfBaiduGood++;
				wordGood w = new wordGood(userInput.getText(), numOfBaiduGood, numOfJinshanGood, numOfYoudaoGood);
				
				//TODO: update the wordGood to Internet
			}
		});
 	}
}

/*******************
TODO:
build a server, storage the time of clicking good
, username and password
********************/
