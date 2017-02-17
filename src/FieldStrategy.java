/**
 * Created by vincent on 2/17/17.
 */
import client.InfluenceCell;
import client.InfluenceField;
import javafx.geometry.Pos;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FieldStrategy  {
    public InfluenceField field;
    public int we;
    public int unitsCount;
    public List<InfluenceCell> myCells;

    public FieldStrategy(InfluenceField field, int we, int unitsCount, List<InfluenceCell> myCells) {
        this.field = field;
        this.we = we;
        this.unitsCount = unitsCount;
        this.myCells = myCells;
    }

    public String cellToString(InfluenceCell cell) {
        String str = "[" + cell.getX() + ":" + cell.getY() + "-" + cell.getUnitsCount() + "]";
        if(cell.getOwner() == we) {
            str += " (we)";
        } else {
            str += " (enemy)";
        }
        return str;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < field.getWidth() && y >= 0 && y < field.getHeight();
    }

    public class PossibleAttack {
        public InfluenceCell ourCell;
        public InfluenceCell enemyCell;
        public int diff;

        public PossibleAttack(InfluenceCell ourCell, InfluenceCell enemyCell) {
            this.ourCell = ourCell;
            this.enemyCell = enemyCell;
            if(enemyCell == null)
                diff = 0;
            else
                diff = enemyCell.getUnitsCount() - ourCell.getUnitsCount();
        }

        public String toString() {
            return "Possible attack : " + cellToString(ourCell) + " vs " + cellToString(enemyCell) + " diff : " + diff;
        }
        public int getDiff() { return diff; }
    }

    public static void sort(List<PossibleAttack> attacks) {
        attacks.sort(new Comparator<PossibleAttack>() {
            @Override
            public int compare(PossibleAttack o1, PossibleAttack o2) {
                if(o1.enemyCell == null) return 100;
                return o1.diff - o2.diff;
            }
        });
    }

    public List<InfluenceCell> getNeighborhood(InfluenceCell cell) {
        ArrayList<InfluenceCell> res = new ArrayList<>();
        for(int dx=-1; dx <=1; dx++) {
            for(int dy=-1; dy<=1; dy++) {
                if(dx != 0 || dy != 0) {
                    int x = cell.getX() + dx;
                    int y = cell.getY() + dy;
                    if(isValid(x, y)) {
                        res.add(field.getCell(x, y));
                    }
                }
            }
        }
        return res;
    }

    public List<InfluenceCell> filterEnemy(List<InfluenceCell> cells) {
        List<InfluenceCell> enemies = new ArrayList<>();
        for(InfluenceCell cell : cells) {
            if(cell.getOwner() != we) {
                enemies.add(cell);
            }
        }
        return enemies;
    }

    public List<InfluenceCell> getMyAttackerCells() {
        List<InfluenceCell> res = new ArrayList<>();
        for(InfluenceCell cell : myCells) {
            if(cell.getUnitsCount() >= 2) res.add(cell);
        }
        return res;
    }

    public PossibleAttack getPossibleAttack() {
        List<PossibleAttack> possibleAttacks = new ArrayList<>();
        List<InfluenceCell> attackers = getMyAttackerCells();
        for(InfluenceCell cell : attackers) {
            List<InfluenceCell> neighbors = getNeighborhood(cell);
            List<InfluenceCell> enemies = filterEnemy(neighbors);
            ArrayList<PossibleAttack> localAttacks = new ArrayList<>();
            for(InfluenceCell enemy : enemies) {
                localAttacks.add(new PossibleAttack(cell, enemy));
            }
            possibleAttacks.addAll(localAttacks);
        }
        if(possibleAttacks.size() > 0) {
            sort(possibleAttacks);
            System.out.println("Sort");
            for(PossibleAttack at : possibleAttacks) {
                System.out.println(at.toString());
            }
            System.out.println("EndSort");
            System.out.println(possibleAttacks.get(0).toString());
            return possibleAttacks.get(0);
        } else {
            System.out.println("No attack possible this round");
            return null;
        }
    }



}
