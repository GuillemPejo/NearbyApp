package fje.guillem.nearbyapp.Vistes;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fje.guillem.nearbyapp.R;
import fje.guillem.nearbyapp.Recursos.Utils;

/**
 * Classe SplashActivty
 *
 * Aquesta classe mostra una animació només obrir l'aplicació i despres passa a una altre activitat
 *
 * @author Guillem Pejó
 */

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView titol, subtitol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        titol = findViewById(R.id.titol);
        subtitol = findViewById(R.id.subtitol);

        this.showSplashAnimation();
        this.goToDashboard();
    }

    /**
     * Es mostra l'animació mitjançant la classe Animation.
     */
    private void showSplashAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom);
        logo.startAnimation(animation);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        titol.startAnimation(fadeIn);
        subtitol.startAnimation(fadeIn);
    }

    /**
     * Passa a la activity DashBoard despres de 2 segons
     */
    private void goToDashboard() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Utils.openActivity(SplashActivity.this, DashBoardActivity.class);
                    finish();
                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}

