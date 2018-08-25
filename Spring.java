package or;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Spring {
	static double[][] cc = {{0},{0,6,10,8,10}, {0,3,5,4,5},{0,6.2,10.7,8.5,10.7},{0,3.1,5.4,4.3,5.4},{0,7.2,12,9.6,12}};
	
	static double[][] l = {{0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};
	
	static double[][] u = {{0},{0,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE},
			{0,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE},
			{0,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE},
			{0,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE},
			{0,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE}};
	
	static double[][] c = {{0},{0,0.04,0.05,0.06,0.05,0.03},{0,0.17,0.14,0.13,0.21,0.15},
			{0,0.06,0.00,0.05,0.02,0.04},{0,0.12,0.14,0.1,0.1,0.15}};
	
	
	static double[][] xTmp = new double[6][5];
	static double tmp = Double.MAX_VALUE;
	static int times = 0;
	static int stack = 0;
	
	static int[][] xOpt = new int[6][5];
	static double opt = Double.MAX_VALUE;
	
	
	
	private static void doCplex() throws IloException {
		IloCplex cplex = new IloCplex();
		
		IloNumVar[][] x = new IloNumVar[6][5];

		
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 5; j++) {
				x[j][i] = cplex.numVar(0 ,Double.MAX_VALUE,"X"+j+i);
			}
		}
		
		IloLinearNumExpr expr1 = cplex.linearNumExpr();
		IloLinearNumExpr expr2 = cplex.linearNumExpr();
		IloLinearNumExpr expr3 = cplex.linearNumExpr();
		IloLinearNumExpr expr4 = cplex.linearNumExpr();
		IloLinearNumExpr expr5 = cplex.linearNumExpr();
		for (int i = 1; i <= 4; i++) {
			expr1.addTerm(c[i][1], x[1][i]);
			expr2.addTerm(c[i][2], x[1][i]);
			expr3.addTerm(c[i][3], x[2][i]);
			expr4.addTerm(c[i][4], x[2][i]);
			expr5.addTerm(c[i][5], x[2][i]);
		}
		cplex.addLe(expr1, 500);
		cplex.addLe(expr2, 400);
		cplex.addLe(expr3, 600);
		cplex.addLe(expr4, 550);
		cplex.addLe(expr5, 500);
		
		IloLinearNumExpr expr6 = cplex.linearNumExpr();
		IloLinearNumExpr expr7 = cplex.linearNumExpr();
		IloLinearNumExpr expr8 = cplex.linearNumExpr();
		IloLinearNumExpr expr9 = cplex.linearNumExpr();
		IloLinearNumExpr expr10 = cplex.linearNumExpr();
		for (int i = 1; i <= 4; i++) {
			expr1.addTerm(c[i][1], x[3][i]);
			expr2.addTerm(c[i][2], x[3][i]);
			expr3.addTerm(c[i][3], x[4][i]);
			expr4.addTerm(c[i][4], x[4][i]);
			expr5.addTerm(c[i][5], x[4][i]);
		}
		cplex.addLe(expr6, 100);
		cplex.addLe(expr7, 100);
		cplex.addLe(expr8, 100);
		cplex.addLe(expr9, 100);
		cplex.addLe(expr10, 100);
		
		cplex.addGe(cplex.sum(x[2][1],x[4][1]), 1800);
		cplex.addGe(cplex.sum(x[2][2],x[4][2]), 1400);
		cplex.addGe(cplex.sum(x[2][3],x[4][3]), 1600);
		cplex.addGe(cplex.sum(x[2][4],x[4][4]), 1800);
		
		cplex.addEq(cplex.sum(x[2][1],x[4][1]), cplex.sum(cplex.sum(x[1][1],x[3][1]),x[5][1]));
		cplex.addEq(cplex.sum(x[2][2],x[4][2]), cplex.sum(cplex.sum(x[1][2],x[3][2]),x[5][2]));
		cplex.addEq(cplex.sum(x[2][3],x[4][3]), cplex.sum(cplex.sum(x[1][3],x[3][3]),x[5][3]));
		cplex.addEq(cplex.sum(x[2][4],x[4][4]), cplex.sum(cplex.sum(x[1][4],x[3][4]),x[5][4]));
		
		IloLinearNumExpr expr = cplex.linearNumExpr();
		expr.addTerm(1.2, x[1][1]); expr.addTerm(1.6, x[1][2]);
		expr.addTerm(2.1, x[1][3]); expr.addTerm(2.4, x[1][4]);
		expr.addTerm(1.2, x[3][1]); expr.addTerm(1.6, x[3][2]);
		expr.addTerm(2.1, x[3][3]); expr.addTerm(2.4, x[3][4]);
		cplex.addLe(expr, 10000);
		
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 5; j++) {
				cplex.addLe(x[j][i], u[j][i]);
				cplex.addGe(x[j][i], l[j][i]);
			}
		}
		
		IloLinearNumExpr objective = cplex.linearNumExpr();
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 5; j++) {
				objective.addTerm(cc[j][i], x[j][i]);
			}
		}
		cplex.addMinimize(objective);	
		if (cplex.solve()) {
			//  record the answer;
			tmp = cplex.getObjValue();
			for (int i = 1; i <= 4; i++) {
				for (int j = 1; j <= 5; j++) {
					xTmp[j][i] = cplex.getValue(x[j][i]);
				}
			}
		}
		else {
			tmp = Double.MAX_VALUE;
		}
		cplex.clearModel();
		cplex.end();
	}
	
	
	public static void main(String[] args) throws IloException {
		search();
		System.out.println("the opt value is : " + opt);
		for (int j = 1; j <= 5; j++) {
			for (int i = 1; i <= 4; i++) {
				System.out.print("  x"+j+i+" : "+xOpt[j][i]);
			}
			System.out.println();
		}
	}

	private static void search() throws IloException {
		times++;
		System.out.println("times : " + times +"   stack : " + stack);
		doCplex();
		if ((opt- tmp) / tmp >= 1e-6) {
			// check if Integer
			boolean check = true;
			int iTmp = 0, jTmp = 0;
			for (int i = 1; i <= 4; i++) {
				for (int j = 1; j <= 5; j++) {
					if (xTmp[j][i] % 1 >= 1e-5)  {
						check = false; 
						iTmp = i; jTmp = j;
						break;
					}
				}
			}
			if (check) {
				opt = tmp;
				for (int i = 1; i <= 4; i++) {
					for (int j = 1; j <= 5; j++) {
						xOpt[j][i] = (int)xTmp[j][i];
					}
				}
				return ;
			}
			double lTmp = l[jTmp][iTmp];
			double uTmp = u[jTmp][iTmp];
			
			stack++;
			u[jTmp][iTmp] = (int)xTmp[jTmp][iTmp];
			search();
			u[jTmp][iTmp] = uTmp;
			stack--;
			
			stack++;
			l[jTmp][iTmp] = (int)xTmp[jTmp][iTmp] + 1;
			search();
			l[jTmp][iTmp] = lTmp;
			stack--;
			
			
			
		}
	}
}