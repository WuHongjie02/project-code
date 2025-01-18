import java.util.ArrayList;
import java.util.Random;

public class AntColonySystem {
	public int carNr;//车辆数量
	public double[][] Graph;
	public Customer[] customers;
	public ArrayList<Integer> untreated[]; // 记录每一位agent k未服务过的客户
	public int customerNr; // 客户数量
	public int agentNr; // agent数量
	public int capacity; // 车辆容量
	public int IterMax; // 最大迭代次数
	public Solution[] solutions; // agents
	public Solution bestSolution;
	public int[] r; // agent k 出发位置、当前位置、下一位置
	public double[][] pheromone; // 信息素
	public double[][] herustic; // 启发值
	public double[][] infoPhe; // infoPhe = pheromone ^ beta * herustic ^ sita
	public double pheromone_0; // 信息素初始值
	public double w1, w2; // 计算概率的参数
	public double alpha, beta, sita; // 计算infoPhe的参数，
	public double Q;
	public double sumHer;
	public Random rand;
	//public double[] solution_distance;
	ArrayList<Double> values = new ArrayList<Double>();
	
	public AntColonySystem(Parameter parameter, ReadIn readIn)
	{
		this.customerNr = readIn.customerNr;
		this.agentNr = customerNr;
		this.capacity = readIn.capacity;
		this.Graph = readIn.Graph;
		this.customers = readIn.customers;
		this.IterMax = parameter.IterMax;
		this.solutions = new Solution[agentNr + 10]; // 设置agents数量和城市数一样多
		this.untreated = new ArrayList[agentNr + 10]; // 数组数量等于agents数
		for (int i = 0; i < agentNr + 10; i++) untreated[i] = new ArrayList<>();
		this.r = new int[agentNr + 10];
		this.pheromone = new double[customerNr + 10][customerNr + 10];//信息素矩阵
		this.herustic = new double[customerNr + 10][customerNr + 10];//启发值矩阵，1/route.distance
		this.infoPhe = new double[customerNr + 10][customerNr + 10];//信息函数数组
		this.alpha = parameter.Alpha;//挥发系数
		this.beta = parameter.Beta;//pheromone比重
		this.sita = parameter.Sita;//herustic比重
		this.w1 = parameter.w1;
		this.w2 = parameter.w2;
		this.rand = new Random();//随机数
		this.carNr=readIn.carNr;//
	}
	
	// 初始化总体参数
	public void init()
	{
		// 计算信息素初始值
		double totalDistance = 0;
		double num = 0;
		for (int i = 0; i < customerNr + 1; i++)
		{
			for (int j = 0; j < customerNr + 1; j++)
			{
				if (i != j)
				{
					totalDistance += Graph[i][j];//将距离矩阵中的所有值加在一起
					num ++;
				}
			}
		}
		pheromone_0 = num / (totalDistance * (customerNr + 1));//pheromone初始值。

		// 初始化信息素、启发值
		for (int i = 0; i < customerNr + 1; i++)
		{
			for (int j = 0; j < customerNr + 1; j++)
			{
				if (i != j)
				{
					pheromone[i][j] = pheromone[j][i] = pheromone_0;//信息素矩阵初始化
					herustic[i][j] = herustic[j][i] = 1 / Graph[i][j]; //启发值矩阵初始化
				}
			}
		}
	}
	
	// 初始化agent参数
	public void reset()
	{
		// 初始化每位agent未服务的客户
		for (int i = 0; i < agentNr; i++)
		{
			untreated[i].clear();//清空
			for ( int j = 0; j < customerNr; j++)
			{
				untreated[i].add(j + 1);//所有的untreated都是1-100
			}
		}
		// 初始化起始服务客户
		for (int i = 0; i < agentNr; i++)
		{
			solutions[i] = new Solution();//构建100只蚂蚁
			r[i] = 0;//表示位置
		}
		
	}
	
