import java.util.*;
import java.util.regex.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.math.*;
import javax.swing.*;
import java.security.MessageDigest;

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
	static final int BAIDU = 0;
	static final int YOUDAO = 1;
	static final int JINSHAN = 2;
	
	public static String getMD5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			return new BigInteger(1, md.digest()).toString(16);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String decodeUnicode(String theString) {      
   
    char aChar;      
   
     int len = theString.length();      
   
    StringBuffer outBuffer = new StringBuffer(len);      
   
    for (int x = 0; x < len;) {      
   
     aChar = theString.charAt(x++);      
   
     if (aChar == '\\') {      
   
      aChar = theString.charAt(x++);      
   
      if (aChar == 'u') {      
   
       // Read the xxxx      
   
       int value = 0;      
   
       for (int i = 0; i < 4; i++) {      
   
        aChar = theString.charAt(x++);      
   
        switch (aChar) {      
   
        case '0':      
   
        case '1':      
   
        case '2':      
   
        case '3':      
   
       case '4':      
   
        case '5':      
   
         case '6':      
          case '7':      
          case '8':      
          case '9':      
           value = (value << 4) + aChar - '0';      
           break;      
          case 'a':      
          case 'b':      
          case 'c':      
          case 'd':      
          case 'e':      
          case 'f':      
           value = (value << 4) + 10 + aChar - 'a';      
          break;      
          case 'A':      
          case 'B':      
          case 'C':      
          case 'D':      
          case 'E':      
          case 'F':      
           value = (value << 4) + 10 + aChar - 'A';      
           break;      
          default:      
           throw new IllegalArgumentException(      
             "Malformed   \\uxxxx   encoding.");      
          }      
   
        }      
         outBuffer.append((char) value);      
        } else {      
         if (aChar == 't')      
          aChar = '\t';      
         else if (aChar == 'r')      
          aChar = '\r';      
   
         else if (aChar == 'n')      
   
          aChar = '\n';      
   
         else if (aChar == 'f')      
   
          aChar = '\f';      
   
         outBuffer.append(aChar);      
   
        }      
   
       } else     
   
       outBuffer.append(aChar);      
   
      }      
   
      return outBuffer.toString();      
   
     }  
	
	public static String executeYoudao(String s) {
		int b = s.indexOf("CDATA[");
		int e = s.indexOf(']', b);
		if (b == -1 || e == -1) {
			return "";
		}
		return s.substring(b+6, e);
	}
	
	public static String executeBaidu(String s) {
		int b = s.indexOf("dst");
	int e = s.indexOf('}', b);
		if (b == -1 || e == -1) {
			return "";
		}
		String a = s.substring(b+6, e-1);
		try {
			String ss = decodeUnicode(a);
			return ss;
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}
	
	public static String executeJinshan(String s) {
		s = s.replace("</div>", "");
		s = s.replace("\t", "");
		s = s.replace(" ", "");
		return s;
	}
	
	public static String delHTMLTag(String htmlStr){
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        String regEx_html = "<[^>]+>";
         
        Pattern p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
        Matcher m_script = p_script.matcher(htmlStr); 
        htmlStr = m_script.replaceAll("");
         
        Pattern p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
        Matcher m_style = p_style.matcher(htmlStr); 
        htmlStr = m_style.replaceAll("");
         
        Pattern p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
        Matcher m_html = p_html.matcher(htmlStr); 
        htmlStr = m_html.replaceAll("");
		
		htmlStr = htmlStr.replaceAll("<p .*?>", "\r\n");
		htmlStr = htmlStr.replaceAll("<br\\s*/?>", "\r\n"); 
		htmlStr = htmlStr.replaceAll("\\<.*?>", "");

        return htmlStr.trim();
	}
	
	public static String findMeaning(String filename, int website) throws Exception {
		String url = " ";
		String url1 = "";
		String tag1 = "";	//beginning of phonetic
		String tag2 = "";	//end of phonetic
		String tag3 = "";	//beginning of meaning
		String tag4 = "";	//end of meaning
		String phonetic = "";
		String meaning = "";
		if (website == BAIDU) {
			url = "http://api.fanyi.baidu.com/api/trans/vip/translate?q=";
			url1 = "&from=en&to=zh&appid=20161125000032746&salt=1435660289&sign=" + getMD5("20161125000032746"+filename+"1435660289"+"I01RGa7OMNe80kJ_Yc0a");
			tag1 = " ";
			tag2 = " ";
			tag3 = " ";
			tag4 = " ";
		}
		else if (website == JINSHAN) {
			url = "http://dict.cn/";
			tag1 = "phonetic";
			tag2 = "div";
			tag3 = "dict";
			tag4 = "dict-chart";
			//tag4 = "style";
		}
		else if (website == YOUDAO) {
			url = "http://fanyi.youdao.com/openapi.do?keyfrom=dictionary123&key=630620708&type=data&doctype=xml&version=1.1&q=";
			tag1 = "phonetic";
			tag2 = "phonetic";
			tag3 = "explains";
			tag4 = "explains";
		}
		else {
			return url;
		}
		
		String line = "";
		boolean isBeginning = false;
		URL u;
		try{
			if (website != BAIDU){
				u = new URL(url+filename);
			}
			else {
				u = new URL(url+filename+url1);
			}
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(2000);
			if (connection.getResponseCode() == 200) {
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				Pattern pattern = Pattern.compile("<.*>");
				Matcher matcher = null;
				
				//phonetic
				while ((line = reader.readLine()) != null) {
					if (website == BAIDU) {
						line = executeBaidu(line);
						//System.out.println(line);
						return line;
					}
					
					matcher = pattern.matcher(line);
					if (isBeginning){
						
						if (line.indexOf(tag2) != -1){	//reaches end
							isBeginning = false;
							if (website == YOUDAO) {
								line = executeYoudao(line);
							}
							if (website == JINSHAN) {
								//System.out.println(line);
								line = executeJinshan(line);
							}
							if (line != "" && line != "\n") {
								phonetic += "\n"+line;
							}
							break;
						}
						
						if (website == YOUDAO) {
							line = executeYoudao(line);
						}
						
						if (website == JINSHAN) {
							//System.out.println(line);
							line = delHTMLTag(line);
							System.out.println(line);
						}
						
						if (line != "" && line != "\n") {
							phonetic += "\n"+line;
						}
					}
					else {
						if (line.indexOf(tag1) != -1){	//reaches beginning
							isBeginning = true;
							continue;
						}
					}
				}
				
				isBeginning = false;
				
				//meaning
				int lin = 0;
				while ((line = reader.readLine()) != null) {
					lin++;
					if (website != YOUDAO && lin < 10) {
						//System.out.println(line);
					}
					matcher = pattern.matcher(line);
					if (isBeginning){
						if (line.indexOf(tag4) != -1){	//reaches end
							isBeginning = false;
							if (website == YOUDAO) {
								line = executeYoudao(line);
							}
							if (website == JINSHAN) {
								line = delHTMLTag(line);
								line = executeJinshan(line);
								System.out.println(line);
							}
							if (line != "" && line != "\n"){
								meaning += "\n"+line;
							}
							break;
						}
						if (website == YOUDAO) {
							line = executeYoudao(line);
						}
						if (website == JINSHAN) {
							System.out.println(line);
							line = delHTMLTag(line);
							line = executeJinshan(line);
							
						}
						if (line != "" && line != "\n") {
							meaning += "\n"+line;
						}
					}
					else {
						if (line.indexOf(tag3) != -1){	//reaches beginning
							isBeginning = true;
							if (website == JINSHAN) {
								//line = delHTMLTag(line);
								System.out.println(line+"**************");
							}
							
							continue;
						}
					}
				}
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
		return "phonetic:" + phonetic + "meaning:" + meaning;
	}
	
	public static void main(String[] args) throws Exception {		
		ArrayList wordlist = new ArrayList();
		
		DataSave d = DataSave.getInstance();
		
		//the whole frame
		JFrame frame = new JFrame("Dictionary");
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				d.logout();
				System.exit(0);
			}
		});
		
		//place to input words and click button
		JPanel inputArea = new JPanel();
		JButton search = new JButton("search");
		JTextField userInput = new JTextField(20);
		inputArea.add(userInput, BorderLayout.EAST);
		inputArea.add(search, BorderLayout.WEST);	
		
		//place to choose website
		JCheckBox chooseBaidu = new JCheckBox("baidu", true);
		JCheckBox chooseJinshan = new JCheckBox("jinshan", true);
		JCheckBox chooseYoudao = new JCheckBox("youdao", true);
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
		
		meaning.setLayout(new GridLayout(1, 3));
		meaning.add(baidu);
		meaning.add(jinshan);
		meaning.add(youdao);
		
		JPanel log = new JPanel();
		JButton register = new JButton("register");
		JButton login = new JButton("Log in");
		JButton sendCard = new JButton("send word card");
		JButton showCard = new JButton("show word cards");
		log.setLayout(new GridLayout(1, 4));
		log.add(register);
		log.add(login);
		log.add(sendCard);
		log.add(showCard);
		
		//add to main frame
		frame.add(inputArea, BorderLayout.NORTH);
		frame.add(meaning, BorderLayout.CENTER);
		frame.add(log, BorderLayout.SOUTH);
		
		frame.setVisible(true);
		
		sendCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				String username = "";
				String word = "";
				username = JOptionPane.showInputDialog("please input username:");
				word = JOptionPane.showInputDialog("please input word:");
				d.sendCard(username, word);
				JOptionPane a = new JOptionPane();
				a.showMessageDialog(null, "Your card has been successfully sent", "Thank you", JOptionPane.PLAIN_MESSAGE);
			}
		});
		
		showCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if (!(d.isLoggedIn())) {
					JOptionPane a = new JOptionPane();
					a.showMessageDialog(null, "You must be logged in first!", "No cards", JOptionPane.PLAIN_MESSAGE);
					return;
				}
				String[][] card = d.showcards();
				
				JFrame c = new JFrame("cards");
				JTextArea lrc = new JTextArea("");
				lrc.setLineWrap(true);
				JScrollPane scroll = new JScrollPane(lrc);
				scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
				scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				scroll.setVisible(true);
				lrc.setBackground(new Color(240,240,240));
				lrc.setEditable(false);
				lrc.setLineWrap(true);
				
				for (int i = 0; i < card[0].length; i++) {
					lrc.append(card[0]+"\t"+card[1]);
				}
				c.add(lrc);
				c.setVisible(true);
			}
		});
		
		register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if (!(d.isLoggedIn())) {
					JOptionPane a = new JOptionPane();
					a.showMessageDialog(null, "You must be logged in first!", "No cards", JOptionPane.PLAIN_MESSAGE);
					return;
				}
				String username = "";
				String password = "";
				username = JOptionPane.showInputDialog("please input username:");
				password = JOptionPane.showInputDialog("please input password:");
				if (d.register(username, password)) {
					JOptionPane a = new JOptionPane();
					a.showMessageDialog(null, "register success", "", JOptionPane.PLAIN_MESSAGE);
				}
				else {
					JOptionPane a = new JOptionPane();
					a.showMessageDialog(null, "register failed", "", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				String username = "";
				String password = "";
				username = JOptionPane.showInputDialog("please input username:");
				password = JOptionPane.showInputDialog("please input password:");
				if (d.login(username, password)) {
					JOptionPane a = new JOptionPane();
					a.showMessageDialog(null, "login success",
					"", JOptionPane.PLAIN_MESSAGE);
				}
				else {
					JOptionPane a = new JOptionPane();
					a.showMessageDialog(null, "login failed",
					"", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		//show meaning by clicking
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				String newWord = userInput.getText();
				String bMeaning = newWord;	//baidu meaning
				String jMeaning = newWord;	//jinshan meaning
				String yMeaning = newWord;	//youdao meaning
				
				numOfBaiduGood = d.baiduCount(newWord);
				numOfJinshanGood = d.jinshanCount(newWord);
				numOfYoudaoGood = d.youdaoCount(newWord);
				
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
					try {
						bMeaning = findMeaning(newWord, BAIDU);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
					baiduMeaning.setText("Baidu:\n" + bMeaning);
				}
				else {
					baiduMeaning.setText("");
				}
				
				if (chooseJinshan.isSelected() ) {
					try {
						jMeaning = findMeaning(newWord, JINSHAN);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
					jinshanMeaning.setText("Jinshan:\n" + jMeaning);
				}
				else {
					jinshanMeaning.setText("");
				}
				
				if (chooseYoudao.isSelected() ){
					try {
						yMeaning = findMeaning(newWord, YOUDAO);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
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
				d.baiduClick(userInput.getText());
			}
		});
		jinshanGood.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				numOfBaiduGood++;
				d.jinshanClick(userInput.getText());
			}
		});
		youdaoGood.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				numOfBaiduGood++;
				d.youdaoClick(userInput.getText());
			}
		});
 	}
}

/*******************
TODO:
build a server, storage the time of clicking good
, username and password
********************/
