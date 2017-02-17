import client.InfluenceCell;
import client.InfluenceClient;
import client.InfluenceField;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by brice on 08/02/17.
 */
public class Main
{
    public static void main(String[] args)
    {
        // Initialisation du random
        Random r = new Random();

        // On récupère l'instance du client (cela le crée en fait)
        InfluenceClient client = InfluenceClient.getInstance();

        // On se connecte au serveur : le premier paramètre est son adresse IP, le deuxième est le nom de votre équipe
        // Cet appel affiche votre numéro sur le plateau de jeu
        client.connect("127.0.0.1", "JavaClient");

        // Tant que la partie est en cours
        while (client.getStatus() == InfluenceClient.Status.ONGOING)
        {
            // On attend notre tour, ce qui permet de récupérer le nouveau plateau de jeu
            InfluenceField field = client.nextRound();

            // Après l'appel à nextRound, il se peut que le statut ait été modifié
            if (client.getStatus() != InfluenceClient.Status.ONGOING)
            {
                break;
            }

            ArrayList<InfluenceCell> myCells;
            client.printLog("Attacking");

            // On va essayer de lancer 10 attaques
            for (int i = 0; i < 10; i++)
            {
                // On récupère toutes les cellules qui nous appartiennent
                myCells = client.getMyCells();

                FieldStrategy stategy = new FieldStrategy(field, client.getNumber(), client.remainingUnits(), myCells);
                FieldStrategy.PossibleAttack attack = stategy.getPossibleAttack();
                if(attack != null)
                    field = client.attack(attack.ourCell.getX(), attack.ourCell.getY(), attack.enemyCell.getX(), attack.enemyCell.getY());
            }

            // On prévient le serveur qu'on a fini les attaques, il nous renvoie le nombre d'unités à placer
            int unitsToAdd = client.endAttacks();

            // Phase de placement des unités

            // On récupère les cellules qui nous appartiennent
            myCells = client.getMyCells();
            // Tant que l'on peut placer des unités
            for (int i = 0; i < unitsToAdd; i++)
            {
                // On choisit une cellule aléatoirement parmi celles qui nous appartiennent
                InfluenceCell c = myCells.get(r.nextInt(myCells.size()));
                // On ajoute une unité à la cellule choisie
                client.addUnits(c, 1);
            }

            // On indique au serveur que l'on a fini notre tour
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
