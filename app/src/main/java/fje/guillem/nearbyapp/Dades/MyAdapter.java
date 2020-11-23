package fje.guillem.nearbyapp.Dades;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ivbaranov.mli.MaterialLetterIcon;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import fje.guillem.nearbyapp.R;
import fje.guillem.nearbyapp.Recursos.FilterHelper;
import fje.guillem.nearbyapp.Recursos.Utils;
import fje.guillem.nearbyapp.Vistes.DetailActivity;

import static fje.guillem.nearbyapp.Recursos.Utils.searchString;

/**
 * Classe MyAdapter
 *
 * Aquesta classe permet posar en un Recycledview les dades
 *
 * @author Guillem Pejó
 */


public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {

    private final Context c;
    private final int[] mMaterialColors;
    public List<Comerç> comerços;
    private List<Comerç> filterList;
    private FilterHelper filterHelper;

    interface ItemClickListener {
        void onItemClick(int pos);
    }
    /** Classe ViewHolder
        Mantenim a la memòria els inflated widgets per poder-los reciclar
    */

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nomtxt,descripciotxt, categoriatxt;
        private final MaterialLetterIcon mIcon;
        private ItemClickListener itemClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.mMaterialLetterIcon);
            nomtxt = itemView.findViewById(R.id.nomtxt);
            descripciotxt = itemView.findViewById(R.id.descripciotxt);
            categoriatxt = itemView.findViewById(R.id.categoriatxt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

    /**
     * Constructor
     * - Rep un objecte de context que necessitem per a inflate
     * - Rep la llista que es pot presentar al reciclerview
     * @param mContext
     * @param comerços
      */
        public MyAdapter(Context mContext, List<Comerç> comerços) {
        this.c = mContext;
        this.comerços = comerços;
        this.filterList = comerços;
        mMaterialColors = c.getResources().getIntArray(R.array.colors);
    }
    /**
     * - Inflate item_comerce.xml en un objecte de vista amb què podem treballar amb codi
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c)
                .inflate(R.layout.item_comerce, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Metode onBindViewHolder
     * - Enllaça les dades amb els items del recycleview
     * - Ressalta els resultats de cerca mitjançant la classe Spannable
     * - Adjunta el clicklistener al recycledview per tal de poder obrir activity_detail
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Obtenim el comerç actual

        final Comerç c = comerços.get(position);

        //Enllaçem les dades
        holder.nomtxt.setText(c.getNom());
        holder.descripciotxt.setText(c.getDescripcio());
        holder.categoriatxt.setText(c.getCategoria());
        holder.mIcon.setInitials(true);
        holder.mIcon.setInitialsNumber(2);
        holder.mIcon.setLetterSize(25);

        //Otorguem un color random  als Material Letter Icons
        holder.mIcon.setShapeColor(mMaterialColors[new Random().nextInt(mMaterialColors.length)]);
        holder.mIcon.setLetter(c.getNom());

        //Obtenim el nom i la categoria
        String nom = c.getNom().toLowerCase(Locale.getDefault());
        String categoria = c.getCategoria().toLowerCase(Locale.getDefault());

        //Ressaltem el nom mentres s'estigui buscan
        if (nom.contains(searchString) && !(searchString.isEmpty())) {
            int startPos = nom.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().
                    newSpannable(holder.nomtxt.getText());
            spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.nomtxt.setText(spanString);
        } else {
            //Utils.show(ctx, "Search string empty");
        }

        //Ressaltem la categoria mentres s'estigui buscan
        if (categoria.contains(searchString) && !(searchString.isEmpty())) {

            int startPos = categoria.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().
                    newSpannable(holder.categoriatxt.getText());
            spanString.setSpan(new ForegroundColorSpan(Color.BLUE), startPos, endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.categoriatxt.setText(spanString);
        }

        //Obre detailactivity quan es clickat
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Utils.sendComerceToActivity(MyAdapter.this.c, c,
                        DetailActivity.class);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comerços.size();
    }

    @Override
    public Filter getFilter() {
        if(filterHelper==null){
            filterHelper=FilterHelper.newInstance(filterList,this);
        }
        return filterHelper;
    }
}
