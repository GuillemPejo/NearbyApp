package fje.guillem.nearbyapp.Vistes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SearchView;

import fje.guillem.nearbyapp.Dades.MyAdapter;
import fje.guillem.nearbyapp.R;
import fje.guillem.nearbyapp.Recursos.FirebaseCRUD;
import fje.guillem.nearbyapp.Recursos.Utils;

/**
 * Classe ComerçosActivity
 *
 * Aquesta classe mostra la vista del activity_comerços, mostra les dades i configura el menu superior
 *
 * @author Guillem Pejó
 */

public class ComercosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private RecyclerView rv;
    public ProgressBar mProgressBar;
    private FirebaseCRUD recurs_crud = new FirebaseCRUD();
    private LinearLayoutManager layoutManager;
    MyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comercos);

        mProgressBar = findViewById(R.id.mProgressBarLoad);
        mProgressBar.setIndeterminate(true);

        Utils.showProgressBar(mProgressBar);

        rv = findViewById(R.id.mRecyclerView);

        layoutManager = new LinearLayoutManager(this);

        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);

        adapter=new MyAdapter(this,Utils.DataCache);

        rv.setAdapter(adapter);

        bindDades();
    }

    private void bindDades(){
        recurs_crud.select(this,Utils.getDatabaseRefence(),mProgressBar,rv,adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.comerce_page_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);
        searchView.setQueryHint("Buscar");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Utils.sendComerceToActivity(this,null,CRUDActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {return false;}

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {return false;}

    @Override
    public boolean onQueryTextSubmit(String query) {return false;}

    @Override
    public boolean onQueryTextChange(String query) {
        Utils.searchString=query;
        MyAdapter adapter=new MyAdapter(this,Utils.DataCache);
        adapter.getFilter().filter(query);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        return false;
    }
}
