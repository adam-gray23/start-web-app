import java.util.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import java.io.*;

public class startMainVisitor extends startBaseVisitor<Object>{
    Scanner scanner = new Scanner(System.in);
    String sessionToken = "";
    int currentPrintLine = 0;
    int currentLineLength = 0;
    Stack<HashMap<String, Object>> mappy = new Stack<HashMap<String, Object>>();
    HashMap<String, nameTextLine> memoryMap = new HashMap<String, nameTextLine>();

    public class nameTextLine{
        public String text;
        public int line;
        public nameTextLine(String text, int line){
            this.text = text;
            this.line = line;
        }

        public String getString() {
            return text;
        }

        public void setString(String text) {
            this.text = text;
        }

        public int getIntValue() {
            return line;
        }

        public void setIntValue(int line) {
            this.line = line;
        }
    }

    public startMainVisitor(){
        readFile();
        HashMap<String, Object> map = new HashMap<String, Object>();
        mappy.push(map);
    }

    public startMainVisitor(String token){
        sessionToken = token;
        readFile();
        HashMap<String, Object> map = new HashMap<String, Object>();
        mappy.push(map);

    }

    public ArrayList<Integer> breakPointArr = new ArrayList<>();
    public ArrayList<Integer> linesStoppedOnSoFar = new ArrayList<>();
    //function to read in a text file
    public void readFile(){
        //read a text file, only 1 line in the file, will contain numbers separated by commas
        //split on the commas and store in breakPointArr
        File file = new File("breakpoints.txt");
        try {
            Scanner sc = new Scanner(file);
            String line = sc.nextLine();
            //split on comma, if not "" add the position in the array + 1 to the arraylist
            String[] arr = line.split(",");
            for (int i = 0; i < arr.length; i++){
                if (!arr[i].equals("")){
                    breakPointArr.add(i + 1);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(NoSuchElementException e){
            //do nothing
        }
    }

    public void breakpoint(int line){
        //change instrcut.txt to say "paused"
        try (PrintWriter writer = new PrintWriter(new FileWriter("instruct.txt"))) {
            writer.println("paused");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //call the main function of callDjango.java
        callDjango.pauseCode(line, sessionToken);
        //call the main function of fileChecker.java
        fileChecker.main(null);
        readFile();
    }

    public void printLine(String line){
        callDjango.printLine(line, currentPrintLine, currentLineLength, sessionToken);
        // increase currentPrintLine by number of \n characters in line
        for (int i = 0; i < line.length(); i++){
            currentLineLength++;
            if (line.charAt(i) == '\n'){
                currentLineLength = 0;
                currentPrintLine++;
            }
        }
    }

    //show the tree
    @Override
    public Object visitProgram(startParser.ProgramContext ctx) {
        // Iterate through each line
        for (int i = 0; ctx.line(i) != null; i++) {
            String lineText = ctx.line(i).getText();
            if (ctx.line(i).getText().contains("function")){
                visit(ctx.line(i));
            }
            else if (ctx.line(i).getText().contains("loop for")){
                //dont wait because we will stop inside the loop function
                visit(ctx.line(i));
            }
            else if (ctx.line(i).getText().equals("nl")){
                //if last line is nl, break
                if (ctx.line(i + 1) == null){
                    visit(ctx.line(i));
                    break;
                }
                else{
                    visit(ctx.line(i));
                    continue;
                }
            }
            else if (ctx.line(i).comment() != null){
                visit(ctx.line(i));
            }
            else if (!lineText.equals("nl") && ctx.line(i).comment() == null) {
                // Visit the line
                visit(ctx.line(i));

                //if we have just visited an if statement, continue to the next line
                if (ctx.line(i).if_statement() != null){
                    continue;
                }

                //check if we are on the last line
                if (ctx.line(i + 1) == null){
                    //check if the last line is a function
                    if (ctx.line(i + 1) != null && ctx.line(i + 1).getText().equals("nl")){
                        break;
                    }
                    else{
                        if (breakPointArr.contains(ctx.line(i).start.getLine())){
                            //if line not in stopped on, then stop
                            if (!linesStoppedOnSoFar.contains(ctx.line(i).start.getLine())){
                                int line = ctx.line(i).start.getLine();
                                linesStoppedOnSoFar.add(line);
                                breakpoint(line);
                            }
                            else{
                                continue;
                            }
                        }
                        else{
                            continue;
                        }
                    }
                }
                //else if next line an nl, and two down is a comment dont wait
                else if (ctx.line(i + 1) != null && ctx.line(i + 1).getText().equals("nl") ){
                    if (ctx.line(i + 2) != null){
                        if (ctx.line(i + 2).comment() != null){
                            if (breakPointArr.contains(ctx.line(i).start.getLine()) && !linesStoppedOnSoFar.contains(ctx.line(i).start.getLine())){
                                int line = ctx.line(i).start.getLine();
                                linesStoppedOnSoFar.add(line);
                                breakpoint(line);
                            }
                            else{
                                continue;
                            }
                        }
                        else{
                            //if the current line in global arraylist of breakpoints, wait for user input
                            if (breakPointArr.contains(ctx.line(i).start.getLine()) && !linesStoppedOnSoFar.contains(ctx.line(i).start.getLine())){
                                int line = ctx.line(i).start.getLine();
                                linesStoppedOnSoFar.add(line);
                                breakpoint(line);
                            }
                            else{
                                continue;
                            }
                        }
                    }
                    else{
                        //means next line is the last line and is nl
                        if (breakPointArr.contains(ctx.line(i).start.getLine()) && !linesStoppedOnSoFar.contains(ctx.line(i).start.getLine())){
                            int line = ctx.line(i).start.getLine();
                            linesStoppedOnSoFar.add(line);
                            breakpoint(line);
                        }
                        else{
                            continue;
                        }
                    }
                }
                // Wait for user input before executing the next line
                else if (ctx.line(i + 1) != null && (!ctx.line(i + 1).getText().equals("function"))) {
                    //check if any of the parents of the current line are for loops
                    boolean isForLoop = false;
                    for (int j = 0; j < ctx.line(i).parent.getChildCount(); j++){
                        if (ctx.line(i).parent.getChild(j).getText().equals("for")){
                            isForLoop = true;
                            break;
                        }
                    }
                    //if the current line is a for loop, continue
                    if (!isForLoop){
                        //rest global variables
                        loopedOver = null;
                        currentCharInArray = null;
                        lastNonSpecialChar = null;
                    }
                    //if the current line in global arraylist of breakpoints, wait for user input
                    if (breakPointArr.contains(ctx.line(i).start.getLine()) && !linesStoppedOnSoFar.contains(ctx.line(i).start.getLine())){
                        int line = ctx.line(i).start.getLine();
                        linesStoppedOnSoFar.add(line);
                        breakpoint(line);
                    }
                    else{
                        continue;
                    }
                }
            }
        }
        scanner.close();
        return null;
    }

    //override the visit function for assignment
    @Override
    public Object visitAssignment(startParser.AssignmentContext ctx) {
        //get the variable name
        String var = ctx.NAME().getText();
        //get the value
        Object val = visit(ctx.expression());
        //put the variable and value into the hashmap
        HashMap<String, Object> map = mappy.peek();
        map.put(var, val);
        memoryMap.put(var, new nameTextLine(val.toString(), ctx.start.getLine()));
        //output all of the current values of each variable in the map to a txt file
        //one k,v per line
        writeHashMapToFile(map, "memory.csv");
        //return null
        return null;
    }

    private void writeHashMapToFile(HashMap<String, Object> map, String fileName) {
        //check if file exists, if not make it in current dir
        File file = new File(fileName);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (HashMap.Entry<String, nameTextLine> entry : memoryMap.entrySet()) {
            if (entry.getValue().getString().contains("$Function@")){
                entry.getValue().setString("function at line " + entry.getValue().getIntValue());
            }
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (HashMap.Entry<String, Object> entry : map.entrySet()) {
                //if the current entry value contains $Function@, we want to output the value of same function in memoryMap, else output the value
                if (entry.getValue().toString().contains("$Function@")){
                    writer.println(entry.getKey() + "," + memoryMap.get(entry.getKey()).getString());
                }
                else{
                    writer.println(entry.getKey() + "," + entry.getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //override the visit function for term
    @Override
    public Object visitTerm(startParser.TermContext ctx) {
        //check if the term is an INT
        if(ctx.INT() != null){
            //convert Object to Int
            int val = Integer.parseInt(ctx.INT().getText());
            //return the value
            return val;
        }
        //check if the term is a BOOLEAN
        else if(ctx.BOOLEAN() != null){
            //convert Object to Boolean
            boolean val = Boolean.parseBoolean(ctx.BOOLEAN().getText());
            //return the value
            return val;
        }
        //check if the term is a STRING
        else if(ctx.STRING() != null){
            //convert Object to String
            String val = ctx.STRING().getText();
            //remove the quotes on the string
            val = val.substring(1, val.length() - 1);
            //return the value
            return val;
        }
        //check if the term is a float
        else if(ctx.FLOAT() != null){
            //convert Object to float
            float val = Float.parseFloat(ctx.FLOAT().getText());
            //return the value
            return val;
        }
        else {
            return null;
        }
    }

    @Override
    public Object visitNameExpression(startParser.NameExpressionContext ctx){
        String var = ctx.NAME().getText();

        HashMap<String, Object> map = mappy.peek();

        if(!map.containsKey(var)){
            //print out the line number and column number
            printLine("Assignment Error!\nToken: " + var + "\nline " + ctx.start.getLine() + ", column " + ctx.start.getCharPositionInLine() + ": Variable " + var + " is not defined!\n");
            System.exit(0);
        }

        return map.get(var);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitAddsubExpression(startParser.AddsubExpressionContext ctx){
        //get the left and right values
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));
        //switch cases for the operators
        switch(ctx.addsub().getText()){
            case "add":
            case "+":
                if(left instanceof Float && right instanceof Float){
                    return (Float) left + (Float) right;
                }
                else if(left instanceof Integer && right instanceof Float){
                    return (Integer) left + (Float) right;
                }
                else if(left instanceof Float && right instanceof Integer){
                    return (Float) left + (Integer) right;
                }
                else if(left instanceof Integer && right instanceof Integer){
                    return (Integer) left + (Integer) right;
                }
                else if(left instanceof String && right instanceof String){
                    return (String) left + (String) right;
                }
                //first is list, second is anything else
                else if (visit(ctx.expression(0)) instanceof ArrayList) {
                    //create a new arraylist
                    ArrayList<Object> arr = new ArrayList<Object>();
                    //add the first array to the new arraylist
                    arr.addAll((ArrayList<Object>) visit(ctx.expression(0)));
                    //add the second element to the new arraylist
                    arr.add(visit(ctx.expression(1)));
                    //return the new arraylist
                    return arr;
                }
                else{
                    //invalid operation
                    printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only add two numbers or two strings together!\n");
                    System.exit(0);
                }
            case "sub":
            case "-":
                if(left instanceof Float && right instanceof Float){
                    return (Float) left - (Float) right;
                }
                else if(left instanceof Integer && right instanceof Float){
                    return (Integer) left - (Float) right;
                }
                else if(left instanceof Float && right instanceof Integer){
                    return (Float) left - (Integer) right;
                }
                else if(left instanceof Integer && right instanceof Integer){
                    return (Integer) left - (Integer) right;
                }
                else{
                    //invalid operation
                    printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only subtract two numbers!\n");
                    System.exit(0);
                }
            default:
                return null;
        }
    }

    @Override
    public Object visitMuldivExpression(startParser.MuldivExpressionContext ctx){
        //get the left and right values
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));
        //switch cases for the operators
        switch(ctx.muldiv().getText()){
            case "mult":
            case "*":
                if(left instanceof Float && right instanceof Float){
                    return (Float) left * (Float) right;
                }
                else if(left instanceof Integer && right instanceof Float){
                    return (Integer) left * (Float) right;
                }
                else if(left instanceof Float && right instanceof Integer){
                    return (Float) left * (Integer) right;
                }
                else if(left instanceof Integer && right instanceof Integer){
                    return (Integer) left * (Integer) right;
                }
                else{
                    //invalid operation
                    printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only multiply two numbers!\n");
                    System.exit(0);
                }
            case "div":
            case "/":
                if (right instanceof Float){
                    if ((Float) right == 0.0){
                        printLine("Error: Divide by zero error!\nLine " + ctx.start.getLine() + "\n");
                        System.exit(0);
                    }
                }
                if (right instanceof Integer){
                    if ((Integer) right == 0){
                        printLine("Error: Divide by zero error!\nLine " + ctx.start.getLine() + "\n");
                        System.exit(0);
                    }
                }
                if(left instanceof Float && right instanceof Float){
                    return (Float) left / (Float) right;
                }
                else if(left instanceof Integer && right instanceof Float){
                    return (Integer) left / (Float) right;
                }
                else if(left instanceof Float && right instanceof Integer){
                    return (Float) left / (Integer) right;
                }
                else if(left instanceof Integer && right instanceof Integer){
                    double r = (Integer) right;
                    double l = (Integer) left;
                    return l / r;
                }
                else{
                    printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only divide two numbers!\n");
                    System.exit(0);
                }
            case "mod":
            case "%":
                if(left instanceof Float && right instanceof Float){
                    return (Float) left % (Float) right;
                }
                else if(left instanceof Integer && right instanceof Float){
                    return (Integer) left % (Float) right;
                }
                else if(left instanceof Float && right instanceof Integer){
                    return (Float) left % (Integer) right;
                }
                else if(left instanceof Integer && right instanceof Integer){
                    return (Integer) left % (Integer) right;
                }
                else{
                    //invalid operation
                    printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only get the mod of two numbers!\n");
                    System.exit(0);
                }
            default:
                return null;
        }
    }

    //override the visit function for power_expression
    @Override
    public Object visitPowerExpression(startParser.PowerExpressionContext ctx) {
        //get the left and right values
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));
        //handle the different cases, possible combinations of floats and ints
        if(left instanceof Float && right instanceof Float){
            return Math.pow(((Float) left),((Float) right));
        }
        else if(left instanceof Integer && right instanceof Float){
            return Math.pow(((Integer) left),((Float) right));
        }
        else if(left instanceof Float && right instanceof Integer){
            return Math.pow(((Float) left),((Integer) right));
        }
        else if(left instanceof Integer && right instanceof Integer){
            return Math.pow(((Integer) left),((Integer) right));
        }
        else{
            //invalid operation
            printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only raise a number to another number!\n");
            System.exit(0);
        }
        return null;
    }

    //override the visit function for boolExpression
    @Override
    public Object visitBoolExpression(startParser.BoolExpressionContext ctx){
        //get the left and right values
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));
        //switch cases for and as well as or
        switch(ctx.bool().getText()){
            case "and":
                if(left instanceof Boolean && right instanceof Boolean){
                    //return the boolean value of the and operation
                    return (Boolean) left && (Boolean) right;
                }
            case "or":
                if(left instanceof Boolean && right instanceof Boolean){
                    //return the boolean value of the or operation
                    return (Boolean) left || (Boolean) right;
                }
            default:
                return null;
        }
    }

    //override the visit function for parenExpression
    @Override
    public Object visitParenExpression(startParser.ParenExpressionContext ctx){
        //return the value of the expression inside the brackets
        return visit(ctx.expression());
    }

    //override the visit function for notExpression
    @Override
    public Object visitNotExpression(startParser.NotExpressionContext ctx){
    //get the value of the expression
    Object exp = visit(ctx.expression());
    //if the expression is a boolean, return the opposite boolean value
    if(exp instanceof Boolean){
        if(((Boolean)exp).booleanValue() == true){
            return false;
        }
        else{
            return true;
        }
    }
    //if the expression is not a boolean, print an error message and exit
    else{
        printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only negate a boolean!\n");
        System.exit(0);
    }
    return null;
}

//override the visit function for compExpression
@Override
public Object visitCompExpression(startParser.CompExpressionContext ctx){
    //get the left and right values
    Object left = visit(ctx.expression(0));
    Object right = visit(ctx.expression(1));
    //switch cases for the different comparison operators
    //every case has alternate ways of writing the operator
    switch(ctx.comp().getText()){
        case "equals":
        case "==":
            //check if both are equal
            return left.equals(right);
        case "not equals":
        case "!=":
            //check if both are not equal
            return !(left.equals(right));
        case ">=":
        case "greater than or equal to":
            //possible combinations of floats and ints
            //return the correct boolean value
            if(left instanceof Float && right instanceof Float){
                return (Float) left >= (Float) right;
            }
            else if(left instanceof Integer && right instanceof Float){
                return (Integer) left >= (Float) right;
            }
            else if(left instanceof Float && right instanceof Integer){
                return (Float) left >= (Integer) right;
            }
            else if(left instanceof Integer && right instanceof Integer){
                return (Integer) left >= (Integer) right;
            }
            //if they are strings
            else if(left instanceof String && right instanceof String){
                //convert to strings and compare
                String s1 = left.toString();
                String s2 = right.toString();
                if (s1.compareTo(s2) >= 0){
                    return true;
                }
                else {
                    //invalid operation
                    printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only compare two numbers or two strings!\n");
                    System.exit(0);
                }
            }
        case "<=":
        case "less than or equal to":
            //possible combinations of floats and ints
            //return the correct boolean value
            if(left instanceof Float && right instanceof Float){
                return (Float) left <= (Float) right;
            }
            else if(left instanceof Integer && right instanceof Float){
                return (Integer) left <= (Float) right;
            }
            else if(left instanceof Float && right instanceof Integer){
                return (Float) left <= (Integer) right;
            }
            else if(left instanceof Integer && right instanceof Integer){
                return (Integer) left <= (Integer) right;
            }
            //if they are strings
            else if(left instanceof String && right instanceof String){
                //convert to strings and compare
                String s1 = left.toString();
                String s2 = right.toString();
                if (s1.compareTo(s2) <= 0){
                    return true;
                }
                else {
                    //invalid operation
                    printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only compare two numbers or two strings!\n");
                    System.exit(0);
                }
            }
        case ">":
        case "greater than":
            //possible combinations of floats and ints
            //return the correct boolean value
            if(left instanceof Float && right instanceof Float){
                return (Float) left > (Float) right;
            }
            else if(left instanceof Integer && right instanceof Float){
                return (Integer) left > (Float) right;
            }
            else if(left instanceof Float && right instanceof Integer){
                return (Float) left > (Integer) right;
            }
            else if(left instanceof Integer && right instanceof Integer){
                return (Integer) left > (Integer) right;
            }
            //if they are strings
            else if(left instanceof String && right instanceof String){
                //convert to strings and compare
                String s1 = left.toString();
                String s2 = right.toString();
                if (s1.compareTo(s2) > 0){
                    return true;
                }
                else {
                    //invalid operation
                    printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only compare two numbers or two strings!\n");
                    System.exit(0);
                }
            }
        case "<":
        case "less than":
            //possible combinations of floats and ints
            //return the correct boolean value
            if(left instanceof Float && right instanceof Float){
                return (Float) left < (Float) right;
            }
            else if(left instanceof Integer && right instanceof Float){
                return (Integer) left < (Float) right;
            }
            else if(left instanceof Float && right instanceof Integer){
                return (Float) left < (Integer) right;
            }
            else if(left instanceof Integer && right instanceof Integer){
                return (Integer) left < (Integer) right;
            }
            //if they are strings
            else if(left instanceof String && right instanceof String){
                //convert to strings and compare
                String s1 = left.toString();
                String s2 = right.toString();
                if (s1.compareTo(s2) < 0){
                    return true;
                }
                else {
                    //invalid operation
                    printLine("Invalid Operation!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only compare two numbers or two strings!\n");
                    System.exit(0);
                }
            }
        default:
        //default case is an error
        try{
            var lclass = left.getClass();
            var rclass = right.getClass();
            //split classes on the .
            String[] lsplit = lclass.toString().split("\\.");
            String[] rsplit = rclass.toString().split("\\.");
            //get the last element of the split
            String lclass2 = lsplit[lsplit.length - 1];
            String rclass2 = rsplit[rsplit.length - 1];
            printLine("Error: Invalid comparison!\nLine " + ctx.getStart().getLine() + ": " + lclass2 + " cannot be compared to " + rclass2 + "\n");
            System.exit(0);
            return null;
        }
        //catch the null pointer exception if there is one
        catch(Exception e){
            printLine("Error: Null values cannot be compared!\nLine " + ctx.getStart().getLine() + "\nTry and ensure that no nullable values are compared!\n");
            System.exit(0);
            return null;
        }
    }
}

    //override the visit function for print_statement
    @Override
    public Object visitPrint_statement(startParser.Print_statementContext ctx) {
        //create an empty string
        String out = "";
        //loop through the expressions and print them
        for(int i = 0; ctx.expression(i) != null; i++){
            //add a space if it is not the first expression
            if (i != 0){
                out += " ";
            }
            //add the expression to the string
            out += (visit(ctx.expression(i)));
        }
        printLine(out);
        //return null
        return null;
    }

    //define a global arrayList to store lines of an if statement visited
    //will be reset at the end of each final parent if statement
    public int startBlock = 0;
    public int endBlock = 0;
    ArrayList<Integer> linesVisitedInIf = new ArrayList<Integer>();
    //override the visit function for if_statement
    @Override
    public Object visitIf_statement(startParser.If_statementContext ctx) {
        //get the start and end of the whole elif block
        startBlock = ctx.start.getLine();
        endBlock = ctx.stop.getLine();
        //visit the expression within the if statement and assign it to a variable
        Object val = visit(ctx.expression());
        //wait for user input after condition is checked
        //if the current line in global arraylist of breakpoints, wait for user input
        if (breakPointArr.contains(ctx.start.getLine())){
            int line = ctx.start.getLine();
            linesStoppedOnSoFar.add(line);
            breakpoint(line);
        }
        //if the value is true, visit the block
        if(val instanceof Boolean){
            if(((Boolean)val).booleanValue() == true){
                //check if there is a return statement in the block
                Object obj = visit(ctx.block());
                if(obj != null){
                    var parent = ctx.getParent();
                    while(parent != null){
                        //if the parent is an if statement break
                        if (parent instanceof startParser.If_statementContext){
                            return null;
                        }
                        parent = parent.getParent();
                    }
                    //reset the linesVisitedInIf arraylist
                    linesVisitedInIf = new ArrayList<Integer>();
                    return obj;
                }
            }
            //if there is an else if block, visit it
            else if (ctx.elif_block() != null) {
                var returnElif = visit(ctx.elif_block());
                return returnElif;
            }
            else {
                return null;
            }
        }
        //invalid if statement
        else{
            printLine("Error: Invalid if/otherwise statement!\nLine " + ctx.getStart().getLine() + "\nRemember, you can only use a boolean value in an if statement!\n");
            System.exit(0);
        }
        var parent = ctx.getParent();
        while(parent != null){
            //if the parent is an if statement break
            if (parent instanceof startParser.If_statementContext){
                return null;
            }
            parent = parent.getParent();
        }
        //reset the linesVisitedInIf arraylist
        linesVisitedInIf = new ArrayList<Integer>();
        return null;
    }

    //override the visit function for while_statement
    @Override
    public Object visitWhile_statement(startParser.While_statementContext ctx) {
        //get the first line of the while statement
        //visit the expression within the while statement and assign it to a variable
        Object val = visit(ctx.expression());
        //reset the startOfIf to prev in case of nested while loops
        //wait after condition is checked
        //if the current line in global arraylist of breakpoints, wait for user input
        if (breakPointArr.contains(ctx.start.getLine())){
            int line = ctx.start.getLine();
            linesStoppedOnSoFar.add(line);
            breakpoint(line);
        }
        //while the value is true, visit the block
        while(val instanceof Boolean){
            if(((Boolean)val).booleanValue() == true){
                Object obj = visit(ctx.block());
                if (obj != null){
                    return obj;
                }
                val = visit(ctx.expression());
                //if the current line in global arraylist of breakpoints, wait for user input
                if (breakPointArr.contains(ctx.start.getLine())){
                    int line = ctx.start.getLine();
                    linesStoppedOnSoFar.add(line);
                    breakpoint(line);
                }
                else{
                    continue;
                }
            }
            else {
                break;
            }
        }
        //return null
        return null;
    }

    //override the visit function for array
    @Override
    public Object visitArray(startParser.ArrayContext ctx) {
        //create a new arraylist
        ArrayList<Object> arr = new ArrayList<Object>();
        //visit the array elements and add them to the arraylist
        for(int i = 0; i < ctx.expression().size(); i++){
            arr.add(visit(ctx.expression(i)));
        }
        //return the arraylist
        return arr;
    }

    //override the visit function for array_index
    @Override
    @SuppressWarnings("unchecked")
    public Object visitArray_index(startParser.Array_indexContext ctx) {
        try{
            //visit the expression within the array index and assign it to a variable
            HashMap<String, Object> map = mappy.peek();

            Object val = map.get(ctx.NAME().getText());
            //if the value is an arraylist, visit the index
            if(val instanceof ArrayList){
                //change val to be an ArrayList
                ArrayList<Object> arr = (ArrayList<Object>) val;
                //visit the index
                Object index = visit(ctx.expression());
                //if the index is an integer, return the element at that index
                if(index instanceof Integer){
                    return arr.get((Integer) index);
                }
                //if there is an index error print it
                else{
                    printLine("Error: Index: '" + index + "' is not an integer!\nLine " + ctx.start.getLine() + "\n");
                    System.exit(0);
                }
            }
            //if the val is string
            else if (val instanceof String){
                String s = (String) val;
                Object index = visit(ctx.expression());
                //if the index is an integer, return the element at that index
                if(index instanceof Integer){
                    return s.charAt((Integer) index);
                }
                //otherwise print an error
                else{
                    printLine("Error: Index: '" + index + "' is not an integer!\nLine " + ctx.start.getLine() + "\n");
                    System.exit(0);
                }
            }
            else if (val instanceof Integer){
                //get the value at the index
                String s = val.toString();
                Object index = visit(ctx.expression());
                if(index instanceof Integer){
                    return s.charAt((Integer) index);
                }
                else{
                    printLine("Error: Index: '" + index + "' is not an integer!\nLine " + ctx.start.getLine() + "\n");
                    System.exit(0);
                }
            }
            else if (val instanceof Float){
                //get the value at the index
                String s = val.toString();
                //get the item at the index
                Object index = visit(ctx.expression());
                //check if item at index is .
                if(s.charAt((Integer) index) == '.'){
                    return ".";
                }
                //if the index is an integer, return the element at that index
                else if (index instanceof Integer){
                    return s.charAt((Integer) index);
                }
                //otherwise print an error
                else{
                    printLine("Error: Index: '" + index + "' is not an integer!\nLine " + ctx.start.getLine() + "\n");
                    System.exit(0);
                }
            }
            //if the val is not an arraylist, print an error
            else{
                printLine("Error: " + ctx.NAME().getText() + " is not an array, string or number, or has not been defined!\nLine " + ctx.start.getLine() + "\n");
                System.exit(0);
            }
            //return null
            return null;
        }
        //catch the error if the index is out of bounds
        catch (Exception e){
            String s = e.toString();
            String[] parts = s.split(" ");
            int line = ctx.start.getLine();
            printLine("Error: Index " + parts[2] + " out of bounds for array of length " + parts[parts.length - 1] + "\nOffending Symbol/Token: " + ctx.NAME().getText() + "\nLine: " + line + "\nRemember: Arrays are indexed from 0 to length of the array - 1\n");
            System.exit(0);
        }
        return null;
    }


    //override the visit function for array_index_assignment
    @Override
    @SuppressWarnings("unchecked")
    public Object visitArray_index_assignment(startParser.Array_index_assignmentContext ctx) {
        try{
            //visit the expression within the array index assignment and assign it to a variable
            HashMap<String, Object> map = mappy.peek();
            Object val = map.get(ctx.NAME().getText());
            //if the value is an arraylist, visit the index
            if(val instanceof ArrayList){
                //change val to be an ArrayList
                ArrayList<Object> arr = (ArrayList<Object>) val;
                //visit the index
                Object index = visit(ctx.expression(0));
                //visit the value to be assigned
                Object value = visit(ctx.expression(1));
                //if the index is an integer, assign the value to the element at that index
                if(index instanceof Integer){
                    arr.set((Integer) index, value);
                }
                //replace the arraylist in the map with the new arraylist
                map.put(ctx.NAME().getText(), arr);
                memoryMap.put(ctx.NAME().getText(), new nameTextLine(arr.toString(), ctx.start.getLine()));
                //write to the memory
                writeHashMapToFile(map, "memory.csv");
            }
        }
        //catch the error if the index is out of bounds
        catch (Exception e){
            String s = e.toString();
            String[] parts = s.split(" ");
            int line = ctx.start.getLine();
            printLine("Error: Index " + parts[2] + " out of bounds for array of length " + parts[parts.length - 1] + "\nOffending Symbol/Token: " + ctx.NAME().getText() + "\nLine: " + line + "\nRemember: Arrays are indexed from 0 to length of the array - 1\n");
            System.exit(0);
        }
        //return null
        return null;
    }

    //override the visit function for append
    @Override
    @SuppressWarnings("unchecked")
    public Object visitAppendExpression(startParser.AppendExpressionContext ctx) {
        //check if both expressions are arrays
        if (visit(ctx.expression(0)) instanceof ArrayList && visit(ctx.expression(1)) instanceof ArrayList) {
            //create a new arraylist
            ArrayList<Object> arr = new ArrayList<Object>();
            //add the first array to the new arraylist
            arr.addAll((ArrayList<Object>) visit(ctx.expression(0)));
            //add the second array to the new arraylist
            arr.addAll((ArrayList<Object>) visit(ctx.expression(1)));
            //return the new arraylist
            return arr;
        }
        //error if both expressions are not arrays
        else{
            printLine("Error: Both expressions must be arrays!\nLine " + ctx.start.getLine() + "\nRemember: You can only append two arrays together!\n");
            System.exit(0);
        }
        //return null
        return null;
    }

    //override the visit function for remove
    @Override
    @SuppressWarnings("unchecked")
    public Object visitRemove(startParser.RemoveContext ctx) {
        //look at the map on the top of the stack
        HashMap<String, Object> map = mappy.peek();

        Object val = map.get(ctx.NAME().getText());
        if (val instanceof ArrayList) {
            //create a new arraylist
            ArrayList<Object> arr = new ArrayList<Object>();
            //add the array to the new arraylist
            arr.addAll((ArrayList<Object>) val);
            //if remove all is true, remove all elements of the expression
            if (ctx.all() != null){
                //visit the expression
                Object exp = visit(ctx.expression());
                //remove all elements of the expression
                arr.removeAll(Collections.singleton(exp));
                //replace the arraylist in the map with the new arraylist
                map.put(ctx.NAME().getText(), arr);
                memoryMap.put(ctx.NAME().getText(), new nameTextLine(arr.toString(), ctx.start.getLine()));
                //write to the memory
                writeHashMapToFile(map, "memory.csv");
                //return null
                return null;
            }
            //visit the index
            Object index = visit(ctx.expression());
            //remove the element at the index
            arr.remove(index);
            //replace the arraylist in the map with the new arraylist
            map.put(ctx.NAME().getText(), arr);
            memoryMap.put(ctx.NAME().getText(), new nameTextLine(arr.toString(), ctx.start.getLine()));
            writeHashMapToFile(map, "memory.csv");
        }
        //return null
        return null;
    }

    //override the visit function for length
    @Override
    @SuppressWarnings("unchecked")
    public Object visitLengthExpression(startParser.LengthExpressionContext ctx) {
        //check the expression is a list
        if (visit(ctx.expression()) instanceof ArrayList) {
            //return the length of the arraylist
            return ((ArrayList<Object>) visit(ctx.expression())).size();
        }
        //check the expression is a string
        else if (visit(ctx.expression()) instanceof String){
            return visit(ctx.expression()).toString().length();
        }
        //error if the expression is not a list or a string
        else{
            String[] a = visit(ctx.expression()).getClass().toString().split("\\.");
            printLine("Error: Cannot get length of " + a[a.length - 1] + "\nLine: " + ctx.start.getLine() + "\nRemember: You can only get the length of an array or a string!\n");
            System.exit(0);
        }
        //return null
        return null;
    }

    public String loopedOver;
    public Object currentCharInArray;
    public String lastNonSpecialChar;

    //override the visit function for for loops
    @Override
    @SuppressWarnings("unchecked")
    public Object visitFor_statement(startParser.For_statementContext ctx) {
        HashMap<String, Object> map = mappy.peek();
        HashMap<String, Object> map2 = new HashMap<String, Object>(mappy.peek());
        //visit the expression within the for loop and assign it to a variable
        Object val = visit(ctx.expression());
        if (loopedOver == null){
            loopedOver = val.toString();
        }
        
        if (lastNonSpecialChar == null){
            for (int i = loopedOver.length() - 1; i >= 0; i--){
                if (loopedOver.charAt(i) == ','){
                    lastNonSpecialChar = loopedOver.substring(i + 1, loopedOver.length()).replaceAll("]", "").replaceAll("\"", "");
                    //remove the space at the start of the string
                    if (lastNonSpecialChar.charAt(0) == ' '){
                        lastNonSpecialChar = lastNonSpecialChar.substring(1, lastNonSpecialChar.length());
                    }
                    break;
                }
            }
        }
        //if the value is an arraylist, visit the block
        if(val instanceof ArrayList){
            //change val to be an ArrayList
            ArrayList<Object> arr = (ArrayList<Object>) val;
            //visit the block for each element in the arraylist
            for(int i = 0; i < arr.size(); i++){
                //set current char
                currentCharInArray = arr.get(i);
                //create a new variable for the element in the arraylist
                String var = ctx.NAME().getText();
                //put the element in the arraylist into the variable
                map.put(var, arr.get(i));
                //visit the block
                //check if theres a return statement
                //wait here if the current line is in the global arraylist of breakpoints
                if (breakPointArr.contains(ctx.start.getLine())){
                    int line = ctx.start.getLine();
                    linesStoppedOnSoFar.add(line);
                    breakpoint(line);
                }
                Object obj = visit(ctx.block());
                if (obj != null){
                    //reet global variables
                    mappy.pop();
                    mappy.push(map2);
                    return obj;
                }
            }
        }
        //if the value is a string, visit the block
        else if (val instanceof String){
            String str = (String) val;
            for(int i = 0; i < str.length(); i++){
                //create a new variable for the element in the arraylist
                String var = ctx.NAME().getText();
                //put the element in the arraylist into the variable
                map.put(var, str.charAt(i));
                //visit the block
                //check if theres a return statement
                Object obj = visit(ctx.block());
                if (obj != null){
                    mappy.pop();
                    mappy.push(map2);
                    return obj;
                }
            }
        }
        //otherwise error
        else{
            int line = ctx.start.getLine();
            printLine("Error: " + val + " is not an array or string\nOffending Symbol/Token: " + ctx.NAME().getText() + "\nLine: " + line + "\nRemember to loop up in numbers use a while loop\n");
            System.exit(0);
        }
        return null;
    }


    @Override
    public Object visitFunction(startParser.FunctionContext ctx) {
        //look at the map on the top of the stack
        HashMap<String, Object> map = mappy.peek();

        //create a new arraylist to store the arguments
        ArrayList<String> args = new ArrayList<String>();
        //visit the arguments and add them to the arraylist
        for(int i = 1; i < ctx.NAME().size(); i++){
            args.add(ctx.NAME(i).getText());
        };
        //create a new function object
        Function func = new Function(args, ctx.block());
        //print out the block
        map.put(ctx.NAME(0).getText(), func);
        memoryMap.put(ctx.NAME(0).getText(), new nameTextLine(func.toString(), ctx.start.getLine()));
        //return null
        //check if we need to wait
        return null;
    }

    //make a function object
    public class Function {
        //store the arguments
        ArrayList<String> args;
        //store the block
        startParser.BlockContext block;

        //constructor for the function object
        public Function(ArrayList<String> args, startParser.BlockContext block){
            this.args = args;
            this.block = block;
        }
    }

    @Override
    public Object visitFunction_call(startParser.Function_callContext ctx) {
        try{
            HashMap<String, Object> map = new HashMap<String, Object>(mappy.get(0));

            //get the function object from the map
            Function func = (Function) map.get(ctx.NAME().getText());
            //get the arguments from the function object
            ArrayList<String> args = func.args;
            //get the block from the function object
            startParser.BlockContext block = func.block;
            //check if the number of arguments is correct
            if (args.size() != ctx.expression().size()){
                int line = ctx.start.getLine();
                printLine("Error: Incorrect number of arguments for function " + ctx.NAME().getText() + "\nOffending Symbol/Token: " + ctx.NAME().getText() + "\nLine: " + line + "\nRemember to use the correct number of arguments for the function!\n");
                System.exit(0);
            }
            //put the args into the original map
            for (int i = 0; i < args.size(); i++) {
                map.put(args.get(i), visit(ctx.expression(i)));
            }
            //push the map onto the stack
            mappy.push(map);
            //check if there is no lines in the block
            if (block.line().size() == 0){
                //we need to stop on the function definition line
                //this is the first line of the block - 1
                int line = block.start.getLine() - 1;
                //if the current line in global arraylist of breakpoints, wait for user input
                if (breakPointArr.contains(line) && !linesStoppedOnSoFar.contains(line)){
                    linesStoppedOnSoFar.add(line);
                    breakpoint(line);
                }
            }
            //check if there is a return statement
            if (block.return_statement() != null){
                //return the value of the return statement
                visit(block);
                Object val = visit(block.return_statement());
                mappy.pop();
                return val;
            }
            //otherwise visit the block
            else{
                Object val = visit(block);
                mappy.pop();
                return val;
            }
        }
        //if the function is not defined, error
        catch (Exception e){
            //show the error
            printLine(e.toString());
            int line = ctx.start.getLine();
            printLine("Error: Function " + ctx.NAME().getText() + " not defined!\nOffending Symbol/Token: " + ctx.NAME().getText() + "\nLine: " + line + "\nRemember to define your functions before you call them!\n");
            System.exit(0);
        }
        return null;
    }

    //stack to store an ArrayList of ints
    public Stack<ArrayList<Integer>> stackOfLines = new Stack<ArrayList<Integer>>();
    public boolean openBraceInLoop = false;
    public boolean closeBraceInLoop = false;
    //visit the override function for block
    @Override
    public Object visitBlock(startParser.BlockContext ctx) {
        //get the parent node
        var parent = ctx.getParent().getClass().getSimpleName();
        //check if the block is empty
        if (ctx.line().size() == 0){
            //get the line of the opening curly brace
            int line = ctx.start.getLine();
            //get the line of the closing curly brace
            int line2 = ctx.stop.getLine();
            //check if we need to stop on the opening curly brace
            if (breakPointArr.contains(line) && !linesStoppedOnSoFar.contains(line)){
                //check if any of the parent nodes are not while or for statements
                var checkParent = ctx.getParent();
                while (checkParent != null){
                    //if the parent is an if statement break
                    if (checkParent.getClass().getSimpleName().equals("For_statementContext") || checkParent.getClass().getSimpleName().equals("While_statementContext")){
                        openBraceInLoop = true;
                        break;
                    }
                    //try to get the next parent
                    try{
                        checkParent = checkParent.getParent();
                    }
                    //if there is an error, break
                    catch (Exception e){
                        checkParent = null;
                        break;
                    }
                }
                //if we have found a loop, then we dont add the line to linesStoppedOnSoFar
                //else we add the line to linesStoppedOnSoFar
                if (!openBraceInLoop){
                    linesStoppedOnSoFar.add(line);
                    breakpoint(line);
                }
                else{
                    breakpoint(line);
                }
            }
            //check if we need to stop on the closing curly brace
            if (breakPointArr.contains(line2) && !linesStoppedOnSoFar.contains(line2)){
                //check if any of the parent nodes are not while or for statements
                var checkParent = ctx.getParent();
                while (checkParent != null){
                    //if the parent is an if statement break
                    if (checkParent.getClass().getSimpleName().equals("For_statementContext") || checkParent.getClass().getSimpleName().equals("While_statementContext")){
                        closeBraceInLoop = true;
                        break;
                    }
                    //try to get the next parent
                    try{
                        checkParent = checkParent.getParent();
                    }
                    //if there is an error, break
                    catch (Exception e){
                        checkParent = null;
                        break;
                    }
                }
                //if we have found a loop, then we dont add the line to linesStoppedOnSoFar
                //else we add the line to linesStoppedOnSoFar
                if (!closeBraceInLoop){
                    linesStoppedOnSoFar.add(line2);
                    breakpoint(line2);
                }
                else{
                    breakpoint(line2);
                }
            }
            return null;
        }
        switch (parent) {
            case "While_statementContext": //look back at this, use if for reference and test
                int parentLine = ctx.getParent().start.getLine();
                int startWhile = ctx.start.getLine();
                int endWhile = ctx.stop.getLine();
                ArrayList<Integer> linesWhile = new ArrayList<Integer>();
                for (int i = startWhile; i <= endWhile; i++){
                    linesWhile.add(i);
                }
                //find what line the first .line() is on
                int firstlineWhile = ctx.line(0).start.getLine();
                //find what line the last .line() is on
                int lastlineWhile = ctx.line(ctx.line().size() - 1).start.getLine();
                //for all the lines in lines before the first line, check if any need to be stopped on
                for (int i = parentLine; i < firstlineWhile; i++){
                    if (breakPointArr.contains(i) && !linesStoppedOnSoFar.contains(i)){
                        int line = i;
                        breakpoint(line);
                    }
                }

                for (int i = 0; i < ctx.line().size(); i++) {
                    //visit the line
                    Object val = visit(ctx.line(i));
                    //print line text
                    //if the current line equals nl, continue, else wait for input
                    if (ctx.line(i).getText().equals("nl")){
                        visit(ctx.line(i));
                        continue;
                    }
                    //else if the line starts with loop while
                    else if (ctx.line(i).getText().startsWith("loop while")){
                        continue;
                    }
                    else if (ctx.line(i).getText().startsWith("if")){
                        continue;
                    }
                    else {
                        //if the current line in global arraylist of breakpoints, wait for user input
                        if (breakPointArr.contains(ctx.line(i).start.getLine())){
                            int line = ctx.line(i).start.getLine();
                            linesStoppedOnSoFar.add(line);
                            breakpoint(line);
                        }
                        else{
                            continue;
                        }
                    }
                }
                //for all the lines in lines after the last line, check if any need to be stopped on
                for (int i = lastlineWhile + 1; i <= linesWhile.get(linesWhile.size() - 1); i++){
                    if (breakPointArr.contains(i) && !linesStoppedOnSoFar.contains(i)){
                        int line = i;
                        breakpoint(line);
                    }
                }
                return null;

            case "For_statementContext":
                //get all lines the block covers
                int startFor = ctx.start.getLine();
                int endFor = ctx.stop.getLine();
                ArrayList<Integer> linesFor = new ArrayList<Integer>();
                for (int i = startFor; i <= endFor; i++){
                    linesFor.add(i);
                }
                //find what line the first .line() is on
                int firstlineFor = ctx.line(0).start.getLine();
                //find what line the last .line() is on
                int lastlineFor = ctx.line(ctx.line().size() - 1).start.getLine();
                //for all the lines in lines before the first line, check if any need to be stopped on
                for (int i = startFor; i < firstlineFor; i++){
                    if (breakPointArr.contains(i)){
                        int line = i;
                        linesStoppedOnSoFar.add(line);
                        breakpoint(line);
                    }
                }
                for (int i = 0; i < ctx.line().size(); i++) {
                    //visit the line
                    Object val = visit(ctx.line(i));
                    //if next line is not null, wait
                    if (ctx.line(i).getText().equals("nl")){
                        visit(ctx.line(i));
                        continue;
                    }
                    else {
                        //check if we are at the last line
                        if (i == ctx.line().size() - 1){
                            //make sure line is not start with loop for, dont wanna have 2 stops for the same line
                            if (breakPointArr.contains(ctx.line(i).start.getLine()) && !ctx.line(i).getText().startsWith("loop for")){
                                int line = ctx.line(i).start.getLine();
                                linesStoppedOnSoFar.add(line);
                                breakpoint(line);
                            }
                            else{
                                continue;
                            }

                        }
                        else {                            //if the current char is not the last non special char, wait
                            if (currentCharInArray.toString().equals(lastNonSpecialChar)){
                                //if the current line in global arraylist of breakpoints, wait for user input
                                if (breakPointArr.contains(ctx.line(i).start.getLine()) && !ctx.line(i).getText().startsWith("loop for")){
                                    int line = ctx.line(i).start.getLine();
                                    linesStoppedOnSoFar.add(line);
                                    breakpoint(line);
                                }
                                else{
                                    continue;
                                }
                            }
                            else {
                                //if the current line in global arraylist of breakpoints, wait for user input
                                if (breakPointArr.contains(ctx.line(i).start.getLine()) && !ctx.line(i).getText().startsWith("loop for")){
                                    int line = ctx.line(i).start.getLine();
                                    linesStoppedOnSoFar.add(line);
                                    breakpoint(line);
                                }
                                else{
                                    continue;
                                }
                            }
                        }
                    }
                }
                // for all the lines in lines after the last line, check if any need to be stopped on
                for (int i = endFor; i <= linesFor.get(linesFor.size() - 1); i++){
                    if (breakPointArr.contains(i)){
                        int line = i;
                        linesStoppedOnSoFar.add(line);
                        breakpoint(line);
                    }
                }
                return null;

            case "FunctionContext":
                //get all lines the block covers
                int startFunc = ctx.start.getLine();
                int endFunc = ctx.stop.getLine();
                ArrayList<Integer> linesFunc = new ArrayList<Integer>();
                for (int i = startFunc; i <= endFunc; i++){
                    linesFunc.add(i);
                }
                //find what line the first .line() is on
                int firstlineFunc = ctx.line(0).start.getLine();
                //find what line the last .line() is on
                int lastlineFunc = ctx.line(ctx.line().size() - 1).start.getLine();
                //for all the lines in lines before the first line, check if any need to be stopped on
                for (int i = 0; i < firstlineFunc; i++){
                    if (breakPointArr.contains(i) && !linesStoppedOnSoFar.contains(i)){
                        int line = i;
                        linesStoppedOnSoFar.add(line);
                        breakpoint(line);
                    }
                }
                for (int i = 0; i < ctx.line().size(); i++) {
                    //visit the line
                    Object val = visit(ctx.line(i));
                    //check if line is nl
                    if (ctx.line(i).getText().equals("nl")){
                        visit(ctx.line(i));
                        continue;
                    }
                    else {
                        if (breakPointArr.contains(ctx.line(i).start.getLine())){
                            int line = ctx.line(i).start.getLine();
                            linesStoppedOnSoFar.add(line);
                            breakpoint(line);
                            if (val != null){
                                return val;
                            }
                        }
                        else{
                            if (val != null){
                                return val;
                            }
                        }
                    }
                }
                //for all the lines in lines after the last line, check if any need to be stopped on
                for (int i = lastlineFunc + 1; i <= linesFunc.get(linesFunc.size() - 1); i++){
                    if (breakPointArr.contains(i) && !linesStoppedOnSoFar.contains(i)){
                        int line = i;
                        linesStoppedOnSoFar.add(line);
                        breakpoint(line);
                    }
                }
                return null;

            case "If_statementContext":
                printLine("If statement\n");
                int startIf;
                int endIf;
                int firstlineIf;
                int lastlineIf;
                ArrayList<Integer> linesIf = new ArrayList<Integer>();
                ArrayList<Integer> thisPass = new ArrayList<Integer>();
                printLine("thisPass: " + thisPass + "\n");
                boolean loopCheck = false;
                //check if we are in an elif block by checking if currentStartElif and currentEndElif are not 0
                if (currentStartElif != 0 && currentEndElif != 0){
                    printLine("currentStartElif: " + currentStartElif + " currentEndElif: " + currentEndElif + "\n");
                    //then the start line is the currentStartElif and the end line is the currentEndElif
                    startIf = currentStartElif;
                    endIf = currentEndElif;
                    firstlineIf = currentLine1;
                    lastlineIf = currentLineMinus1;
                    printLine("startIf: " + startIf + " endIf: " + endIf + "\n");
                    printLine("firstlineIf: " + firstlineIf + " lastlineIf: " + lastlineIf + "\n");
                    for (int i = startIf; i <= endIf; i++){
                        linesIf.add(i);
                    }
                    var checkParent = ctx.getParent();
                    while (checkParent != null){
                        //if the parent is an if statement break
                        if (checkParent.getClass().getSimpleName().equals("For_statementContext") || checkParent.getClass().getSimpleName().equals("While_statementContext")){
                            loopCheck = true;
                            break;
                        }
                        //try to get the next parent
                        try{
                            checkParent = checkParent.getParent();
                        }
                        //if there is an error, break
                        catch (Exception e){
                            checkParent = null;
                            break;
                        }
                    }
                    //for all the lines in lines before the first line, check if any need to be stopped on
                    for (int i = startIf; i < firstlineIf; i++){
                        if(loopCheck){
                            //if were in a loop, we need to still be able to stop multiple times on the same line
                            if (breakPointArr.contains(i) && !linesVisitedInIf.contains(i) && !linesStoppedOnSoFar.contains(i)){
                                //check if we have been there this pass
                                if (thisPass.contains(i)){
                                    continue;
                                }
                                else{
                                    int line = i;
                                    thisPass.add(line);
                                    printLine("thisPass: " + thisPass + "\n");
                                    breakpoint(line);
                                }
                            }
                        }
                        else{
                            if (breakPointArr.contains(i) && !linesStoppedOnSoFar.contains(i) && !linesVisitedInIf.contains(i)){
                                int line = i;
                                linesStoppedOnSoFar.add(line);
                                linesVisitedInIf.add(line);
                                breakpoint(line);
                            }
                        }
                    }
                }
                else{
                    //get all lines the block covers
                    startIf = ctx.start.getLine();
                    endIf = ctx.stop.getLine();
                    printLine("startIf: " + startIf + " endIf: " + endIf + "\n");
                    //find what line the first .line() is on
                    firstlineIf = ctx.line(0).start.getLine();
                    //find what line the last .line() is on
                    lastlineIf = ctx.line(ctx.line().size() - 1).start.getLine();
                    printLine("firstlineIf: " + firstlineIf + " lastlineIf: " + lastlineIf + "\n");
                    for (int i = startIf; i <= endIf; i++){
                        linesIf.add(i);
                    }
                    var checkParent = ctx.getParent();
                    while (checkParent != null){
                        //if the parent is an if statement break
                        if (checkParent.getClass().getSimpleName().equals("For_statementContext") || checkParent.getClass().getSimpleName().equals("While_statementContext")){
                            loopCheck = true;
                            break;
                        }
                        //try to get the next parent
                        try{
                            checkParent = checkParent.getParent();
                        }
                        //if there is an error, break
                        catch (Exception e){
                            checkParent = null;
                            break;
                        }
                    }

                    //for all the lines in lines before the first line, check if any need to be stopped on
                    for (int i = startIf; i < firstlineIf; i++){
                        if (loopCheck){
                            //if were in a loop, we need to still be able to stop multiple times on the same line
                            if (breakPointArr.contains(i) && !linesVisitedInIf.contains(i) && !linesStoppedOnSoFar.contains(i)){
                                //check if we have been there this pass
                                if (thisPass.contains(i)){
                                    continue;
                                }
                                else{
                                    int line = i;
                                    linesVisitedInIf.add(line);
                                    thisPass.add(line);
                                    printLine("thisPass: " + thisPass + "\n");
                                    breakpoint(line);
                                }
                            }
                        }
                        else{
                            if (breakPointArr.contains(i) && !linesStoppedOnSoFar.contains(i) && !linesVisitedInIf.contains(i)){
                                int line = i;
                                linesStoppedOnSoFar.add(line);
                                linesVisitedInIf.add(line);
                                breakpoint(line);
                            }
                        }
                    }
                }


                Object valToReturn = null;

                for (int i = 0; i < ctx.line().size(); i++) {
                    //visit the line
                    Object val = visit(ctx.line(i));
                    //if the current line equals nl, continue, else wait for input
                    if (ctx.line(i).getText().equals("nl")){
                        //visit(ctx.line(i));
                        continue;
                    }
                    //if line is an if statement dont wait, we have already waited
                    else if (ctx.line(i).getText().startsWith("if")){
                        //get the line of the program this .line() is on
                        int line = ctx.line(i).start.getLine();
                        int line2 = ctx.line(ctx.line().size() - 1).start.getLine();
                        //add all lines between to a tmpArr
                        ArrayList<Integer> tmpArr = new ArrayList<Integer>();
                        for (int j = line; j < line2; j++){
                            tmpArr.add(j);
                        }
                        //push tmpArr onto the stack
                        stackOfLines.push(tmpArr);
                        //remove all lines in breakPointArr that are in tmpArr
                        breakPointArr.removeAll(tmpArr);
                        //print breakPointArr
                        //visit the if statement
                        visit(ctx.line(i));
                        //pop tmpArr off the stack
                        //check if stack is empty
                        if (!stackOfLines.isEmpty()){
                            stackOfLines.pop();
                        }
                    }
                    else {
                        //check if we are at the last line, if so no wait, else wait for user input
                        if (i == ctx.line().size() - 1){
                            if (breakPointArr.contains(ctx.line(i).start.getLine()) && !linesVisitedInIf.contains(ctx.line(i).start.getLine())){
                                int line = ctx.line(i).start.getLine();
                                linesStoppedOnSoFar.add(line);
                                //if not loopCheck, then we need to add the line to linesVisitedInIf
                                if (!loopCheck){
                                    linesVisitedInIf.add(line);
                                }
                                breakpoint(line);
                                if (val != null){
                                    valToReturn = val;
                                    break;
                                }
                            }
                            else{
                                if (val != null){
                                    valToReturn = val;
                                    break;
                                }
                            }
                        }
                        else {
                            //if the next line is nl and we are on the second last line, dont wait
                            if (ctx.line(i + 1).getText().equals("nl") && i == ctx.line().size() - 2){
                                //we need to check if the next line in program exists
                                //get parent
                                var parent2 = ctx.getParent();
                                //loop back until we find program
                                while (parent2 != null){
                                    if(parent2 instanceof startParser.ProgramContext){
                                        //print child 0 of program
                                        //get the line that comes after that
                                        var line = (parent2.getChildCount() - 2);
                                        //start at this node, while not null update to parent
                                        var parent3 = ctx.getParent();
                                        int numOfIfs = 0;
                                        while (parent3 != null){
                                            if (parent3 instanceof startParser.If_statementContext){
                                                numOfIfs++;
                                            }
                                            parent3 = parent3.getParent();
                                        }

                                        var parent4 = ctx.getParent();
                                        while (parent4 != null){
                                            if (parent4 instanceof startParser.If_statementContext){
                                                numOfIfs--;
                                                if (numOfIfs == 0){
                                                    //get the text of this line, save it
                                                    String text = parent4.getText();
                                                    String lineText = parent2.getChild(line).getText();
                                                    //if the text is the same as text on line, dont wait
                                                    if (text.equals(lineText)){
                                                        if (breakPointArr.contains(ctx.line(i).start.getLine()) && !linesVisitedInIf.contains(ctx.line(i).start.getLine())){
                                                            int lineCheck = ctx.line(i).start.getLine();
                                                            //if not loopCheck, then we need to add the line to linesVisitedInIf
                                                            if (!loopCheck){
                                                                linesVisitedInIf.add(line);
                                                            }
                                                            breakpoint(lineCheck);
                                                        }
                                                        else{
                                                            continue;
                                                        }
                                                    }
                                                    else{
                                                        //if the current line in global arraylist of breakpoints, wait for user input
                                                        if (breakPointArr.contains(ctx.line(i).start.getLine()) && !linesVisitedInIf.contains(ctx.line(i).start.getLine())){
                                                            int lineToPass = ctx.line(i).start.getLine();
                                                            //if not loopCheck, then we need to add the line to linesVisitedInIf
                                                            if (!loopCheck){
                                                                linesVisitedInIf.add(line);
                                                            }
                                                            breakpoint(lineToPass);
                                                        }
                                                        else{
                                                            continue;
                                                        }
                                                    }
                                                }
                                            }
                                            parent4 = parent4.getParent();
                                        }

                                        break;
                                    }
                                    else{
                                        parent2 = parent2.getParent();
                                    }
                                }
                            }
                            else {
                                //if the current line in global arraylist of breakpoints, wait for user input
                                if (breakPointArr.contains(ctx.line(i).start.getLine()) && !linesVisitedInIf.contains(ctx.line(i).start.getLine())){
                                    int line = ctx.line(i).start.getLine();
                                    linesStoppedOnSoFar.add(line);
                                    //if not loopCheck, then we need to add the line to linesVisitedInIf
                                    if (!loopCheck){
                                        linesVisitedInIf.add(line);
                                    }
                                    breakpoint(line);
                                }
                                else{
                                    continue;
                                }
                            }
                        }
                    }
                }
                //for all the lines in lines after the last line, check if any need to be stopped on
                for (int i = lastlineIf + 1; i <= linesIf.get(linesIf.size() - 1); i++){
                    int line = i;
                    if (loopCheck){
                        if (breakPointArr.contains(i) && !linesVisitedInIf.contains(i) && !linesStoppedOnSoFar.contains(i)){
                            //check if we have been there this pass
                            if (thisPass.contains(i)){
                                continue;
                            }
                            else{
                                thisPass.add(line);
                                breakpoint(line);
                            }
                        }
                    }
                    else{
                        //if the current line in global arraylist of breakpoints, wait for user input
                        if (breakPointArr.contains(i) && !linesStoppedOnSoFar.contains(i) && !linesVisitedInIf.contains(i)){
                            linesStoppedOnSoFar.add(line);
                            breakpoint(line);
                        }
                    }
                }
                loopCheck = false;
                if (valToReturn != null){
                    return valToReturn;
                }
                return null;

            case "Elif_blockContext":
                // get all lines the block covers
                int startElif = ctx.start.getLine();
                int endElif = ctx.stop.getLine();
                ArrayList<Integer> linesElif = new ArrayList<Integer>();
                for (int i = startElif; i <= endElif; i++){
                    linesElif.add(i);
                }
                //find what line the first .line() is on
                int firstlineElif = ctx.line(0).start.getLine();
                //find what line the last .line() is on
                int lastlineElif = ctx.line(ctx.line().size() - 1).start.getLine();
                //for all the lines in lines before the first line, check if any need to be stopped on
                for (int i = startElif; i < firstlineElif; i++){
                    if (breakPointArr.contains(i)){
                        int line = i;
                        linesStoppedOnSoFar.add(line);
                        breakpoint(line);
                    }
                }
                for (int i = 0; i < ctx.line().size(); i++) {
                    //visit the line
                    Object val = visit(ctx.line(i));
                    //if the current line equals nl, continue, else wait for input
                    if (ctx.line(i).getText().equals("nl")){
                        visit(ctx.line(i));
                        continue;
                    }
                    else {
                        //check if we are at the last line, if so no wait, else wait for user input
                        if (i == ctx.line().size() - 1){
                            if (breakPointArr.contains(ctx.line(i).start.getLine())){
                                int line = ctx.line(i).start.getLine();
                                linesStoppedOnSoFar.add(line);
                                breakpoint(line);
                                if (val != null){
                                    return val;
                                }
                            }
                            else{
                                if (val != null){
                                    return val;
                                }
                            }
                        }
                        else {
                            //if the current line in global arraylist of breakpoints, wait for user input
                            if (breakPointArr.contains(ctx.line(i).start.getLine())){
                                int line = ctx.line(i).start.getLine();
                                linesStoppedOnSoFar.add(line);
                                breakpoint(line);
                            }
                            else{
                                continue;
                            }
                        }
                    }
                }
                //for all the lines in lines after the last line, check if any need to be stopped on
                for (int i = lastlineElif + 1; i <= linesElif.get(linesElif.size() - 1); i++){
                    if (breakPointArr.contains(i)){
                        int line = i;
                        linesStoppedOnSoFar.add(line);
                        breakpoint(line);
                    }
                }
                return null;

            default:
                break;
        }
        return null;
    }

    int currentStartElif = 0;
    int currentEndElif = 0;
    public int currentLine1 = 0;
    public int currentLineMinus1 = 0;
    //override the visit function for elif_block
    @Override
    public Object visitElif_block(startParser.Elif_blockContext ctx) {
        //check if any of the parents are a loop
        var parent = ctx.getParent();
        boolean loopCheck = false;
        while (parent != null){
            if (parent instanceof startParser.For_statementContext || parent instanceof startParser.While_statementContext){
                loopCheck = true;
                break;
            }
            else{
                parent = parent.getParent();
            }
        }
        //if its a block, visit the block
        if (ctx.block() != null) {
            //get the line this elif statement starts on
            int startElif = ctx.start.getLine();
            //get the line this elif block ends on
            int endElif = ctx.stop.getLine();
            //check if the line before start line has a breakpoint and has not been stopped on
            if (breakPointArr.contains(startElif - 1) && !linesStoppedOnSoFar.contains(startElif - 1)){
                int line = startElif - 1;
                //if we are in a loop, dont add to array but still stop, else do both
                if (loopCheck){
                    breakpoint(line);
                }
                else{
                    linesStoppedOnSoFar.add(line);
                    breakpoint(line);
                }
            }
            //visit the block
            Object obj = visit(ctx.block());
            if (obj != null){
                return obj;
            }
            else {
                return null;
            }
        }
        else{
            //get the line this elif statement starts on
            int startElif = ctx.start.getLine();
            //get the line this elif block ends on
            int endElif;
            endElif = ctx.if_statement().block().stop.getLine();
            //check if the block has any .line()s
            if (ctx.if_statement().block().line().size() > 0){
                //get the line the first .line() is on
                currentLine1 = ctx.if_statement().block().line(0).start.getLine();
                //get the line the last .line() is on
                currentLineMinus1 = ctx.if_statement().block().line(ctx.if_statement().block().line().size() - 1).start.getLine();
            }
            else{
                //get the line the first .line() is on
                currentLine1 = ctx.if_statement().block().start.getLine();
                //get the line the last .line() is on
                currentLineMinus1 = ctx.if_statement().block().stop.getLine();
            }
            currentStartElif = startElif;
            currentEndElif = endElif;
            //visit the elif block
            var returnVal = visit(ctx.if_statement());
            //reset the start and end elif so we can check if we are in an elif block
            currentStartElif = 0;
            currentEndElif = 0;
            currentLine1 = 0;
            currentLineMinus1 = 0;
            return returnVal;
        }
    }

    //override the visit function for return_statement
    @Override
    public Object visitReturn_statement(startParser.Return_statementContext ctx) {
        //visit the expression within the return statement and assign it to a variable
        Object val = visit(ctx.expression());
        var i = ctx.getParent();
        while (i != null){
            if (i instanceof startParser.FunctionContext){
                return val;
            }
            else {
                i = i.getParent();
            }
        }
        //throw error
        int line = ctx.start.getLine();
        printLine("Error: Return statement outside of function\nOffending Symbol/Token: " + ctx.getText() + "\nLine: " + line + "\nRemember to return a value from a function!\n");
        System.exit(0);
        return null;
    }

    //override the visit function for the new line
    @Override
    public Object visitNl(startParser.NlContext ctx){
        printLine("\n");
        return null;
    }

    @Override
    //visit comment
    public Object visitComment(startParser.CommentContext ctx){
        ArrayList<Integer> comments = new ArrayList<Integer>();
        int firstLineOfComment = ctx.start.getLine();
        comments.add(firstLineOfComment);
        // Add all lines covered by the comment
        Token startToken = ctx.start;
        Token stopToken = ctx.stop;
        int startLine = startToken.getLine();
        int stopLine = stopToken.getLine();
        for (int i = startLine + 1; i <= stopLine; i++) {
            comments.add(i);
        }
        // or check if anything in breakPointArr is in comments
        for (int i = 0; i < comments.size(); i++) {
            if (breakPointArr.contains(comments.get(i)) && !linesStoppedOnSoFar.contains(comments.get(i))) {
                int line = comments.get(i);
                linesStoppedOnSoFar.add(line);
                breakpoint(line);
                //remove the line from breakPointArr
                breakPointArr.remove(comments.get(i));
            }
        }
        return null;
    }
}