import client.InfluenceCell;
import client.InfluenceClient;
import client.InfluenceField;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by brice on 08/02/17.
 */
public class Program
{
    public static void main(String[] args)
    {
        Random r = new Random();
        InfluenceClient client = InfluenceClient.getInstance();

        client.connect("127.0.0.1", "JavaClient");

        while (client.getStatus() == InfluenceClient.Status.ONGOING)
        {
            InfluenceField field = client.nextRound();
            if (client.getStatus() != InfluenceClient.Status.ONGOING)
            {
                break;
            }

            ArrayList<InfluenceCell> myCells;
            client.printLog("Attacking");
            for (int i = 0; i < 10; i++)
            {
                myCells = client.getMyCells();
                InfluenceCell c = myCells.get(r.nextInt(myCells.size()));
                if (c.getUnitsCount() >= 2)
                {
                    int dx = c.getX() + r.nextInt(3) - 1;
                    int dy = c.getY() + r.nextInt(3) - 1;
                    if (dx >= 0 && dx < field.getWidth() && dy >= 0 && dy < field.getHeight())
                    {
                        InfluenceCell cellToAttack = field.getCell(dx, dy);
                        if (cellToAttack != null && cellToAttack.getOwner() != client.getNumber())
                        {
                            field = client.attack(c.getX(), c.getY(), cellToAttack.getX(), cellToAttack.getY());
                        }
                    }
                }
            }

            int unitsToAdd = client.endAttacks();

            myCells = client.getMyCells();
            for (int i = 0; i < unitsToAdd; i++)
            {
                InfluenceCell c = myCells.get(r.nextInt(myCells.size()));
                client.addUnits(c, 1);
            }
            client.endAddingUnits();
        }

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
