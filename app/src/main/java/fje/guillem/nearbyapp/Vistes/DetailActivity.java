package fje.guillem.nearbyapp.Vistes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fje.guillem.nearbyapp.Dades.Comerç;
import fje.guillem.nearbyapp.R;
import fje.guillem.nearbyapp.Recursos.Utils;

/**
 * Classe DetailActivity
 *
 * Aquesta classe mostra la vista del activity_detail, mostra les dades i configura el menu superior
 *
 * @author Guillem Pejó
 */

public class DetailActivity extends AppCompatActivity implements  View.OnClickListener {

    private TextView nomtv, descripciotv, adreçatv, categoriatv,msgbeacontv, bIdtv;
    private Comerç receivedComerç;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FloatingActionButton editFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_detail);
        nomtv = findViewById(R.id.nomtv);
        descripciotv = findViewById(R.id.descripciotv);
        adreçatv = findViewById(R.id.adreçatv);
        categoriatv = findViewById(R.id.categoriatv);
        msgbeacontv = findViewById(R.id.msgbeacontv);
        bIdtv = findViewById(R.id.bIdtv);
        editFAB=findViewById(R.id.editFAB);
        editFAB.setOnClickListener(this);
        mCollapsingToolbarLayout=findViewById(R.id.mCollapsingToolbarLayout);
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));

        receiveAndShowData();
    }

    /**
     * Rebem les dades i les mostrem en els seus respectius TextViews
     */
        private void receiveAndShowData(){
        receivedComerç = Utils.receiveComerce(getIntent(),DetailActivity.this);

        if(receivedComerç != null){
            nomtv.setText(receivedComerç.getNom());
            descripciotv.setText(receivedComerç.getDescripcio());
            adreçatv.setText(receivedComerç.getAdreça());
            categoriatv.setText(receivedComerç.getCategoria());
            msgbeacontv.setText(receivedComerç.getMsgbeacon());
            bIdtv.setText(receivedComerç.getbId());


            mCollapsingToolbarLayout.setTitle(receivedComerç.getNom());
        }

    }

    /**
     * Si l'usuari pitja al Butto d'edició, s'envia el comerç actual per editar-lo
     * @param v
     */
    @Override
    public void onClick(View v) {
        int id =v.getId();
        if(id == R.id.editFAB){
            Utils.sendComerceToActivity(this, receivedComerç,CRUDActivity.class);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Utils.sendComerceToActivity(this, receivedComerç,CRUDActivity.class);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

