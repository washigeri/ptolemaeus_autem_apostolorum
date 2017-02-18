package ia;

import client.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by malek on 17/02/17.
 */

public class Main {



    public static void main (String[] args){
        // On récupère l'instance du client (cela le crée en fait)
        InfluenceClient client = InfluenceClient.getInstance();
        // On se connecte au serveur : le premier paramètre est son adresse IP, le deuxième est le nom de votre équipe
        // Cet appel affiche votre numéro sur le plateau de jeu
        client.connect("127.0.0.1", "Washigeri");
        // Tant que la partie est en cours
        while (client.getStatus() == InfluenceClient.Status.ONGOING)
        {
            InfluenceField field = client.nextRound();

            if (client.getStatus() != InfluenceClient.Status.ONGOING){
                break;
            }
            ArrayList<InfluenceCell> myCells;
            client.printLog("Rash B.");

            for(int i = 0; i < 20; i++){
              myCells = client.getMyCells();
              Strategy2 strat = new Strategy2(myCells, field, client.getNumber());
              Coup best = strat.meilleur_coup();
              if( best == null){
                  break;
              }
              else {
                  InfluenceCell victime = best.victime;
                  InfluenceCell attaquant = best.attaquant;
                  if (victime != null && victime.getOwner() != client.getNumber())
                  {
                      // On attaque la cible et on récupère le plateau de jeu après l'attaque
                      field = client.attack(attaquant.getX(), attaquant.getY(), victime.getX(), victime.getY());
                  }
              }
            }
            int unitsToAdd = client.endAttacks();

            myCells = client.getMyCells();
            Strategy2 strat = new Strategy2(myCells, field, client.getNumber());
            for( int i = 0; i < unitsToAdd; i++){
                InfluenceCell c = strat.pointCellule();
                client.addUnits(c, 1);
                InfluenceCell newCell = new InfluenceCell(c.getX(), c.getY(), c.getOwner(), c.getUnitsCount() + 1);
                int index = Strategy2.indexOf(c,myCells);
                if( index != - 1){
                    myCells.set(index, newCell);
                }
            }

            client.endAddingUnits();
        }
        // On est sorti de la boucle donc la partie est terminée
        switch (client.getStatus())
        {
            case VICTORY:
                System.out.println("YOU WON!");
                break;
            case DEFEAT:
                System.out.println("YOU LOST!");
                break;
            case CONNECTION_LOST:
                System.out.println("YOU LOST BECAUSE OF YOUR CONNECTION");
                break;
            default:
                System.out.println("NOT REACHABLE");
                break;
        }
    }
}
