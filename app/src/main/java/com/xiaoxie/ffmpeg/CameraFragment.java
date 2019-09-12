package com.xiaoxie.ffmpeg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.xcb.cameralibrary.base.AspectRatio;
import com.xcb.cameralibrary.base.Constants;
import com.xcb.cameralibrary.view.CameraView;

import java.util.Iterator;
import java.util.Set;

import androidx.fragment.app.Fragment;

public class CameraFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private CameraView cameraView;
    private ImageView iv_aspect_ratio;
    private ImageView iv_flash;
    private ImageView iv_switch_camera;
    private int currentCameraID;
    private Set<AspectRatio> ratioSet;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        finViews(view);
        setListener();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void finViews(View view) {
        cameraView = view.findViewById(R.id.camera);
        iv_aspect_ratio = view.findViewById(R.id.iv_aspect_ratio);
        iv_flash = view.findViewById(R.id.iv_flash);
        iv_switch_camera = view.findViewById(R.id.iv_switch_camera);
    }

    private void setListener() {
        cameraView.setOnClickListener(this);
        iv_aspect_ratio.setOnClickListener(this);
        iv_flash.setOnClickListener(this);
        iv_switch_camera.setOnClickListener(this);
    }

    private void startPreview() {
        cameraView.start();
        currentCameraID = cameraView.getFacing();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                break;
            case R.id.iv_switch_camera:
                switchCamera();
                break;
            case R.id.iv_flash:
                flash();
                break;
            case R.id.iv_aspect_ratio:
                setAspectRatio();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (cameraView != null) {
            cameraView.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startPreview();
    }

    private void switchCamera() {
        if (cameraView != null) {
            int cameraID = cameraView.getFacing();
            if (cameraID == Constants.FACING_FRONT) {
                cameraView.setFacing(Constants.FACING_BACK);
            } else {
                cameraView.setFacing(Constants.FACING_FRONT);
            }
        }
    }

    private void flash() {
        if (cameraView != null) {
            int currentMOde = cameraView.getFlash();
            if (currentMOde == Constants.FLASH_AUTO) {
                iv_flash.setImageResource(R.drawable.ic_flash_on);
                cameraView.setFlash(Constants.FLASH_ON);
            } else if (currentMOde == Constants.FLASH_ON) {
                iv_flash.setImageResource(R.drawable.ic_flash_off);
                cameraView.setFlash(Constants.FLASH_OFF);
            } else {
                iv_flash.setImageResource(R.drawable.ic_flash_auto);
                cameraView.setFlash(Constants.FLASH_AUTO);
            }
        }
    }

    private void setAspectRatio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(getItems(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Iterator<AspectRatio> it = ratioSet.iterator();
                for (int i = 0; i < ratioSet.size(); i++) {
                    AspectRatio aspectRatio = it.next();
                    if (i == which) {
                        Toast.makeText(getContext(), aspectRatio.toString(), Toast.LENGTH_SHORT).show();
                        cameraView.setAspectRatio(aspectRatio);
                        break;
                    }
                }
            }
        });
        builder.create().show();
    }

    private CharSequence[] getItems() {
        ratioSet = cameraView.getSupportedAspectRatios();
        int length = ratioSet.size();
        Iterator<AspectRatio> it = ratioSet.iterator();
        CharSequence[] charSequences = new CharSequence[length];

        for (int i = 0; i < ratioSet.size(); i++) {
            AspectRatio aspectRatio = it.next();
            if (aspectRatio != null) {
                String s = aspectRatio.toString();
                charSequences[i] = s;
            }
        }
        return charSequences;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraView != null) {
            cameraView.stop();
        }
    }
}
