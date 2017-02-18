package ia;

import client.InfluenceCell;
import client.InfluenceField;

import java.util.ArrayList;

/**
 * Created by malek on 17/02/17.
 */
public class Strategy {

    InfluenceField field;
    ArrayList<InfluenceCell> myCells;
    InfluenceCell cellToAttack;
    int owner;



    public Strategy(InfluenceField plat, ArrayList<InfluenceCell> cells, int me){
        this.field = plat;
        this.myCells = cells;
        this.owner = me;
    }

    private boolean neighbors( InfluenceCell myCell){
        boolean res = false;
        for(short i = -1 ; i< 2; i++){
            for(short j = -1; j < 2; j++){
                InfluenceCell cell = field.getCell(myCell.getX() + i, myCell.getY() + j);
                if( isValid(cell) && ( cell.getOwner() != owner) && (!res)){
                    res = true;
                }
            }
        }
        if(!res){
            System.out.println("neighbor rekt");
        }
        else {
            System.out.println("bite");
        }
        return res;
    }

    private  ArrayList<InfluenceCell> getBorder(){
        ArrayList<InfluenceCell> borderCells = new ArrayList<InfluenceCell>();
        for(int i=0;i<field.getHeight();i++)
        {
            for(InfluenceCell cell : myCells){
                if( (cell.getY() == i) && neighbors(cell)) {
                    borderCells.add(cell);
                }
            }
        }
        if( borderCells.isEmpty()){
            System.out.println("border_cells_empty!!!!");
        }
        return (borderCells);


    }


    private ArrayList<InfluenceCell> getEnemies(InfluenceCell cell){
        ArrayList<InfluenceCell> res = new ArrayList<InfluenceCell>();
        for(short i = -1 ; i< 2; i++) {
            for (short j = -1; j < 2; j++) {
                InfluenceCell c = field.getCell(cell.getX() + i, cell.getY() + j);
                if( isValid(c) && (c.getOwner() != owner)){
                    res.add(c);
                }
            }
        }
        if(res.isEmpty()){
            System.out.println("enemies empty");
        }
        return res;
    }

    private boolean isValid(InfluenceCell cell){
        if( cell != null) {
            int x = cell.getX(), y = cell.getY();
            return ((x >= 0) && (x < field.getWidth()) && (y >= 0) && (y < field.getHeight()));
        }
        else {
            return false;
        }
    }

    public InfluenceCell get_result_strategy(){

        ArrayList<InfluenceCell> borderCells = getBorder();
        ArrayList<InfluenceCell> enemies;
        ArrayList<Coup> coupsListe = new ArrayList<Coup>();
        for( InfluenceCell cell : borderCells ){
            enemies = getEnemies(cell);
            for(InfluenceCell enemy : enemies){
                coupsListe.add(new Coup(cell, enemy));
            }
        }
        Coup best = Coup.max_coup(coupsListe);
        if( best == null) { System.out.println("null");}
        setCellToAttack(best != null ? best.victime : null);
        return best.attaquant;


    }

    private void setCellToAttack(InfluenceCell target){
        this.cellToAttack = target;
    }
}
