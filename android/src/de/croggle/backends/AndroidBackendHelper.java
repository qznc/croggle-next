package de.croggle.backends;

import android.content.Context;
import android.view.Window;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;

import de.croggle.backends.android.AndroidContentValues;
import de.croggle.backends.android.AndroidDatabaseHelper;
import de.croggle.backends.sqlite.ContentValues;
import de.croggle.backends.sqlite.DatabaseHelper;

/**
 * A class to help with additional capabilities of the different backends,
 * without directly referencing them (using reflection). By doing so, the helper
 * can be part of every platform's build without pulling in dependencies to the
 * different backends.
 * 
 * The class's methods degrade gracefully, meaning that they silently ignore if
 * a certain functionality is currently unavailable.
 */
public class AndroidBackendHelper extends BackendHelper {

	private final Runnable acquirer = new Runnable() {
		@Override
		public void run() {
			AndroidApplication app = (AndroidApplication) Gdx.app;
			Window win = app.getWindow();
			win.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	};

	private final Runnable releaser = new Runnable() {
		@Override
		public void run() {
			AndroidApplication app = (AndroidApplication) Gdx.app;
			Window win = app.getWindow();
			win.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	};

	public AndroidBackendHelper() {
	}

	public static Context getAndroidContext() {
		return ((AndroidBackendHelper) backend).getContext();
	}

	@Override
	protected boolean wakelockAcquire() {
		Gdx.app.postRunnable(acquirer);
		return true;
	}

	@Override
	protected boolean wakelockRelease() {
		Gdx.app.postRunnable(releaser);
		return true;
	}

	@Override
	protected DatabaseHelper instantiateDatabaseHelper() {
		return new AndroidDatabaseHelper(getAndroidContext());
	}

	@Override
	protected ContentValues instantiateContentValues() {
		return new AndroidContentValues();
	}

	@Override
	protected String assetDirPath() {
		return "";
	}

	protected Context getContext() {
		return (Context) Gdx.app;
	}
}
