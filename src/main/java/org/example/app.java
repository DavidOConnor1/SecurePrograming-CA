package org.example;

import java.util.Scanner;

public class app {
    AuthSystem controls = new AuthSystem();
    Scanner input = new Scanner(System.in);

    static void main() {
        System.out.println("Select from the following choices \n1.Sign Up \n2.Login");

        int option = input.nextInt();
        switch (option){
            case 1:
                System.out.println("Please Enter your username");
                String username = input.nextLine();
                System.out.println("Please Enter your password");
                String password = controls.hash(input.nextLine());

                boolean signUp = controls.register(username, password);
                if(signUp)
                {
                    System.out.println("User Register Successfully");
                } else {
                    System.out.println("Registration failed (username already in use");
                }
                break;
            case 2:
                System.out.println("Please Enter Your Username and password");
                System.out.println("Username");
                String name = input.nextLine();
                System.out.println("Password");
                String pass = input.nextLine();

                controls.login(name, pass);

                break;
        }


    }
}
