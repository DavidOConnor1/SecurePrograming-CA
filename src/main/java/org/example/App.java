package org.example;

import java.io.IOException;
import java.util.Scanner;

public class App {
    private static UserManager storage = new UserManager("db.dat");
    private static AuthSystem controls = new AuthSystem(storage);
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args)
    {//open main

        System.out.println("Select from the following choices \n1.Sign Up \n2.Login");

        int option = input.nextInt();
        input.nextLine(); // take input

        switch (option) {
            case 1:
                System.out.println("Please Enter your username");
                String username = input.nextLine();

                System.out.println("Please Enter your password");
                String password = input.nextLine();

                String hashedPassword = AuthUtils.hash(password);

                boolean signUp = controls.register(username, hashedPassword);
                if (signUp) {
                    System.out.println("User Registered Successfully");
                    try {
                        storage.storeUsers(controls.getUsers());
                    } catch (IOException e) {
                        System.out.println("Error Saving User data"+e.getMessage());
                    }
                } else {
                    System.out.println("Registration failed (username already in use)");
                }
                break;

            case 2:

                System.out.println("Please Enter Your Username"); //prompts user to enter their username
                String name = input.nextLine(); //reads input from user

                String tempSessionId = "Temp_"+System.currentTimeMillis(); //creating temp login session
                boolean loggedIn = false; //set to false as default

                /*The loop below will allow the user to keep trying to login.
                The loop will finish if the user enters the correct details or they reach max
                login attempts
                * */
                while (!loggedIn)
                {//open while
                    System.out.println("Password"); //display to user to enter the password
                    String pass = input.nextLine(); //receives input for password


                    //Takes required details
                    String session = controls.login(name, pass,tempSessionId);

                    if (session != null) {//open if
                        System.out.println("Login Successful! Welcome " + name + " Your Session ID is " + session); //ID provided is a new session ID
                        loggedIn = true; //breaks loop
                    }//close if
                    else { //open else
                        System.out.println("Invalid username or password");
                    }//close else
                }//end loop

                break;

            default:
                System.out.println("Invalid option");
        }
    }
}
