public class Parameter {
	public int IterMax;//搜索迭代次数
	public double Alpha,Sita,Q,Beta;
	public double w1, w2;
	public double[][] Graph;//记录图

	Parameter() {
		this.IterMax = 200;//最大迭代数
		this.Alpha = 0.5;//0.7;//挥发参数
		this.Beta = 1;//表示pheromones的比重 0.5-5
		this.Sita = 8;//表示herustic的比重 1-5
		this.Q=1;//表示信息素强度
		this.w1 = 0.95;
		this.w2 = 0.05;
	}
	
	Parameter(int interation, double alpha, int beta, int sita, double w1, double w2) {
		this.IterMax = interation;
		this.Alpha = alpha;
		this.Beta = beta;
		this.Sita = sita;
		this.w1 = w1;
		this.w2 = w2;
	}
}
