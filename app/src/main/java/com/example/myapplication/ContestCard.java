package com.example.myapplication;

public class ContestCard {
    private Long division;
    private boolean registered;
    private String contestName;

    public ContestCard(Long division, boolean registered, String contestName) {
        this.division = division;
        this.registered = registered;
        this.contestName = contestName;
    }

    public Long getDivision() {
        return division;
    }

    public void setDivision(Long division) {
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
