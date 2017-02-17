package client;

/**
 * Classe permettant de représenter une cellule du jeu
 */
public class InfluenceCell
{
    /**
     * Coordonnée x de la cellule (colonne de la cellule, commence à 0)
     */
    private int x;

    /**
     * Coordonnée y de la cellule (ligne de la cellule, commence à 0)
     */
    private int y;

    /**
     * Numéro du joueur à qui appartient la cellule (0 si elle est vide)
     */
    private int owner;

    /**
     * Nombre de soldats présents sur la cellule
     */
    private int unitsCount;

    /**
     *
     * @param x Coordonnée x de la cellule (colonne de la cellule, commence à 0)
     * @param y Coordonnée y de la cellule (ligne de la cellule, commence à 0)
     * @param owner Numéro du joueur à qui appartient la cellule (0 si elle est vide)
     * @param unitsCount Nombre de soldats présents sur la cellule
     */
    public InfluenceCell(int x, int y, int owner, int unitsCount)
    {
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.unitsCount = unitsCount;
    }

    /**
     * Permet d'accéder à la coordonnée x de la cellule
     * @return La coordonnée x de la cellule courante
     */
    public int getX()
    {
        return x;
    }

    /**
     * Permet d'accéder à la coordonnée y de la cellule
     * @return La coordonnée y de la cellule courante
     */
    public int getY()
    {
        return y;
    }

    /**
     * Permet d'accéder au propriétaire de la cellule
     * @return Le numéro du joueur à qui appartient la cellule (0 si elle n'appartient à personne)
     */
    public int getOwner()
    {
        return owner;
    }

    /**
     * Permet d'accéder au nombre d'unités sur la cellule
     * @return Le nombre de soldats présents sur la cellule
     */
    public int getUnitsCount()
    {
        return unitsCount;
    }
}
