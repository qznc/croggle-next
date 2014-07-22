package de.croggle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.Timer;

import de.croggle.backends.AndroidBackendHelper;
import de.croggle.backends.BackendHelper;
import de.croggle.backends.LocalizationBackend;
import de.croggle.backends.android.AndroidLocalizationBackend;
import de.croggle.data.LocalizationHelper;

/**
 * Android backend that initializes the central ApplicationListener.
 */
public class MainActivity extends AndroidApplication {

	/**
	 * Initializes the central ApplicationListener. Is called by the android
	 * lifecycle as soon as the app is started. On return, the inner app
	 * lifecycle of ApplicationListener is started.
	 * 
	 * @param savedInstanceState
	 *            a Bundle containing the activity's previously frozen state, if
	 *            there was one.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BackendHelper backendHelper = new AndroidBackendHelper();
		backendHelper.set();

		LocalizationBackend locBack = new AndroidLocalizationBackend(this);
		LocalizationHelper.setBackend(locBack);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.a = 8;

		initialize(new Croggle(), config);

		/*
		 * In case our timer thread has not survived: Definitively kill it by
		 * calling dispose() and create a new one
		 */
		try {
			Field threadField = Timer.class.getDeclaredField("thread");
			threadField.setAccessible(true);
			Object oldThread = threadField.get(null);

			if (oldThread != null) {
				Class<?> threadClass = oldThread.getClass();
				Method disposeMethod = threadClass.getDeclaredMethod("dispose",
						(Class<?>[]) null);
				disposeMethod.setAccessible(true);
				// dispose invocation erases all preexisting Timer instances
				// from
				// the TimerThread queue!
				disposeMethod.invoke(oldThread, (Object[]) null);

				Constructor<?> threadConstructor = threadClass
						.getConstructor((Class<?>[]) null);
				threadConstructor.setAccessible(true);
				Object newThread = threadConstructor
						.newInstance((Object[]) null);
				threadField.set(null, newThread);
			}
			/*
			 * Now all the reflection exception goodness
			 */
		} catch (NoSuchFieldException ignored) {
		} catch (IllegalAccessException ignored) {
		} catch (IllegalArgumentException ignored) {
		} catch (NoSuchMethodException ignored) {
		} catch (InstantiationException ignored) {
		} catch (InvocationTargetException ignored) {
		}
	}
}
