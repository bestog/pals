package com.bestog.pals;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bestog.pals.provider.LocationProvider;

/**
 * Class: MainFragment
 * Einstellungen werden hier angezeigt und koennen geaendert werden
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public class MainFragment extends Fragment {

  private Intent intent;
  private SharedPreferences settings;
  private Switch switchMozilla;
  private Switch switchOpenBMap;
  private Switch switchOpenMap;
  private Switch switchOpenCell;
  private Button toggleButton;
  private TextView result;

  /**
   * Ueberpruefen, ob LocationService derzeit laeuft
   *
   * @return boolean
   */
  private boolean isServiceOnline() {
    ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (LocationService.class.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_main, container, false);
    settings = getActivity().getSharedPreferences("location-settings", Context.MODE_PRIVATE);
    intent = new Intent(getActivity(), LocationService.class);
    switchMozilla = (Switch) view.findViewById(R.id.switchMozilla);
    switchOpenBMap = (Switch) view.findViewById(R.id.switchOpenBMap);
    switchOpenMap = (Switch) view.findViewById(R.id.switchOpenMap);
    switchOpenCell = (Switch) view.findViewById(R.id.switchOpenCell);
    toggleButton = (Button) view.findViewById(R.id.toggleButton);
    result = (TextView) view.findViewById(R.id.result);
    toggleButton.setOnClickListener(new serviceListener());
    setSettings();
    if (isServiceOnline()) {
      setThemeStartService();
    }
    return view;
  }

  @Override
  public void onPause() {
    super.onPause();
    toggleButton.setOnClickListener(new serviceListener());
  }

  @Override
  public void onResume() {
    super.onResume();
    toggleButton.setOnClickListener(new serviceListener());
  }

  /**
   * Einstellungen der Provider holen und zuweisen
   */
  private void setSettings() {
    switchMozilla.setChecked(settings.getBoolean(LocationProvider.PROVIDER_MOZILLA, false));
    switchOpenBMap.setChecked(settings.getBoolean(LocationProvider.PROVIDER_OPENBMAP, false));
    switchOpenMap.setChecked(settings.getBoolean(LocationProvider.PROVIDER_OPENMAP, false));
    switchOpenCell.setChecked(settings.getBoolean(LocationProvider.PROVIDER_OPENCELL, false));
  }

  /**
   * Buttons aendern, wenn LocationService gestartet wird
   */
  private void setThemeStartService() {
    toggleButton.setOnClickListener(new serviceListener());
    toggleButton.setBackgroundColor(getResources().getColor(R.color.flat_red));
    toggleButton.setText(getString(R.string.service_stop));
    result.setVisibility(View.VISIBLE);
    result.setText(getString(R.string.service_run));
  }

  /**
   * ServiceListener, um zu schauen, ob der Service derzeit laeuft.
   * Wenn nein, dann startet er den LocationService mit den neuen Einstellungen.
   * Wenn ja, dann wird der LocationService beendet.
   */
  private class serviceListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      if (!isServiceOnline()) {
        if (!switchMozilla.isChecked() &&
            !switchOpenMap.isChecked() &&
            !switchOpenCell.isChecked() &&
            !switchOpenBMap.isChecked()) {
          Toast.makeText(getActivity(), getString(R.string.min_provider), Toast.LENGTH_SHORT)
              .show();
        } else {
          setThemeStartService();
          Editor editor = settings.edit();
          intent.putExtra(LocationProvider.PROVIDER_MOZILLA, switchMozilla.isChecked());
          editor.putBoolean(LocationProvider.PROVIDER_MOZILLA, switchMozilla.isChecked());
          intent.putExtra(LocationProvider.PROVIDER_OPENMAP, switchOpenMap.isChecked());
          editor.putBoolean(LocationProvider.PROVIDER_OPENMAP, switchOpenMap.isChecked());
          intent.putExtra(LocationProvider.PROVIDER_OPENCELL, switchOpenCell.isChecked());
          editor.putBoolean(LocationProvider.PROVIDER_OPENCELL, switchOpenCell.isChecked());
          intent.putExtra(LocationProvider.PROVIDER_OPENBMAP, switchOpenBMap.isChecked());
          editor.putBoolean(LocationProvider.PROVIDER_OPENBMAP, switchOpenBMap.isChecked());
          editor.apply();
          switchMozilla.setEnabled(false);
          switchOpenBMap.setEnabled(false);
          switchOpenCell.setEnabled(false);
          switchOpenMap.setEnabled(false);
          getActivity().startService(intent);
        }
      } else {
        toggleButton.setBackgroundColor(getResources().getColor(R.color.flat_green));
        toggleButton.setText(getResources().getText(R.string.service_start));
        result.setVisibility(View.GONE);
        switchMozilla.setEnabled(true);
        switchOpenBMap.setEnabled(true);
        switchOpenCell.setEnabled(true);
        switchOpenMap.setEnabled(true);
        getActivity().stopService(intent);
      }
    }
  }
}

