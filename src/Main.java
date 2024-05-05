//A java program that parses a simple java primitive data type initialization/declaration
//May 5, 2024
//3CS-A || Dimaunahan, Meneses
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    static String input;
    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    static Parser parser;
    public static void main(String[] args) throws Exception {
        parser = new Parser();
        
        while (true) {
            try {
                System.out.print("\nEnter your Java Primitive Variable Initialization/Declaration: ");
                input = reader.readLine();
                parser.parseStatement(input);
                
            } catch (Exception e) {
                System.out.println("----------Invalid Input!----------");
                parser.clearResult();//Result clearing functions when unexpected errors arise
            }
        }
    }
}
