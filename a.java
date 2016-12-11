import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.media.*;
import javax.swing.event.*;
import javax.sound.sampled.*;
import it.sauronsoftware.jave.*;
import sun.audio.*;

public class a extends JFrame{	
	static JFrame frame = new JFrame("Music Player");
	static final int EXTERNAL_BUFFER_SIZE = 1280;	//read how much data once
	static BorderLayout borderlayout = new BorderLayout();
	static ImageIcon defaultcover = new ImageIcon("defaultcover.png");
	static int volumeNow = 50;
	static float volumeStep = 10;
	static boolean onGoing = false;
	static boolean keepProcedure = false;
	static boolean musicChoosen = false;
	static int currentNumber = 0;
	static int totalNumber = 0;
	static int musicPlayed = 0;
	static ArrayList<String> playList = new ArrayList<String>();	//current playlist
	static String Filename = "";
	static ArrayList<String> lyricList = new ArrayList<String>();
	static int lyricLine = 0;
	
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
	
	public static float setVolume(int vo){
		if (vo == 0){
			return -80;
		}
		if (vo == 50){
			return 0;
		}
		if (vo < 50){
			return (float)(50-vo) * -1;
		}
		else{
			return (float)(vo-50) * 6 / 50;
		}
	}
	
	public static String findFilename(String filename){
		int dot = filename.lastIndexOf('.');
		String shortFilename = filename.substring(0, dot);
		return shortFilename;
	}
	
	public static String findAbsoluteFilename(String filename){
		int dot = filename.lastIndexOf('.');
		int slash = filename.lastIndexOf('\\');
		String shortFilename = filename.substring(slash+1, dot);
		return shortFilename;
	}
	
	public static String findFilenameAppendix(String filename){
		int dot = filename.lastIndexOf('.');
		String shortFilename = "";
		if (dot+4 < filename.length()){
			shortFilename = filename.substring(dot+1, dot+4);
		}
		else{
			shortFilename = filename.substring(dot+1, filename.length());
		}
		return shortFilename;
	}
	
