package interfaces;

import logique.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class InterfaceDomino extends JFrame {

    private JPanel tablePanel;
    private JPanel centerPanel;
    private final int width = 38;
    private final int header = 75;
    private final Vector<Joueur> joueurs;
    private Vector<Domino> dominosPlaces;
    private int partieActuelle = 1;
    private final int nombreParties = 3;
    private final int pointsPourVictoire = 1;
    private static int nombrej = 4;
    private int prochainPremierJoueurIndex ;

    public InterfaceDomino(Vector<Domino> dominosPlaces, Vector<Joueur> joueurs, int premierJoueurIndex) {
        this.dominosPlaces = dominosPlaces;
        this.joueurs = joueurs;
        this.prochainPremierJoueurIndex = premierJoueurIndex;

        for (int i = 0; i < joueurs.size(); i++) {
            joueurs.get(i).setIstours(i == premierJoueurIndex);
        }

        setTitle("Jeu de Domino - Partie " + partieActuelle + "/" + nombreParties);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        nouvelleManche();
        initializeUI();

        setVisible(true);
    }

    private void initializeUI() {
        getContentPane().removeAll();

        // Panel d'information de partie
        JPanel infoPartiePanel = createInfoPartiePanel();
        add(infoPartiePanel, BorderLayout.NORTH);

        // Centre avec les dominos placés
        centerPanel = new JPanel(new BorderLayout());
        tablePanel = createTablePanel();

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(new Color(34, 139, 34));
        wrapperPanel.add(tablePanel);

        centerPanel.add(wrapperPanel, BorderLayout.CENTER);
        centerPanel.setBackground(new Color(34, 139, 34));

        // Panels des joueurs
        add(createJoueurPanel(joueurs.get(0)), BorderLayout.NORTH);
        add(createJoueurPanel(joueurs.get(1)), BorderLayout.SOUTH);

        if (joueurs.size() > 2) {
            add(createJoueurPanel(joueurs.get(2)), BorderLayout.WEST);
        }

        if (joueurs.size() > 3) {
            add(createJoueurPanel(joueurs.get(3)), BorderLayout.EAST);
        }

        add(centerPanel, BorderLayout.CENTER);

        // Mettre à jour l'état des dominos
        updateDominosState();

        revalidate();
        repaint();
    }

    private JPanel createInfoPartiePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(new Color(34, 139, 34));

        JLabel partieLabel = new JLabel("Partie " + partieActuelle + "/" + nombreParties);
        partieLabel.setFont(new Font("Arial", Font.BOLD, 16));
        partieLabel.setForeground(Color.WHITE);

        panel.add(partieLabel);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(new Color(34, 139, 34));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (Domino d : dominosPlaces) {
            ImageIcon icon = createDominoIcon(d);
            JLabel label = new JLabel(icon);
            panel.add(label);
        }

        return panel;
    }

    private ImageIcon createDominoIcon(Domino d) {
        ImageIcon original = new ImageIcon(d.getImages());
        Image resized = original.getImage().getScaledInstance(width, header, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(resized);

        if (!d.isface()) {
            icon = rotateImage180(icon);
        }

        if (!d.isRenversed()) {
            icon = rotateImage(icon);
        }

        return icon;
    }

    private JPanel createJoueurPanel(Joueur j) {
        boolean isHorizontal = !(joueurs.indexOf(j) == 2 || joueurs.indexOf(j) == 3);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel du nom
        JPanel namePanel = createNamePanel(j);

        // Panel d'information
        JPanel infoPanel = createInfoPanel(j);

        // Panel des dominos
        JPanel dominoPanel = createDominoPanel(j, isHorizontal);

        JScrollPane scrollPane = new JScrollPane(dominoPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        if (isHorizontal) {
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        } else {
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        }

        // Organisation des composants selon la position
        int position = joueurs.indexOf(j);
        if (position == 0) { // Nord
            mainPanel.add(namePanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(infoPanel, BorderLayout.SOUTH);
            mainPanel.setPreferredSize(new Dimension(0, 180));
        } else if (position == 1) { // Sud
            mainPanel.add(infoPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(namePanel, BorderLayout.SOUTH);
            mainPanel.setPreferredSize(new Dimension(0, 180));
        } else { // Est/Ouest
            mainPanel.add(namePanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(infoPanel, BorderLayout.SOUTH);
            mainPanel.setPreferredSize(new Dimension(180, 0));
        }

        return mainPanel;
    }

    private JPanel createNamePanel(Joueur j) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(240, 240, 240));

        JLabel nom = new JLabel("👤 " + j.getNom() + " " + j.getScoreTotal() + " pts ");
        nom.setFont(new Font("Arial", Font.BOLD, 16));

        if (j.isIstours()) {
            JButton btn = new JButton("passer");
            btn.addActionListener(e -> {
                for (Domino jo : j.getSesDomy()) {
                    jo.setisNoir(true);
                }
                changeTour();
            });
            panel.add(btn);
        }

        panel.add(nom);
        return panel;
    }

    private JPanel createInfoPanel(Joueur j) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));

        JLabel pts = new JLabel("Manche: " + j.getPoint() + " pts");
        JLabel tour = new JLabel(j.isIstours() ? "🟢 Son tour" : "⚪ En attente");

        pts.setAlignmentX(Component.CENTER_ALIGNMENT);
        tour.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(pts);
        panel.add(tour);

        return panel;
    }

    private JPanel createDominoPanel(Joueur j, boolean isHorizontal) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 240, 240));

        if (isHorizontal) {
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        } else {
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        }

        for (Domino d : j.getSesDomy()) {
            ImageIcon icon = createDominoIcon(d);
            JLabel dominoLabel = new JLabel(icon);
            dominoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            if (j.isIstours()) {
                setupDominoClickListener(j, d, dominoLabel);
            }

            panel.add(dominoLabel);
        }

        return panel;
    }

    private void setupDominoClickListener(Joueur j, Domino d, JLabel dominoLabel) {
        d.setEstDansMainJoueurActuel(true);
        dominoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (!d.isNoir()) {
                    showPlacementDialog(j, d);
                }
            }
        });
    }

    private void showPlacementDialog(Joueur j, Domino d) {
        JCheckBox exceptionCheckBox = new JCheckBox("Autoriser les exceptions");
        JLabel label = new JLabel("<html><b>Choisissez le côté pour placer le domino :</b></html>");
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(exceptionCheckBox);

        Object[] options = { "Gauche", "Droite" };

        int choixCote = JOptionPane.showOptionDialog(
                this,
                panel,
                "Positionnement",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (choixCote != JOptionPane.CLOSED_OPTION) {
            boolean coteDroit = (choixCote == 1);
            boolean avecException = exceptionCheckBox.isSelected();

            boolean placed = Domino.testCompatibilite(d, dominosPlaces, true, coteDroit, avecException);
            if (placed) {
                j.getSesDomy().remove(d);
                changeTour();
                initializeUI();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Placement impossible!",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateDominosState() {
        for (Joueur jo : joueurs) {
            if (!jo.isIstours()) {
                for (Domino doo : jo.getSesDomy()) {
                    doo.setisNoir(true);
                }
            }
        }
    }

    private void changeTour() {
        int currentIndex = -1;

        // Vérifier si un joueur n'a plus de dominos
        for (Joueur joueur : joueurs) {
            if (joueur.getSesDomy().isEmpty()) {
                finDeManche(joueur);
                return;
            }
        }

        // Trouver le joueur actuel
        for (int i = 0; i < joueurs.size(); i++) {
            if (joueurs.get(i).isIstours()) {
                currentIndex = i;
                joueurs.get(i).setIstours(false);
                break;
            }
        }

        if (currentIndex == -1)
            currentIndex = 0;

        // Trouver le prochain joueur qui peut jouer
        int tries = 0;
        int nextIndex = currentIndex;

        while (tries < joueurs.size()) {
            nextIndex = (nextIndex + 1) % joueurs.size();
            Vector<Domino> suggestions = Domino.suggestion(dominosPlaces, joueurs.get(nextIndex).getSesDomy());

            if (!suggestions.isEmpty()) {
                joueurs.get(nextIndex).setIstours(true);
                initializeUI();
                return;
            }
            tries++;
        }

        // Si personne ne peut jouer, fin de manche
        finDeManche(null);
    }

    private void finDeManche(Joueur joueurSansDominos) {
        Joueur gagnant = joueurSansDominos;
        int pointsGagnes = 0;

        if (joueurSansDominos == null) {
            // Déterminer le gagnant (celui avec le moins de points)
            int minSomme = Integer.MAX_VALUE;

            for (Joueur j : joueurs) {
                int somme = 0;
                for (Domino d : j.getSesDomy()) {
                    somme += d.getSomme();
                }

                if (somme < minSomme) {
                    minSomme = somme;
                    gagnant = j;
                }
            }

            // Calculer les points gagnés
            for (Joueur j : joueurs) {
                if (j != gagnant) {
                    for (Domino d : j.getSesDomy()) {
                        pointsGagnes += d.getSomme();
                    }
                }
            }
        } else {
            // Le joueur sans dominos gagne, calculer ses points
            for (Joueur j : joueurs) {
                if (j != joueurSansDominos) {
                    for (Domino d : j.getSesDomy()) {
                        pointsGagnes += d.getSomme();
                    }
                }
            }
        }

        if (gagnant != null) {
            gagnant.ajouterAuScoreTotal(pointsGagnes);
        }

        // Afficher les résultats
        StringBuilder sb = new StringBuilder("Fin de la manche :\n");
        for (Joueur j : joueurs) {
            sb.append(j.getNom()).append(" : ").append(j.getPoint())
                    .append(" pts (Total: ").append(j.getScoreTotal()).append(" pts)\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Manche terminée", JOptionPane.INFORMATION_MESSAGE);

        // Passer à la partie suivante ou terminer le jeu
        partieActuelle++;
        if (partieActuelle > nombreParties || gagnant.getScoreTotal() >= pointsPourVictoire) {
            finDuJeu(gagnant);
        } else {
            // Nouvelle manche
            dominosPlaces.clear();
            setTitle("Jeu de Domino - Partie " + partieActuelle + "/" + nombreParties);
            nouvelleManche();
            initializeUI();
        }

        // Donner le tour au premier joueur
        joueurs.get(0).setIstours(true);
        Domino.suggestion(dominosPlaces, joueurs.get(0).getSesDomy());
    }

    private void finDuJeu(Joueur gagnant) {
        String message = "🏆 Le gagnant est : " + gagnant.getNom() + " avec " + gagnant.getScoreTotal() + " points !";
        JOptionPane.showMessageDialog(this, message, "Fin du jeu", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

   public void nouvelleManche() {
    Vector<Domino> dominos = Domino.generateDominos();
    Vector<Vector<Domino>> mains = Domino.distribuerDominos(dominos, joueurs.size());
    dominosPlaces = new Vector<>();

    for (int i = 0; i < joueurs.size(); i++) {
        joueurs.get(i).getSesDomy().clear();
        joueurs.get(i).getSesDomy().addAll(mains.get(i));
        joueurs.get(i).setPoint(0);
        joueurs.get(i).setIstours(i == prochainPremierJoueurIndex);
    }

    // Activer les suggestions pour le premier joueur
    Domino.suggestion(dominosPlaces, joueurs.get(prochainPremierJoueurIndex).getSesDomy());
}

    private ImageIcon rotateImage(ImageIcon icon) {
        Image img = icon.getImage();
        BufferedImage bufferedImage = new BufferedImage(
                img.getHeight(null),
                img.getWidth(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bufferedImage.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((img.getHeight(null) - img.getWidth(null)) / 2,
                (img.getWidth(null) - img.getHeight(null)) / 2);
        at.rotate(Math.toRadians(90), img.getWidth(null) / 2.0, img.getHeight(null) / 2.0);
        g2d.drawImage(img, at, null);
        g2d.dispose();

        return new ImageIcon(bufferedImage);
    }

    private ImageIcon rotateImage180(ImageIcon icon) {
        Image img = icon.getImage();
        int w = img.getWidth(null);
        int h = img.getHeight(null);

        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(180), w / 2.0, h / 2.0);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return new ImageIcon(bufferedImage);
    }

    public static void main(String[] args) {
        Vector<Domino> dominos = new Vector<>();
        Vector<Joueur> joueurs = new Vector<>();
        String[] noms = { "Ando", "Tina", "Joel", "Sariaka" };

        for (int i = 0; i < nombrej; i++) {
            joueurs.add(new Joueur(0, noms[i], false));
        }

        new InterfaceDomino(dominos, joueurs, 2);

    }
}