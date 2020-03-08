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
	public static boolean cheaknotes(String line) {//检测一行字符串是否含有注释
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
	public static boolean cheakempty(String line) {//检测一行字符串是否是空行
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
				else if(cheakcode(line)) {//若不为空行，检测是否代码行					
					codeline++;
				}
				else if(cheaknotes(line)) {//若不为空行且不为代码行，检测是否含有注释行，若含有即可判断为注释行
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
	public static boolean Compare(File file1,File file2) {//比较两文件名是否匹配，包括通配符的判断，file1为输入文件名创建的文件对象，file2为真实存在文件
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
	public static void SearchFile(File file,String[] parameter) {//递归查找文件
		
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
					SearchFile(file2, parameter);
				}
			}else {//若不为文件夹，也不是实际文件时
				File[] fs = new File(file.getParent()).listFiles();//获取当前文件路径下的所有文件
				LinkedList<File> newFolder = new LinkedList<>();
				for (File f : fs) {//遍历文件夹，建立适合通配符的文件集合
					if(Compare(file,f)||f.isDirectory()) {//若该文件符合文件名要求或该文件为文件夹，即加入文件集合
						
						newFolder.add(f);
					}
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
	public static void main(String[] args) {
		String path = args[args.length-1];//提取文件路径
		File file = new File(path);
		String[] parameter = makeParameter(args,true);
		HanldFile(file, parameter);
	}
}