	// 构造完整解
	public void construct_solution()
	{
			// 为每一位agent分别构造解，为每一只蚂蚁构建路线
			for (int i = 0; i < agentNr; i++)
			{
				// 路径开始
				Route route = new Route();
				//添加中心
				route.customers.add(0);
				//untreated[i].remove(0);


				while (untreated[i].size() != 0)//执行条件为untreated非空
				{
					int next = select_next(i, route);//选择下一个地点，i表示当前位置的序号，route表示已经走过的路径
					// 如果下一个选择不合法或客户已配送完毕，返回中心
					if (next == 0)
					{
						route.customers.add(0);//添加回到中心
						route.time += Graph[r[i]][0]+customers[0].Service;//添加返回中心的时间
						route.distance += Graph[r[i]][0];//添加返回中心的距离
						solutions[i].routes.add(route);//添加解路线
						solutions[i].totalCost += route.distance;//路程总耗费

						route = new Route();//回到中心后创建新的路径
						route.customers.add(0);//添加从中心出发
						r[i] = 0;//更新位置
					}
					else
					{
						route.customers.add(next);//添加下一位置的序号
						route.load += customers[next].Demand;//容量计算
						route.time = Math.max(route.time + Graph[r[i]][next], customers[next].Begin) + customers[next].Service;
						//route.time += Graph[r[i]][next] + customers[next].Service;//时间计算
						route.distance += Graph[r[i]][next];//路程计算
						r[i] = next;//更新后的位置
						//从未服务的客户中移除next
						for (int j = 0; j < untreated[i].size(); j++)
						{
							if (untreated[i].get(j) == next)
								untreated[i].remove(j);
						}
					}
				}
				//System.out.println(untreated[i].size());
				//如果遍历完所有未服务的点，没找到适合的位置，则返回中心
				// 最后一条路径返回配送中心
				route.customers.add(0);
				route.time = Math.max(Graph[r[i]][0], customers[0].Begin) + customers[0].Service;
				//route.time = Graph[r[i]][0] + customers[0].Service;//
				route.distance += Graph[r[i]][0];
				solutions[i].routes.add(route);
				//System.out.println(solutions[i].routes);
				solutions[i].totalCost += route.distance;
			}
		//}
	}

//	public int select_next(int k, Route route) //k表示当前所在位置,route表示已经走过的路径
//	{
//		// 若全部处理完，返回配送中心
//		if (untreated[k].size() == 0)
//			return 0;
//
//		// 计算概率
//		double sumPhe = 0;
//		//double sumTime = 0;
//		double[] infoPhe = new double[agentNr];
//		//double[] infoTime = new double[agentNr];
//		/*
//		for (int i = 0; i < untreated[k].size(); i++)
//		{
//			//信息素函数
//			infoPhe[i] =Math.pow(pheromone[r[k]][untreated[k].get(i)], beta)
//					* Math.pow(herustic[r[k]][untreated[k].get(i)], sita);
//			infoTime[i] = 1 / (Math.abs(route.time - customers[untreated[k].get(i)].Begin) +
//					Math.abs(route.time - customers[untreated[k].get(i)].End));
//			sumPhe += infoPhe[i];
//			sumTime += infoTime[i];
//		}
//		*/
//		for (int i = 0; i < untreated[k].size(); i++)//！
//		{
//			//信息素函数
//			infoPhe[i] =Math.pow(pheromone[r[k]][untreated[k].get(i)], beta)
//					* Math.pow(herustic[r[k]][untreated[k].get(i)], sita);
//			sumPhe += infoPhe[i];
//			sumHer+=herustic[r[k]][untreated[k].get(i)];//未服务点位的启发值累加
//		}
//
//		double rate = rand.nextDouble();//生成随机概率
//		int next = 0;
//		double sum_prob = 0;
//
//		//赌轮盘
//		// 生成0-1随机数，累加概率，若大于当前累加部分，返回当前城市编号
//		for (int i = 0; i < untreated[k].size(); i++)
//		{
//			//sum_prob += infoPhe[i] * w1 / sumPhe + infoTime[i] * w2 / sumTime;//选择城市的概率
//			sum_prob+=infoPhe[i]/sumPhe;//选择城市的概率
//			if (rate <= sum_prob)//rate < sum_prob
//			{
//				next = untreated[k].get(i);//获取所选择城市的序号
//				// 检验合法性
//				double time = route.time + Graph[r[k]][next];//时间
//				double load = route.load + customers[next].Demand;//容量
//				//if (time > customers[next].End || load > capacity||time<customers[next].Begin)
//				//	continue;
//				//else
//				//	break;
//				if (time <= customers[next].End &&load<=capacity&&time>=customers[next].Begin)//约束条件
//					break;
//				else
//					continue;
//			}
//		}
//
//		// 检验合法性
//		double time = route.time + Graph[r[k]][next];
//		double load = route.load + customers[next].Demand;
//		if (time > customers[next].End|| load > capacity)//||time<customers[next].Begin
//			next = 0;
//		return next;
//		//if (time <= customers[next].End&& time>=customers[next].Begin&&load<=capacity)
//		//	return next;
//		//else
//		//	next=0;
//		//	return next;
//	}

