package org.example;

import java.io.IOException;
import java.util.Scanner;

public class App {
    private static UserManager storage = new UserManager("db.dat"); //manages user data persistence
    private static AuthSystem controls = new AuthSystem(storage);   //handles registration, login and session control
    private static Scanner input = new Scanner(System.in); //reading input

    public static void main(String[] args)
    {//open main

        System.out.println("Select from the following choices \n1.Sign Up \n2.Login"); //presents choices

        int option = input.nextInt();
        input.nextLine(); //consumes leftover new line before reading next input

        switch (option) {
            case 1:
                System.out.println("Please Enter your username");
                String username = input.nextLine(); //takes username
                String sessionToken = controls.startRegistrationSession(username); //starts session and creates token
                System.out.println("Please Enter your password");
                String password = input.nextLine();//takes password

                String hashedPassword = AuthUtils.hash(password); //hashes the password

                boolean signUp = controls.register(username, hashedPassword,sessionToken); //attempt to register new user

                    if (signUp) { //open if
                        System.out.println("User Registered Successfully");

                        //save user data to disk
                        try {//open try
                            storage.storeUsers(controls.getUsers());

                        }/*close try*/ catch (IOException e) {//open catch
                            System.out.println("Error Saving User data" + e.getMessage());
                        }//close catch
                    } else {//open else
                        System.out.println("Registration failed (username already in use)");
                    }//close else

                break; //end switch

            case 2:

                //user login flow
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


                    //attempt login with provided details
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
                //if the user selects anything beside 1 or 2
                System.out.println("Invalid option");
        }
    }
}
