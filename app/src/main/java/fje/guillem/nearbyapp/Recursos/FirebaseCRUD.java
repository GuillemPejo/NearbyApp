package fje.guillem.nearbyapp.Recursos;

import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import fje.guillem.nearbyapp.Dades.Comerç;
import fje.guillem.nearbyapp.Dades.MyAdapter;
import fje.guillem.nearbyapp.Vistes.ComercosActivity;

import static fje.guillem.nearbyapp.Recursos.Utils.DataCache;

/**
 * Classe FireBase CRUD
 *
 * Aquesta classe permet fer operacions CRUD en una realtime database de Firebase
 *
 * @author Guillem Pejó
 */

public class FirebaseCRUD {

    /**
     * Aquest mètode permet publicar a la realtime database de Firebase
     * @param a
     * @param mDatabaseRef
     * @param pb
     * @param comerç
     */
    public void insert(final AppCompatActivity a, final DatabaseReference mDatabaseRef, final ProgressBar pb, final Comerç comerç) {
        //Comprova que passis un Comerç vàlid. Si no, retorna false.
        if (comerç == null) {
            Utils.showInfoDialog(a,"HA FALLAT LA VALIDACIÓ","Comerç és null");
            return;
        } else {
            //En cas contrari, s'intenta desar les dades a la realtime database de Firebase
            Utils.showProgressBar(pb);

            mDatabaseRef.child("Comerce").push().setValue(comerç).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Utils.hideProgressBar(pb);

                            if(task.isSuccessful()){
                                Utils.openActivity(a, ComercosActivity.class);
                                Utils.show(a,"Felicitats! S'ha afegit correctament");
                            }else{
                                Utils.showInfoDialog(a,"Opss.. Alguna cosa ha anat malament",task.getException().
                                        getMessage());
                            }
                        }
                    });
        }
    }

    /**
     * Aquest mètode permet recuperar dades de la realtime database de Firebase
     * @param a
     * @param db
     * @param pb
     * @param rv
     * @param adapter
     */
    public void select(final AppCompatActivity a, DatabaseReference db, final ProgressBar pb, final RecyclerView rv, final MyAdapter adapter) {
        Utils.showProgressBar(pb);

        db.child("Comerce").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataCache.clear();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //Obtenim l'objecte comerç per tal d'omplir l'ArrayList
                        Comerç comerç = ds.getValue(Comerç.class);
                        comerç.setKey(ds.getKey());
                        DataCache.add(comerç);
                    }

                    adapter.notifyDataSetChanged();

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.hideProgressBar(pb);
                            rv.smoothScrollToPosition(DataCache.size());
                        }
                    });
                }else {
                    Utils.show(a,"No s'han trobat nous items");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FIREBASE CRUD", databaseError.getMessage());
                Utils.hideProgressBar(pb);
                Utils.showInfoDialog(a,"Opss.. Alguna cosa ha anat malament",databaseError.getMessage());
            }
        });
    }
    /**
     * Aquest mètode permet actualitzar dades de la realtime database de Firebase
     * @param a
     * @param mDatabaseRef
     * @param pb
     * @param updatedComerce
     */
    public void update(final AppCompatActivity a,final DatabaseReference mDatabaseRef, final ProgressBar pb, final Comerç updatedComerce) {

        if(updatedComerce == null){
            Utils.showInfoDialog(a,"HA FALLAT LA VALIDACIÓ","Comerç és null");
            return;
        }

        Utils.showProgressBar(pb);
        mDatabaseRef.child("Comerce").child(updatedComerce.getKey()).setValue(updatedComerce)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Utils.hideProgressBar(pb);

                        if(task.isSuccessful()){
                            Utils.show(a, updatedComerce.getNom() + " Actualitzat satisfactoriament.");
                            Utils.openActivity(a, ComercosActivity.class);
                        }else {
                            Utils.showInfoDialog(a,"Opps.. Alguna cosa ha anat malament",task.getException().
                                    getMessage());
                        }
                    }
                });
    }

    /**
     * Aquest mètode permet esborrar dades de la realtime database de Firebase
     * @param a
     * @param mDatabaseRef
     * @param pb
     * @param selectedComerce
     */
    public void delete(final AppCompatActivity a, final DatabaseReference mDatabaseRef, final ProgressBar pb, final Comerç selectedComerce) {
        Utils.showProgressBar(pb);
        final String selectedComerceKey = selectedComerce.getKey();
        mDatabaseRef.child("Comerce").child(selectedComerceKey).removeValue().
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Utils.hideProgressBar(pb);

                        if(task.isSuccessful()){
                            Utils.show(a, selectedComerce.getNom() + " S'ha esborrat correctament.");
                            Utils.openActivity(a, ComercosActivity.class);
                        }else{
                            Utils.showInfoDialog(a,"Opps.. Alguna cosa ha anat malament",task.getException().getMessage());
                        }
                    }
                });
    }
}

