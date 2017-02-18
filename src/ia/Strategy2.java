package ia;

import client.InfluenceCell;
import client.InfluenceField;

import java.util.ArrayList;

/**
 * Created by malek on 18/02/17.
 */
public class Strategy2 {

    ArrayList<InfluenceCell> myCells;
    InfluenceField field;
    int me;

    Strategy2(ArrayList<InfluenceCell> cells, InfluenceField plateau, int owner){

        myCells = new ArrayList<>(cells);
        field = plateau;
        me = owner;
    }

    ArrayList<InfluenceCell> border(){
        ArrayList<InfluenceCell> res = new ArrayList<>();
        for(InfluenceCell cell : myCells){
            if( check_neighbors(cell)){
                res.add(cell);
            }
       }
        return res;
    }

    ArrayList<InfluenceCell> filter(ArrayList<InfluenceCell> border){
        ArrayList<InfluenceCell> res = new ArrayList<>();
        for(InfluenceCell cell : border){
            if( cell.getUnitsCount() >= 2){
                res.add(cell);
            }
        }
        return  res;
    }

    boolean check_neighbors(InfluenceCell cell){
        boolean res = false;
        for(int i = -1; i<2; i++){
            for(int j = -1; j< 2; j++){
                int dx = cell.getX() + i;
                int dy = cell.getY() + j;
                if(isValid(dx, dy) && field.getCell(dx,dy).getOwner() != me ){
                    res = true;
                    j = 10;
                    i = 10;
                }
            }
        }
        return res;
    }

    boolean isValid(int x, int y){
        return ((x>=0) && (x < field.getWidth()) && (y>=0) && (y < field.getHeight()));
    }

    ArrayList<InfluenceCell> enemies(InfluenceCell cell){
        ArrayList<InfluenceCell> res = new ArrayList<>();
        for(int i = -1; i<2; i++){
            for(int j= -1; j<2; j++){
                int dx = cell.getX() + i;
                int dy = cell.getY() + j;
                if( isValid(dx, dy) && field.getCell(dx,dy).getOwner() != me ){
                    res.add(field.getCell(dx,dy));
                }
            }
        }
        return res;
    }

    Coup meilleur_coup(InfluenceCell cell){
        ArrayList<InfluenceCell> enemies = enemies(cell);
        Coup res = new Coup();
        if( enemies != null && !enemies.isEmpty() ){
            res = new Coup(cell, enemies.get(0));
            for( int i = 1; i < enemies.size(); i++){
                Coup temp = new Coup(cell, enemies.get(i));
                if( temp.score > res.score){
                    res = temp;
                }
            }
            return res;
        }
        else {
            return null;
        }
    }

    Coup meilleur_coup(){
        Coup res = null;
        ArrayList<InfluenceCell> attaquants = filter(border());
        if( attaquants != null && !attaquants.isEmpty()){
            res = meilleur_coup(attaquants.get(0));
            for(int i = 1; i< attaquants.size(); i++){
                Coup temp = meilleur_coup(attaquants.get(i));
                if( res == null ){
                    res = temp;
                }
                else if( temp.score > res.score){
                    res = temp;
                }
            }
        }
        return res;
    }

    InfluenceCell pointCellule(){
        InfluenceCell res = null;
        ArrayList<InfluenceCell> bords = border();
        Coup temp = new Coup(enemies(bords.get(0)).get(0), bords.get(0));
        for( InfluenceCell cell : bords){
            ArrayList<InfluenceCell> enemies = enemies(cell);
            for( InfluenceCell enemy : enemies){
                Coup temp2 = new Coup(enemy, cell);
                if( temp2.score < temp.score){
                    temp = temp2;
                }
            }
        }
        res = temp.victime;
        return res;
    }

    static int indexOf(InfluenceCell c, ArrayList<InfluenceCell> list){
        if(list != null && !list.isEmpty()) {
            int res = 0;
            for (InfluenceCell cell : list) {
                if (c.getX() == cell.getX() && c.getY() == cell.getY() && c.getOwner() == cell.getOwner()) {
                    return res;
                } else {
                    res++;
                }
            }
            return -1;
        }
        else{
            return -1;
        }
    }


}
