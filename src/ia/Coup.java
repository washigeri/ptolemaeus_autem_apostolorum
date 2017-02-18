package ia;

import client.InfluenceCell;

import java.util.ArrayList;

/**
 * Created by malek on 18/02/17.
 */
public class Coup {
    InfluenceCell attaquant;
    InfluenceCell victime;
    int score;

    public Coup(InfluenceCell attq, InfluenceCell def){
        this.attaquant = attq;
        this.victime = def;
        this.score = attq.getUnitsCount() - def.getUnitsCount();
    }

    public static Coup max_coup(ArrayList<Coup> list){
        Coup res = null;
        if(!list.isEmpty()){
            res = list.get(0);
            for( Coup c : list){
                if( c.score > res.score ){
                    res = c;
                }
            }
        }
        return  res;
    }
}
