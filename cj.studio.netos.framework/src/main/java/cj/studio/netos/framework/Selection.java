package cj.studio.netos.framework;

import android.app.Activity;

 class Selection implements ISelection {
    Object selectedViewport;
    @Override
    public Object selectedViewport() {
        return selectedViewport;
    }

    @Override
    public void selectViewport(Object o) {
        selectedViewport=o;
    }
}
