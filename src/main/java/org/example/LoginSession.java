package org.example;

public class LoginSession {
    private int attempts =0; //declares attempts as 0
    private long lockUntil=0; //declares lock until as 0

    //get attempts
    public int getAttempts() { //open get
        return attempts; //return attempts
    }//close get

    //increases the increment attempts
    public void incrementFailedAttempts()
    {//open
        attempts++;
    }//close

    //resets attempts after user sucessfully logins
    public void resetAttempts()
    {//open
        attempts =0;
        lockUntil=0;
    }//close

    //sets the lock out time
    public void lock(long duration)
    {//open
        lockUntil = System.currentTimeMillis() + duration;
        attempts = 0;
    }//close

    //checks lockout time
    public boolean lockedOut()
    {//open
        return System.currentTimeMillis() < lockUntil; //will check the systems time left against the lock time
   }//close
}//close class
