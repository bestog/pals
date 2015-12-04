package com.bestog.pals;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Class: MainActivity
 *
 * @author Sebastian Gottschlich
 * @version 1.0
 */
public class MainActivity extends FragmentActivity {

  @Override
  public void onBackPressed() {
    FragmentManager fragmentManager = getFragmentManager();
    if (fragmentManager.getBackStackEntryCount() == 1) {
      super.onBackPressed();
    } else {
      fragmentManager.popBackStack();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getFragmentManager().beginTransaction()
        .replace(R.id.frame_layout, new MainFragment())
        .addToBackStack(null)
        .commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_info) {
      getFragmentManager().beginTransaction()
          .replace(R.id.frame_layout, new InfoFragment())
          .addToBackStack(null)
          .commit();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
