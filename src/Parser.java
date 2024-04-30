import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Parser {
    /*
     * The program parses for the following:
     * 
     * <statement> =:: <variable declaration> | <variable initialization>
     * <Variable Declaration> =:: <data type> <identifier>;
     * <Variable Initialization> =:: <data type> <identifier> = <value>;
     * <value> =:: <character> | <digit> | <boolean>
     * <character> =:: 'a' | 'b' | ... |'Z'
     * <digit> =:: "0" | ... | 9
     * <boolean> =:: "True" | "False"
     */

    private HashMap<String, String> map = new HashMap<String, String>();
    private ArrayList<String> result = new ArrayList<String>();
    private int index;
    private String input;

    public Parser(){
        setupHashMap();
    }

    public void parseStatement(String input) throws SyntaxErrorException{
        this.input = input;
        index = 0;
        String temp ="";
        parseDataType();
        parseIdentifier();

        if(input.charAt(index) == '='){
            temp += input.charAt(index);
            index++;
            checkForToken(temp);
            skipForWhiteSpaces();
            parseValue();
            if(input.charAt(index) == ';'){
                checkForToken(input);
            }else{
                throw new SyntaxErrorException("Expected a semicolon at index " + index);
            }

        }else if(input.charAt(index) == ';'){
            temp += input.charAt(index);
            index++;
            checkForToken(temp);
        }else{
            result.clear();
            throw new SyntaxErrorException("Expected '=' or ';' at index " + index);
        }

        display();
        result.clear();

    }

    public void parseDataType() throws SyntaxErrorException{
        String temp = "";

        //Stores characters into temp to be checked
        while(index < input.length() && Character.isLetter(input.charAt(index))){
            temp += input.charAt(index);
            index++;
        }
        //If the string at the position of data type does not match with the available data types, input is invalid
        if(!checkForToken(temp)){
            System.out.println(checkForToken(temp));
            System.out.println("\""+temp+"\"");
            result.clear();
            throw new SyntaxErrorException("Expected data type at index " + index);
        }
        skipForWhiteSpaces();
    }

    public void parseIdentifier() throws SyntaxErrorException{
        String temp = "";
        //Stores characters to temp to be stored
        while(index < input.length() && Character.isLetter(input.charAt(index)) || input.charAt(index) == '_'){
            //If character is not a letter or an underscore, input is invalid
            if(!Character.isLetter(input.charAt(index)) || input.charAt(index) != '_' ){
                result.clear();
                throw new SyntaxErrorException("Expected an identifier at index " + index);
            }
            temp += input.charAt(index);
            index++;
        }
        addIdentifier(temp);
        skipForWhiteSpaces();
    }
    public void parseValue() throws SyntaxErrorException{
        //Checks if the value is a character or a digit or a boolean value
        if(input.charAt(index) == '\''){
            parseCharacter();
        }else if(Character.isDigit(input.charAt(index))){
            parseDigit();
        }else if(Character.isLetter(input.charAt(index))){
            parseBoolean();
        }
        skipForWhiteSpaces();
        
    }

    public void parseCharacter() throws SyntaxErrorException{
        String temp ="";
        //Checking for char
        if(input.charAt(index) == '\''){
            temp += input.charAt(index);
            index++;
            checkForToken(temp);
            temp = "";//reset temp
            //Check for character within quotation marks
            if(Character.isLetter(input.charAt(index))){
                temp += input.charAt(index);
                index++;
                addCharacter(temp);
                temp = "";//reset temp
                if(input.charAt(index) == '\''){
                    temp += input.charAt(index);
                    index++;
                    checkForToken(temp);
                    temp = "";//reset temp
                }else{
                    throw new SyntaxErrorException("Expected a single quotation (') mark at index " + index);
                }
            }else{
                throw new SyntaxErrorException("Expected a character at index " + index);
            }
        }else{
            throw new SyntaxErrorException("Expected a single quotation (') mark at index " + index);
        }
    }

    public void parseDigit() throws SyntaxErrorException{
        String temp ="";
        //Gathers whole digit string, 
        while (index < input.length() && Character.isLetterOrDigit(input.charAt(index)) || input.charAt(index) == '.') {
            temp += input.charAt(index);
            index++;
        }
        addDigit(temp);
        skipForWhiteSpaces();
    }

    public void parseBoolean() throws SyntaxErrorException{
        String temp ="";
        //Stores characters to temp
        while(index < input.length() && Character.isLetter(input.charAt(index))){
            temp += input.charAt(index);
            index++;
        }
        //Checks if temp is "True" or "False", else it is invalid
        if(temp.equals("True")){
            addBoolean(temp);
        }else if(temp.equals("False")){
            addBoolean(temp);
        }else{
            throw new SyntaxErrorException("Expected a boolean value of 'True' or 'False' at index "+ index);
        }
    }

    // Checks the input string if it matches one of the keys in the hashmap of
    // lexemes:tokens pairs
    public boolean checkForToken(String string) {
        boolean tokenMatch = false;
        for (String key : map.keySet()) {
            if (string.equals(key)) {
                result.add(string + " : " + map.get(key));
                tokenMatch = true;
                break;
            }
        }
        return tokenMatch;
    }

    public void addIdentifier(String identifier){
        result.add(identifier + " : Identifier");
    }
    public void addCharacter(String character){
        result.add(character + " : Character");
    }
    public void addBoolean(String bool){
        result.add(bool + " : Character");
    }

    public void addDigit(String digit) throws SyntaxErrorException{
        if(identifyNumericType(digit).equals("Not a numeric type")){
            result.clear();
            throw new SyntaxErrorException("Expected a valid digit at index "+ index);//An exception is thrown if the digit provided is not a valid numeric type
        }
        result.add(digit + " : " + identifyNumericType(digit));//Digit is added to result if valid
    }



    public void skipForWhiteSpaces() {
        while (index < input.length() && input.charAt(index) == ' ') {
            index++;
        }
    }

    public String identifyNumericType(String str) {
        // Regular expressions to match different numeric types
        String byteRegex = "-?\\d+[bB]";
        String shortRegex = "-?\\d+[sS]";
        String intRegex = "-?\\d+";
        String longRegex = "-?\\d+[lL]";
        String floatRegex = "-?\\d+\\.\\d+[fF]?";
        String doubleRegex = "-?\\d+\\.\\d+([dD]|\\.)?";

        // Checking if the input str matches one of the patterns
        if (Pattern.matches(byteRegex, str)) {
            return "Byte Literal";
        } else if (Pattern.matches(shortRegex, str)) {
            return "Short Literal";
        } else if (Pattern.matches(intRegex, str)) {
            return "Integer Literal";
        } else if (Pattern.matches(longRegex, str)) {
            return "Long Literal";
        } else if (Pattern.matches(floatRegex, str)) {
            return "Float Literal";
        } else if (Pattern.matches(doubleRegex, str)) {
            return "Double Literal";
        } else {
            return "Not a numeric type";
        }
    }

    // Simply filling up the hashmap with values beforehand
    public void setupHashMap() {
        map.put("char", "Keyword");
        map.put("boolean", "Keyword");
        map.put("byte", "Keyword");
        map.put("short", "Keyword");
        map.put("int", "Keyword");
        map.put("long", "Keyword");
        map.put("float", "Keyword");
        map.put("double", "Keyword");
        map.put("=", "Equal Sign");
        map.put("'", "Single Quotation");
        map.put(";", "Semicolon");
    }

    public void display(){
        System.out.println("\n----- Lexeme : Token Pairs -----\n");
            for (String str : result) {
                System.out.println(str);
            }
    }

    
}

class SyntaxErrorException extends Exception {
    public SyntaxErrorException(String message) {
        super(message);
    }
}