	public static ArrayList<String> findLyricByName(String filename) throws Exception {
		lyricLine = 0;
		ArrayList<String> lrcList = new ArrayList<String>();
		String line = "";
		boolean isPoem = false;
		try{
			URL url = new URL("https://zh.moegirl.org/"+filename);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(2000);
			if (connection.getResponseCode() == 200) {
				InputStream inputStream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				Pattern pattern = Pattern.compile("<.*>");
				Matcher matcher = null;
				while ((line = reader.readLine()) != null) {
					matcher = pattern.matcher(line);
					if (isPoem){
						if (line.indexOf("poem") != -1){
							break;
						}
						if (matcher.find()) {
							line = delHTMLTag(line);
							lrcList.add(line);
							lyricLine++;
						}
						else {
							System.out.println(line);
							lrcList.add(line);
							lyricLine++;
						}
					}
					else {
						if (line.indexOf("poem") != -1){
							isPoem = true;
							continue;
						}
					}
				}
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return lrcList;
	}
	
	//core function for playing
	public static void play(String Filename) throws Exception{ 
		String fileType = findFilenameAppendix(Filename);
		if (fileType.equals("wav") ){
			boolean interrupt = false;
			File soundFile = new File(Filename);
			AudioInputStream audioInputStream = null;
			try{
				audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			}
			catch (Exception e){
				e.printStackTrace();
			}
			AudioFormat	audioFormat = audioInputStream.getFormat();
			SourceDataLine line = null;
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			
			try{
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(audioFormat);
			}
			catch (LineUnavailableException e){
				e.printStackTrace();
			}
			catch (Exception e){
				e.printStackTrace();
			}
			line.start();
			
			int	nBytesRead = 0;
			byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
			while (nBytesRead != -1){
				try{
					nBytesRead = audioInputStream.read(abData, 0, abData.length);
				}
				catch (IOException e){
					e.printStackTrace();
				}
				if (nBytesRead >= 0){
					int	nBytesWritten = line.write(abData, 0, nBytesRead);
				}
				if (!keepProcedure){
					line.drain();
					line.close();
					return;
				}
				
				FloatControl volctrl=(FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);   
				float a = setVolume(volumeNow);
				volctrl.setValue(a);
				//System.out.println(a);
			
				if (!onGoing){
					while(true){
						if (onGoing){
							break;
						}
						if (!keepProcedure){
							line.drain();
							line.close();
							return;
						}
						System.out.println("waiting...");
					}
				}
			}
			line.drain();
			line.close();
			return;
		}
		else if (fileType.equals("mp3")) {
			
		}
		else if (fileType.equals("amr")) {
			changeToWAV(Filename, Filename+".wav");
			play(Filename+".wav");
			File file = new File(Filename+".wav");
		}
	}
	
	public static boolean changeToMp3(String sourcePath, String targetPath) {  
        File source = new File(sourcePath);  
        File target = new File(targetPath);  
        AudioAttributes audio = new AudioAttributes();  
        Encoder encoder = new Encoder();  
  
        audio.setCodec("libmp3lame");  
        EncodingAttributes attrs = new EncodingAttributes();  
        attrs.setFormat("mp3");  
        attrs.setAudioAttributes(audio);  
  
        try {  
            encoder.encode(source, target, attrs);  
        } catch (IllegalArgumentException e) {  
            e.printStackTrace();  
        } catch (InputFormatException e) {  
            e.printStackTrace();  
        } catch (EncoderException e) {  
            e.printStackTrace();  
        }
		return true;		
    }  
	
	public static boolean changeToWAV(String sourcePath, String targetPath) {  
        File source = new File(sourcePath);  
        File target = new File(targetPath);  
        AudioAttributes audio = new AudioAttributes();  
        Encoder encoder = new Encoder();  
  
        audio.setCodec("libmp3lame");  
        EncodingAttributes attrs = new EncodingAttributes();  
        attrs.setFormat("wav");  
        attrs.setAudioAttributes(audio);  
  
        try {  
            encoder.encode(source, target, attrs);  
        } catch (IllegalArgumentException e) {  
            e.printStackTrace();  
        } catch (InputFormatException e) {  
            e.printStackTrace();  
        } catch (EncoderException e) {  
            e.printStackTrace();  
        }  
		return true;
    } 
	
	public static void main(String[] args) throws Exception {
		
		//set main frame
		frame.setSize(1000, 700);
		//frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(1, 2));
		
		//lyric on the left
		JPanel lrcPanel = new JPanel();
		JTextArea lrc = new JTextArea("");
		lrc.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(lrc);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setVisible(true);
		lrc.setBackground(new Color(102, 204, 255));
		lrc.setEditable(false);
		lrc.setLineWrap(true);
		
		//others on the right
		JPanel rightPlay = new JPanel();
		JPanel mainPlay = new JPanel();
		JPanel upPlay = new JPanel();
		JPanel middlePlay = new JPanel();
		JPanel downPlay = new JPanel();
		JPanel visualPlay = new JPanel();
		JPanel visualPlay1 = new JPanel();
		JPanel visualControl = new JPanel();
		JPanel upLoadPlay = new JPanel();
		JPanel otherPlay = new JPanel();
		JLabel coverImage = new JLabel(defaultcover);
		
		JButton startButton = new JButton("->");
		startButton.setSize(8, 8);
		JButton pauseButton = new JButton("||");
		pauseButton.setSize(8, 8);
		JButton stopButton = new JButton("[]");
		stopButton.setSize(8, 8);
		JButton loadButton = new JButton("LOAD");
		stopButton.setSize(12, 8);
		JSlider volume = new JSlider();
		JCheckBox oneCircle = new JCheckBox("Circle", false);
		JButton saveList = new JButton("Save current list");
		saveList.setSize(12, 8);
		JComboBox musicList = new JComboBox();
		JButton convert2Mp3 = new JButton("convert to mp3");
		JButton convert2WAV = new JButton("convert to wav");
		JButton upLoadList = new JButton("upload playlist");
		JButton upLoadFile = new JButton("upload file");
		JButton downloadList = new JButton("download playlist");
		JButton downloadFile = new JButton("download file");
		
		visualPlay.setLayout(new GridLayout(1, 1));
		//final visual spec = new visual(); 
		final visual_osu spec = new visual_osu(); 		
		visualPlay.add(spec);
		new Thread(spec).start();

		mainPlay.setLayout(new GridLayout(1, 3));
		upPlay.add(startButton);
		upPlay.add(pauseButton);
		upPlay.add(stopButton);
		visualControl.setLayout(new GridLayout(1, 2));
		middlePlay.setLayout(new GridLayout(1, 2));
		middlePlay.add(oneCircle);
		middlePlay.add(volume);
		downPlay.setLayout(new GridLayout(1, 3));
		downPlay.add(loadButton);
		downPlay.add(musicList);
		downPlay.add(saveList);
		otherPlay.setLayout(new GridLayout(1, 2));
		otherPlay.add(convert2Mp3);
		otherPlay.add(convert2WAV);
		upLoadPlay.setLayout(new GridLayout(1, 4));
		upLoadPlay.add(upLoadList);
		upLoadPlay.add(upLoadFile);
		upLoadPlay.add(downloadFile);
		upLoadPlay.add(downloadList);
		
		mainPlay.setLayout(new GridLayout(6, 1));
		
		mainPlay.add(upPlay);
		mainPlay.add(visualPlay);
		mainPlay.add(middlePlay);
		mainPlay.add(downPlay);
		mainPlay.add(upLoadPlay);
		mainPlay.add(otherPlay);
		
		rightPlay.setLayout(new GridLayout(2, 1));
		rightPlay.add(coverImage);
		rightPlay.add(mainPlay);
		
		frame.add(scroll);
		frame.add(rightPlay);
		
		frame.setVisible(true);
		
		upLoadFile.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDialog fileDialog = new FileDialog(frame);
				fileDialog.setVisible(true);
				String filename = fileDialog.getDirectory() + fileDialog.getFile();
				if (fileDialog.getFile() != null){
					//TODO: upLoadFile
					a.showMessageDialog(null, "upload complete", "upload", JOptionPane.PLAIN_MESSAGE);
				}
				else {
					//a.showMessageDialog(null, "file not found", "change", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		
		upLoadList.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filename = "playlist.txt";
				File file = new File(filename);
				if (file.exists()){
					//TODO: upLoadFile
					a.showMessageDialog(null, "upload complete", "upload", JOptionPane.PLAIN_MESSAGE);
				}
				else {
					a.showMessageDialog(null, "list not found, maybe you should save current list first",
					"upload", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		
		convert2Mp3.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDialog fileDialog = new FileDialog(frame);
				fileDialog.setVisible(true);
				String filename = fileDialog.getDirectory() + fileDialog.getFile();
				if (fileDialog.getFile() != null){
					change c = new change(filename, filename+".mp3", 1);
					Thread t2 = new Thread(c);
					t2.start();
				}
				else {
					JOptionPane a = new JOptionPane();
					a.showMessageDialog(null, "file not found", "change", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		
		convert2WAV.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDialog fileDialog = new FileDialog(frame);
				fileDialog.setVisible(true);
				String filename = fileDialog.getDirectory() + fileDialog.getFile();
				if (fileDialog.getFile() != null){
					change c = new change(filename, filename+".wav", 2);
					Thread t2 = new Thread(c);
					t2.start();
				}
				else {
					JOptionPane a = new JOptionPane();
					a.showMessageDialog(null, "file not found", "change", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		
		volume.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				volumeNow = volume.getValue();
			}
		});
		
		saveList.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PrintWriter output = new PrintWriter(new File("playlist.txt"));
					int i = 0;
					while (i < totalNumber){
						output.println((String)(playList.get(i)));
						i++;
					}
					output.close();
					JOptionPane a = new JOptionPane();
					a.showMessageDialog(null, "Save complete", "save", JOptionPane.PLAIN_MESSAGE);
				}
				catch (IOException ex) {  
					ex.printStackTrace();  
				}
			}
		});
		
		loadButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDialog fileDialog = new FileDialog(frame);
				fileDialog.setVisible(true);
				String filename = fileDialog.getDirectory() + fileDialog.getFile();
				if (fileDialog.getFile() != null){
					playList.add(filename);
					musicList.addItem(filename);
					totalNumber++;
				}
			}
		});
		
		startButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onGoing = true;
				keepProcedure = true;
			}
		});
		
		pauseButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onGoing = false;
				keepProcedure = true;
			}
		});
		
		stopButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onGoing = false;
				keepProcedure = false;
			}
		});
		
		//choose music from combo box
		musicList.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e){
				currentNumber = musicList.getSelectedIndex();
				onGoing = false;
				keepProcedure = false;
				musicChoosen = true;
			}
		});
		
		//default playlist
		File sourceFile = new File("playlist.txt");
		if (!sourceFile.exists()){
			if (args.length >= 1){	
				//use current song as list and store it into disk
				PrintWriter output = new PrintWriter(sourceFile);
				output.println(args[0]);
				playList.add(args[0]);
				totalNumber++;
				output.close();
			}
			else{
				//open the list manually
				JOptionPane a = new JOptionPane();
				a.showMessageDialog(null, "Please search and open songs", "No songs found", JOptionPane.PLAIN_MESSAGE);
			}
		}
		else{
			//read song from filelist
			Scanner input = new Scanner(sourceFile);
			while (input.hasNext()){
				String s = input.nextLine();
				playList.add(s);
				musicList.addItem(s);
				totalNumber++;
			}
		}
		
		
		while (true){
			
			//take filename to play it
			if (playList.size() == 0){
				while (true) {
					System.out.println("waiting...");
					if (playList.size() != 0){
						break;
					}
				}
			}
			Filename = (String)(playList.get(currentNumber));
			
			File f = new File(Filename);
			if (!(f.exists())){
				playList.remove(currentNumber);
				totalNumber--;
				JOptionPane a = new JOptionPane();
				a.showMessageDialog(null, "Failed to open", "No songs found", JOptionPane.PLAIN_MESSAGE);
				continue;
			}
			
			//System.out.println(currentNumber + " " + totalNumber);
			//play current music
			try{
				if (onGoing & keepProcedure){
					
					lyricList = findLyricByName(findAbsoluteFilename(Filename));
					String nameProfix = findFilename(Filename);
					lrc.setText(findAbsoluteFilename(Filename) +"."+ findFilenameAppendix(Filename) + "\n");
					
					//read cover and lyric
					
					File lrcFile = new File(nameProfix+".txt");
					if (lrcFile.exists()) {
						Scanner input = new Scanner(lrcFile);
						while (input.hasNext()){
							String s = input.nextLine();
							lrc.append("\n"+s);
						}
					}
					else {
						int currentLine = 0;
						PrintWriter output = new PrintWriter(lrcFile);
						while (true) {
							if (currentLine >= lyricLine){
								break;
							}
							output.println(lyricList.get(currentLine));
							lrc.append("\n"+lyricList.get(currentLine));
							currentLine++;
						}
						output.close();
					}
					
					while (true){
						File file = new File(nameProfix+".jpg");
						if (file.exists()){
							defaultcover = new ImageIcon(nameProfix+".jpg");
							break;
						}
						file = new File(nameProfix+".jpeg");
						if (file.exists()){
							defaultcover = new ImageIcon(nameProfix+".jpeg");
							break;
						}
						file = new File(nameProfix+".bmp");
						if (file.exists()){
							defaultcover = new ImageIcon(nameProfix+".bmp");
							break;
						}
						file = new File(nameProfix+".png");
						if (file.exists()){
							defaultcover = new ImageIcon(nameProfix+".png");
							break;
						}
						file = new File(nameProfix+".gif");
						if (file.exists()){
							defaultcover = new ImageIcon(nameProfix+".gif");
							break;
						}
						if (!file.exists()){
							defaultcover = new ImageIcon("defaultcover.png");
						}
						break;
					}
					coverImage.setIcon(defaultcover);
					
					frame.setVisible(true);
					
					//everything is ready. starts playing
					frame.setVisible(true);
					if (musicPlayed == 0){
						musicChoosen = false;
					}
					play(Filename);
					musicPlayed++;
					if (!musicChoosen) {
						if (!oneCircle.isSelected()){	
							currentNumber++;	//one music circle
						}
						if (currentNumber >= totalNumber){
							currentNumber = 0;
						}
					}
					else{
						musicChoosen = false;
					}
				}
				
			}
			catch(FileNotFoundException e){ 
				System.out.print("FileNotFoundException "); 
			}
		
			catch(IOException e){ 
				System.out.print("error occurred"); 
			}
		}
	}
}

