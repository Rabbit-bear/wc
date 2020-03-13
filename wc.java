package wc;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class wc {
	LinkedList<String> Message = new LinkedList<>();//构造输出总信息
	public  boolean cheakcode(String line) {//检测一行字符串是否是代码行
		boolean iscode = false;
		boolean exitcode = false;
		boolean exitnotes = false;
		if(line.length()!=0) {
			char[] cs = line.toCharArray();
			for(int i=0;i<cs.length;i++) {
				if((i+1<cs.length)&&(cs[i]=='/')&&(cs[i+1]=='/')) {
					exitnotes = true;
				}
				if ((('a'<=cs[i]&&cs[i]<='z')||('A'<=cs[i]&&cs[i]<='Z'))&&(!exitnotes)) {//检测是否出现字母
					exitcode = true;			
				}
				if(exitcode&&(!exitnotes)) {
					iscode = true;
					break;
				}
			}
		}
		return iscode;
	}
	public  boolean cheaknotes(String line) {//检测一行字符串是否含有注释
		boolean isnotes = false;//作为返回判断标识
		if(line.length()!=0) {//当该行长度不为0是进行操作
			char[] cs = line.toCharArray();
			for(int i=0;i<cs.length;i++) {
				if((i+1<cs.length)&&(cs[i]=='/')&&(cs[i+1]=='/')) {//若出现相邻的的两个/即可判断该行含有注释
					isnotes = true;
					break;
				}
			}
		}
		return isnotes;
	}
	public  boolean cheakempty(String line) {//检测一行字符串是否是空行
		boolean isempty = true;//作为返回值
		boolean exit = false;//记录是否出现{或者}
		if(line.length()!=0) {
			char[] cs = line.toCharArray();
			for(int i=0;i<cs.length;i++) {	
				if(cs[i]!=' '&&cs[i]!='{'&&cs[i]!='}') {//若该行出现了空格，控制字符或至多一个{，}以外的字符即可判断不为空行
					isempty = false;
					break;
				}
				else if((cs[i]=='{'||cs[i]=='}')&&(!exit)){//第一次出现{或}
					exit = true;
				}
				else if((cs[i]=='{'||cs[i]=='}')&&(exit)){//第二次出现{或}，即可判断不符合空行标准
					isempty = false;
					break;
				}
			}
		}
		return isempty;
	}
	public  String HanldFile(File file,LinkedList<String> parameter) {//统计更复杂的数据（代码行 / 空行 / 注释行）
		int AllLine = 0;//统计总代码行数
		int codeline = 0;//统计代码行
		int emptyline = 0;//统计空行
		int notesline = 0;//统计注释行
		int CharAmount = (int)file.length();//字符数
		int word = 0;
		String line;
		
		if(parameter.getFirst().equals("-s")||!file.isFile()) {//处理多个文件
			SearchFile(file, parameter);
			return null;
		}
		
		String Output = String.format("文件：%s 的相关信息：%n",file.getAbsoluteFile());//构造输入信息
		try (
				FileReader fReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fReader);
			){	
			while((line=bufferedReader.readLine())!=null) {//检测读取文本是否应当结束
				int count = 0;//记录每一行的单词数
				boolean notfound = true;//当前遍历字符不是英文的标识
				char[] sentence = line.toCharArray();
				for(int i=0;i<sentence.length;i++) {
					//当前字符是字母的标识
					boolean cheakletters = (('a'<=sentence[i]&&sentence[i]<='z')||('A'<=sentence[i]&&sentence[i]<='Z'));
					if(cheakletters&&notfound) {//读取到单词第一个字母
						count++;
						notfound = false;
					}
					else if((!cheakletters)&&(!notfound)) {//读取单词的下一个非字母字符
						notfound = true;
					}
				}
				word += count;
				if(cheakempty(line)) {//检测是否空行
					emptyline++;
				}
				else if(cheakcode(line)) {//若不为空行，检测是否代码行					
					codeline++;
				}
				else if(cheaknotes(line)) {//若不为空行且不为代码行，检测是否含有注释行，若含有即可判断为注释行
					notesline++;
				}
				AllLine++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("找不到文件");
		}catch (IOException e) {
			e.printStackTrace();
			System.out.println("打开文件失败");
		}
		for(int i=0;i<parameter.size();i++) {
			switch (parameter.get(i)) {
			case "-c": //返回文件 file.c 的字符数
				Output += String.format("字符数：%d  ",CharAmount);
				break;
			case "-w": //返回文件 file.c 的单词数
				Output += String.format("单词数：%d  ",word);
				break;
			case "-l": //返回文件 file.c 的行数
				Output += String.format("行数：%d  ",AllLine);
				break;
			case "-a"://返回更复杂的数据（代码行 / 空行 / 注释行）
				Output += String.format("代码行：%d  空行：%d  注释行：%d%n",codeline,emptyline,notesline);
				break;
			default :
				System.out.println("参数输入有误，无法提供信息");
			}
		}
		Message.add(Output);//添加信息
		System.out.println(Output);//输出总需求信息
		return Output;
	}
	public  void printMessage(JPanel panel,String sentence,int count) {//给面板添加信息内容
		JLabel label = new JLabel(sentence);
		label.setBounds(10, 5+20*count, 10*sentence.length(), 20);
		panel.add(label);
	}
	public  void GUIgetFile() {//图形界面显示
		JFrame frame = new JFrame("wc.exe");//设置父窗体
		frame.setSize(500, 250);
		frame.setLocation(400,200);
		frame.setLayout(null);
		frame.setResizable(false);
		
		JDialog showMessage = new JDialog(frame);//设置文件信息显示窗体
		showMessage.setSize(500, 250);
		showMessage.setLocation(400,200);
		showMessage.setTitle("文件信息");
		showMessage.setLayout(null);
		showMessage.setModal(true);
		showMessage.setVisible(false);
		showMessage.setResizable(false);
		
		JPanel panel = new JPanel();//基本面板
		panel.setLayout(null);
		
		
		JScrollPane scrollPane = new JScrollPane(panel);//设置带有滑条的面板
		showMessage.setContentPane(scrollPane);
		
		JLabel tip = new JLabel("文件路径");//文字提示
		tip.setBounds(50, 10, 100, 100);
		
		JTextField path = new JTextField();//文件路径输入框
		path.setPreferredSize(new Dimension(80,30));
		path.setBounds(120, 45, 250, 30);
		
		
		JRadioButton[] setele = new JRadioButton[5];//选项按钮
		setele[1] = new JRadioButton("字符数");
		setele[2] = new JRadioButton("单词数");
		setele[3] = new JRadioButton("行数");
		setele[4] = new JRadioButton("详细行信息");
		setele[0] = new JRadioButton("递归");
		for(int i=0;i<setele.length;i++) {
			int length = setele[i].getText().length();
			setele[i].setSelected(false);
			setele[i].setBounds(20+i*90, 100, 90, 20);
			frame.add(setele[i]);
		}
		
		JOptionPane warn = new JOptionPane("错误提示");
		
		JButton showHistory = new JButton("历史记录");//查看历史记录按钮
		showHistory.setBounds(300, 150, 100, 30);
		showHistory.addActionListener(new ActionListener() {//按钮监听器
			@Override
			public void actionPerformed(ActionEvent e) {
				showMessage.setVisible(true);
			}
		});
		
		JButton search = new JButton("开始");//开始按钮
		search.setBounds(100, 150, 100, 30);
		search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(path.getText());
				LinkedList<String> parameter = new LinkedList<>();
				//构造参数集合
				if(setele[0].isSelected()) {
					parameter.add("-s");
				}if(setele[1].isSelected()) {
					parameter.add("-c");
				}if(setele[2].isSelected()) {
					parameter.add("-w");
				}if(setele[3].isSelected()) {
					parameter.add("-l");
				}if(setele[4].isSelected()) {
					parameter.add("-a");
				}
				//错误处理
				if(new String(path.getText()).length()==0) {
					warn.showMessageDialog(showMessage, "未选择文件");
					return;
				}
				System.out.println(path.getText().trim().length());
				if(!setele[0].isSelected()&&!setele[1].isSelected()&&!setele[4].isSelected()&&!setele[2].isSelected()&&!setele[3].isSelected()) {
					warn.showMessageDialog(showMessage, "请选择功能");
					return;
				}
				if(file.isFile()&&setele[0].isSelected()) {
					warn.showMessageDialog(showMessage, "文件不可递归");
					return;
				}
				System.out.println(path.getText());
				System.out.println(path.getText().length());
				
				for(int i=0;i<setele.length;i++) {//重置按钮
					setele[i].setSelected(false);
				}
				Date now = new Date();
				Message.add("当前时间： "+now.toString());
				
				SearchFile(file, parameter);//执行文件操作
				
				while(true) {//等待线程结束
					if(Thread.activeCount()==2) {
						break;
					}
				}
				int MaxWordAmount = 0;//记录最大输出文本长度
				for(int i=0;i<Message.size();i++) {
					if(MaxWordAmount<Message.get(i).length()) {
						MaxWordAmount=Message.get(i).length();
					}
					printMessage(panel, Message.get(i), i);
				}
				panel.setPreferredSize(new Dimension(MaxWordAmount*9,20*(Message.size()-10)+220));//定义面板尺寸
				showMessage.setVisible(true);//显示文本信息视图
				
			}
		});
		
		JButton openFile = new JButton("···");//图形化文件选择按钮
		openFile.setBounds(400, 45, 20, 30);
		openFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jfc.showDialog(new JLabel(), "选择");
				if(jfc.getSelectedFile()!=null) {
					path.setText(jfc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		//添加组件至父窗体
		//frame.add(box);
		frame.add(search);
		frame.add(openFile);
		frame.add(tip);
		frame.add(path);
		frame.add(showHistory);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	public  boolean Compare(File file1,File file2) {//比较两文件名是否匹配，包括通配符的判断，file1为输入文件名创建的文件对象，file2为真实存在文件
		boolean same = true;//返回值
		int i,m;//i作为输入文件名转化成字符数组的下标，m作为真实存在文件的文件名转化成字符数组的下标
		char[] file1Name = file1.getName().toCharArray();
		char[] file2Name = file2.getName().toCharArray();
		for(i=0,m=0;m<file1Name.length&&same&&i<file2Name.length;i++,m++) {
			if(file1Name[m]!=file2Name[i]) {//当两文件名出现不同时
				if(file1Name[m]!='?'&&file1Name[m]!='*') {//若不为？或者*，既是文件名不符，可以结束函数，返回false
					same = false;
				}
				else if (file1Name[m]=='?') {//出现通配符？，应当跳过该位置差异，继续遍历
					if(m==file1Name.length-1&&i!=file2Name.length-1) {
						same = false;
					}
					continue;
				}
				else if (file1Name[m]=='*') {//出现通配符*，应当跳过多个位置差异，再继续遍历
					if(i==file1Name.length-1)break;//若刚好是最后一位，即可跳出循环
					for(m = i+1;m<file1Name.length-1&&i<file2Name.length;i++) {//跳出多个差异过程
						if(file2Name[i]==file1Name[m]) {//出现相同字符，即跳过差异过程须结束，回归第一层循环
							break;
						}
					}
				}
			}
		}
		return same;
	}
	public  void SearchFile(File file,LinkedList<String> parameter) {//查找文件
		//记录是否递归查找
		boolean isrecurrence = false;
		if(parameter.getFirst().equals("-s")) {
			parameter.removeFirst();
			isrecurrence = true;
		}
		if(file.isFile()) {//判断该文件对象是否为文件
			Thread thread = new Thread() {//多线程处理文件
				public void run() {
					HanldFile(file, parameter);
				}
			};
			thread.start();
		}
		else {
			if(file.isDirectory()) {//判断该文件对象是否为文件夹
				File[] fs = file.listFiles();//获取当前文件下的所有文件
				for (File file2 : fs) {
					if(!isrecurrence&&file2.isDirectory())continue;//不递归查找，则跳过文件夹
					SearchFile(file2, parameter);
				}
			}else {//若不为文件夹，也不是实际文件时
				File[] fs = new File(file.getParent()).listFiles();//获取当前文件路径下的所有文件
				LinkedList<File> newFolder = new LinkedList<>();
				for (File f : fs) {//遍历文件夹，建立适合通配符的文件集合
					if(Compare(file,f)||f.isDirectory()) {//若该文件符合文件名要求或该文件为文件夹，即加入文件集合
						if(!isrecurrence&&f.isDirectory())continue;//不递归查找，则跳过文件夹
						newFolder.add(f);
					}
				}
				if(newFolder.size()==0) {
					System.out.println("不存在文件");
				}
				for (File f : newFolder) {//循环调用该函数
					if(f.isDirectory())//若为文件夹须进行文件名处理
						SearchFile(new File(f.getAbsolutePath()+"/"+file.getName()),parameter);
					else {
						SearchFile(f, parameter);
					}
				}
			}	
		}
	}
	public void start(String[] args) {
		
		if(args.length==1&&args[0].equals("-x")) {//调动图形界面
			GUIgetFile();	
		}else {
			if(args.length==0) {//无参数输入时
				Scanner in = new Scanner(System.in);
				System.out.println("请输入你的功能和文件名(参数名在前文件路径名在后，以空格分开)");
				System.out.print("-c:字符数\n-w:单词数\n-l:行数\n-s:递归\n-a:详细行信息\n>>");
				String line = in.nextLine();
				args = line.split(" ");
			}
			String path = args[args.length-1];//提取文件路径
			if(!path.contains("\\")) {//查找当前目录下的文件，重新构建路径
				path = System.getProperty("user.dir")+"\\"+args[args.length-1];
			}
			File file = new File(path);
			LinkedList<String> parameter = new LinkedList<>();
			for(int i=0;i<args.length-1;i++) {//构造参数链表
				parameter.add(args[i]);
			}
			HanldFile(file, parameter);
		}
	}
	
	public static void main(String[] args) {
		new wc().start(args);
	}
}