	public int select_next(int k, Route route) //k表示当前所在位置,route表示已经走过的路径
	{
		// 若全部处理完，返回配送中心
		if (untreated[k].size() == 0)
			return 0;

		// 计算概率
		double sumPhe = 0;
		double[] infoPhe = new double[agentNr];
		double sumTime=0;
		double[] infoTime=new double[agentNr];
		//System.out.println(untreated[k].size());
		for (int i = 0; i < untreated[k].size(); i++)
		{
			//信息素函数
			infoPhe[i] =Math.pow(pheromone[r[k]][untreated[k].get(i)], beta)
					* Math.pow(herustic[r[k]][untreated[k].get(i)], sita);
			sumPhe += infoPhe[i];
			sumHer+=herustic[r[k]][untreated[k].get(i)];//未服务点位的启发值累加
			//时间窗函数
			infoTime[i]=1/(double)(Math.abs(route.time+Graph[r[k]][untreated[k].get(i)]-customers[untreated[k].get(i)].Begin)
					+Math.abs(route.time+Graph[r[k]][untreated[k].get(i)]-customers[untreated[k].get(i)].End));
			sumTime+=infoTime[i];
		}

		double rate = rand.nextDouble();//生成随机概率
		int next = 0;
		double sum_prob = 0;

		//赌轮盘
		// 生成0-1随机数，累加概率，若大于当前累加部分，返回当前城市编号
		for (int i = 0; i < untreated[k].size(); i++)
		{
			sum_prob+=w1*infoPhe[i]/sumPhe+w2*infoTime[i]/sumTime;//选择城市的概率
			//sum_prob+=infoPhe[i]/sumPhe;
			if (rate <= sum_prob)//rate < sum_prob
			{
				next = untreated[k].get(i);//获取所选择城市的序号
				// 检验合法性
				double time = route.time + Graph[r[k]][next];//时间
				double load = route.load + customers[next].Demand;//容量
				if (time <= customers[next].End &&load<=capacity)//约束条件&&time>=customers[next].Begin
					break;
				else
				{
					next = 0;
					continue;
				}
			}
		}
		//System.out.println(next);

		return next;
	}
	
	// 更新信息素
	public void update_pheromone() {
		Solution now_best = new Solution();
		now_best.totalCost = Integer.MAX_VALUE;
		double delta = 0;//精英强化参数
		
		// 查找最优解
		for (int i = 0; i < agentNr; i++)
		{
			if (solutions[i].totalCost < now_best.totalCost)
				now_best = solutions[i];
		}
		
		// 更新最优解 若当前最优代替历史最优，增加信息素时获得增益
		if (now_best.totalCost < bestSolution.totalCost)
		{
			//增益系数
			delta = (bestSolution.totalCost - now_best.totalCost) / bestSolution.totalCost;
			bestSolution = now_best;
		}
		
		//更新信息素含量 
		// 信息素挥发 
		for (int i = 0; i < customerNr; i ++)
			for (int j = 0; j < customerNr; j ++)
				pheromone[i][j] *= (1 - alpha);
		// 信息素增加
		for (int i = 0; i < now_best.routes.size(); i ++){
			for (int j = 1; j < now_best.routes.get(i).customers.size(); j++)
			{
				pheromone[now_best.routes.get(i).customers.get(j - 1)][now_best.routes.get(i).customers.get(j)]
						+=sumHer+ (Q / (double)now_best.totalCost) * (1 + delta);
				// 对称处理
				pheromone[now_best.routes.get(i).customers.get(j)][now_best.routes.get(i).customers.get(j - 1)]
						= pheromone[now_best.routes.get(i).customers.get(j - 1)][now_best.routes.get(i).customers.get(j)];

			}
		}
	}
	
	public Solution ACS_Strategy()
	{
		bestSolution = new Solution();
		bestSolution.totalCost = Integer.MAX_VALUE;//Integer.MAX_VALUE表示int数据类型的最大取值数：2 147 483 647
		init();
		for (int i = 0; i < IterMax; i++)
		{
			reset();//初始化agent信息 
			construct_solution();//对于所有的agent构造一个完整的tour 
			update_pheromone();//更新信息素 
			System.out.println("iteration : " + i + "\tbest solution cost = " + bestSolution.totalCost);

			values.add(bestSolution.totalCost);//添加数据画图
		}


		return bestSolution;
	}
}
