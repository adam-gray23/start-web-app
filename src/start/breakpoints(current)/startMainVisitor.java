//import the hashmap and map
import java.util.*;

import java.io.*;
public class startMainVisitor extends startBaseVisitor<Object>{
    Scanner scanner = new Scanner(System.in);
    String sessionToken = "";
    Stack<HashMap<String, Object>> mappy = new Stack<HashMap<String, Object>>();
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
        //call the main function of callFlask.java
        //callFlask.main(line, sessionToken);
        //call the main function of fileChecker.java
        fileChecker.main(null);
        readFile();
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
                        //print the current line content
                        continue;
                    }
                }
                //else if next line an nl, and two down is a comment dont wait
                else if (ctx.line(i + 1) != null && ctx.line(i + 1).getText().equals("nl") ){
                    if (ctx.line(i + 2) != null){
                        if (ctx.line(i + 2).comment() != null){
                            continue;
                        }
                        else{
                            //if the current line in global arraylist of breakpoints, wait for user input
                            if (breakPointArr.contains(ctx.line(i).start.getLine())){
                                int line = ctx.line(i).start.getLine();
                                breakpoint(line);
                            }
                            else{
                                continue;
                            }
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
                    if (breakPointArr.contains(ctx.line(i).start.getLine())){
                        int line = ctx.line(i).start.getLine();
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
        //output all of the current values of each variable in the map to a txt file
        //one k,v per line
        writeHashMapToFile(map, "output.txt");
        //return null
        return null;
    }

    private void writeHashMapToFile(HashMap<String, Object> map, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (HashMap.Entry<String, Object> entry : map.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
                writer.flush();
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
            System.out.println("Assignment Error!");
            System.out.println("Token: " + var);
            System.out.print("line " + ctx.start.getLine() + ", column " + ctx.start.getCharPositionInLine() + ": ");
            System.out.println("Variable " + var + " is not defined!");
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
                    System.out.println("Invalid Operation!");
                    int line = ctx.getStart().getLine();
                    System.out.println("Line " + line);
                    System.out.println("Remember, you can only add two numbers or two strings together!");
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
                    System.out.println("Invalid Operation!");
                    int line = ctx.getStart().getLine();
                    System.out.println("Line " + line);
                    System.out.println("Remember, you can only subtract two numbers!");
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
                    System.out.println("Invalid Operation!");
                    int line = ctx.getStart().getLine();
                    System.out.println("Line " + line);
                    System.out.println("Remember, you can only multiply two numbers!");
                    System.exit(0);
                }
            case "div":
            case "/":
                if (right instanceof Float){
                    if ((Float) right == 0.0){
                        System.out.println("Error: Divide by zero error.");
                        int line = ctx.start.getLine();
                        System.out.println("line " + line);
                        System.exit(0);
                    }
                }
                if (right instanceof Integer){
                    if ((Integer) right == 0){
                        System.out.println("Error: Divide by zero error.");
                        int line = ctx.start.getLine();
                        System.out.println("line " + line);
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
                    System.out.println("Invalid Operation!");
                    int line = ctx.getStart().getLine();
                    System.out.println("Line " + line);
                    System.out.println("Remember, you can only divide two numbers!");
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
                    System.out.println("Invalid Operation!");
                    int line = ctx.getStart().getLine();
                    System.out.println("Line " + line);
                    System.out.println("Remember, you can only get the mod of two numbers!");
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
            System.out.println("Invalid Operation!");
            int line = ctx.getStart().getLine();
            System.out.println("Line " + line);
            System.out.println("Remember, you can only raise a number to another number!");
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
        System.out.println("Invalid Operation!");
        int line = ctx.getStart().getLine();
        System.out.println("Line " + line);
        System.out.println("Remember, you can only negate a boolean!");
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
                    System.out.println("Invalid Operation!");
                    int line = ctx.getStart().getLine();
                    System.out.println("Line " + line);
                    System.out.println("Remember, you can only compare two numbers or two strings!");
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
                    System.out.println("Invalid Operation!");
                    int line = ctx.getStart().getLine();
                    System.out.println("Line " + line);
                    System.out.println("Remember, you can only compare two numbers or two strings!");
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
                    System.out.println("Invalid Operation!");
                    int line = ctx.getStart().getLine();
                    System.out.println("Line " + line);
                    System.out.println("Remember, you can only compare two numbers or two strings!");
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
                    System.out.println("Invalid Operation!");
                    int line = ctx.getStart().getLine();
                    System.out.println("Line " + line);
                    System.out.println("Remember, you can only compare two numbers or two strings!");
                }
            }
        default:
        //default case is an error
        try{
            System.out.println("Error: Invalid comparison!");
            //get the line
            int line = ctx.getStart().getLine();
            var lclass = left.getClass();
            var rclass = right.getClass();
            //split classes on the .
            String[] lsplit = lclass.toString().split("\\.");
            String[] rsplit = rclass.toString().split("\\.");
            //get the last element of the split
            String lclass2 = lsplit[lsplit.length - 1];
            String rclass2 = rsplit[rsplit.length - 1];
            System.out.println("Line " + line + ": " + lclass2 + " cannot be compared to " + rclass2);
            System.exit(0);
            return null;
        }
        //catch the null pointer exception if there is one
        catch(Exception e){
            System.out.println("Error: Null values cannot be compared!");
            int line = ctx.getStart().getLine();
            System.out.println("Line " + line);
            System.out.println("Try and ensure that no nullable values are compared!");
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
        System.out.print(out);
        //return null
        return null;
    }

    //override the visit function for if_statement
    @Override
    public Object visitIf_statement(startParser.If_statementContext ctx) {
        //visit the expression within the if statement and assign it to a variable
        Object val = visit(ctx.expression());
        //wait for user input after condition is checked
        //if the current line in global arraylist of breakpoints, wait for user input
        if (breakPointArr.contains(ctx.start.getLine())){
            int line = ctx.start.getLine();
            breakpoint(line);
        }
        //if the value is true, visit the block
        if(val instanceof Boolean){
            if(((Boolean)val).booleanValue() == true){
                //check if there is a return statement in the block
                Object obj = visit(ctx.block());
                if(obj != null){
                    return obj;
                }
            }
            //if there is an else if block, visit it
            else if (ctx.elif_block() != null) {
                return visit(ctx.elif_block());
            }
            else {
                return null;
            }
        }
        //invalid if statement
        else{
            System.out.println("Error: Invalid if/otherwise statement!");
            int line = ctx.getStart().getLine();
            System.out.println("Line " + line);
            System.out.println("Remember, you can only use a boolean value in an if statement!");
            System.exit(0);
        }
        return null;
    }

    //override the visit function for while_statement
    @Override
    public Object visitWhile_statement(startParser.While_statementContext ctx) {
        //visit the expression within the while statement and assign it to a variable
        Object val = visit(ctx.expression());
        //wait after condition is checked
        //if the current line in global arraylist of breakpoints, wait for user input
        if (breakPointArr.contains(ctx.start.getLine())){
            int line = ctx.start.getLine();
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
                    System.out.println("Error: Index: '" + index + "' is not an integer!");
                    System.out.println("Line: " + ctx.start.getLine());
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
                    System.out.println("Error: Index: '" + index + "' is not an integer!");
                    System.out.println("Line: " + ctx.start.getLine());
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
                    System.out.println("Error: Index: '" + index + "' is not an integer!");
                    System.out.println("Line: " + ctx.start.getLine());
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
                    System.out.println("Error: Index: '" + index + "' is not an integer!");
                    System.out.println("Line: " + ctx.start.getLine());
                    System.exit(0);
                }
            }
            //if the val is not an arraylist, print an error
            else{
                System.out.println("Error: " + ctx.NAME().getText() + " is not an array, string or number, or has not been defined!");
                System.exit(0);
            }
            //return null
            return null;
        }
        //catch the error if the index is out of bounds
        catch (Exception e){
            String s = e.toString();
            String[] parts = s.split(" ");
            System.out.println("Error: Index " + parts[2] + " out of bounds for array of length " + parts[parts.length - 1]);
            System.err.println("Offending Symbol/Token: " + ctx.NAME().getText());
            int line = ctx.start.getLine();
            System.err.println("Line: " + line);
            System.out.println("Remember: Arrays are indexed from 0 to length of the array - 1");
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
            }
        }
        //catch the error if the index is out of bounds
        catch (Exception e){
            String s = e.toString();
            String[] parts = s.split(" ");
            System.out.println("Error: Index " + parts[2] + " out of bounds for array of length " + parts[parts.length - 1]);
            System.err.println("Offending Symbol/Token: " + ctx.NAME().getText());
            int line = ctx.start.getLine();
            System.err.println("Line: " + line);
            System.out.println("Remember: Arrays are indexed from 0 to length of the array - 1");
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
            System.out.println("Error: Both expressions must be arrays!");
            int line = ctx.start.getLine();
            System.err.println("Line: " + line);
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
                //return null
                return null;
            }
            //visit the index
            Object index = visit(ctx.expression());
            //remove the element at the index
            arr.remove(index);
            //replace the arraylist in the map with the new arraylist
            map.put(ctx.NAME().getText(), arr);
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
            System.out.println("Error: Cannot get length of " + a[a.length - 1]);
            System.out.println("Line: " + ctx.start.getLine());
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
            System.out.println("Error: " + val + " is not an array or a string");
            System.err.println("Offending Symbol/Token: " + ctx.NAME().getText());
            int line = ctx.start.getLine();
            System.err.println("Line: " + line);
            System.out.println("Remember to loop up in numbers use a while loop");
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
        //return null
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
                System.err.println("Error: Incorrect number of arguments for function " + ctx.NAME().getText());
                System.err.println("Offending Symbol/Token: " + ctx.NAME().getText());
                int line = ctx.start.getLine();
                System.err.println("Line: " + line);
                System.exit(0);
            }
            //put the args into the original map
            for (int i = 0; i < args.size(); i++) {
                map.put(args.get(i), visit(ctx.expression(i)));
            }
            //push the map onto the stack
            mappy.push(map);
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
            System.err.println("Error: Function " + ctx.NAME().getText() + " not defined!");
            System.err.println("Offending Symbol/Token: " + ctx.NAME().getText());
            int line = ctx.start.getLine();
            System.err.println("Line: " + line);
            System.out.println("Remember to define your functions before you call them!");
            System.exit(0);
        }
        return null;
    }

    //visit the override function for block
    @Override
    public Object visitBlock(startParser.BlockContext ctx) {
        //get the parent node
        var parent = ctx.getParent().getClass().getSimpleName();
        switch (parent) {
            case "While_statementContext": //look back at this, use if for reference and test
            //needs to be able to stop on the line condition also
            //still need to fix bug with if last line of loop is last program line, dont wait
            //niall is being difficult
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
                    else {
                        //if the current line in global arraylist of breakpoints, wait for user input
                        if (breakPointArr.contains(ctx.line(i).start.getLine())){
                            int line = ctx.line(i).start.getLine();
                            breakpoint(line);
                        }
                        else{
                            continue;
                        }
                    }
                }
                return null;

            case "For_statementContext":
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
                            //we need to see if the next line is null
                            continue;

                        }
                        else {                            //if the current char is not the last non special char, wait
                            if (currentCharInArray.toString().equals(lastNonSpecialChar)){
                                continue;
                            }
                            else {
                                //if the current line in global arraylist of breakpoints, wait for user input
                                if (breakPointArr.contains(ctx.line(i).start.getLine())){
                                    int line = ctx.line(i).start.getLine();
                                    breakpoint(line);
                                }
                                else{
                                    continue;
                                }
                            }
                        }
                    }
                }
                return null;

            case "FunctionContext":
                for (int i = 0; i < ctx.line().size(); i++) {
                    //visit the line
                    Object val = visit(ctx.line(i));
                    if (val != null){
                        return val;
                    }
                }
                return null;

            case "If_statementContext":
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
                            if (val != null){
                                return val;
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
                                                        break;
                                                    }
                                                    else{
                                                        //if the current line in global arraylist of breakpoints, wait for user input
                                                        if (breakPointArr.contains(ctx.line(i).start.getLine())){
                                                            int lineToPass = ctx.line(i).start.getLine();
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
                                if (breakPointArr.contains(ctx.line(i).start.getLine())){
                                    int line = ctx.line(i).start.getLine();
                                    breakpoint(line);
                                }
                                else{
                                    continue;
                                }
                            }
                        }
                    }
                }
                return null;

            case "Elif_blockContext":
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
                            if (val != null){
                                return val;
                            }
                        }
                        else {
                            //if the current line in global arraylist of breakpoints, wait for user input
                            if (breakPointArr.contains(ctx.line(i).start.getLine())){
                                int line = ctx.line(i).start.getLine();
                                breakpoint(line);
                            }
                            else{
                                continue;
                            }
                        }
                    }
                }
                return null;

            default:
                break;
        }
        return null;
    }

    //override the visit function for elif_block
    @Override
    public Object visitElif_block(startParser.Elif_blockContext ctx) {
        //if its a block, visit the block
        if (ctx.block() != null) {
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
            //visit the elif block
            return visit(ctx.if_statement());
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
        System.err.println("Error: Return statement outside of function");
        System.err.println("Offending Symbol/Token: " + ctx.getText());
        int line = ctx.start.getLine();
        System.err.println("Line: " + line);
        System.out.println("Remember to return a value from a function!");
        System.exit(0);
        return null;
    }

    //override the visit function for the new line
    @Override
    public Object visitNl(startParser.NlContext ctx){
        System.out.println();
        return null;
    }
}