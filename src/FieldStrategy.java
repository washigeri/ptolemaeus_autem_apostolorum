/**
 * Created by vincent on 2/17/17.
 */
import client.InfluenceCell;
import client.InfluenceField;
import javafx.geometry.Pos;
import javafx.scene.control.Cell;

import java.lang.reflect.Array;
import java.util.*;

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
                diff = ourCell.getUnitsCount();
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

    class Point {
        public int x;
        public int y;
        public Point(int x, int y) {this.x=x; this.y=y;}
    }

    public InfluenceCell getMiddleEnemy() {
        int x = 0;
        int y = 0;

        int divide = 0;

        for(int i=0; i<field.getWidth(); i++) {
            for(int j=0; j<field.getHeight(); j++) {
                if(field.getCell(i, j).getOwner() > 0 && field.getCell(i,j).getOwner() != we) {
                    divide += field.getCell(i,j).getUnitsCount();
                    x += i;
                    y += j;
                }
            }
        }

        return field.getCell(x/divide, y/divide);
    }

    public List<InfluenceCell> getNeighborhood(InfluenceCell cell) {
        ArrayList<InfluenceCell> res = new ArrayList<>();

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(-1,-1));
        points.add(new Point(1,1));

        points.add(new Point(1,0));
        points.add(new Point(-1,0));

        points.add(new Point(0,-1));
        points.add(new Point(0,1));

        points.add(new Point(1,-1));
        points.add(new Point(-1,1));





        for(Point p : points) {
            if (p.x != 0 || p.y != 0) {
                int x = cell.getX() + p.x;
                int y = cell.getY() + p.y;
                if (isValid(x, y)) {
                    res.add(field.getCell(x, y));
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

    public List<InfluenceCell> filterRealEnemy(List<InfluenceCell> cells) {
        List<InfluenceCell> enemies = new ArrayList<>();
        for(InfluenceCell cell : cells) {
            if(cell.getOwner() != we && cell.getOwner() > 0) {
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

    public double distance(InfluenceCell a, InfluenceCell b) {
        return (double) Math.sqrt(Math.pow( a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    public List<PossibleAttack> getAllPossibleAttack() {
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
        return possibleAttacks;
    }

    public PossibleAttack getPossibleAttack() {
        List<PossibleAttack> possibleAttacks = getAllPossibleAttack();
        if(possibleAttacks.size() > 0) {
            sort(possibleAttacks);

            if(possibleAttacks.get(0).diff <= 0) {

                System.out.println("Sort");
                for (PossibleAttack at : possibleAttacks) {
                    System.out.println(at.toString());
                }
                System.out.println("EndSort");
                System.out.println(possibleAttacks.get(0).toString());
                return possibleAttacks.get(0);
            } else {
                System.out.println("No good attack possible this round");
                return null;
            }
        } else {
            System.out.println("No attack possible this round");
            return null;
        }
    }

    public HashMap<InfluenceCell, Integer> getUnitsIncrease(int leftUnits) {
        HashMap<InfluenceCell, Integer> toAdd = new HashMap<>();
        int oo = leftUnits;
        InfluenceCell bary = getMiddleEnemy();
        while(leftUnits > 0) {
            int origLeftUnits = leftUnits;
            myCells.sort(new Comparator<InfluenceCell>() {
                @Override
                public int compare(InfluenceCell o1, InfluenceCell o2) {
                    double distanceA = distance(o1, bary);
                    double distanceB = distance(o2, bary);
                    return (int) (distanceA - distanceB);
                }
            });
            for(InfluenceCell cell : myCells) {
                List<InfluenceCell> neighbors = getNeighborhood(cell);
                List<InfluenceCell> enemies = filterRealEnemy(neighbors);

                int nbRealEnemies = enemies.size();

                if(nbRealEnemies > 0) {


                    int unitsToAdd = (int) Math.ceil((double) nbRealEnemies / 2.0);
                    if(toAdd.containsKey(cell)) toAdd.put(cell, toAdd.get(cell) + unitsToAdd);
                    else toAdd.put(cell, unitsToAdd);
                    leftUnits -= unitsToAdd;
                }
            }
            /*for(InfluenceCell cell : myCells) {
                List<InfluenceCell> neighbors = getNeighborhood(cell);
                List<InfluenceCell> enemies = filterEnemy(neighbors);

                if(enemies.size() > 1) {
                    if(toAdd.containsKey(cell)) toAdd.put(cell, toAdd.get(cell) + 1);
                    else toAdd.put(cell, 1);
                    leftUnits -= 1;
                }
            }*/
            if(origLeftUnits == leftUnits) break;
        }
        System.out.println("PA UA :" + (oo - leftUnits));

        //1 handling
        for(InfluenceCell cell : myCells) {
            if(leftUnits > 0 && cell.getUnitsCount() == 1) {
                if(toAdd.containsKey(cell)) toAdd.put(cell, toAdd.get(cell) + 1);
                else toAdd.put(cell, 1);
                leftUnits--;
            }
        }


        //Left random
        Random rand = new Random();
        for(int i=0; i<leftUnits; i++) {
            int randomCellIndex = rand.nextInt(myCells.size());
            InfluenceCell cell = myCells.get(randomCellIndex);
            if(toAdd.containsKey(cell)) toAdd.put(cell, toAdd.get(cell) + 1);
            else toAdd.put(cell, 1);
        }
        return toAdd;
    }



}
