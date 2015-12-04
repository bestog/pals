package com.bestog.pals;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Class: InfoFragment
 * Zeigt Infos ueber die App und welche externen Bibliotheken verwendet wurden
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public class InfoFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_info, container, false);
    try {
      PackageInfo info = getActivity().getPackageManager()
          .getPackageInfo(getActivity().getPackageName(), 0);
      TextView version = (TextView) view.findViewById(R.id.version);
      version.setText(String.format(getString(R.string.info_version), info.versionName));
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return view;
  }

}

