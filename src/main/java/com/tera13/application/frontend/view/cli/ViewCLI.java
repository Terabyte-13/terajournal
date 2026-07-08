package com.tera13.application.frontend.view.cli;

import com.tera13.application.frontend.viewcontroller.DiaryCLIManager;

public abstract class ViewCLI {
    private DiaryCLIManager sm;

    public abstract void show();

    public DiaryCLIManager getSm() {
        return sm;
    }

    public void setSm(DiaryCLIManager sm) {
        this.sm = sm;
    }
}