class waitlist implements Runnable {
	
	public JDialog await;
	
	public void run(JFrame frame) {
		await = new JDialog(frame, "converting");
		await.setBackground(new Color(102, 204, 255));
		JProgressBar progress = new JProgressBar();
		progress.setOrientation(JProgressBar.HORIZONTAL);
		progress.setPreferredSize(new Dimension(300, 20));
		progress.setString("converting...");
		progress.setStringPainted(true);
		progress.setBorderPainted(true);
		progress.setIndeterminate(true);
		await.setSize(300,60);
		await.setLocationRelativeTo(null);
		await.getContentPane().add(progress);
		await.setVisible(true);
	}
	
	@Override
	public void run() {
		await = new JDialog();
		await.setBackground(new Color(102, 204, 255));
		JProgressBar progress = new JProgressBar();
		progress.setOrientation(JProgressBar.HORIZONTAL);
		progress.setPreferredSize(new Dimension(300, 20));
		progress.setString("converting...");
		progress.setStringPainted(true);
		progress.setBorderPainted(true);
		progress.setIndeterminate(true);
		await.setSize(300,60);
		await.setLocationRelativeTo(null);
		await.getContentPane().add(progress);
		await.setVisible(true);
	}
	
	public void stop() {
		await.dispose();
	}
	
}

