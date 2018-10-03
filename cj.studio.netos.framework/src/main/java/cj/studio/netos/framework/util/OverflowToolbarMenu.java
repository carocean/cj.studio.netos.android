package cj.studio.netos.framework.util;

import android.util.Log;
import android.view.Menu;

import java.lang.reflect.Method;

public class OverflowToolbarMenu {
    public static void showIcon(Menu menu){
        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
            try{
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            }
            catch(NoSuchMethodException e){
                Log.e("onMenuOpened", e.getMessage());
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}
