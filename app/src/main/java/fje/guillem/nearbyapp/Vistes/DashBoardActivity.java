package fje.guillem.nearbyapp.Vistes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.Preference;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import fje.guillem.nearbyapp.Dades.Comerç;
import fje.guillem.nearbyapp.R;
import fje.guillem.nearbyapp.Recursos.Utils;

import static fje.guillem.nearbyapp.Recursos.Utils.showToastMessage;

/**
 * Classe DashBoard Activity
 *
 * Aquesta classe
 *
 * @author Guillem Pejó
 */


public class DashBoardActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier{

    private static final String TAG = "NEARBYAPP";

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final long DEFAULT_SCAN_PERIOD_MS = 3000l;
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";

    private boolean clicked = false;
    private boolean haveDetectedBeaconsSinceBoot = false;
    public String id = "";
    ArrayList<String> beaconDetected = new ArrayList<String>();


    private final static String CHANNEL_ID = "NOTIFICATION";

    // Per poder interactuar amb els beacons des d'una activitat
    private BeaconManager mBeaconManager;

    // Representa el criteri de camps amb els que buscar Beacons
    private Region mRegion;

    private LinearLayout veureComerçosCard, afegirComerçosCard, gestioBeaconsCard, estadistiquesCard;
    private TextView txCt, txcD;
    private ImageButton searchBeacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);


        //BEACONS--------------------------------------------------------------------
        // Per defecte, AndroidBeaconLibrary només troba AltBeacons. Si es vol trobar un tipus de Beacon diferent,
        // s'ha d’especificar la disposició de bytes

        mBeaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        // Fixa un protocol Beacon, AltBeacon en aquest cas
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));

        //FUTURES IMPLEMENTACIONS -> SWITCH ENTRE PROTOCOLS
        /*if(whatLayoutWorks().equals("EDDYSTONE_UID_LAYOUT")){
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        }else {
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        }*/

        ArrayList<Identifier> identifiers = new ArrayList<>();

        mRegion = new Region(ALL_BEACONS_REGION, identifiers);

        //--------------------------------------------------------------------

        veureComerçosCard = findViewById(R.id.veureComerçosCard);
        afegirComerçosCard = findViewById(R.id.afegirComerçosCard);
        gestioBeaconsCard = findViewById(R.id.gestioBeaconsCard);
        estadistiquesCard = findViewById(R.id.estadistiquesCard);
        searchBeacons =  findViewById(R.id.searchBeacons);
        txCt =  findViewById(R.id.txCt);
        txcD =  findViewById(R.id.txCd);

        //Envia a la classe Comerços per tal de poder veure tots els comerços inscrits
        veureComerçosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(DashBoardActivity.this,
                        ComercosActivity.class);
            }
        });

        //Envia a la classe CRUD per tal de poder inscriure nous comerços
        afegirComerçosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(DashBoardActivity.this,
                        CRUDActivity.class);
            }
        });

        //Envia a la classe Beacon Settings per tal de poder configruar els avisos i el protocol
        gestioBeaconsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(DashBoardActivity.this,
                        BeaconSettings.class);
            }
        });

        //Activa la cerca de Beacons
        searchBeacons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicked){
                    clicked = false;
                    beaconDetected.clear();
                    stopDetectingBeacons();
                }else {
                    clicked = true;
                    txCt.setText("Cercant...");
                    txcD.setText("");
                    verifyBluetooth();
                    permisionControl();
                }
            }
        });
    }

    //Verifica que tenim tots els permisos necessaris per la cerca de Beacons
    private void permisionControl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Aquesta aplicació necessita accés a la ubicació de fons");
                            builder.setMessage("Concediu accés a la ubicació perquè aquesta aplicació pugui detectar balises en segon pla\n.");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @TargetApi(23)
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                            PERMISSION_REQUEST_BACKGROUND_LOCATION);
                                }

                            });
                            builder.show();
                        }
                        else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Funcionalitat limitada");
                            builder.setMessage("Com que no s'ha concedit accés a la ubicació de fons, aquesta aplicació no podrà descobrir Beacons en segon pla. Vés a Configuració -> Aplicacions -> Permisos i atorga accés a aquesta ubicació en segon pla.");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                }
                            });
                            builder.show();
                        }
                    }
                    startDetectingBeacons();
                }
            } else {
                if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            PERMISSION_REQUEST_FINE_LOCATION);
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Funcionalitat limitada");
                    builder.setMessage("Com que no s'ha concedit accés a la ubicació, aquesta aplicació no podrà descobrir Beacons. Vés a Configuració -> Aplicacions -> Permisos i atorga accés d'accés a aquesta aplicació.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
            }
        }
    }

    //Verifica que el BlueTooth esta activat, necessari en la cerca de Beacons
    private void verifyBluetooth() {

        try {
            if (!org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth no habilitat");
                builder.setMessage("Activeu el bluetooth en la configuració i reinicieu aquesta aplicació.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //finish();
                        //System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE no disponible");
            builder.setMessage("Aquest dispositiu no és compatible amb Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            builder.show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permisos de localització precisa concedits");
                    startDetectingBeacons();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Funcionalitat limitada");
                    builder.setMessage("Com que no s'ha concedit accés a la ubicació, aquesta aplicació no podrà descobrir Beacons.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_BACKGROUND_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permís d’ubicació en segon pla concedit ");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Funcionalitat limitada");
                    builder.setMessage("Ja que no s'ha concedit accés a la ubicació en segon pla, aquesta aplicació no podrà descobrir Beacons en un segon pla.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {

            // Usuario ha activado el bluetooth
            if (resultCode == RESULT_OK) {

                startDetectingBeacons();

            } else if (resultCode == RESULT_CANCELED) { // User refuses to enable bluetooth

                showToastMessage(this,getString(R.string.no_bluetooth_msg));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Comença a detectar Beacons
     */
    private void startDetectingBeacons() {

        // Fixa in periode d'escaneig
        mBeaconManager.setForegroundScanPeriod(DEFAULT_SCAN_PERIOD_MS);

        // Enllaça el servei de Beacons. Obté un callback quan esta llest per a ser usat
        mBeaconManager.bind(this);

        // Marca com a premut el botón de cercar
        searchBeacons.setAlpha(.5f);

    }

    @Override
    public void onBeaconServiceConnect() {

        try {
            // Comença a buscar els beacons que encaixin amb l'objecte Regió passat, incloent actualitzacions a la distància estimada
            mBeaconManager.startRangingBeaconsInRegion(mRegion);

            showToastMessage(this,getString(R.string.start_looking_for_beacons));

        } catch (RemoteException e) {
            Log.d(TAG, "S'ha produït una excepció al començar a buscar beacons " + e.getMessage());
        }

        mBeaconManager.addRangeNotifier(this);
    }

    /**
     *  Mètode cridat cada DEFAULT_SCAN_PERIOD_MS segons amb els beacons detectats durant aquest període i fa la detecció
     */
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        //Fa servir DecimalFormat per obtenir nomes 2 unitats alhora de mostrar la distància
        DecimalFormat df = new DecimalFormat("0.00");

        if (beacons.size() == 0) {
            showToastMessage(this,getString(R.string.no_beacons_detected));
        }
        for (final Beacon beacon : beacons) {
            if (!beaconDetected.contains(String.valueOf(beacon.getId1()))) {
                //Obtenim la distància
                final String distance = df.format(beacon.getDistance());
                beaconDetected.add(String.valueOf(beacon.getId1()));


                //Busquem al Firebase el comerç identificat amb el mateix bId que el del Beacon detectat
                Query q = FirebaseDatabase.getInstance().getReference("Comerce").orderByChild("bId").equalTo(String.valueOf(beacon.getId1()));
                q.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                            //El desem en un objecte Comerç
                            Comerç comerç = datasnapshot.getValue(Comerç.class);
                            comerç.setKey(datasnapshot.getKey());
                            //Si les preferencies del usuari ho permeten, llencem una notificació amb el nom, la distància i el msg de notificació del comerç
                            if(askNotificationActive()){
                                sendNotificationChannel(comerç.getNom(),comerç.getMsgbeacon(), distance, comerç);
                            }else{
                                Toast.makeText(DashBoardActivity.this, comerç.getNom() + ": "+ comerç.getMsgbeacon() + " a "+distance+"m", Toast.LENGTH_SHORT).show();
                            }
                            //Si les preferencies del usuari ho permeten, avisa a l'usuari amb una vibració
                            if(askVibrationActive()){
                                shakeIt();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("FIREBASE SELECT", databaseError.getMessage());
                        Utils.showInfoDialog(DashBoardActivity.this,"Opss.. Alguna cosa ha anat malament",databaseError.getMessage());
                    }
                });

                //
            }
        }
    }

    /**
     *  Mètode que comprova les preferències de notifiacions de l'usuari amb la classe sharedPreferences
     */
    private boolean askNotificationActive(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyConfig", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("SWITCH_NOTIF", true);
    }

    /**
     *  Mètode que comprova les preferencies de vibració de l'usuari amb la classe sharedPreferences
     */
    private boolean askVibrationActive(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyConfig", Context.MODE_PRIVATE);
        return  sharedPreferences.getBoolean("SWITCH_VIBRATE", true);
    }

    /**
     *  Mètode que comprova les preferencies de protocols de l'usuari amb la classe sharedPreferences, i en funcio d'això envia el resultat per gestionar-ho amb la classe BeaconManager
     */
    private String whatLayoutWorks(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyConfig", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("R2", false));
            return "EDDYSTONE_UID_LAYOUT";
         //if(...)
        // Aqui pots implementar els altres layouts, com IBeacon, Eddystone TLM, o amb layouts personalitzat passant un long amb els bytes
    }

    /**
     *  Mètode que envia una notificació
     * @param title
     * @param msg
     * @param distance
     */
    private void sendNotificationChannel(String title, String msg, String distance, Comerç comerce){
        //final Intent intent = new Intent(this, DetailActivity.class);
        //intent.putExtra("COMERCE_KEY", comerce);
        final Intent intent = new Intent(this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title + " - A "+distance+" metres")
                    .setContentText(msg)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(msg))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            final NotificationManagerCompat notifiction = NotificationManagerCompat.from(this);

            notifiction.notify(100, builder.build());
    }

    /**
     *  Mètode que fa vibrar el dispositiu durant 1 segon
     */
    private void shakeIt(){
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(1000,50));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000);
        }
    }

    /**
     *  Mètode que finalitza la cerca de Beacons
     */
    private void stopDetectingBeacons() {

        try {
            mBeaconManager.stopMonitoringBeaconsInRegion(mRegion);
            showToastMessage(this,getString(R.string.stop_looking_for_beacons));
        } catch (RemoteException e) {
            Log.d(TAG, "Se ha producido una excepción al parar de buscar beacons " + e.getMessage());
        }

        mBeaconManager.removeAllRangeNotifiers();

        // Desenlazar servicio de beacons
        mBeaconManager.unbind(this);

        // Activar botón de comenzar
        txCt.setText("Cerca");
        txcD.setText("Cerca Beacons propers");
        searchBeacons.setAlpha(1f);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        haveDetectedBeaconsSinceBoot = false;
        mBeaconManager.removeAllRangeNotifiers();
        mBeaconManager.unbind(this);
    }

    protected void onPause() {
        super.onPause();

        try {
            mBeaconManager.stopMonitoringBeaconsInRegion(mRegion);
        } catch (RemoteException e) {
            Log.d(TAG, "Se ha producido una excepción al parar de buscar beacons " + e.getMessage());
        }

        mBeaconManager.removeAllRangeNotifiers();

        // Desenlazar servicio de beacons
        mBeaconManager.unbind(this);

        // Activar botón de comenzar
        txCt.setText("Cerca");
        txcD.setText("Cerca Beacons propers");
        searchBeacons.setAlpha(1f);
    }

}
