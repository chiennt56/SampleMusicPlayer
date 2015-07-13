package com.example.chiennt.musicplayer.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.chiennt.musicplayer.R;
import com.example.chiennt.musicplayer.model.event.ServiceEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by Nguyen on 12/07/2015.
 */
public class ServiceStatusFragment extends DialogFragment implements View.OnClickListener {
    private Button btnStartService, btnStopService;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_service_status, null);
        btnStartService = (Button) view.findViewById(R.id.btnStartService);
        btnStopService = (Button) view.findViewById(R.id.btnStopService);
        btnStartService.setOnClickListener(this);
        btnStopService.setOnClickListener(this);
        this.getDialog().setTitle("Service Status");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getDialog().getWindow().setLayout(getActivity().getWindow().getDecorView().getWidth() * 9 /10,
                getActivity().getWindow().getDecorView().getHeight() * 7 /10);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartService:
                EventBus.getDefault().post(new ServiceEvent(ServiceEvent.TYPE.START));
                break;
            case R.id.btnStopService:
                EventBus.getDefault().post(new ServiceEvent(ServiceEvent.TYPE.STOP));
                break;
            default:
                break;
        }
    }
}