class change implements Runnable {
	
	public File source;
	public File target;
	public JDialog await;
	public String sourcePath;
	public String targetPath;
	public int targetFormat;
	public AudioAttributes audio;
	Encoder encoder;
	EncodingAttributes attrs;
	
	change(String s, String t, int f) {
		source = new File(s);
		sourcePath = s;
		targetPath = t;
		target = new File(t);
		audio = new AudioAttributes();  
        encoder = new Encoder(); 
		audio.setCodec("libmp3lame");  
        attrs = new EncodingAttributes(); 
		if (f == 1) {
			attrs.setFormat("mp3"); 
		}
		else {
			attrs.setFormat("wav"); 
		}
		attrs.setAudioAttributes(audio);
	}
	
	//if f == 1 convert to mp3;
	//if f == 2 convert to wav;
	
	@Override
	public void run() {
		try {  
		
			//preparing for progress
			await = new JDialog();
			await.setBackground(new Color(102, 204, 255));
			JProgressBar progress = new JProgressBar();
			progress.setOrientation(JProgressBar.HORIZONTAL);
			progress.setPreferredSize(new Dimension(300, 20));
			progress.setString("converting...");
			progress.setStringPainted(true);
			progress.setBorderPainted(true);
			progress.setIndeterminate(true);
			await.setSize(300,60);
			await.setLocationRelativeTo(null);
			await.getContentPane().add(progress);
			await.setVisible(true);
			
			//real encoding
			source = new File(sourcePath);
			target = new File(targetPath);
            encoder.encode(source, target, attrs); 

			//finishing encoding, clear
			await.dispose();
			
			JOptionPane a = new JOptionPane();
			a.showMessageDialog(null, "change complete\n" + targetPath, "change", JOptionPane.PLAIN_MESSAGE);
			
        } catch (IllegalArgumentException e) {  
            e.printStackTrace();  
        } catch (InputFormatException e) {  
            e.printStackTrace();  
        } catch (EncoderException e) {  
            e.printStackTrace();  
        }
	}
}
