package cj.sutdio.netos.netflow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cj.studio.netos.framework.annotation.Viewport;

@Viewport(name = "/netflow")
public class NetflowViewport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewport_netflow);
    }
}
