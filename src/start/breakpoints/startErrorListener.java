import java.util.*;
import org.antlr.v4.runtime.*;


public class startErrorListener extends BaseErrorListener{
    String token = "";
    String id = "";

    public startErrorListener(){
        super();
    }

    public startErrorListener(String token, String id){
        super();
        this.token = token;
        this.id = id;
    }

    public static boolean hasError = false;
    //override the syntaxError method in antlr
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charpos, String msg, RecognitionException e){
        callDjango cd = new callDjango();
        hasError = true;
        msg = errorChecker(msg, offendingSymbol);
        System.out.println(msg);
        //System.err.println("Syntax Error!");
        String final_msg = "Syntax Error!" + "\n" + "Offending Symbol/Token: " + ((Token) offendingSymbol).getText() + "\n" + "line " + line + ", column " + (charpos) + ": " + msg; 
        cd.printLine(final_msg, token);
        callDjango.endCode(token, id);
        System.exit(0);
    }

    public String errorChecker(String msg, Object offendingSymbol){
        //create new message to be updated
        //message is updated based on the error message as shown below
        String new_msg = "";
        if (msg.contains("mismatched input")){
            if (msg.contains("expecting {'(', 'not', 'length of', '[', INT, BOOLEAN, 'null', STRING, FLOAT, NAME}")){
                new_msg += "Assignment is incomplete or wrong, expecting a value!\n";
            }
            else if (msg.contains("expecting {',', ')'}") || msg.contains("expecting {',', ']'}")){
                new_msg += "Possible missing commas or mismatched brackets!\n";
                new_msg += "If using write statement check for syntax errors or spelling errors!\n";
            }
            else if (msg.contains("expecting {'{', 'mult', 'div', 'mod', '*', '/', '%', 'add', 'sub', '+', '-', '==', '!=', '<=', '>=', '>', '<', 'equals', 'not equals', 'greater than', 'less than', 'greater than or equal to', 'less than or equal to', 'pow', '^', 'and', 'or', 'concat'}") && (((Token) offendingSymbol).getText().equals("[") || ((Token) offendingSymbol).getText().equals("]"))){
                new_msg += "Possible use of incorrect brackets!\n";
            }
            else if (msg.contains("expecting {')', 'mult', 'div', 'mod', '*', '/', '%', 'add', 'sub', '+', '-', '==', '!=', '<=', '>=', '>', '<', 'equals', 'not equals', 'greater than', 'less than', 'greater than or equal to', 'less than or equal to', 'pow', '^', 'and', 'or', 'concat'}") && ((Token) offendingSymbol).getText().equals("<EOF>")){
                new_msg += "Possible missing opening or closing brackets!\n";
            }
            else if(msg.contains("mismatched input 'write' expecting NAME")){
                new_msg += "Cannot override write function!\n";
            }
            else{
                new_msg += "Unexpected operation or line, or possible symbols in assignment name!\n";
            }
        }
        if (msg.contains("no viable alternative")){
            if (((Token) offendingSymbol).getText().equals("each")){
                new_msg += "Possible spelling error in or around Token: " + ((Token) offendingSymbol).getText() + "\n";
                new_msg += "Did you mean 'loop for each'?\n";
            }
            if (msg.contains("while")){
                new_msg += "Possible spelling error in or around Token: " + ((Token) offendingSymbol).getText() + "\n";
                new_msg += "Did you mean 'loop while'?\n";
            }
            if (msg.contains("loop")){
                new_msg += "Possible spelling error in or around Token: " + ((Token) offendingSymbol).getText() + "\n";
                new_msg += "Did you mean 'loop while' or 'loop for each'?\n";
            }
            else{
                new_msg += "Possible spelling or symbolic error in or around Token: " + ((Token) offendingSymbol).getText() + "\n";
                new_msg += "Make sure you are using valid keywords or symbols correctly and check your spelling!\n";
            }
        }
        if (msg.contains("extraneous input")){
            new_msg += "Statement on this line is possibly wrong or incomplete!\n";
            new_msg += "Check for spelling errors or syntax errors (Remember your spaces)!\n";
        }
        if (msg.contains("missing")){
            new_msg += msg + "'";
        }
        //strip last newline
        new_msg = new_msg.substring(0, new_msg.length() - 1);
        return new_msg;
    }
}