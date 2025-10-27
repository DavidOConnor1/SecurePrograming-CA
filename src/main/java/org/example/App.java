package org.example;

import java.io.IOException;
import java.util.Scanner;

public class App {
    private static AuthSystem controls = new AuthSystem();
    private static UserManager storage = new UserManager("db.dat");
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args)
    {//open main

        try
        {//open try
            controls.setUsers(storage.loadUsers());
        } //close try
        catch(IOException | ClassNotFoundException e)
        {//open catch
            System.out.println("No existing users. Starting Fresh");
        }//close catch

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
                System.out.println("Please Enter Your Username");
                String name = input.nextLine();

                System.out.println("Password");
                String pass = input.nextLine();

                String session = controls.login(name, pass);
                if (session != null)
                    {//open if
                        System.out.println("Login Successful! Welcome"+name+"Your Session ID is"+session);
                    }//close if
                else
                { //open else
                    System.out.println("Invalid username or password");
                }//close else

                break;

            default:
                System.out.println("Invalid option");
        }
    }
}
