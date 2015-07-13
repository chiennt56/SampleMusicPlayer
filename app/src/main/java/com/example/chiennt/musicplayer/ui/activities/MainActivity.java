package com.example.chiennt.musicplayer.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chiennt.musicplayer.R;
import com.example.chiennt.musicplayer.model.event.ServiceEvent;
import com.example.chiennt.musicplayer.services.Constants;
import com.example.chiennt.musicplayer.services.ForegroundService;
import com.example.chiennt.musicplayer.ui.fragments.ServiceStatusFragment;

/**
 * Created by Nguyen on 12/07/2015.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ServiceStatusFragment serviceStatusFragment;
    private Button btnBindService, btnUnbindServer;

    @Override
    int getLayoutSource(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    void initVaribles(Bundle saveInstanceState) {
        btnBindService = (Button) findViewById(R.id.btnBindService);
        btnUnbindServer = (Button) findViewById(R.id.btnUnBindService);
        btnBindService.setOnClickListener(this);
        btnUnbindServer.setOnClickListener(this);
    }

    @Override
    void initData(Bundle saveInstanceState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBindService:
                Intent intent = new Intent(this, BindServiceActivity.class);
                startActivity(intent);
                break;
            case R.id.btnUnBindService:
                serviceStatusFragment = new ServiceStatusFragment();
                serviceStatusFragment.show(getSupportFragmentManager(),"Dialog");
                break;
        }
    }

    public void onEvent(ServiceEvent event){
        switch (event.type){
            case START:
                Intent startIntent = new Intent(MainActivity.this, ForegroundService.class);
                startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                startService(startIntent);
                Toast.makeText(this, "Start service", Toast.LENGTH_SHORT).show();
            break;
            case STOP:
                Intent stopIntent = new Intent(MainActivity.this, ForegroundService.class);
                stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                startService(stopIntent);
                Toast.makeText(this, "Stop service", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
