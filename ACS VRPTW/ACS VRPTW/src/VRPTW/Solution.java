import java.util.ArrayList;

public class Solution
{
	public double totalCost;
	public double totalTime;
	public ArrayList <Route> routes=new ArrayList<>();
	
	public void cal_tatalCost()
	{
		totalCost = 0;
		for (int i = 0; i < routes.size(); i++)
		{
			totalCost += routes.get(i).distance;
		}
	}
	public void cal_tatalTime()
	{
		totalTime = 0;
		for (int i = 0; i < routes.size(); i++)
		{
			totalTime += routes.get(i).time;
		}
	}

}
