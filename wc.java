package wc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class wc {
	
	public static int getCharAmount(File file) {//统计字符数
		return (int)file.length();
	}
	public static int getWordAmount(File file) {//统计单词数
		int word = 0;//统计单词数
		String line = null;//文本每行转化为字符串
		try (
				FileReader fReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fReader);
			){
			while((line=bufferedReader.readLine())!=null) {//检测是否读取到最后一行
				int count = 0;//记录每一行的单词数
				boolean notfound = true;//当前遍历字符不是英文的标识
				char[] sentence = line.toCharArray();
				for(int i=0;i<sentence.length;i++) {
					//当前字符是字母的标识
					boolean cheakletters = Character.isUpperCase(sentence[i])||Character.isLowerCase(sentence[i]);
					if(cheakletters&&notfound) {//读取到单词第一个字母
						count++;
						notfound = false;
					}
					else if((!cheakletters)&&(!notfound)) {//读取单词的下一个非字母字符
						notfound = true;
					}
				}
				word += count;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("找不到文件");
		}catch (IOException e) {
			e.printStackTrace();
			System.out.println("打开文件失败");
		}
		return word ;
	}
	public static int getLineAmount(File file) {//统计行数
		int line = 0;//统计文本行数
		try (
				FileReader fReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fReader);
			){	
			while(bufferedReader.readLine()!=null) {//检测读取文本是否应当结束
				line++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("找不到文件");
		}catch (IOException e) {
			e.printStackTrace();
			System.out.println("打开文件失败");
		}
		
		return line;
	}
	public static boolean cheakcode(String line) {//检测一行字符串是否是代码行
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
	public static boolean cheaknotes(String line) {//检测一行字符串是否是注释行
		boolean isnotes = false;
		if(line.length()!=0) {
			char[] cs = line.toCharArray();
			for(int i=0;i<cs.length;i++) {
				if((i+1<cs.length)&&(cs[i]=='/')&&(cs[i+1]=='/')) {
					isnotes = true;
					break;
				}
			}
		}
		return isnotes;
	}
	public static boolean cheakempty(String line) {//检测一行字符串是否是空行
		boolean isempty = true;
		boolean exit = false;
		if(line.length()!=0) {
			char[] cs = line.toCharArray();
			for(int i=0;i<cs.length;i++) {	
				if(cs[i]!=' '&&cs[i]!='{'&&cs[i]!='}') {
					isempty = false;
					break;
				}
				else if((cs[i]=='{'||cs[i]=='}')&&(!exit)){
					exit = true;
				}
				else if((cs[i]=='{'||cs[i]=='}')&&(exit)){
					isempty = false;
					break;
				}
			}
		}
		return isempty;
	}
	public static void getAdvancedLineAmount(File file) {//统计更复杂的数据（代码行 / 空行 / 注释行）
		int codeline = 0;//统计代码行
		int emptyline = 0;//统计空行
		int notesline = 0;//统计注释行
		String line;
		try (
				FileReader fReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fReader);
			){	
			while((line=bufferedReader.readLine())!=null) {//检测读取文本是否应当结束
				if(cheakempty(line)) {//检测是否空行
					emptyline++;
				}
				else if(cheakcode(line)) {//检测是否代码行					
					codeline++;
				}
				else if(cheaknotes(line)) {//检测是否注释行
					notesline++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("找不到文件");
		}catch (IOException e) {
			e.printStackTrace();
			System.out.println("打开文件失败");
		}
		System.out.printf("文件：%s 的相关信息：%n代码行：%d%n空行：%d%n注释行：%d%n",file.getAbsoluteFile(),codeline,emptyline,notesline);
	}
	public static boolean Compare(File file1,File file2) {//带有通配符比较两文件路径是否匹配
		boolean same = true;
		char[] file1Name = file1.getName().toCharArray();
		char[] file2Name = file2.getName().toCharArray();
		for(int i=0;i<file1Name.length&&same;i++) {
			if(file1Name[i]!=file2Name[i]) {
				if(file1Name[i]!='?'&&file1Name[i]!='*') {
					same = false;
				}
				else if (file1Name[i]=='?') {
					if(i==file1Name.length-1&&i!=file2Name.length-1) {
						same = false;
					}
					continue;
				}
				else if (file1Name[i]=='*') {
					for(int m =file1Name.length-1,n = file2Name.length-1;m>i;m--,n--) {
						if(file1Name[m]!=file2Name[n]) {
							if(file1Name[m]=='?') {
								continue;
							}
							same = false;
							break;
						}
					}
					if(same) {
						return same;
					}
				}
			}
		}
		
		return same;
	}
	public static void SearchFile(File file,String[] parameter) {//递归查找文件
		
		if(file.isFile()) {//判断该文件对象是否为文件
			Thread thread = new Thread() {//多线程处理文件
				public void run() {
					HanldFile(file, parameter);
				}
			};
			thread.start();
		}
		else {//判断该文件对象是否为文件夹
			File[] fs = new File(file.getParent()).listFiles();
			LinkedList<File> newFolder = new LinkedList<>();
			for (File f : fs) {//遍历文件夹，建立适合通配符的文件集合
				if(Compare(file,f)||f.isDirectory()) {
					
					newFolder.add(f);
				}
			}
			for (File f : newFolder) {
				if(f.isDirectory())
					SearchFile(new File(f.getAbsolutePath()+"/"+file.getName()),parameter);
				else {
					SearchFile(f, parameter);
				}
			}
		}
	}
	public static void HanldFile(File file,String[] parameter) {
		
outterLoop:for(int i=0;i<parameter.length;i++) {
			switch (parameter[i]) {
			case "-s"://递归处理
				SearchFile(file,makeParameter(parameter,false));
				break outterLoop;//跳出外部循环，实现递归操作
			case "-c": //返回文件 file.c 的字符数
				System.out.printf("文件：%s 的相关信息：%n字符数：%d%n",file.getAbsoluteFile(),getCharAmount(file));
				break;
			case "-w": //返回文件 file.c 的单词数
				System.out.printf("文件：%s 的相关信息：%n单词数：%d%n",file.getAbsoluteFile(),getWordAmount(file));
				break;
			case "-l": //返回文件 file.c 的行数
				System.out.printf("文件：%s 的相关信息：%n行数：%d%n",file.getAbsoluteFile(),getLineAmount(file));
				break;
			case "-a"://返回更复杂的数据（代码行 / 空行 / 注释行）
				getAdvancedLineAmount(file);
				break;
			default :
				System.out.println("参数输入有误，无法提供信息");
			}
		}
	}
	public static String[] makeParameter(String[] args,boolean tag) {//参数单独分离成数组
		String[] parameter = new String[(int)(args.length-1)];//tag为true时去除最后一个元素，false时去除第一个元素
		if(tag) {
			for(int i=0;i<args.length-1;i++) {//去除最后一个元素
				parameter[i] = args[i];
			}
		}
		else {
			for(int i=1;i<args.length;i++) {//去除第一个元素
				parameter[i-1] = args[i];
			}
		}
		return parameter;
	}
	public static boolean isContainWildCard(String path) {
		boolean isContain = false;
		if(path.length()!=0) {
			char[] cs = path.toCharArray();
			for (char c : cs) {
				if(c=='*'||c=='?') {
					isContain = true;
					break;
				}
			}
		}
		return isContain;
	}
	public static void main(String[] args) {
		String path = args[args.length-1];//提取文件路径
		File file = new File(path);
		String[] parameter = makeParameter(args,true);
		HanldFile(file, parameter);
	}
}
