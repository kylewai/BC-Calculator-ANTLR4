import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Stack;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public class MyVisitor extends BCBaseVisitor<Double>{
	Stack<HashMap<String, Double>> scopes = new Stack<HashMap<String, Double>>();
	HashMap<String, Function> funcNames = new HashMap<>();
	ArrayList<Double> printList = new ArrayList<>();
    ArrayList<Double> exprList = new ArrayList<>();
	Scanner input = new Scanner(System.in);
	String funcName = "";

    public Double visitMultExpr(BCParser.MultExprContext ctx){
		scopes.push(new HashMap<String, Double>());
		for(int i = 0; i < ctx.getChildCount(); i++){
			try{
				visit(ctx.getChild(i));
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		return 0.0;
    }
	public Double visitIfStat(BCParser.IfStatContext ctx){
		Double condBlock = visit(ctx.condPlusStat());
		if(condBlock == 0){
			if(ctx.statBlock() != null) visit(ctx.statBlock());
		}
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by {@link BCParser#whileStat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitWhileStat(BCParser.WhileStatContext ctx){
		Double condBlock;
		while(true){
			try{
				condBlock = visit(ctx.condPlusStat());
			}
			catch(Exception e){
				if(e.getMessage().equals("Break")){
					break;
				}
				throw e;
			}
			if(condBlock == 0){
				break;
			}
		}
		return 0.0;
  	}
	/**
	 * Visit a parse tree produced by {@link BCParser#forStat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitForStat(BCParser.ForStatContext ctx){
		visit(ctx.getChild(2));
		while(true){
			Double exprRes = visit(ctx.getChild(4));
			if(exprRes == 0){
				break;
			}
			try{
				visit(ctx.getChild(8));
			}
			catch(Exception e){
				if(!e.getMessage().equals("Continue outside of for/while loop")){
					if(!e.getMessage().equals("Break")){
						throw e;
					}
					else{
						break;
					}
				}
			}
			visit(ctx.getChild(6));
		}
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by {@link BCParser#forSetup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	/**
	 * Visit a parse tree produced by the {@code func1}
	 * labeled alternative in {@link BCParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitFunction(BCParser.FunctionContext ctx){
		funcName = ctx.VAR().getText();
		Function func = new Function(ctx);
		funcNames.put(funcName, func);
		if(ctx.varList() != null) visit(ctx.varList());
		return 0.0;
    }

	public Double visitVarList(BCParser.VarListContext ctx){
		/*HashMap<String, Double> newScope = new HashMap<>();
		for(int i = 0; i < ctx.getChildCount(); i++){
			newScope.put(ctx.getChild(i).getText(), 0);
		}
		scopes.push(newScope);
		*/
		ArrayList<String> params = new ArrayList<String>();
		for(int i = 0; i < ctx.getChildCount(); i++){
			if(!ctx.getChild(i).getText().equals(",")){
				params.add(ctx.getChild(i).getText());
			}
		}
		String[] arr = params.toArray(new String[params.size()]);
		funcNames.get(funcName).setParams(arr);
		return 0.0;
	}
	/**
	 * Visit a parse tree produced by {@link BCParser#funcCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitFuncCall1(BCParser.FuncCall1Context ctx) {
		ArrayList<Double> args = new ArrayList<>();
		if(ctx.expr() != null){
			Double firstExpr = visit(ctx.expr());
			args.add(firstExpr);
			if(ctx.list() != null){
				visit(ctx.list());
				for(double i : printList){
					args.add(i);
				}
				printList.clear();
			}
		}
		Function myFunc = funcNames.get(ctx.VAR().getText());
		if(myFunc.params.length != args.size()){
			throw new RuntimeException("Incorrect number of parameters");
		}
		HashMap<String, Double> newScope = new HashMap<>();
		for(int i = 0; i < args.size(); i++){
			String paramName = myFunc.params[i];
			newScope.put(paramName, args.get(i));
		}
		scopes.push(newScope);
		Double returnRes = 0.0;
		try{
			visit(myFunc.ctx.statBlock());
		}
		catch(Exception e){
			try{
				returnRes = Double.parseDouble(e.getMessage());
			}
			catch(Exception f){
				System.out.println(f);
				throw f;
			}
		}
		return returnRes;
    }
	public Double visitFuncCall2(BCParser.FuncCall2Context ctx) {
		ArrayList<Double> args = new ArrayList<>();
		if(ctx.assign() != null){
			Double firstExpr = visit(ctx.assign());
			args.add(firstExpr);
			if(ctx.list() != null){
				visit(ctx.list());
				for(double i : printList){
					args.add(i);
				}
				printList.clear();
			}
		}
		Function myFunc = funcNames.get(ctx.VAR().getText());
		if(myFunc.params.length != args.size()){
			throw new RuntimeException("Incorrect number of parameters");
		}
		HashMap<String, Double> newScope = new HashMap<>();
		for(int i = 0; i < args.size(); i++){
			String paramName = myFunc.params[i];
			newScope.put(paramName, args.get(i));
		}
		scopes.push(newScope);
		Double returnRes = 0.0;
		try{
			visit(myFunc.ctx.statBlock());
		}
		catch(Exception e){
			try{
				returnRes = Double.parseDouble(e.getMessage());
			}
			catch(Exception f){
				System.out.println(f);
				throw f;
			}
		}
		return returnRes;
    }
	/**
	 * Visit a parse tree produced by the {@code condPlusStat1}
	 * labeled alternative in {@link BCParser#condPlusStat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitCondPlusStat1(BCParser.CondPlusStat1Context ctx){
		Double condRes = visit(ctx.expr());
		if(condRes != 0){
			visit(ctx.statBlock());
			return 1.0;
		}
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code condPlusStat2}
	 * labeled alternative in {@link BCParser#condPlusStat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitCondPlusStat2(BCParser.CondPlusStat2Context ctx){
		Double condRes = visit(ctx.assign());
		if(condRes != 0){
			visit(ctx.statBlock());
			return 1.0;
		}
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by {@link BCParser#statBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitStatBlock(BCParser.StatBlockContext ctx){
		int length = ctx.getChildCount();
		for(int i = 0; i < length; i++){
			visit(ctx.getChild(i));
		}
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code stat1}
	 * labeled alternative in {@link BCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitStat1(BCParser.Stat1Context ctx){
		visit(ctx.ifStat());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code stat2}
	 * labeled alternative in {@link BCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitStat2(BCParser.Stat2Context ctx){
		visit(ctx.whileStat());
		return 0.0;
    }

	public Double visitStat6(BCParser.Stat6Context ctx){
		visit(ctx.forStat());
		return 0.0;
    }
	public Double visitStat7(BCParser.Stat7Context ctx){
		return visit(ctx.funcCall());
    }
	/**
	 * Visit a parse tree produced by the {@code stat3}
	 * labeled alternative in {@link BCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitStat3(BCParser.Stat3Context ctx){
		visit(ctx.exprWrap());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code stat4}
	 * labeled alternative in {@link BCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitStat4(BCParser.Stat4Context ctx){
		visit(ctx.assignWrap());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code stat5}
	 * labeled alternative in {@link BCParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitStat5(BCParser.Stat5Context ctx){
		visit(ctx.printWrap());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code return0.0}
	 * labeled alternative in {@link BCParser#returnStat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitReturn0(BCParser.Return0Context ctx){
		if(scopes.size() > 1){
			scopes.pop();
		}
		else{
			throw new RuntimeException("----Illegal return statement");
		}
		throw new RuntimeException("0.0");
    }
	/**
	 * Visit a parse tree produced by the {@code return1}
	 * labeled alternative in {@link BCParser#returnStat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitReturn1(BCParser.Return1Context ctx){
		Double exprRes = visit(ctx.expr());
		if(scopes.size() > 1){
			scopes.pop();
			throw new RuntimeException(exprRes + "");
		}
		else{
			throw new RuntimeException("----Illegal return statement");
		}
    }
	/**
	 * Visit a parse tree produced by the {@code return2}
	 * labeled alternative in {@link BCParser#returnStat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitReturn2(BCParser.Return2Context ctx){
		Double assignRes = visit(ctx.assign());
		if(scopes.size() > 1){
			scopes.pop();
			throw new RuntimeException(assignRes + "");
		}
		else{
			throw new RuntimeException("----Illegal return statement");
		}
  	}

	public Double visitContinue(BCParser.ContinueContext ctx) {
		throw new RuntimeException("Continue outside of for/while loop");
	}

	public Double visitBreak(BCParser.BreakContext ctx){
		throw new RuntimeException("Break");
	}
	/**
	 * Visit a parse tree produced by the {@code exprWrap1}
	 * labeled alternative in {@link BCParser#exprWrap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprWrap1(BCParser.ExprWrap1Context ctx){
		Double exprRes = visit(ctx.expr());
		if(!exprList.isEmpty()){
			for(double i : exprList){
				System.out.println(i);
			}
			exprList.clear();
		}
		System.out.println(exprRes);
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code exprWrap2}
	 * labeled alternative in {@link BCParser#exprWrap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprWrap2(BCParser.ExprWrap2Context ctx){
		visit(ctx.exprStatement());
		if(!exprList.isEmpty()){
            for(double i : exprList){
                System.out.println(i);
            }
            exprList.clear();
        }
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code exprWrap3}
	 * labeled alternative in {@link BCParser#exprWrap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprWrap3(BCParser.ExprWrap3Context ctx){
		visit(ctx.exprStatement());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code assignWrap1}
	 * labeled alternative in {@link BCParser#assignWrap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssignWrap1(BCParser.AssignWrap1Context ctx){
		visit(ctx.assignStatement());
		if(!exprList.isEmpty()){
            for(double i : exprList){
                System.out.println(i);
            }
            exprList.clear();
        }
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code assignWrap2}
	 * labeled alternative in {@link BCParser#assignWrap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssignWrap2(BCParser.AssignWrap2Context ctx){
		visit(ctx.assign());
		if(!exprList.isEmpty()){
			for(double i : exprList){
				System.out.println(i);
			}
			exprList.clear();
		}
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code assignWrap3}
	 * labeled alternative in {@link BCParser#assignWrap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssignWrap3(BCParser.AssignWrap3Context ctx){
		visit(ctx.assignStatement());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code printWrap1}
	 * labeled alternative in {@link BCParser#printWrap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitPrintWrap1(BCParser.PrintWrap1Context ctx){
		visit(ctx.printStatement());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code printWrap2}
	 * labeled alternative in {@link BCParser#printWrap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitPrintWrap2(BCParser.PrintWrap2Context ctx){
		visit(ctx.print());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code printWrap3}
	 * labeled alternative in {@link BCParser#printWrap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitPrintWrap3(BCParser.PrintWrap3Context ctx){
		visit(ctx.printStatement());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by {@link BCParser#exprStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprStatement(BCParser.ExprStatementContext ctx){
		Double exprRes = visit(ctx.expr());
		exprList.add(exprRes);
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by {@link BCParser#assignStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssignStatement(BCParser.AssignStatementContext ctx){
		visit(ctx.assign());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by {@link BCParser#printStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitPrintStatement(BCParser.PrintStatementContext ctx){
		visit(ctx.print());
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code print1}
	 * labeled alternative in {@link BCParser#print}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitPrint1(BCParser.Print1Context ctx){
		System.out.print(visit(ctx.expr()));
		if(ctx.list() != null) visit(ctx.list());
		for(double i : printList){
			System.out.print(i);
		}
		System.out.println();
		printList.clear();
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code print2}
	 * labeled alternative in {@link BCParser#print}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitPrint2(BCParser.Print2Context ctx){
		System.out.print(visit(ctx.assign()));
		if(ctx.list() != null) visit(ctx.list());
		for(double i : printList){
			System.out.print(i);
		}
		System.out.println();
		printList.clear();
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by {@link BCParser#list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitList(BCParser.ListContext ctx){
		int length = ctx.getChildCount();
		for(int i = 0; i < length; i++){
			visit(ctx.getChild(i));
		}
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by {@link BCParser#exprListEl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprListEl(BCParser.ExprListElContext ctx){
		printList.add(visit(ctx.expr()));
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by {@link BCParser#assignListEl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssignListEl(BCParser.AssignListElContext ctx){
		printList.add(visit(ctx.assign()));
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code exprExpExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprExpExpr(BCParser.ExprExpExprContext ctx){
		/*if((int)$ra.result != $ra.result){
                System.out.println("error: non-integer exponent");
            }*/
        return Math.pow(visit(ctx.expr(0)), visit(ctx.expr(1)));
    }
	/**
	 * Visit a parse tree produced by the {@code exprOrExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprOrExpr(BCParser.ExprOrExprContext ctx){
		return (visit(ctx.expr(0)) != 0 || visit(ctx.expr(1)) != 0)? 1.0 : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code exprOp2Assign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprOp2Assign(BCParser.ExprOp2AssignContext ctx){
		String op = ctx.op.getText();
		Double exprRes = visit(ctx.expr());
		Double assignRes = visit(ctx.assign());
		switch(op){
                case "*": return exprRes * assignRes;
                case "/": return exprRes / assignRes;
                case "%": return exprRes % assignRes;
            }
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code negAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitNegAssign(BCParser.NegAssignContext ctx){
		return -1 * visit(ctx.assign());
    }
	/**
	 * Visit a parse tree produced by the {@code integer}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitInteger(BCParser.IntegerContext ctx){
		return Double.parseDouble(ctx.INT().getText());
    }
	/**
	 * Visit a parse tree produced by the {@code logAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitLogAssign(BCParser.LogAssignContext ctx){
		return Math.log(visit(ctx.assign()));
    }
	/**
	 * Visit a parse tree produced by the {@code exprOp1Expr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprOp1Expr(BCParser.ExprOp1ExprContext ctx){
		String op = ctx.op.getText();
		Double expr1Res = visit(ctx.expr(0));
		Double expr2Res = visit(ctx.expr(1));
		Double returnVal;
		if((op).equals("+")){
			returnVal = expr1Res + expr2Res;
		}
		else{
			returnVal = expr1Res - expr2Res;
		}
		return returnVal;
    }
	/**
	 * Visit a parse tree produced by the {@code logExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitLogExpr(BCParser.LogExprContext ctx){
		return Math.log(visit(ctx.expr()));
    }
	/**
	 * Visit a parse tree produced by the {@code exprAndAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprAndAssign(BCParser.ExprAndAssignContext ctx){
		return (visit(ctx.expr()) != 0 && visit(ctx.assign()) != 0)? 1.0 : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code parensAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitParensAssign(BCParser.ParensAssignContext ctx){
		return visit(ctx.assign());
    }
	/**
	 * Visit a parse tree produced by the {@code expAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExpAssign(BCParser.ExpAssignContext ctx){
		return Math.exp(visit(ctx.assign()));
    }
	/**
	 * Visit a parse tree produced by the {@code exprExpAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprExpAssign(BCParser.ExprExpAssignContext ctx){
		/*if((int)$ra.result != $ra.result){
                System.out.println("error: non-integer exponent");
            }*/
        return Math.pow(visit(ctx.expr()), visit(ctx.assign()));
    }
	/**
	 * Visit a parse tree produced by the {@code preDecrement}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitPreDecrement(BCParser.PreDecrementContext ctx){
		String var = ctx.VAR().getText();
		Double result;
		if(scopes.peek().containsKey(var)){
			result = scopes.peek().get(var) - 1;
			scopes.peek().put(var, scopes.peek().get(var) - 1);
		}
		else{
			scopes.peek().put(var, -1 * 1.0);
			result = -1 * 1.0;
		}
		return result;
    }
	/**
	 * Visit a parse tree produced by the {@code expExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExpExpr(BCParser.ExpExprContext ctx){
		return Math.exp(visit(ctx.expr()));
    }
	/**
	 * Visit a parse tree produced by the {@code assignAndExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssignAndExpr(BCParser.AssignAndExprContext ctx){
		return (visit(ctx.assign()) != 0 && visit(ctx.expr()) != 0)? 1.0 : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code postIncrement}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitPostIncrement(BCParser.PostIncrementContext ctx){
		String var = ctx.VAR().getText();
		Double result;
		if(scopes.peek().containsKey(var)){
			result = scopes.peek().get(var);
			scopes.peek().put(var, scopes.peek().get(var) + 1);
		}
		else{
			scopes.peek().put(var, 1 * 1.0);
			result = 0.0;
		}
		return result;
    }
	/**
	 * Visit a parse tree produced by the {@code exprOrAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprOrAssign(BCParser.ExprOrAssignContext ctx){
		return (visit(ctx.expr()) != 0 || visit(ctx.assign()) != 0)? 1.0 : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code sinAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitSinAssign(BCParser.SinAssignContext ctx){
		return Math.sin(visit(ctx.assign()));
    }
	/**
	 * Visit a parse tree produced by the {@code exprAndExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprAndExpr(BCParser.ExprAndExprContext ctx){
		return (visit(ctx.expr(0)) != 0 && visit(ctx.expr(1)) != 0)? 1.0 : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code assignOrExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssignOrExpr(BCParser.AssignOrExprContext ctx){
		return (visit(ctx.assign()) != 0 || visit(ctx.expr()) != 0)? 1.0 : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code assignAndAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssignAndAssign(BCParser.AssignAndAssignContext ctx){
		return (visit(ctx.assign(0)) != 0 && visit(ctx.assign(1)) != 0)? 1.0 : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code read}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitRead(BCParser.ReadContext ctx){
		Double result = input.nextDouble();
		return result;
    }
	/**
	 * Visit a parse tree produced by the {@code sinExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitSinExpr(BCParser.SinExprContext ctx){
		return Math.sin(visit(ctx.expr()));
    }
	/**
	 * Visit a parse tree produced by the {@code double}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitDouble(BCParser.DoubleContext ctx){
		return Double.parseDouble(ctx.DOUBLE().getText());
    }
	/**
	 * Visit a parse tree produced by the {@code var}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitVar(BCParser.VarContext ctx){
		String var = ctx.VAR().getText();
		return scopes.peek().containsKey(var)? scopes.peek().get(var) : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code postDecrement}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitPostDecrement(BCParser.PostDecrementContext ctx){
		String var = ctx.VAR().getText();
		Double result;
		if(scopes.peek().containsKey(var)){
			result = scopes.peek().get(var);
			scopes.peek().put(var, scopes.peek().get(var) - 1);
		}
		else{
			scopes.peek().put(var, -1 * 1.0);
			result = 0.0;
		}
		return result;
    }
	/**
	 * Visit a parse tree produced by the {@code sqrtExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitSqrtExpr(BCParser.SqrtExprContext ctx){
		return Math.sqrt(visit(ctx.expr()));
    }
	/**
	 * Visit a parse tree produced by the {@code exprOp1Assign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprOp1Assign(BCParser.ExprOp1AssignContext ctx){
		String op = ctx.op.getText();
		Double exprRes = visit(ctx.expr());
		Double assignRes = visit(ctx.assign());
		Double returnVal;
		if((op).equals("+")){
			returnVal = exprRes + assignRes;
		}
		else{
			returnVal = exprRes - assignRes;
		}
		return returnVal;
    }
	/**
	 * Visit a parse tree produced by the {@code parensExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitParensExpr(BCParser.ParensExprContext ctx){
		return visit(ctx.expr());
    }
	/**
	 * Visit a parse tree produced by the {@code assignOrAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssignOrAssign(BCParser.AssignOrAssignContext ctx){
		return (visit(ctx.assign(0)) != 0 || visit(ctx.assign(1)) != 0)? 1.0 : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code cosAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitCosAssign(BCParser.CosAssignContext ctx){
		return Math.cos(visit(ctx.assign()));
    }
	/**
	 * Visit a parse tree produced by the {@code preIncrement}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitPreIncrement(BCParser.PreIncrementContext ctx){
		String var = ctx.VAR().getText();
		Double result;
		if(scopes.peek().containsKey(var)){
			result = scopes.peek().get(var) + 1;
			scopes.peek().put(var, scopes.peek().get(var) + 1);
		}
		else{
			scopes.peek().put(var, 1 * 1.0);
			result = 1.0;
		}
		return result;
    }
	/**
	 * Visit a parse tree produced by the {@code sqrtAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitSqrtAssign(BCParser.SqrtAssignContext ctx){
		return Math.sqrt(visit(ctx.assign()));
    }
	/**
	 * Visit a parse tree produced by the {@code notAssign}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitNotAssign(BCParser.NotAssignContext ctx){
		return (visit(ctx.assign()) == 0)? 1.0 : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitNotExpr(BCParser.NotExprContext ctx){
		return (visit(ctx.expr()) == 0)? 1.0 : 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code cosExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitCosExpr(BCParser.CosExprContext ctx){
		return Math.cos(visit(ctx.expr()));
    }
	/**
	 * Visit a parse tree produced by the {@code negExpr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitNegExpr(BCParser.NegExprContext ctx){
		return -1 * visit(ctx.expr());
    }
	/**
	 * Visit a parse tree produced by the {@code exprOp2Expr}
	 * labeled alternative in {@link BCParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitExprOp2Expr(BCParser.ExprOp2ExprContext ctx){
		String op = ctx.op.getText();
		Double expr1Res = visit(ctx.expr(0));
		Double expr2Res = visit(ctx.expr(1));
		switch(op){
			case "*": return expr1Res * expr2Res;
			case "/": return expr1Res / expr2Res;
			case "%": return expr1Res % expr2Res;
        }
		return 0.0;
    }
	/**
	 * Visit a parse tree produced by the {@code assign1}
	 * labeled alternative in {@link BCParser#assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssign1(BCParser.Assign1Context ctx){
		Double exprRes = visit(ctx.expr());
		scopes.peek().put(ctx.VAR().getText(), exprRes);
        return exprRes;
    }
	/**
	 * Visit a parse tree produced by the {@code assign2}
	 * labeled alternative in {@link BCParser#assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	public Double visitAssign2(BCParser.Assign2Context ctx){
		Double assignRes = visit(ctx.assign());
		scopes.peek().put(ctx.VAR().getText(), assignRes);
		return assignRes;
    }
	public Double visitAssign3(BCParser.Assign3Context ctx){
		String var = ctx.VAR().getText();
		String expr = ctx.expr().getText();
		Double exprRes = visit(ctx.expr());
		Double result = 0.0;
        switch(ctx.op.getText()){
                case "*": result = scopes.peek().get(var) * exprRes;
                    scopes.peek().put(var, result);
                    break;
                case "/": result = scopes.peek().get(var) / exprRes;
                    scopes.peek().put(var, result);
                    break;
                case "%": result = scopes.peek().get(var) % exprRes;
                    scopes.peek().put(var, result);
                    break;
                case "+": result = scopes.peek().get(var) + exprRes;
                    scopes.peek().put(var, result);
                    break;
                case "-": result = scopes.peek().get(var) - exprRes;
                    scopes.peek().put(var, result);
                    break;
                case "^": result = Math.pow(scopes.peek().get(var), exprRes);
                    scopes.peek().put(var, result);
                    break;
        }
		return result;
    }
	public Double visitExprLess1Expr(BCParser.ExprLess1ExprContext ctx) { 
		return (visit(ctx.expr(0)) < visit(ctx.expr(1)))? 1.0 : 0.0; 
	}
	public Double visitExprLess2Expr(BCParser.ExprLess2ExprContext ctx) { 
		return (visit(ctx.expr()) < visit(ctx.assign()))? 1.0 : 0.0; 
	}
	public Double visitExprLess3Expr(BCParser.ExprLess3ExprContext ctx) { 
		return (visit(ctx.assign()) < visit(ctx.expr()))? 1.0 : 0.0;  
	}
	public Double visitExprLess4Expr(BCParser.ExprLess4ExprContext ctx) { 
		return (visit(ctx.assign(0)) < visit(ctx.assign(1)))? 1.0 : 0.0;  
	}
	public Double visitExprLessEq1Expr(BCParser.ExprLessEq1ExprContext ctx) { 
		return (visit(ctx.expr(0)) <= visit(ctx.expr(1)))? 1.0 : 0.0; 
	}
	public Double visitExprLessEq2Expr(BCParser.ExprLessEq2ExprContext ctx) { 
		return (visit(ctx.expr()) <= visit(ctx.assign()))? 1.0 : 0.0;  
	}
	public Double visitExprLessEq3Expr(BCParser.ExprLessEq3ExprContext ctx) { 
		return (visit(ctx.assign()) <= visit(ctx.expr()))? 1.0 : 0.0;   
	}
	public Double visitExprLessEq4Expr(BCParser.ExprLessEq4ExprContext ctx) { 
		return (visit(ctx.assign(0)) <= visit(ctx.assign(1)))? 1.0 : 0.0;  
	}
	public Double visitExprGreat1Expr(BCParser.ExprGreat1ExprContext ctx) { 
		return (visit(ctx.expr(0)) > visit(ctx.expr(1)))? 1.0 : 0.0;  
	}
	public Double visitExprGreat2Expr(BCParser.ExprGreat2ExprContext ctx) { 
		return (visit(ctx.expr()) > visit(ctx.assign()))? 1.0 : 0.0; 
	}
	public Double visitExprGreat3Expr(BCParser.ExprGreat3ExprContext ctx) { 
		return (visit(ctx.assign()) > visit(ctx.expr()))? 1.0 : 0.0;  
	}
	public Double visitExprGreat4Expr(BCParser.ExprGreat4ExprContext ctx) { 
		return (visit(ctx.assign(0)) > visit(ctx.assign(1)))? 1.0 : 0.0;  
	}
	public Double visitExprGreatEq1Expr(BCParser.ExprGreatEq1ExprContext ctx) { 
		return (visit(ctx.expr(0)) >= visit(ctx.expr(1)))? 1.0 : 0.0;  
	}
	public Double visitExprGreatEq2Expr(BCParser.ExprGreatEq2ExprContext ctx) { 
		return (visit(ctx.expr()) >= visit(ctx.assign()))? 1.0 : 0.0; 
	}
	public Double visitExprGreatEq3Expr(BCParser.ExprGreatEq3ExprContext ctx) { 
		return (visit(ctx.assign()) >= visit(ctx.expr()))? 1.0 : 0.0;   
	}
	public Double visitExprGreatEq4Expr(BCParser.ExprGreatEq4ExprContext ctx) { 
		return (visit(ctx.assign(0)) >= visit(ctx.assign(1)))? 1.0 : 0.0;   
	}
	public Double visitExprEq1Expr(BCParser.ExprEq1ExprContext ctx) { 
		return (Double.compare(visit(ctx.expr(0)), visit(ctx.expr(1))) == 0)? 1.0 : 0.0; 
	}
	public Double visitExprEq2Expr(BCParser.ExprEq2ExprContext ctx) { 
		return (Double.compare(visit(ctx.expr()), visit(ctx.assign())) == 0)? 1.0 : 0.0;  
	}
	public Double visitExprEq3Expr(BCParser.ExprEq3ExprContext ctx) { 
		return (Double.compare(visit(ctx.assign()), visit(ctx.expr())) == 0)? 1.0 : 0.0;  
	}
	public Double visitExprEq4Expr(BCParser.ExprEq4ExprContext ctx) { 
		return (Double.compare(visit(ctx.assign(0)), visit(ctx.assign(1))) == 0)? 1.0 : 0.0;  
	}
	public Double visitExprNotEq1Expr(BCParser.ExprEq1ExprContext ctx) { 
		return (visit(ctx.expr(0)) != visit(ctx.expr(1)))? 1.0 : 0.0; 
	}
	public Double visitExprNotEq2Expr(BCParser.ExprEq2ExprContext ctx) { 
		return (visit(ctx.expr()) != visit(ctx.assign()))? 1.0 : 0.0; 
	}
	public Double visitExprNotEq3Expr(BCParser.ExprEq3ExprContext ctx) { 
		return (visit(ctx.assign()) != visit(ctx.expr()))? 1.0 : 0.0;  
	}
	public Double visitExprNotEq4Expr(BCParser.ExprEq4ExprContext ctx) { 
		return (visit(ctx.assign(0)) != visit(ctx.assign(1)))? 1.0 : 0.0;  
	}
}