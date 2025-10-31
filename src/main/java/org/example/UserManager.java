package org.example;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

//used for persistence of user data
public class UserManager {

    private final String filename; //user data will be stored

    //constructor to manage user data stored in the .dat file
    public UserManager(String filename) {
        this.filename = filename;
    }


    //saves a map of user to disk using object serialization
    public void storeUsers(Map<String, AuthSystem.User> users) throws IOException
    {//open method
        //used to make sure the stream closes automatically
        try(ObjectOutputStream objs = new ObjectOutputStream(new FileOutputStream(filename))){
            objs.writeObject(users);
        }//close try
    }//close method

    @SuppressWarnings("unchecked")
    public Map<String, AuthSystem.User> loadUsers() throws IOException, ClassNotFoundException
    {//open method
        File file = new File(filename);

        if (!file.exists())
        {//open if
            return new HashMap<>(); //no file created yet, will return an empty map
        }//close if

        //deserialize map from file
        try(ObjectInputStream objInput = new ObjectInputStream(new FileInputStream(filename)))
        {//open try
            return (Map<String, AuthSystem.User>) objInput.readObject();
        }//close try
    }//close method
}//close class
