package com.example.teohe.studentaid;

/**
 * Created by Thomas on 17/11/2017.
 */

public class Mark
{
    private Module module;
    private String markName;
    private float markWorth;
    private float markScore;

    public Mark(Module module, String markName, float markWorth, float markScore)
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

    public float getMarkWorth() {
        return markWorth;
    }

    public void setMarkWorth(float markWorth) {
        this.markWorth = markWorth;
    }

    public float getMarkScore() {
        return markScore;
    }

    public void setMarkScore(float markScore) {
        this.markScore = markScore;
    }

    public String getMarkModuleName()
    {
        return getModule().getModuleName();
    }
}
