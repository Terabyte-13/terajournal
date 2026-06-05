package com.tera13.application.frontend.view.cli;

import com.tera13.application.frontend.viewcontroller.CLIManager;

public abstract class ViewCLI {
    private CLIManager sm;

    public abstract void show();

    public CLIManager getSm() {
        return sm;
    }

    public void setSm(CLIManager sm) {
        this.sm = sm;
    }
}
