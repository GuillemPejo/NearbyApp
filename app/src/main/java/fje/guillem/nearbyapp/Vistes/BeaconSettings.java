package fje.guillem.nearbyapp.Vistes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import fje.guillem.nearbyapp.R;
import fje.guillem.nearbyapp.Recursos.Utils;

/**
 * Classe BeaconSettings
 *
 * Aquesta classe desa les preferències de l'usuari
 *
 * @author Guillem Pejó
 */

public class BeaconSettings extends AppCompatActivity {

    private Switch sN, sV;
    private RadioButton rb_altbeacon, rb_edduid, rb_eddurl, rb_eddtlm, rb_eddeid, rb_ibeacon, rb_other;
    private EditText edtx_layout;

    private SharedPreferences sharedPreferences;
    public static final String S1 = "SWITCH_NOTIF";
    public static final String S2 = "SWITCH_VIBRATE";
    public static final String L = "LAYOUT PROTOCOL";
    public static final String R1 = "ALTBEACON";
    public static final String R2 = "EDDYSTONE_UID";
    public static final String R3 = "EDDYSTONE_URL";
    public static final String R4 = "EDDYSTONE_TLM";
    public static final String R5 = "EDDYSTONE_EID";
    public static final String R6 = "IBEACON";
    public static final String R7 = "OTHER";
    public static final String E7 = "MSG LAYOUT";

    //LAYOUTS IMPLEMENTATS A LA APLICACIÓ
    public static final boolean ALTBEACON = true;
    public static final boolean EDDYSTONE_UID = false;
    public static final boolean EDDYSTONE_URL = false;
    public static final boolean EDDYSTONE_TLM = false;
    public static final boolean EDDYSTONE_EID = false;
    public static final boolean IBEACON = false;
    public static final boolean ALTRES = false;


    private static final boolean[] layouts_conf = {ALTBEACON,EDDYSTONE_UID,EDDYSTONE_URL,EDDYSTONE_TLM,EDDYSTONE_EID, IBEACON, ALTRES};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        sN = findViewById(R.id.sN);
        sV = findViewById(R.id.sV);

        edtx_layout = findViewById(R.id.edtx_layout);

        rb_altbeacon = findViewById(R.id.rb_altbeacon);
        rb_edduid = findViewById(R.id.rb_edduid);
        rb_eddurl = findViewById(R.id.rb_eddurl);
        rb_eddtlm = findViewById(R.id.rb_eddtlm);
        rb_eddeid = findViewById(R.id.rb_eddeid);
        rb_ibeacon = findViewById(R.id.rb_ibeacon);
        rb_other = findViewById(R.id.rb_other);

        //Desactiva els raddiobuttons dels layaouts encara no implementats
        RadioButton[] layouts = {rb_altbeacon,rb_edduid,rb_eddurl,rb_eddtlm,rb_eddeid, rb_ibeacon,rb_other};
        for(int i=0; i<layouts_conf.length; i++){
               layouts[i].setEnabled(layouts_conf[i]);
        }

        //Crea un arxiu MyConfig.xml al dispositiu, de tal manera que les dades de configuració quedaran desades allà
        sharedPreferences = getSharedPreferences("MyConfig", Context.MODE_PRIVATE);

        switchNotification();
        switchVibration();

    }

    //Gestiona el switch de les notificacions
    private void switchVibration() {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        //Guarda el valor del switch, per saber si l'usuari vol rebre avisos amb vibració o no
        sV.setChecked(sharedPreferences.getBoolean(S2,true));
        sV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sV.setText("ON");
                    editor.putBoolean(S2, true);
                } else {
                    sV.setText("OFF");
                    editor.putBoolean(S2, false);
                }
                editor.apply();
            }
        });

        loadRadioButtons();
        edtx_layout.setEnabled(false);
    }

    //Gestiona el switch de les vibracions
    private void switchNotification() {

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        //Guarda el valor del switch, per saber si l'usuari vol rebre avisos amb notificacion o no
        sN.setChecked(sharedPreferences.getBoolean(S1,true));
        sN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sN.setText("ON");
                    editor.putBoolean(S1, true);
                } else {
                    sN.setText("OFF");
                    editor.putBoolean(S1, false);
                }
                editor.apply();

            }
        });
    }

    //Crida a la funcio saveRadioButtons i gestiona l'EditText
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rb_altbeacon:
                if (checked)
                    saveRadioButtons();
                break;
            case R.id.rb_edduid:
                if (checked)
                    saveRadioButtons();
                break;
            case R.id.rb_eddurl:
                if (checked)
                    saveRadioButtons();
                    break;
            case R.id.rb_eddtlm:
                if (checked)
                    saveRadioButtons();
                    break;
            case R.id.rb_eddeid:
                if (checked)
                    saveRadioButtons();
                     break;
            case R.id.rb_ibeacon:
                if (checked)
                    saveRadioButtons();
                    break;
            case R.id.rb_other:
                String lay = String.valueOf(edtx_layout.getText());
                if (checked)
                    edtx_layout.setEnabled(true);
                    if(!lay.isEmpty()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(E7, String.valueOf(edtx_layout.getText()));
                        saveRadioButtons();
                        editor.apply();

                    }else{
                            Utils.showToastMessage(this, "Si no introdueixes el layout, no es desara");
                        }
                    break;
        }

    }

    //Desa l'estat dels radiobuttons
    public void saveRadioButtons(){
        sharedPreferences = getSharedPreferences("MyConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(R1, rb_altbeacon.isChecked());
        editor.putBoolean(R2, rb_edduid.isChecked());
        editor.putBoolean(R3, rb_eddurl.isChecked());
        editor.putBoolean(R4, rb_eddtlm.isChecked());
        editor.putBoolean(R5, rb_eddeid.isChecked());
        editor.putBoolean(R6, rb_ibeacon.isChecked());
        editor.putBoolean(R7, rb_other.isChecked());
        edtx_layout.setEnabled(false);
        editor.apply();
    }

    //Carrega l'estat dels radiobuttons
    public void loadRadioButtons(){
        sharedPreferences = getSharedPreferences("MyConfig", Context.MODE_PRIVATE);
        rb_altbeacon.setChecked(sharedPreferences.getBoolean(R1, true));
        rb_edduid.setChecked(sharedPreferences.getBoolean(R2, false));
        rb_eddurl.setChecked(sharedPreferences.getBoolean(R3, false));
        rb_eddtlm.setChecked(sharedPreferences.getBoolean(R4, false));
        rb_eddeid.setChecked(sharedPreferences.getBoolean(R5, false));
        rb_ibeacon.setChecked(sharedPreferences.getBoolean(R6, false));
        rb_other.setChecked(sharedPreferences.getBoolean(R7, false));
        edtx_layout.setText(sharedPreferences.getString(E7,"m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

    }

}