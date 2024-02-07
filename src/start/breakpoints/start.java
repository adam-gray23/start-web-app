import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
public class start {
    public static void main(String[] args) throws Exception {
        try{

            // Get input file from command line
            String inputFile = null;
            //make sure there is a file
            if ( args.length>0 ){
                inputFile = args[0];
            }

            // Get input stream
            InputStream is = System.in;
            //if there is a file, get the input stream from the file
            if (inputFile != null){
                is = new FileInputStream(inputFile);
            }

            String fileName = "token.txt";
            File file = new File(fileName);
            if (!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //output arg[1] to token.txt
            if (args.length >= 2) {
                System.out.println("args[1]: " + args[1]);
                String numberAsString = args[1];
                System.out.println("Writing number to token.txt: " + numberAsString);
                try {
                    // Create a BufferedWriter object to write to file
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                    // Write the number to the file
                    writer.write(numberAsString);
                    // Close the writer
                    writer.flush();
                    System.out.println("Number has been written to token.txt successfully.");
                } catch (IOException e) {
                    System.out.println("An error occurred while writing to the file: " + e.getMessage());
                }
            } else {
                System.out.println("Please provide a number as arg[1].");
            }

            // Create lexer and parser
            startLexer lexer = new startLexer(CharStreams.fromStream(is));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            startParser parser = new startParser(tokens);
            //remove the default error listener
            parser.removeErrorListeners();
            parser.addErrorListener(new startErrorListener());
            //create a parse tree for the program
            System.out.println("Parsing the program...");
            ParseTree tree = parser.program();


            //check for errors
            //if so errorListener will print the error and exit
            if (startErrorListener.hasError){
            }
            //if no errors, run the program
            else{
                if(args.length > 1){
                    startMainVisitor eval = new startMainVisitor(args[1]);
                    eval.visit(tree);
                }
                else{
                    startMainVisitor eval = new startMainVisitor();
                    eval.visit(tree);
                }
            }
        //catch file not found error
        }
        catch(FileNotFoundException e){
            System.err.println("Error: File not found!");
            System.err.println("Please check the filename and try again.");
            System.exit(0);
        }
    }
}