package fr.neamar.timezonequicksettings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

public class TimezoneTileService extends TileService {
    public static final String TAG = "TimezoneTileService";

    public static final String PREFS_NAME = "timezone_tile_service";
    public static final String TIMEZONE_KEY = "timezone";
    public static final String TIMEZONE_NAME_KEY = "timezone";

    private SharedPreferences sp = null;
    private SharedPreferences getSharedPreferences() {
        if(sp == null) {
            sp = getBaseContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        }

        return sp;
    }

    private String getTimezone() {
        return getSharedPreferences().getString(TIMEZONE_KEY, "");
    }

    private String getTimezoneName() {
        return getSharedPreferences().getString(TIMEZONE_NAME_KEY, "");
    }

    private Dialog getTimezoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setTitle(R.string.pick_timezone)
                .setItems(R.array.timezone_names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        String[] timezones = getResources().getStringArray(R.array.timezone_names);
                        String timezone = timezones[which];
                        Log.i(TAG, "Setting timezone to "  + timezone);

                        SharedPreferences.Editor editor = getSharedPreferences().edit();
                        editor.putString(TIMEZONE_KEY, timezone);
                        String timezoneName = timezone.replaceAll("^.+/", "");
                        editor.putString(TIMEZONE_NAME_KEY, timezoneName);

                        editor.apply();

                        Toast.makeText(getBaseContext(), String.format(getString(R.string.new_timezone_toast), timezoneName), Toast.LENGTH_SHORT).show();

                        updateTile();
                    }
                });
        return builder.create();
    }

    private void updateTile() {
        Log.e("WTF", "Updated tile.");
        String timezoneToUse = getTimezone();

        Tile tile = getQsTile();

        if(timezoneToUse.isEmpty()) {
            Log.i(TAG, "Timezone not defined yet.");
            tile.setLabel(getString(R.string.tile_label_unitialized));
        }
        else {
            tile.setLabel(getTimezoneName());
        }

        getQsTile().updateTile();
    }

    @Override
    public void onTileAdded() {
        Log.i(TAG, "Tile added.");

        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        Log.i(TAG, "Tile removed.");

        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        Log.i(TAG, "Start listening.");

        updateTile();

        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        Log.i(TAG, "Stop listening.");
        super.onStopListening();
    }

    @Override
    public void onClick() {
        Log.i(TAG, "Tile clicked.");
        showDialog(getTimezoneDialog());

        super.onClick();
    }
}
