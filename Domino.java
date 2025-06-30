package logique;

import interfaces.*;
import java.util.Vector;

public class Domino {
    private String images;
    private boolean isrenversed = false;
    private boolean isNoir = true;
    private double somme;
    private int face_1;
    private int face_2;
    private Domino d_face_1;
    private Domino d_face_2;
    private boolean isface = false;
    private int p = 0;
    boolean peutJouer = false;
    private boolean estDansMainJoueurActuel = false;

    public Domino(String images, double somme, int face_1, int face_2) {
        this.images = images;
        this.somme = somme;
        this.face_1 = face_1;
        this.face_2 = face_2;
    }

    public String getImages() {
        if (peutJouer) {
            return images + "vert.png";
        } else if (!isNoir) {
            return images + "rouge.png";
        } else {
            return images + "noir.png";
        }
    }

    public void setEstDansMainJoueurActuel(boolean valeur) {
        this.estDansMainJoueurActuel = valeur;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public boolean isRenversed() {
        return isrenversed;
    }

    public boolean isface() {
        return isface;
    }

    public void setisface(boolean isface) {
        this.isface = isface;
    }

    public boolean isNoir() {
        return isNoir;
    }

    public void setRenversed(boolean isrenversed) {
        this.isrenversed = isrenversed;
    }

    public void setisNoir(boolean ishorizontal) {
        this.isNoir = ishorizontal;
    }

    public double getSomme() {
        return somme;
    }

    public void setSomme(double somme) {
        this.somme = somme;
    }

    public int getFace_1() {
        return face_1;
    }

    public void setFace_1(int face_1) {
        this.face_1 = face_1;
    }

    public int getFace_2() {
        return face_2;
    }

    public void setFace_2(int face_2) {
        this.face_2 = face_2;
    }

    public Domino getD_face_1() {
        return d_face_1;
    }

    public void setD_face_1(Domino d_face_1) {
        this.d_face_1 = d_face_1;
    }

    public Domino getD_face_2() {
        return d_face_2;
    }

    public void setD_face_2(Domino d_face_2) {
        this.d_face_2 = d_face_2;
    }

    public static Vector<Domino> generateDominos() {
        Vector<Domino> dominos = new Vector<>();

        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                String imagePath = "assets/dominos/domino_" + i + "_" + j + "_";
                double somme = i + j;
                Domino d = new Domino(imagePath, somme, i, j);
                d.setFace_1(i);
                d.setFace_2(j);
                d.setSomme(i + j);
                dominos.add(d);
            }
        }

