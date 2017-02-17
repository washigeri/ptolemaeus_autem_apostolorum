package ia;

import client.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by malek on 17/02/17.
 */

public class Main {




    private  ArrayList<InfluenceCell> getBorder(ArrayList<InfluenceCell> myCells, InfluenceField plateau){
        int xmax = 0,ymax = 0;
        for(int i=0;i<plateau.getHeight();i++)
        {
            for(InfluenceCell cell : myCells){
                if( cell.getY() == i) {

                }
            }
        }
        return (new ArrayList<InfluenceCell>());


    }


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
            client.printLog("Attacking");

            boolean canAttack = true;
            for(int i = 0; i < 20 && canAttack ; i++){
                myCells = client.getMyCells();


            }
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
