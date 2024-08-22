package Authentication;

import Utilities.DirectoryPath;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Authentication class for user authentication
 */
public class Auth {
    private String email;
    private String password;
    private String captcha;
    private final static String usersFileName = "users.txt";

    public String getEmail() {
        return email;
    }

    /**
     * hasPassword method to hash the password
     * @param password - password to be hashed
     * @return - String hashed password
     * @throws NoSuchAlgorithmException
     */
    public String hashPassword(String password) throws NoSuchAlgorithmException {
        // get instance of md5
        MessageDigest md = MessageDigest.getInstance("MD5");

        // hash the password
        byte[] messageDigest = md.digest(password.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        String hashPassword = number.toString(16);
        while (hashPassword.length() < 32)
        {
            hashPassword = "0" + hashPassword;
        }

        // return hashed password
        return hashPassword;
    }

    /**
     * generateCaptcha method to generate random captcha
     * @return - String captcha
     */
    public String generateCaptcha(){
        // characters to add in captcha
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String captcha = "";

        // randomly generate captcha string
        while(captcha.length() != 6){
            captcha += characters.charAt((int)(Math.random()*62));
        }
        this.captcha = captcha;

        // return captcha
        return captcha;
    }

    /**
     * checkInput method to check if inputs are empty
     * @param email
     * @param password
     * @param captcha
     * @return - boolean
     */
    public boolean checkInput(String email, String password, String captcha){
        if(email.isEmpty()){
            return false;
        }
        if(password.isEmpty()){
            return false;
        }
        if (!this.captcha.equals(captcha)) {
            return false;
        }

        return true;
    }

    /**
     * Register method to register new user
     * @param email - user email
     * @param password - user password
     * @param captcha - captcha entered by user
     * @return - boolean
     */
    public boolean Register(String email, String password, String captcha) {
        try {
            // check if captcha is correct
            if(!checkInput(email, password, captcha)){
                throw new Exception("Invalid inputs");
            }

            // access the users file
            File usersFile = new File(DirectoryPath.getStorageDirectory() + usersFileName);

            // check if file exists
            if (!usersFile.exists()) {
                // create the file to store users
                FileWriter fileWriter = new FileWriter(usersFile);
                System.out.println("file created");
                fileWriter.write("users=[(" + email + ":" + hashPassword(password) + ")]");
                fileWriter.close();
                new File(DirectoryPath.getStorageDirectory() + email).mkdir();
                System.out.println("Registration Successful");
                return true;
            }

            // fetch all the users
            FileReader fileReader = new FileReader(usersFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String fileContent = bufferedReader.readLine();
            bufferedReader.close();
            fileReader.close();

            // check if email already registered
            if (fileContent.contains(email)) {
                throw new Exception("User with same email already exists.");
            }

            // open users file with append mode
            FileWriter fileWriter = new FileWriter(usersFile, false);

            // store the user email and password
            fileWriter.write(fileContent.substring(0, fileContent.length() - 1) + ",(" + email + ":" + hashPassword(password) + ")]");
            fileWriter.close();
            new File(DirectoryPath.getStorageDirectory() + email).mkdir();

            // set email and password
            this.email = email;
            this.password = password;

            System.out.println("Registration Successful.");
            return true;
        }catch (Exception e){
            // handle errors
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Login method to login existing user
     * @param email - user email
     * @param password - user password
     * @param captcha - captcha entered by user
     * @return - boolean
     */
    public boolean Login(String email, String password, String captcha) {
        try {
            // check if captcha is correct
            if (!this.captcha.equals(captcha)) {
                throw new Exception("Wrong captcha.");
            }

            // access the users file
            File usersFile = new File(DirectoryPath.getStorageDirectory() + usersFileName);

            // check if users database exists
            if(!usersFile.exists()){
                throw new Exception("No users added, Register first.");
            }

            // read the file
            FileReader fileReader = new FileReader(usersFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String fileContent = bufferedReader.readLine();
            bufferedReader.close();
            fileReader.close();
            String users = fileContent.substring(7, fileContent.length() - 1);
            String[] splitUsers = users.split(",");

            // separate all users
            for (String splitUser : splitUsers) {
                String user = splitUser.substring(1, splitUser.length() - 1);

                // separate the email and password field
                String[] userDetails = user.split(":");

                // check if email and password are correct
                if (email.equals(userDetails[0]) && hashPassword(password).equals(userDetails[1])) {
                    this.email = email;
                    this.password = password;
                    System.out.println("User logged in successful.");
                    return true;
                }
            }

            // throw exception for invalid credentials
            throw new Exception("Invalid credentials.");
        }catch(Exception e){
            // handle error
            System.out.println(e.getMessage());
            return false;
        }
    }
}
