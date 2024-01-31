// Generated from start.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link startParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface startVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link startParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(startParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(startParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(startParser.CommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(startParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(startParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_call(startParser.Function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(startParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#return_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_statement(startParser.Return_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#if_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_statement(startParser.If_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#elif_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElif_block(startParser.Elif_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(startParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#while_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile_statement(startParser.While_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#for_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_statement(startParser.For_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#print_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrint_statement(startParser.Print_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#nl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNl(startParser.NlContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayExpression(startParser.ArrayExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolExpression(startParser.BoolExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code appendExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAppendExpression(startParser.AppendExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayIndexExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayIndexExpression(startParser.ArrayIndexExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpression(startParser.NotExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpression(startParser.ParenExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lengthExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLengthExpression(startParser.LengthExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionExpression(startParser.FunctionExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code powerExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPowerExpression(startParser.PowerExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code muldivExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMuldivExpression(startParser.MuldivExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code addsubExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddsubExpression(startParser.AddsubExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code compExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompExpression(startParser.CompExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code termExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermExpression(startParser.TermExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nameExpression}
	 * labeled alternative in {@link startParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNameExpression(startParser.NameExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(startParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#muldiv}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMuldiv(startParser.MuldivContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#addsub}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddsub(startParser.AddsubContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#comp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp(startParser.CompContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#power}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPower(startParser.PowerContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#bool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool(startParser.BoolContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#concat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcat(startParser.ConcatContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#length}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLength(startParser.LengthContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(startParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#array_index}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_index(startParser.Array_indexContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#array_index_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_index_assignment(startParser.Array_index_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#remove}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRemove(startParser.RemoveContext ctx);
	/**
	 * Visit a parse tree produced by {@link startParser#all}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAll(startParser.AllContext ctx);
}