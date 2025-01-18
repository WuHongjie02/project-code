import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Print
{
	private double bestCost;
	public int capacity; 
	private ArrayList <Route> bestRoutes=new ArrayList<>();
	private double[][] Graph;
	public Customer[] customers;
	public double carNr;
	Print(Solution bestSolution, ReadIn readIn)
	{
		this.bestCost = bestSolution.totalCost;
		this.bestRoutes = bestSolution.routes;
		this.Graph = readIn.Graph;
		this.customers = readIn.customers;
		this.capacity = readIn.capacity;
		this.carNr=readIn.carNr;
	}
	
    //结果输出
	public void Output ()
	{
	    System.out.println("************************************************************");
	    System.out.println("The Minimum Total Distance = "+ bestCost);
	    System.out.println("Concrete Schedule of Each Route as Following : ");

	    int id = 0;//车辆数
		int sum=0;//计算点数
	    for (int i = 1; i < bestRoutes.size(); i++)
	        if ( bestRoutes.get(i).customers.size() > 2&&id<carNr )//
			{
	            id++;
	            System.out.print("No." + id + " : ");
	            for ( int j = 0; j < bestRoutes.get(i).customers.size()-1 ; j++ )//输出每条路径
				{
					System.out.print(bestRoutes.get(i).customers.get(j) + " -> ");
				}
	            System.out.println( bestRoutes.get(i).customers.get(bestRoutes.get(i).customers.size() - 1));//第i条路线的最后一个节点
				int pot=bestRoutes.get(i).customers.size()-2;//节点计算
				sum+=pot;
//				System.out.print("访问的节点数："+pot);
//				System.out.println();
//				System.out.print("总时间："+bestRoutes.get(i).time);
//				System.out.println();
//				System.out.print("总路程："+bestRoutes.get(i).distance);
//				System.out.println();
//				System.out.print("总容量："+bestRoutes.get(i).load);
//				System.out.println();
	        }
//		System.out.println();
//		System.out.print("总结点数："+sum);
//		System.out.println();
	    System.out.println("************************************************************");
	}
	
	public void CheckAns() {
		boolean checkTime = true;
		boolean checkCost = true;
		boolean checkCapacity = true;

		// 检验距离计算是否正确
	    double totalCost = 0;
	    for (int i = 0; i < bestRoutes.size(); i++)
	        for ( int j = 1; j < bestRoutes.get(i).customers.size(); ++j )
	            totalCost += Graph[bestRoutes.get(i).customers.get(j - 1)][bestRoutes.get(i).customers.get(j)];//计算结果中路程的长度
	    // 防止精度损失
	    if (Math.abs(totalCost - bestCost) > 1) checkCost = false;

	    //时间检验
	    for (int i = 0; i < bestRoutes.size(); i++)
		{
	    	int time = 0;
			//System.out.print("Route"+i+":");
	    	for (int j = 1; j < bestRoutes.get(i).customers.size(); j++)
			{
	    		time += Graph[bestRoutes.get(i).customers.get(j - 1)][bestRoutes.get(i).customers.get(j)];//路程时间
				//System.out.print(time+"->");
	    		if (time > customers[bestRoutes.get(i).customers.get(j)].End)
				{
					checkTime = false;
					System.out.println("错误点"+bestRoutes.get(i).customers.get(j));
				}
	    		time = Math.max(time, customers[bestRoutes.get(i).customers.get(j)].Begin)
	    				+ customers[bestRoutes.get(i).customers.get(j)].Service;
				//time +=customers[bestRoutes.get(i).customers.get(j)].Service;//服务时间
				//System.out.print(time+"->");
	    	}
			//System.out.println();

	    }

	    //容量检验
	    for (int i = 0; i < bestRoutes.size(); i++)
		{
	    	int load = 0;
	    	for (int j = 1; j < bestRoutes.get(i).customers.size() - 1; j++)
			{
	    		load += customers[bestRoutes.get(i).customers.get(j)].Demand;
	    	}
	    	if (load > capacity)
				checkCapacity = false;
	    }
	    
	    System.out.println("Check total cost = "  + checkCost);
	    System.out.println("Check time windows = " + checkTime);
	    System.out.println("Check time capacity = " + checkCapacity);
		System.out.println("bestsolution = " + totalCost );//1056.91

		//结果输出
		PrintWriter pw = null;
		try
		{
			pw = new PrintWriter("E:\\大三\\大三上\\人工智能与专家系统\\第一次大作业\\1.txt");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		//每次写入的内容都会覆盖原来的内容
//		for (int i = 0; i < bestRoutes.size(); i++)
//			for ( int j = 0; j < bestRoutes.get(i).customers.size(); ++j )
//			{
//				pw.println(customers[bestRoutes.get(i).customers.get(j)].X);//横坐标
//				pw.println(customers[bestRoutes.get(i).customers.get(j)].Y);//纵坐标
//			}
//		pw.close();
	}

}
