import java.util.concurrent.TimeUnit;
/*
    Copyright (c) 2005, Corey Goldberg

    StopWatch.java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
*/


public class StopWatch {
    
    private long startTime = 0;
    private long stopTime = 0;
    private long startTimeNano = 0;
    private long stopTimeNano = 0;
    private boolean running = false;

    
    public void start() {
        this.startTime = System.currentTimeMillis(); 
        this.running = true;
    }

    public void restartNano(){
    	startTimeNano = 0 ;
    }
    
    public void stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    }
    
    public void startNano() {
        this.startTimeNano = System.nanoTime();
        this.running = true;
    }

    
    public void stopNano() {
        this.stopTimeNano = System.nanoTime(); 
        this.running = false;
    }
  
    public long getElapsedTimeNano() {
        long elapsed;
        if (running) {
             elapsed = (System.nanoTime() - startTimeNano);
        }
        else {
            elapsed = (stopTimeNano - startTimeNano);
        }
        return elapsed;
    }
    
    //elaspsed time in milliseconds
    public long getElapsedTime() {
        long elapsed;
        if (running) {
             elapsed = (System.currentTimeMillis() - startTime);
        }
        else {
            elapsed = (stopTime - startTime);
        }
        return elapsed;
    }
    
    
    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
        long elapsed;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000);
        }
        else {
            elapsed = ((stopTime - startTime) / 1000);
        }
        return elapsed;
    }

    //sample usage
    public static void main(String[] args) {
        StopWatch s = new StopWatch();
        s.start();
        //code you want to time goes here
        s.stop();
        System.out.println("elapsed time in milliseconds: " + s.getElapsedTime());
    }

}