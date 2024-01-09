// Generated from start.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link startParser}.
 */
public interface startListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link startParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(startParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(startParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(startParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(startParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(startParser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(startParser.CommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(startParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(startParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(startParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(startParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#function_call}.
	 * @param ctx the parse tree
	 */
	void enterFunction_call(startParser.Function_callContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#function_call}.
	 * @param ctx the parse tree
	 */
	void exitFunction_call(startParser.Function_callContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(startParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(startParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#return_statement}.
	 * @param ctx the parse tree
	 */
	void enterReturn_statement(startParser.Return_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#return_statement}.
	 * @param ctx the parse tree
	 */
	void exitReturn_statement(startParser.Return_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void enterIf_statement(startParser.If_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void exitIf_statement(startParser.If_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#elif_block}.
	 * @param ctx the parse tree
	 */
	void enterElif_block(startParser.Elif_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#elif_block}.
	 * @param ctx the parse tree
	 */
	void exitElif_block(startParser.Elif_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(startParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(startParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#while_statement}.
	 * @param ctx the parse tree
	 */
	void enterWhile_statement(startParser.While_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#while_statement}.
	 * @param ctx the parse tree
	 */
	void exitWhile_statement(startParser.While_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#for_statement}.
	 * @param ctx the parse tree
	 */
	void enterFor_statement(startParser.For_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#for_statement}.
	 * @param ctx the parse tree
	 */
	void exitFor_statement(startParser.For_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#print_statement}.
	 * @param ctx the parse tree
	 */
	void enterPrint_statement(startParser.Print_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#print_statement}.
	 * @param ctx the parse tree
	 */
	void exitPrint_statement(startParser.Print_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#nl}.
	 * @param ctx the parse tree
	 */
	void enterNl(startParser.NlContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#nl}.
	 * @param ctx the parse tree
	 */
	void exitNl(startParser.NlContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArrayExpression(startParser.ArrayExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArrayExpression(startParser.ArrayExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBoolExpression(startParser.BoolExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBoolExpression(startParser.BoolExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code appendExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAppendExpression(startParser.AppendExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code appendExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAppendExpression(startParser.AppendExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayIndexExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArrayIndexExpression(startParser.ArrayIndexExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayIndexExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArrayIndexExpression(startParser.ArrayIndexExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNotExpression(startParser.NotExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNotExpression(startParser.NotExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterParenExpression(startParser.ParenExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitParenExpression(startParser.ParenExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code lengthExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLengthExpression(startParser.LengthExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lengthExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLengthExpression(startParser.LengthExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionExpression(startParser.FunctionExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionExpression(startParser.FunctionExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code powerExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPowerExpression(startParser.PowerExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code powerExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPowerExpression(startParser.PowerExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code muldivExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMuldivExpression(startParser.MuldivExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code muldivExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMuldivExpression(startParser.MuldivExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code addsubExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAddsubExpression(startParser.AddsubExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code addsubExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAddsubExpression(startParser.AddsubExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code compExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterCompExpression(startParser.CompExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code compExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitCompExpression(startParser.CompExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code termExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterTermExpression(startParser.TermExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code termExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitTermExpression(startParser.TermExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nameExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNameExpression(startParser.NameExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nameExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNameExpression(startParser.NameExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(startParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(startParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#muldiv}.
	 * @param ctx the parse tree
	 */
	void enterMuldiv(startParser.MuldivContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#muldiv}.
	 * @param ctx the parse tree
	 */
	void exitMuldiv(startParser.MuldivContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#addsub}.
	 * @param ctx the parse tree
	 */
	void enterAddsub(startParser.AddsubContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#addsub}.
	 * @param ctx the parse tree
	 */
	void exitAddsub(startParser.AddsubContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#comp}.
	 * @param ctx the parse tree
	 */
	void enterComp(startParser.CompContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#comp}.
	 * @param ctx the parse tree
	 */
	void exitComp(startParser.CompContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#power}.
	 * @param ctx the parse tree
	 */
	void enterPower(startParser.PowerContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#power}.
	 * @param ctx the parse tree
	 */
	void exitPower(startParser.PowerContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#bool}.
	 * @param ctx the parse tree
	 */
	void enterBool(startParser.BoolContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#bool}.
	 * @param ctx the parse tree
	 */
	void exitBool(startParser.BoolContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#concat}.
	 * @param ctx the parse tree
	 */
	void enterConcat(startParser.ConcatContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#concat}.
	 * @param ctx the parse tree
	 */
	void exitConcat(startParser.ConcatContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#length}.
	 * @param ctx the parse tree
	 */
	void enterLength(startParser.LengthContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#length}.
	 * @param ctx the parse tree
	 */
	void exitLength(startParser.LengthContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(startParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(startParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#array_index}.
	 * @param ctx the parse tree
	 */
	void enterArray_index(startParser.Array_indexContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#array_index}.
	 * @param ctx the parse tree
	 */
	void exitArray_index(startParser.Array_indexContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#array_index_assignment}.
	 * @param ctx the parse tree
	 */
	void enterArray_index_assignment(startParser.Array_index_assignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#array_index_assignment}.
	 * @param ctx the parse tree
	 */
	void exitArray_index_assignment(startParser.Array_index_assignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#remove}.
	 * @param ctx the parse tree
	 */
	void enterRemove(startParser.RemoveContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#remove}.
	 * @param ctx the parse tree
	 */
	void exitRemove(startParser.RemoveContext ctx);
	/**
	 * Enter a parse tree produced by {@link startParser#all}.
	 * @param ctx the parse tree
	 */
	void enterAll(startParser.AllContext ctx);
	/**
	 * Exit a parse tree produced by {@link startParser#all}.
	 * @param ctx the parse tree
	 */
	void exitAll(startParser.AllContext ctx);
}