package logique;

import java.util.Vector;

public class Joueur {
    private int point;
    private int scoreTotal;
    private String nom;
    private boolean istours;
    private Vector<Domino> sesDomy;

    public Joueur(int point, String nom, boolean istours) {
        this.point = point;
        this.scoreTotal = 0;
        this.nom = nom;
        this.istours = istours;
        this.sesDomy = new Vector<>();
    }

    public void ajouterAuScoreTotal(int points) {
        this.scoreTotal += points;
    }

    public int getScoreTotal() {
        return scoreTotal;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = (int) point;
    }

    public String getNom() {
        return nom;
    }

    public void addPoints(int points) {
        this.point += points;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public boolean isIstours() {
        return istours;
    }

    public void setIstours(boolean istours) {
        this.istours = istours;
    }

    public Vector<Domino> getSesDomy() {
        return sesDomy;
    }

    public int getSommePointsDominos() {
    int somme = 0;
    for (Domino d : sesDomy) {
        somme += d.getSomme();
    }
    return somme;
}

}