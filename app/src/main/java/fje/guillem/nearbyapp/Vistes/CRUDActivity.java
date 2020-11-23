package fje.guillem.nearbyapp.Vistes;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.firebase.database.DatabaseReference;

import fje.guillem.nearbyapp.Dades.Comerç;
import fje.guillem.nearbyapp.R;
import fje.guillem.nearbyapp.Recursos.FirebaseCRUD;
import fje.guillem.nearbyapp.Recursos.Utils;

/**
 * Classe CRUDActivity
 *
 * Aquesta classe permet validar i inserir les dades
 *
 * @author Guillem Pejó
 */

public class CRUDActivity extends AppCompatActivity {

    private EditText nomtxt, descripciotxt, adreçatxt, categoriatxt, msgbeacontxt, bIdtxt;
    private TextView capçalera;
    private ProgressBar mProgressBar;

    private final Context c = CRUDActivity.this;
    private Comerç receivedComerce;
    private FirebaseCRUD crudHelper = new FirebaseCRUD();
    private DatabaseReference db = Utils.getDatabaseRefence();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud);

        mProgressBar = findViewById(R.id.mProgressBarSave);

        capçalera = findViewById(R.id.capçalera);
        nomtxt = findViewById(R.id.nomtxt);
        descripciotxt = findViewById(R.id.descripciotxt);
        adreçatxt = findViewById(R.id.adreçatxt);
        categoriatxt = findViewById(R.id.categoriatxt);
        msgbeacontxt = findViewById(R.id.msgbeacontxt);
        bIdtxt = findViewById(R.id.bIdtxt);


        this.showSelectedCategoriaInEditText();

    }

    private void insertData() {
        String nom, descripcio, adreça, categoria, msgbeacon, bId;
        if (Utils.validar(nomtxt, descripciotxt, categoriatxt, msgbeacontxt, bIdtxt)) {
            nom = nomtxt.getText().toString();
            descripcio = descripciotxt.getText().toString();
            adreça = adreçatxt.getText().toString();
            categoria = categoriatxt.getText().toString();
            msgbeacon = msgbeacontxt.getText().toString();
            bId = bIdtxt.getText().toString();

            Comerç newComerce = new Comerç("",nom,descripcio,adreça,categoria,msgbeacon, bId);
            crudHelper.insert(this,db,mProgressBar,newComerce);

        }
    }

    /**
     * Valida i actualitza les dades
    */
    private void updateData() {
        String nom, descripcio, adreça, categoria, msgbeacon, bId;
        if (Utils.validar(nomtxt, descripciotxt, categoriatxt, msgbeacontxt, bIdtxt)) {
            nom = nomtxt.getText().toString();
            descripcio = descripciotxt.getText().toString();
            adreça = adreçatxt.getText().toString();
            categoria = categoriatxt.getText().toString();
            msgbeacon = msgbeacontxt.getText().toString();
            bId = bIdtxt.getText().toString();

            Comerç newComerce = new Comerç(receivedComerce.getKey(),nom,descripcio,adreça,categoria,msgbeacon, bId);
            crudHelper.update(this,db,mProgressBar,newComerce);

        }
    }

    /**
     * Esborra les dades de firebase
     */
    private void deleteData() {
        crudHelper.delete(this,db,mProgressBar, receivedComerce);
    }

    /**
     * Mostra la categoria seleccionada en un EditText
     */
    private void showSelectedCategoriaInEditText() {
        categoriatxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.selectCategoria(c, categoriatxt);
            }
        });
    }

    /**
     * Avisa al usuari si pulsa el boto enrere
     */
    @Override
    public void onBackPressed() {
        Utils.showInfoDialog(this, "Alerta", "Estas segur que vols sortir?");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (receivedComerce == null) {
            getMenuInflater().inflate(R.menu.new_item_menu, menu);
            categoriatxt.setText("Afegeix un nou comerç");
        } else {
            getMenuInflater().inflate(R.menu.edit_item_menu, menu);
            capçalera.setText("Edita un comerç existent");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insertMenuItem:
                insertData();
                return true;
            case R.id.editMenuItem:
                if (receivedComerce != null) {
                    updateData();
                } else {
                    Utils.show(this, "NOMÉS ES POT EDIAR EN MODE EDICIÓ");
                }
                return true;
            case R.id.deleteMenuItem:
                if (receivedComerce != null) {
                    deleteData();
                } else {
                    Utils.show(this, "NOMÉS ES POT ESBORRAR EN MODE EDICIÓ");
                }
                return true;
            case R.id.viewAllMenuItem:
                Utils.openActivity(this, ComercosActivity.class);
                finish();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Quan l'activitat passa a onResume, rebem les dades i les dipositem en TextViews
     */
    @Override
    protected void onResume() {
        super.onResume();
        Comerç o = Utils.receiveComerce(getIntent(), c);
        if (o != null) {
            receivedComerce = o;
            nomtxt.setText(receivedComerce.getNom());
            descripciotxt.setText(receivedComerce.getDescripcio());
            adreçatxt.setText(receivedComerce.getAdreça());
            categoriatxt.setText(receivedComerce.getCategoria());
            msgbeacontxt.setText(receivedComerce.getMsgbeacon());
            bIdtxt.setText(receivedComerce.getbId());

        } else {
            //Utils.show(c,"Received comerce is Null");
        }
    }
}


