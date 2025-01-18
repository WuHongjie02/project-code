import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main (String arg[]) {
		long begintime = System.nanoTime();
		
		Parameter parameter = new Parameter();
		ReadIn readIn = new ReadIn();
		readIn.Read("E:\\大三\\大三上\\人工智能与专家系统\\第一次大作业\\solomon-100\\In\\rc101.txt");
		
		System.out.println("waiting for a while ... ...");
		AntColonySystem ACS = new AntColonySystem(parameter, readIn); 
		Solution bestSolution = ACS.ACS_Strategy();
		
		Print print = new Print(bestSolution, readIn);
		print.Output();
		print.CheckAns();
		
	    long endtime = System.nanoTime();
		double usedTime= (endtime - begintime)/(1e9);
		System.out.println();
		System.out.println("Total run time ："+usedTime+"s");


	}
}
