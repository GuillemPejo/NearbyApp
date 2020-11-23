package fje.guillem.nearbyapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
/**
 * Classe MainActivity
 *
 * Aquesta és la classe principal
 *
 * @author Guillem Pejó
 */
public class MainActivity extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        iniciarFirebaase();
    }

    private void iniciarFirebaase() {
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}