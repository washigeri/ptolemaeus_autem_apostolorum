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

    Strategy2(ArrayList<InfluenceCell> cells, InfluenceField plateau){

        myCells = new ArrayList<>(cells);
        field = plateau;
    }

    ArrayList<InfluenceCell> border(){
        ArrayList<InfluenceCell> res = new ArrayList<>();
        for(InfluenceCell cell : myCells){

        }
        return res;
    }

    boolean check_neighbors(InfluenceCell cell){

    }


}
