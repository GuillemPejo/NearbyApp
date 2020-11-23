package fje.guillem.nearbyapp.Recursos;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import fje.guillem.nearbyapp.Dades.Comerç;
import fje.guillem.nearbyapp.Dades.MyAdapter;

/**
 * Classe Filter Helper
 *
 * Aquesta classe prove dels mètodes per a cercar i filtrar items
 *
 * @author Guillem Pejó
 */

public class FilterHelper extends Filter {
    static List<Comerç> currentList;
    static MyAdapter adapter;

    public static FilterHelper newInstance(List<Comerç> currentList, MyAdapter adapter) {
        FilterHelper.adapter=adapter;
        FilterHelper.currentList=currentList;
        return new FilterHelper();
    }

    /*
    * - Realitza el filtre
    */
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults=new FilterResults();

        if(constraint != null && constraint.length()>0)
        {
            constraint=constraint.toString().toUpperCase();

            //Guarda els items trobats
            ArrayList<Comerç> foundFilters=new ArrayList<>();

            String categoria,nom;

            //Itera la llista
            for (int i=0;i<currentList.size();i++)
            {
                categoria= currentList.get(i).getCategoria();
                nom= currentList.get(i).getNom();

                //Busca
                if(categoria.toUpperCase().contains(constraint)){
                    foundFilters.add(currentList.get(i));
                }else if(nom.toUpperCase().contains(constraint)){
                    foundFilters.add(currentList.get(i));
                }
            }

            //Estableix els resultats a la llista de filtres
            filterResults.count=foundFilters.size();
            filterResults.values=foundFilters;
        }else
        {   //Si no troba cap item, la llista segueix intacte
            filterResults.count=currentList.size();
            filterResults.values=currentList;
        }

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        adapter.comerços= (ArrayList<Comerç>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}








