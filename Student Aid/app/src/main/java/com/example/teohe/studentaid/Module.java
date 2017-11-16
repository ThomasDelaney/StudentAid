package com.example.teohe.studentaid;

/**
 * Created by Thomas on 16/11/2017.
 */

public class Module
{
    private String moduleName;
    private int moduleWorth;

    public Module(String moduleName, int moduleWorth)
    {
        this.moduleName = moduleName;
        this.moduleWorth = moduleWorth;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getModuleWorth() {
        return moduleWorth;
    }

    public void setModuleWorth(int moduleWorth) {
        this.moduleWorth = moduleWorth;
    }
}
