import Authentication.Auth;
import Parser.QueryParser;
import Utilities.Constants;
import Utilities.Database;
import Utilities.Transaction;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        boolean isLoggedin = false;
        Auth auth = new Auth();

        while (!isLoggedin) {
            System.out.print("Press 1 for login\nPress 2 for register\nselect:");
            int selection = scanner.nextInt();
            scanner.nextLine();

            // email input
            System.out.print("Enter Email:");
            email = scanner.nextLine();

            // password input
            System.out.print("Enter Password:");
            String password = scanner.nextLine();

            // generate captcha
            String captcha = auth.generateCaptcha();

            // captcha input
            System.out.println("Enter this captcha:" + captcha);
            String userCaptcha = scanner.nextLine();

            // check if request for login or register
            if (selection == 1) {
                isLoggedin = auth.Login(email, password, userCaptcha);
            } else if (selection == 2) {
                isLoggedin = auth.Register(email, password, userCaptcha);
            }
        }

        // create query parser instance
        QueryParser parser = new QueryParser();
        do {
            // query input
            System.out.print(email + ">");
            String query = scanner.nextLine();

            // if input='exit' close the application
            if(query.equalsIgnoreCase("exit")){
                System.exit(0);
            }

            // send query for parsing
            parser.parseQuery(query.trim(), auth.getEmail());
        }while(true);
    }
}