        return dominos;
    }

    public static void ajouter_domy(Vector<Domino> domy, boolean isAvant, Domino atsofoka, boolean isExeption) {
        boolean estSpecial = false;

        if (domy.isEmpty()) {
            boolean estDouble = atsofoka.face_1 == atsofoka.face_2;
            if (estDouble) {
                atsofoka.setRenversed(true);
            }
            atsofoka.setisNoir(true);
            atsofoka.setisface(false);
            domy.add(atsofoka);

        } else if (isAvant) {
            Domino dernier = domy.lastElement();
            boolean estDouble = atsofoka.face_1 == atsofoka.face_2 && dernier.p == 0;
            boolean faceCorrespond = (dernier.face_2 == atsofoka.face_1);

            estSpecial = (atsofoka.face_1 + atsofoka.face_2 == dernier.face_2)
                    && dernier.p == 0
                    && dernier.face_1 != dernier.face_2
                    && isExeption;

            atsofoka.setisface(!faceCorrespond);
            atsofoka.setisNoir(true);

            if (estSpecial) {
                atsofoka.setRenversed(true);
                atsofoka.p++;
                dernier.setD_face_2(atsofoka);
                atsofoka.setD_face_1(dernier);
                atsofoka.setFace_2(dernier.getFace_2());

            } else if (estDouble) {
                atsofoka.setRenversed(true);
                dernier.setD_face_2(atsofoka);
                atsofoka.setD_face_1(dernier);
                atsofoka.setFace_2(dernier.getFace_2());

            } else {
                if (!faceCorrespond) {
                    atsofoka.renverser();
                }

                dernier.setD_face_2(atsofoka);
                atsofoka.setD_face_1(dernier);
            }

            domy.add(atsofoka);

        } else {
            Domino premier = domy.firstElement();
            boolean estDouble = atsofoka.face_1 == atsofoka.face_2 && premier.p == 0;
            boolean faceCorrespond = (premier.face_1 == atsofoka.face_2);

            estSpecial = (atsofoka.face_1 + atsofoka.face_2 == premier.face_1)
                    && domy.lastElement().p == 0
                    && premier.d_face_2 != premier.d_face_1
                    && isExeption;

            atsofoka.setisface(!faceCorrespond);
            atsofoka.setisNoir(true);

            if (estSpecial) {
                atsofoka.setRenversed(true);
                atsofoka.p++;
                premier.setD_face_1(atsofoka);
                atsofoka.setD_face_2(premier);
                atsofoka.setFace_1(premier.getFace_1());

            } else if (estDouble) {
                atsofoka.setRenversed(true);
                premier.setD_face_1(atsofoka);
                atsofoka.setD_face_2(premier);
                atsofoka.setFace_1(premier.getFace_1());

            } else {

                if (!faceCorrespond) {
                    atsofoka.renverser();
                }

                premier.setD_face_1(atsofoka);
                atsofoka.setD_face_2(premier);
            }

            domy.add(0, atsofoka);
        }
    }

    public void renverser() {
        int tmp = this.face_1;
        this.face_1 = this.face_2;
        this.face_2 = tmp;
    }

    public static Vector<Domino> suggestion(Vector<Domino> au_table, Vector<Domino> au_main) {
        Vector<Domino> suggestions = new Vector<>();

        if (au_table.isEmpty()) {
            suggestions.addAll(au_main);
            for (Domino d : au_main) {
                d.setisNoir(false);
                d.setEstDansMainJoueurActuel(true);
            }
            return suggestions;
        }

        Domino debut = au_table.firstElement();
        Domino fin = au_table.lastElement();

        int libredebut = (debut.d_face_1 == null) ? debut.getFace_1() : debut.getFace_2();
        int librefin = (fin.d_face_2 == null) ? fin.getFace_2() : fin.getFace_1();

      

        for (Domino d : au_main) {
            int f1 = d.face_1;
            int f2 = d.face_2;

            boolean debut_match_direct = (f1 == libredebut || f2 == libredebut);
            boolean debut_match_exception = ((d.somme) == libredebut) && !debut.isRenversed() && debut.p == 0;
            boolean fin_match_direct = (f1 == librefin || f2 == librefin);
            boolean fin_match_exception = ((d.somme) == librefin) && fin.p == 0 && !fin.isRenversed();
            

            boolean jouable = debut_match_direct || fin_match_direct || debut_match_exception || fin_match_exception;

            d.setisNoir(!jouable);
            d.setEstDansMainJoueurActuel(true);

            if (jouable) {
                suggestions.add(d);
            }
        }

        return suggestions;
    }

    public static boolean testCompatibilite(Domino domino, Vector<Domino> au_table, boolean placer, boolean coteDroit,
            boolean avecException) {
        if (au_table.isEmpty()) {
            if (placer) {
                ajouter_domy(au_table, true, domino, false);
            }
            return true;
        }

        Domino premier = au_table.firstElement();
        Domino dernier = au_table.lastElement();

        if (coteDroit) {

            if (domino.getFace_1() == dernier.getFace_2() ||
                    domino.getFace_2() == dernier.getFace_2()) {
                if (placer) {
                    boolean reverse = (domino.getFace_1() != dernier.getFace_2());
                    ajouter_domy(au_table, true, domino, avecException && reverse);
                }
                return true;
            }

            if (avecException && (domino.getSomme() == dernier.getFace_2()) && !dernier.isRenversed()) {
                if (placer) {
                    ajouter_domy(au_table, true, domino, true);
                }
                return true;
            }
        } else {

            if (domino.getFace_1() == premier.getFace_1() ||
                    domino.getFace_2() == premier.getFace_1()) {
                if (placer) {
                    boolean reverse = (domino.getFace_2() == premier.getFace_1());
                    ajouter_domy(au_table, false, domino, avecException && reverse);
                }
                return true;
            }

            if (avecException && (domino.getSomme() == premier.getFace_1()) && !premier.isRenversed()) {
                if (placer) {
                    ajouter_domy(au_table, false, domino, true);
                }
                return true;
            }
        }

        return false;
    }

    public static Vector<Vector<Domino>> distribuerDominos(Vector<Domino> dominos, int nbJoueurs) {
        Vector<Vector<Domino>> mains = new Vector<>();
        for (int i = 0; i < nbJoueurs; i++) {
            mains.add(new Vector<Domino>());
        }

        java.util.Collections.shuffle(dominos);

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < nbJoueurs; j++) {
                if (!dominos.isEmpty()) {
                    mains.get(j).add(dominos.remove(0));
                }
            }
        }

        return mains;
    }


  
}