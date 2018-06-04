package kleinster.kreis;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KleinsterKreis {

    static int anzahlP = 50;
    final static int ZULOSENDEP = 13;

    public static int[] ausgelosteP = new int[ZULOSENDEP]; //Index in points
    public static Tuple[] points = new Tuple[anzahlP];        //Koordinaten
    public static Tuple cCenter = new Tuple(0, 0);  //Zentrum des Kreises
    public static Tuple[] kreisP = new Tuple[3];

    public static double radius = 0;

    public static double rad;
    public static Tuple center = new Tuple(0, 0);
    public static boolean finished = false;
    public static double gebrauchteZeit;

    public static void berechne() {
        long wartezeit = 0;

        double wkeiten[] = new double[anzahlP];
        double anzahlLose;
        long lose[] = new long[anzahlP];
        

        int fensterGröße = 500;
        double startTime = System.currentTimeMillis();
        Arrays.fill(lose, 1);
        Arrays.fill(kreisP, new Tuple(0, 0));
        //pos. auslosen
        for (int i = 0; i < anzahlP; i++) {
            points[i] = new Tuple((int) (Math.random() * fensterGröße), (int) (Math.random() * fensterGröße));
            //System.out.println(points[i].x + "," + points[i].y);
        }

        //Grafik öffnen
        KreisFenster kf = new KreisFenster();
        kf.setVisible(true);

        while (!finished) {
            //Anzahl an Losen bestimmen

            anzahlLose = 0;
            for (int i = 0; i < anzahlP; i++) {
                anzahlLose += lose[i];
            }
            //Wkeiten der Punkte bestimmen
            for (int i = 0; i < anzahlP; i++) {
                wkeiten[i] = lose[i] / anzahlLose;
                System.out.println("Wkeit von Punkt " + i + " : " + wkeiten[i]);
                if (Double.isNaN(wkeiten[i])) {
                    System.out.println("Fucked up. Wkeit ist NaN");
                }
            }

            //zu überprüfende Punkte auslosen. getestet
            for (int indexLP = 0; indexLP < ZULOSENDEP; indexLP++) {
                float zz = (float) Math.random();
                System.out.println("Zufallszahl: " + zz);
                System.out.println("IndexLP: " + indexLP);

                boolean equal = true;
                while (equal) {
                    //Es wird überprüft welcher Punkt gewählt werden soll
                    for (int indexP = 0; indexP < anzahlP; indexP++) {
                        if (indexP == 0) {
                            if (zz <= wkeiten[0]) {
                                ausgelosteP[indexLP] = 0;
                                break;
                            }
                            zz -= wkeiten[0];
                        } else {
                            if (zz <= wkeiten[indexP]) {
                                ausgelosteP[indexLP] = indexP;
                                break;
                            }
                            zz -= wkeiten[indexP];
                        }
                    }
                    System.out.println("Index des Gewählten Punktes: " + ausgelosteP[indexLP]);
                    //überprüft ob zufällig ausgewählter Punkt schonmal ausgewählt wurde. Falls ja wird neu generiert
                    for (int i = 0; i < indexLP; i++) {
                        if (ausgelosteP[i] == ausgelosteP[indexLP]) {
                            equal = true;
                            zz = (float) Math.random();
                            System.out.println("Gleiche Zahl! Neue Zufallszahl generiert: " + zz);
                            break;
                        } else {
                            equal = false;
                        }
                    }
                    if (indexLP == 0) {
                        equal = false;
                    }
                }
            }
            boolean foundCircle = false;
            //Alle möglichen Paare von Punkten checken und schauen ob sie den Kleinste Kreis definieren. getestet 

            radius = 100000000;
            for (int i = 0; i < ZULOSENDEP; i++) {
                for (int j = 0; j < ZULOSENDEP; j++) {
                    kreisP[0] = points[ausgelosteP[i]];
                    kreisP[1] = points[ausgelosteP[j]];
                    int x1 = points[ausgelosteP[i]].x;
                    int x2 = points[ausgelosteP[j]].x;

                    int y1 = points[ausgelosteP[i]].y;
                    int y2 = points[ausgelosteP[j]].y;

                    int dx = Math.abs(x1 - x2);
                    int dy = Math.abs(y1 - y2);

                    center.x = (x1 + x2) / 2;
                    center.y = (y1 + y2) / 2;

                    rad = Math.pow((dx * dx + dy * dy), 0.5) / 2;
                    //System.out.println("Gewählte Punkte: P1 = ("+x1+","+y1+") ; P2 = ("+x2+","+y2+")");
                    //wenn zwei mal der selbe Punkt gewählt wurde wird der Rest übersprungen und direkt das nächste Paar geprüft
                    if (rad == 0) {
                        continue;
                    }

                    kf.refresh();
                    try {
                        TimeUnit.MILLISECONDS.sleep(wartezeit);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(KleinsterKreis.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //checken ob alle drin sind
                    boolean found = true;
                    for (int k = 0; k < ZULOSENDEP; k++) {
                        int x = points[ausgelosteP[k]].x;
                        int y = points[ausgelosteP[k]].y;

                        double dx2 = Math.abs(center.x - x);
                        double dy2 = Math.abs(center.y - y);

                        if (rad < Math.pow((dx2 * dx2 + dy2 * dy2), 0.5)) {//wenn der Kreis nicht der kleinste umschließende ist check beenden
                            found = false;
                            System.out.println("Kreis passt nicht um Losmenge");
                            break;
                        }
                    }
                    if (found) {
                        System.out.println("Kreis um Losmenge gefunden");
                        if (rad < radius) {
                            foundCircle = true;
                            radius = rad;
                            cCenter.x = center.x;
                            cCenter.y = center.y;
                            System.out.println("Neuer Kreis gefunden");
                        }
                    }
                }
            }

            //wenn kein Kreis gefunden wurde, alle Kreise, die durch drei Punkte definiert werden checken
            if (!foundCircle) {
                System.out.println("3er Suche");
                for (int i = 0; i < ZULOSENDEP; i++) {
                    for (int j = 1; j < ZULOSENDEP; j++) {
                        for (int k = 2; k < ZULOSENDEP; k++) {
                            kreisP[0] = points[ausgelosteP[i]];
                            kreisP[1] = points[ausgelosteP[j]];
                            kreisP[2] = points[ausgelosteP[k]];

                            if (kreisP[0] == kreisP[1] || kreisP[0] == kreisP[2] || kreisP[1] == kreisP[2]) {
                                System.out.println("Fehler: Punkt wurde doppelt ausgewählt");
                                continue;
                            }
                            float x1 = points[ausgelosteP[i]].x;
                            float x2 = points[ausgelosteP[j]].x;
                            float x3 = points[ausgelosteP[k]].x;

                            float y1 = points[ausgelosteP[i]].y;
                            float y2 = points[ausgelosteP[j]].y;
                            float y3 = points[ausgelosteP[k]].y;

                            float m1;
                            float c1;
                            float m2;
                            float c2;
                            //falls die y-Werte die selben sind kann eine der Normalengleichungen nicht aufgesetellt werden, weil die Funktion senkrecht verlaufen müsste.
                            //stattdessen wird geschaut wo die andere Normale den X-Wert des Mittelpunkt der Strecke, die keine Normale hat genommen.
                            if (y1 == y2 || y2 == y3) {
                                System.out.println("Fehler: Würde durch 0 teilen.");
                                if (y1 == y2) {
                                    m2 = -(x2 - x3) / (y2 - y3);
                                    c2 = (y2 + y3) / 2 - m2 * (x2 + x3) / 2;

                                    center.x = (int) (x1 + x2) / 2;
                                    center.y = (int) (m2 * center.x + c2);

                                } else {//y2 == y3
                                    m1 = -(x1 - x2) / (y1 - y2);
                                    c1 = (y1 + y2) / 2 - m1 * (x1 + x2) / 2;

                                    center.x = (int) (x2 + x3) / 2;
                                    center.y = (int) (m1 * center.x + c1);
                                }

                                kf.refresh();
                            } else {
                                m1 = -(x1 - x2) / (y1 - y2);
                                c1 = (y1 + y2) / 2 - m1 * (x1 + x2) / 2;
                                m2 = -(x2 - x3) / (y2 - y3);
                                c2 = (y2 + y3) / 2 - m2 * (x2 + x3) / 2;
                                if (m1 - m2 == 0) {
                                    System.out.println("Fetter Fehler!");
                                    continue;
                                }
                                center.x = (int) ((c2 - c1) / (m1 - m2));
                                center.y = (int) (m1 * center.x + c1);
                            }

                            double dx = Math.abs(center.x - x1);
                            double dy = Math.abs(center.y - y1);

                            rad = Math.pow((dx * dx + dy * dy), 0.5);
                            kf.refresh();

                            try {
                                TimeUnit.MILLISECONDS.sleep(wartezeit);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(KleinsterKreis.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            boolean found = true;
                            for (int l = 0; l < ZULOSENDEP; l++) {
                                int x = points[ausgelosteP[l]].x;
                                int y = points[ausgelosteP[l]].y;

                                double dx2 = Math.abs(center.x - x);
                                double dy2 = Math.abs(center.y - y);

                                if (rad < Math.pow((dx2 * dx2 + dy2 * dy2), 0.5)) {//wenn der Kreis nicht der kleinste umschließende ist check beenden
                                    found = false;
                                    System.out.println("Kreis passt nicht um Losmenge");
                                    break;
                                }
                            }
                            if (found) {
                                System.out.println("Kreis um Losmenge gefunden");
                                if (rad < radius) {
                                    foundCircle = true;
                                    radius = rad;
                                    cCenter.x = center.x;
                                    cCenter.y = center.y;
                                    System.out.println("Neuer Kreis gefunden");
                                }
                            }
                        }
                    }
                }
            }
            if (foundCircle) {
                //checken ob alle drin sind
                finished = true;
                for (int k = 0; k < anzahlP; k++) {
                    int x = points[k].x;
                    int y = points[k].y;

                    double dx2 = Math.abs(cCenter.x - x);
                    double dy2 = Math.abs(cCenter.y - y);

                    if (radius < Math.pow((dx2 * dx2 + dy2 * dy2), 0.5)) {//wenn der Kreis nicht der kleinste umschließende ist check beenden
                        System.out.println("Kreis passt nicht um alle Punkte");
                        lose[k] *= 2;
                        finished = false;
                    }
                }
            }
        }
        gebrauchteZeit = (System.currentTimeMillis() - startTime) / 1000;
        kf.refresh();

        System.out.println(
                "Kreis passt um alle");
        System.out.println(
                "Gebrauchte Zeit: " + gebrauchteZeit + "s");

    }
}
