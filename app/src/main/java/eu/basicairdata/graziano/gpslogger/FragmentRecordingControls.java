/*
 * FragmentRecordingControls - Java Class for Android
 * Created by G.Capelli on 20/5/2016
 * This file is part of BasicAirData GPS Logger
 *
 * Copyright (C) 2011 BasicAirData
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package eu.basicairdata.graziano.gpslogger;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static eu.basicairdata.graziano.gpslogger.GPSApplication.TOAST_VERTICAL_OFFSET;

/**
 * The Fragment that displays and manages the bottom bar.
 */
public class FragmentRecordingControls extends Fragment {

    Toast toast;

    private TextView tvGeoPointsNumber;
    private TextView tvPlacemarksNumber;
    private TextView tvLockButton;
    private TextView tvStopButton;
    private TextView tvAnnotateButton;
    private TextView tvRecordButton;
    final GPSApplication gpsApp = GPSApplication.getInstance();

    public FragmentRecordingControls() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recording_controls, container, false);

        tvLockButton = view.findViewById(R.id.id_lock);
        tvLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleLock();
            }
        });
        tvStopButton = view.findViewById(R.id.id_stop);
        tvStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRequestStop();
            }
        });
        tvAnnotateButton = view.findViewById(R.id.id_annotate);
        tvAnnotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRequestAnnotation();
            }
        });
        tvRecordButton = view.findViewById(R.id.id_record);
        tvRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleRecord();
            }
        });
        tvGeoPointsNumber = view.findViewById(R.id.id_textView_GeoPoints);
        tvPlacemarksNumber = view.findViewById(R.id.id_textView_Placemarks);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Workaround for Nokia Devices, Android 9
        // https://github.com/BasicAirData/GPSLogger/issues/77
        if (EventBus.getDefault().isRegistered(this)) {
            //Log.w("myApp", "[#] FragmentRecordingControls - EventBus: FragmentRecordingControls already registered");
            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
        Update();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    /**
     * The EventBus receiver for Short Messages.
     */
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onEvent(Short msg) {
        if (msg == EventBusMSG.UPDATE_TRACK) {
            Update();
        }
    }

    /**
     * Toggles the status of the recording, by managing the button behaviour and
     * the status of the recording process.
     * It also displays some toasts to inform the user about some conditions.
     */
    public void onToggleRecord() {
        if (isAdded()) {
            if (!gpsApp.isBottomBarLocked()) {
                if (!gpsApp.isStopButtonFlag()) {
                    gpsApp.setRecording(!gpsApp.isRecording());
                    if (!gpsApp.isFirstFixFound() && (gpsApp.isRecording())) {
                        if (toast != null) toast.cancel();
                        toast = Toast.makeText(gpsApp.getApplicationContext(), R.string.toast_recording_when_gps_found, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM, 0, TOAST_VERTICAL_OFFSET);
                        toast.show();
                    }
                    Update();
                }
            } else {
                if (toast != null) toast.cancel();
                toast = Toast.makeText(gpsApp.getApplicationContext(), R.string.toast_bottom_bar_locked, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, TOAST_VERTICAL_OFFSET);
                toast.show();
            }
        }
    }

    /**
     * Toggles the status of the placemark request, by managing the button behaviour and
     * the status of the request.
     * It also displays some toasts to inform the user about some conditions.
     */
    public void onRequestAnnotation() {
        if (isAdded()) {
            if (!gpsApp.isBottomBarLocked()) {
                if (!gpsApp.isStopButtonFlag()) {
                    gpsApp.setPlacemarkRequested(!gpsApp.isPlacemarkRequested());
                    if (!gpsApp.isFirstFixFound() && (gpsApp.isPlacemarkRequested())) {
                        if (toast != null) toast.cancel();
                        toast = Toast.makeText(gpsApp.getApplicationContext(), R.string.toast_annotate_when_gps_found, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM, 0, TOAST_VERTICAL_OFFSET);
                        toast.show();
                    }
                    Update();
                }
            } else {
                if (toast != null) toast.cancel();
                toast = Toast.makeText(gpsApp.getApplicationContext(), R.string.toast_bottom_bar_locked, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, TOAST_VERTICAL_OFFSET);
                toast.show();
            }
        }
    }

    /**
     * Manages the Stop button behaviour.
     * It also displays some toasts to inform the user about some conditions.
     */
    public void onRequestStop() {
        if (isAdded()) {
            if (!gpsApp.isBottomBarLocked()) {
                if (!gpsApp.isStopButtonFlag()) {
                    gpsApp.setStopButtonFlag(true, gpsApp.getCurrentTrack().getNumberOfLocations() + gpsApp.getCurrentTrack().getNumberOfPlacemarks() > 0 ? 1000 : 300);
                    gpsApp.setRecording(false);
                    gpsApp.setPlacemarkRequested(false);
                    Update();
                    if (gpsApp.getCurrentTrack().getNumberOfLocations() + gpsApp.getCurrentTrack().getNumberOfPlacemarks() > 0) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTrackPropertiesDialog tpDialog = new FragmentTrackPropertiesDialog();
                        gpsApp.setTrackToEdit(gpsApp.getCurrentTrack());
                        tpDialog.setTitleResource(R.string.finalize_track);
                        tpDialog.setFinalizeTrackWithOk(true);
                        tpDialog.show(fm, "");
                    } else {
                        if (toast != null) toast.cancel();
                        toast = Toast.makeText(gpsApp.getApplicationContext(), R.string.toast_nothing_to_save, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, TOAST_VERTICAL_OFFSET);
                        toast.show();
                    }
                }
            } else {
                if (toast != null) toast.cancel();
                toast = Toast.makeText(gpsApp.getApplicationContext(), R.string.toast_bottom_bar_locked, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, TOAST_VERTICAL_OFFSET);
                toast.show();
            }
        }
    }

    /**
     * Manages the Lock button behaviour.
     */
    public void onToggleLock() {
        if (isAdded()) {
            gpsApp.setBottomBarLocked(!gpsApp.isBottomBarLocked());
            if (gpsApp.isBottomBarLocked()) {
                if (toast != null) toast.cancel();
                toast = Toast.makeText(gpsApp.getApplicationContext(), R.string.toast_bottom_bar_locked, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, TOAST_VERTICAL_OFFSET);
                toast.show();
            }
            Update();
        }
    }

    /**
     * Sets the color of a drawable.
     *
     * @param drawable The Drawable
     * @param color The new Color to set
     */
    private void setTextViewDrawableColor(Drawable drawable, int color) {
        if (drawable != null) {
            drawable.clearColorFilter();
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
    }

//    private void setButtonToClickedState(@NonNull TextView button, int imageId, int stringId) {
//        ColorDrawable[] colorDrawables = {new ColorDrawable(getResources().getColor(R.color.colorPrimaryLight)),
//                new ColorDrawable(getResources().getColor(R.color.colorPrimary))};
//        TransitionDrawable transitionDrawable = new TransitionDrawable(colorDrawables);
//
//        button.setBackgroundDrawable(transitionDrawable);
//        if (imageId != 0) button.setCompoundDrawablesWithIntrinsicBounds(0, imageId, 0, 0);
//        button.setTextColor(getResources().getColor(R.color.textColorRecControlSecondary_Active));
//        if (stringId != 0) button.setText(getString(stringId));
//        setTextViewDrawableColor(button.getCompoundDrawables()[1], getResources().getColor(R.color.textColorRecControlPrimary_Active));
//        transitionDrawable.startTransition(500);
//    }

    /**
     * Sets the appearance of a button (TextView + upper compound Drawable) as "Clicked",
     * by setting the specified Drawable and Text and applying the right colours.
     *
     * @param button The TextView button
     * @param imageId The resource of the drawable
     * @param stringId The resource of the string
     */
    private void setButtonToClickedState(@NonNull TextView button, int imageId, int stringId) {
        button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        if (imageId != 0) button.setCompoundDrawablesWithIntrinsicBounds(0, imageId, 0, 0);
        button.setTextColor(getResources().getColor(R.color.textColorRecControlSecondary_Active));
        if (stringId != 0) button.setText(getString(stringId));
        setTextViewDrawableColor(button.getCompoundDrawables()[1], getResources().getColor(R.color.textColorRecControlPrimary_Active));
    }

    /**
     * Sets the appearance of a button (TextView + upper compound Drawable) as "Normal",
     * by setting the specified Drawable and Text and applying the right colours.
     *
     * @param button The TextView button
     * @param imageId The resource of the drawable
     * @param stringId The resource of the string
     */
    private void setButtonToNormalState(@NonNull TextView button, int imageId, int stringId) {
        button.setBackgroundColor(Color.TRANSPARENT);
        if (imageId != 0) button.setCompoundDrawablesWithIntrinsicBounds(0, imageId, 0, 0);
        button.setTextColor(getResources().getColor(R.color.textColorRecControlSecondary));
        if (stringId != 0) button.setText(getString(stringId));
        setTextViewDrawableColor(button.getCompoundDrawables()[1], getResources().getColor(R.color.textColorRecControlPrimary));
    }

    /**
     * Sets the appearance of a button (TextView + upper compound Drawable) as "Disabled"
     * by setting the specified Drawable and Text and applying the right colours.
     *
     * @param button The TextView button
     * @param imageId The resource of the drawable
     * @param stringId The resource of the string
     */
    private void setButtonToDisabledState(@NonNull TextView button, int imageId, int stringId) {
        button.setBackgroundColor(Color.TRANSPARENT);
        if (imageId != 0) button.setCompoundDrawablesWithIntrinsicBounds(0, imageId, 0, 0);
        button.setTextColor(getResources().getColor(R.color.textColorRecControlDisabled));
        if (stringId != 0) button.setText(getString(stringId));
        setTextViewDrawableColor(button.getCompoundDrawables()[1], getResources().getColor(R.color.textColorRecControlDisabled));
    }

    /**
     * Updates the user interface of the fragment.
     * It takes care of the state of each button.
     */
    public void Update() {
        if (isAdded()) {
            final Track track = gpsApp.getCurrentTrack();
            final boolean isRec = gpsApp.isRecording();
            final boolean isAnnot = gpsApp.isPlacemarkRequested();
            final boolean isLck = gpsApp.isBottomBarLocked();
            if (track != null) {
                if (tvGeoPointsNumber != null)            tvGeoPointsNumber.setText(track.getNumberOfLocations() == 0 ? "" : String.valueOf(track.getNumberOfLocations()));
                if (tvPlacemarksNumber != null)           tvPlacemarksNumber.setText(String.valueOf(track.getNumberOfPlacemarks() == 0 ? "" : track.getNumberOfPlacemarks()));
                if (tvRecordButton != null) {
                    if (isRec) setButtonToClickedState(tvRecordButton, R.drawable.ic_pause_24, R.string.pause);
                    else setButtonToNormalState(tvRecordButton, R.drawable.ic_record_24, R.string.record);
                }
                if (tvAnnotateButton != null) {
                    if (isAnnot) setButtonToClickedState(tvAnnotateButton, 0, 0);
                    else setButtonToNormalState(tvAnnotateButton, 0, 0);
                }
                if (tvLockButton != null) {
                    if (isLck) setButtonToClickedState(tvLockButton, R.drawable.ic_unlock_24, R.string.unlock);
                    else setButtonToNormalState(tvLockButton, R.drawable.ic_lock_24, R.string.lock);
                }
                if (tvStopButton != null) {
                    tvStopButton.setClickable(isRec || isAnnot || (track.getNumberOfLocations() + track.getNumberOfPlacemarks() > 0));
                    if (isRec || isAnnot || (track.getNumberOfLocations() + track.getNumberOfPlacemarks() > 0) || gpsApp.isStopButtonFlag()) {
                        if (gpsApp.isStopButtonFlag()) setButtonToClickedState(tvStopButton, 0, 0);
                        else setButtonToNormalState(tvStopButton, 0, 0);
                    } else {
                        setButtonToDisabledState(tvStopButton, 0, 0);
                    }
                }
            }
        }
    }
}