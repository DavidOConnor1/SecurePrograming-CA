package org.example;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private final String filename;

    public UserManager(String filename) {
        this.filename = filename;
    }

    public void storeUsers(map<String, AuthSystem.User> users) throws IOException
    {//open method
        try(ObjectOutputStream objs = new ObjectOutputStream(new FilterOutputStream(filename))){
            objs.writeObject(users);
        }//close try
    }//close method

    public Map<String, AuthSystem.User> loadUsers() throws IOException, ClassNotFoundException
    {//open method
        File file = new File(filename);
        if (!file.exists())
        {
            return new HashMap<>(); //no file created yet, will return an empty map
        }
    }
}
