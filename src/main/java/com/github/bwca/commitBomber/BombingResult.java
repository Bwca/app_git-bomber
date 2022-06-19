package com.github.bwca.commitBomber;

public class BombingResult {

    private long commits = 0L;

    private int days = 0;

    public int getDays() {
        return days;
    }

    public long getCommits() {
        return commits;
    }

    public void addCommits(long amount) {
        commits += amount;
    }

    public void addDay() {
        days++;
    }
}
