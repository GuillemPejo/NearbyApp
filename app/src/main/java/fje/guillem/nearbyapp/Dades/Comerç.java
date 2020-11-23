package fje.guillem.nearbyapp.Dades;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
/**
 * Classe Comerç
 *
 * Amb els getters i setters corresponents
 *
 * @author Guillem Pejó
 */

public class Comerç  implements Serializable {

    private String mId;
    private String nom;
    private String descripcio;
    private String adreça;
    private String categoria;
    private String msgbeacon;
    private String bId;
    private String key;

    public Comerç() {
        //Constructor buit necessari
    }

    public Comerç(String key, String nom, String descripcio, String adreça, String categoria, String msgbeacon, String bId) {
        if (nom.trim().equals("")) {
            nom = "Comerç Sensenom";
        }
        this.key=key;
        this.nom = nom;
        this.descripcio = descripcio;
        this.adreça = adreça;
        this.categoria = categoria;
        this.msgbeacon = msgbeacon;
        this.bId = bId;

    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public String getAdreça() {
        return adreça;
    }

    public void setAdreça(String adreça) {
        this.adreça = adreça;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMsgbeacon() {
        return msgbeacon;
    }

    public void setMsgbeacon(String msgbeacon) {
        this.msgbeacon = msgbeacon;
    }

    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {this.bId = bId; }

    @Override
    public String toString() {
        return getNom();
    }

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
