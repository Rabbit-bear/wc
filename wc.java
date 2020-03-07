import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
	public static void main(String[] args) {
		File file = new File(args[args.length-1]);		
		System.out.printf("文件%s的相关信息：%n",file.getName());
		for(int i=0;i<args.length-1;i++) {
			switch (args[i]) {
			case "-c": //返回文件 file.c 的字符数
				System.out.println("字符数："+getCharAmount(file));
				break;
			case "-w": //返回文件 file.c 的单词数
				System.out.println("单词数："+getWordAmount(file));
				break;
			case "-l": //返回文件 file.c 的行数
				System.out.println("行数："+getLineAmount(file));
				break;
			}
		}
		
	}
}
