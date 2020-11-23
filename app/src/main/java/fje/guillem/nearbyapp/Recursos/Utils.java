package fje.guillem.nearbyapp.Recursos;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.List;

import fje.guillem.nearbyapp.Dades.Comerç;
import fje.guillem.nearbyapp.R;
import fje.guillem.nearbyapp.Vistes.DashBoardActivity;

/**
 * Classe Utils
 *
 * Aquesta classe dota de metòdes utils per la resta de classes
 *
 * @author Guillem Pejó
 */



public class Utils {
    public static List<Comerç> DataCache =new ArrayList<>();
    public static String searchString = "";

    /**
     * Aquest mètode permet mostrar un missatge Toast des de qualsevol activitat facilment
     * @param c
     * @param message
     */
    public static void show(Context c,String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Permet passar un numero de EditTexts i validar-los
     * @param editTexts
     * @return
     */
    public static boolean validar(EditText... editTexts){
        EditText nomtxt = editTexts[0];
        EditText descripciotxt = editTexts[1];
        EditText adreçatxt = editTexts[2];
        EditText msgbeacontxt = editTexts[3];
        EditText bId = editTexts[4];


        if(nomtxt.getText() == null || nomtxt.getText().toString().isEmpty()){
            nomtxt.setError("El nom és obligatòri!");
            return false;
        }
        if(descripciotxt.getText() == null || descripciotxt.getText().toString().isEmpty()){
            descripciotxt.setError("La descripcio és obligatòria!");
            return false;
        }
        if(adreçatxt.getText() == null || adreçatxt.getText().toString().isEmpty()){
            adreçatxt.setError("L'adreça és obligatòria!");
            return false;
        }
        if(msgbeacontxt.getText() == null || msgbeacontxt.getText().toString().isEmpty()){
            msgbeacontxt.setError("El missatge de la notifiació és obligatòri!");
            return false;
        }
        if(bId.getText() == null || bId.getText().toString().isEmpty()){
            bId.setError("L'ID del Beacon és obligatòri!");
            return false;
        }
        return true;

    }

    /**
     * Aquest mètode permet obrir una activity facilment
     * @param c
     * @param clazz
     */
    public static void openActivity(Context c,Class clazz){
        Intent intent = new Intent(c, clazz);
        c.startActivity(intent);
    }


    /**
     * Aquest mètode permet mostrar un Info Dialog des de qualsevol activitat facilment
    * @param activity
    * @param titol
    * @param msg
     */
    public static void showInfoDialog(final AppCompatActivity activity, String titol, String msg) {
        new LovelyStandardDialog(activity, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                .setTopColorRes(R.color.colorAccent)
                .setButtonsColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_baseline_info_24)
                .setTitle(titol)
                .setMessage(msg)
                .setPositiveButton("NO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setNeutralButton("Anar a l'inici", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openActivity(activity, DashBoardActivity.class);
                    }
                })
                .setNegativeButton("SI", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                })
                .show();
    }



    /**
     * Aquest mètode ens permet mostrar un diàleg de selecció única on podem seleccionar i retornar una categoria en EditText.
     * @param c
     * @param categoriatxt
     */
    public static void selectCategoria(Context c, final EditText categoriatxt){
        String[] stars = {"Supermercat","Carnisseria","Fruiteria","Forn","Bar","Restaurant","Botiga de roba",
                "Ferreteria","Òptica","Dentista","Metge","Farmàcia","Estanc","Llibreria","Papereria","Gimnàs",
                "Reparació d'electrodomèstics","Floristeria","Altres" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(c,
                android.R.layout.simple_list_item_1,
                stars);
        new LovelyChoiceDialog(c)
                .setTopColorRes(R.color.colorPrimaryDark)
                .setTitle("Tria una categoria")
                .setTitleGravity(Gravity.CENTER_HORIZONTAL)
                .setIcon(R.drawable.ic_baseline_shopping_cart_24)
                .setMessage("Selecciona la categoria que més defineix el comerç")
                .setMessageGravity(Gravity.CENTER_HORIZONTAL)
                .setItems(adapter, new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(int position, String item) {
                        categoriatxt.setText(item);
                    }
                })
                .show();
    }

    /**
     * Aquest mètode ens permet enviar un objecte comerç serialitzat a una activity específica
     * @param c
     * @param comerce
     * @param clazz
     */
    public static void sendComerceToActivity(Context c, Comerç comerce, Class clazz){
        Intent i=new Intent(c,clazz);
        i.putExtra("COMERCE_KEY", comerce);
        c.startActivity(i);
    }

    /**
     * Aquest mètode ens permet rebre un objecte comerç serialitzat, desserialitzar-lo i retornar-lo
     * @param intent
     * @param c
     */
    public static Comerç receiveComerce(Intent intent, Context c){
        try {
            return (Comerç) intent.getSerializableExtra("COMERCE_KEY");
        }catch (Exception e){
            e.printStackTrace();
            //show(c,"ERROR REBUDA DEL COMERCE: "+e.getMessage());
        }
        return null;
    }
    public static void showProgressBar(ProgressBar pb){
        pb.setVisibility(View.VISIBLE);
    }
    public static void hideProgressBar(ProgressBar pb){
        pb.setVisibility(View.GONE);
    }

    public static DatabaseReference getDatabaseRefence() {
        return FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Aquest mètode permet mostrar un missatge Toast centratm, des de qualsevol activitat, facilment
     * @param c
     * @param message
     */
    public static void showToastMessage(Context c, String message) {
        Toast toast = Toast.makeText(c, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}

