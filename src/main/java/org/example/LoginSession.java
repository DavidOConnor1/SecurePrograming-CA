package org.example;

public class LoginSession {
    private int attempts =0;
    private long lockUntil=0;

    public int getAttempts() {
        return attempts;
    }

    public void incrementFailedAttempts()
    {
        attempts++;
    }

    public void resetAttempts()
    {
        attempts =0;
        lockUntil=0;
    }

    public void lock(long duration)
    {
        lockUntil = System.currentTimeMillis() + duration;
        attempts = 0;
    }

    public boolean lockedOut()
    {//open
        return System.currentTimeMillis() < lockUntil; //will check the systems time left against the lock time
   }//close
}
