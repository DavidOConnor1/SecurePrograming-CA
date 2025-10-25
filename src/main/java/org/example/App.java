package org.example;

import java.io.IOException;
import java.util.Scanner;

public class App {
    private static AuthSystem controls = new AuthSystem();
    private static UserManager storage = new UserManager("db.dat");
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Select from the following choices \n1.Sign Up \n2.Login");

        int option = input.nextInt();
        input.nextLine(); // consume leftover newline

        switch (option) {
            case 1:
                System.out.println("Please Enter your username");
                String username = input.nextLine();

                System.out.println("Please Enter your password");
                String password = controls.hash(input.nextLine());

                boolean signUp = controls.register(username, password);
                if (signUp) {
                    System.out.println("User Registered Successfully");
                    try {
                        storage.storeUsers(controls.getUsers());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
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


                break;

            default:
                System.out.println("Invalid option");
        }
    }
}
