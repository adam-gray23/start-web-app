//import the hashmap and map
import java.util.*;
import java.io.*;
public class startMainVisitor extends startBaseVisitor<Object>{
    Scanner scanner = new Scanner(System.in);
    Stack<HashMap<String, Object>> mappy = new Stack<HashMap<String, Object>>();
    public startMainVisitor(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        mappy.push(map);
    }

    @Override
    public Object visitProgram(startParser.ProgramContext ctx) {
        // Iterate through each line
        for (int i = 0; ctx.line(i) != null; i++) {
            String lineText = ctx.line(i).getText();
            //check if the line is a function
            if (ctx.line(i).getText().contains("function")){
                visit(ctx.line(i));
            }
            else if(ctx.line(i).if_statement() != null || ctx.line(i).while_statement() != null || ctx.line(i).for_statement() != null){
                visit(ctx.line(i));
            }
            else if (!lineText.equals("nl") && ctx.line(i).comment() == null && !ctx.line(i).getText().contains("function")) {
                System.out.println(lineText); //REMOVE LATER
                // Visit the line
                visit(ctx.line(i));
                // Wait for user input before executing the next line
                if (ctx.line(i + 1) != null && (!ctx.line(i + 1).getText().equals("nl")&& !ctx.line(i + 1).getText().equals("function"))) {
                    System.out.print("LINE: Press Enter to continue...");
                    scanner.nextLine();
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
        //while the value is true, visit the block
        while(val instanceof Boolean){
            if(((Boolean)val).booleanValue() == true){
                Object obj = visit(ctx.block());
                if (obj != null){
                    return obj;
                }
                val = visit(ctx.expression());
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

    //override the visit function for for loops
    @Override
    @SuppressWarnings("unchecked")
    public Object visitFor_statement(startParser.For_statementContext ctx) {
        HashMap<String, Object> map = mappy.peek();
        HashMap<String, Object> map2 = new HashMap<String, Object>(mappy.peek());
        //visit the expression within the for loop and assign it to a variable
        Object val = visit(ctx.expression());
        //if the value is an arraylist, visit the block
        if(val instanceof ArrayList){
            //change val to be an ArrayList
            ArrayList<Object> arr = (ArrayList<Object>) val;
            //visit the block for each element in the arraylist
            for(int i = 0; i < arr.size(); i++){
                //create a new variable for the element in the arraylist
                String var = ctx.NAME().getText();
                //put the element in the arraylist into the variable
                map.put(var, arr.get(i));
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
        //visit the lines in the block
        for (int i = 0; i < ctx.line().size(); i++) {
            //visit the line
            Object val = visit(ctx.line(i));
            //if the line is a return statement, return the value
            if (val != null){
                return val;
            }
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