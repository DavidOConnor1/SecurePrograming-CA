package org.example;

public class SessionInfo {
    private final String username; ///declare username as string
    private final long createdAt; //declares the time of the session was made/ when the account was made

    //constructor method
    //generates a new session info instance for the given username
    public SessionInfo(String username) {//open constructor
        this.username = username;
        this.createdAt = System.currentTimeMillis(); //grabs the systems current time
    } //end constructor

    //get username
    public String getUsername() {//open get
        return username;
    }//close get

    //get created at
    public long getCreatedAt() { //open get
        return createdAt;
    } //close get
}//close class
