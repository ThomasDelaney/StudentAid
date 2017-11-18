package com.example.teohe.studentaid;

/**
 * Created by Thomas on 17/11/2017.
 */

public class Mark
{
    private Module module;
    private String markName;
    private int markWorth;
    private int markScore;

    public Mark(Module module, String markName, int markWorth, int markScore)
    {
        this.module = module;
        this.markName = markName;
        this.markWorth = markWorth;
        this.markScore = markScore;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public int getMarkWorth() {
        return markWorth;
    }

    public void setMarkWorth(int markWorth) {
        this.markWorth = markWorth;
    }

    public int getMarkScore() {
        return markScore;
    }

    public void setMarkScore(int markScore) {
        this.markScore = markScore;
    }

    public String getMarkModuleName()
    {
        return getModule().getModuleName();
    }

    public int getMarkModuleWorth()
    {
        return getModule().getModuleWorth();
    }
}
