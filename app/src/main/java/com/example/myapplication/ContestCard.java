package com.example.myapplication;

public class ContestCard {
    private int division;
    private boolean registered;
    private String contestName;

    public ContestCard(int division, boolean registered, String contestName) {
        this.division = division;
        this.registered = registered;
        this.contestName = contestName;
    }

    public int getDivision() {
        return division;
    }

    public void setDivision(int division) {
        this.division = division;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }
}
