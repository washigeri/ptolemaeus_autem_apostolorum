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

    public Coup(){
        super();
    }

    public Coup(InfluenceCell attq, InfluenceCell def){
        this.attaquant = attq;
        this.victime = def;
        this.score = attq.getUnitsCount() - def.getUnitsCount();
    }

}
