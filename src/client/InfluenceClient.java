package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Classe permettant de gérer toutes les interactions avec le serveur de jeu
 */
public class InfluenceClient
{
    /**
     * Socket client permettant de relier le serveur
     */
    private Socket clientSocket = null;

    /**
     * Buffer permettant d'envoyer des données au serveur
     */
    private BufferedOutputStream bos;

    /**
     * Buffer permettant de lire des données envoyées depuis le serveur
     */
    private BufferedInputStream bis;

    /**
     * Port du serveur sur lequel le client doit se connecter (1337 par défaut)
     */
    private int port = 1337;


    /**
     * Numéro de l'équipe
     */
    private int number;

    /**
     * Nombre d'unités à placer au cours d'un tour
     */
    private int unitsToAdd;

    /**
     * Liste des cellules sur lesquelles on souhaite rajouter des unités
     */
    private ArrayList<InfluenceCell> cellsToPower;

    /**
     * Plateau de jeu sur lequel joue le client
     */
    private InfluenceField field;


    /**
     * Enumération des statuts possibles de la partie (en cours, victoire, défaite...)
     */
    public enum Status { ONGOING, VICTORY, DEFEAT, CONNECTION_LOST };

    /**
     * Champ statique représentant l'unique instance du client
     */
    private static InfluenceClient instance = null;

    /**
     * Statut de la partie (en cours, victoire, défaite...)
     */
    private Status status;

    /**
     * Constructeur privé du client
     */
    private InfluenceClient()
    {
        this.unitsToAdd = 0;
        this.cellsToPower = new ArrayList<>();
    }

    /**
     * Permet de récupérer l'instance unique du client
     * @return Le client instancié pour la partie en cours
     */
    public static InfluenceClient getInstance()
    {
        if (InfluenceClient.instance == null)
        {
            InfluenceClient.instance = new InfluenceClient();
        }
        return InfluenceClient.instance;
    }

    /**
     * Permet d'accéder au numéro de l'équipe
     * @return Le numéro de l'équipe qui a été attribué par le serveur
     */
    public int getNumber()
    {
        return this.number;
    }

    /**
     * Permet d'accéder au statut de la partie
     * @return Le statut de la partie : en cours, victoire ou défaite
     */
    public Status getStatus()
    {
        return this.status;
    }

    /**
     * Permet de se connecteur au serveur de jeu
     * @param ipAddress Adresse IP du serveur
     * @param teamName Nom de l'équipe
     */
    public void connect(String ipAddress, String teamName)
    {
        try
        {
            // Connexion au serveur
            this.clientSocket = new Socket(InetAddress.getByName(ipAddress), this.port);
            this.printLog("Connecting to server " + this.clientSocket.getRemoteSocketAddress().toString());

            // Envoi du nom de l'équipe
            if (teamName.length() > 24)
            {
                teamName = teamName.substring(0, 24);
            }
            this.bos = new BufferedOutputStream(this.clientSocket.getOutputStream());
            this.bos.write(teamName.getBytes());
            this.bos.flush();
            this.printLog("Sending team name: " + teamName);

            // Réception du numéro de l'équipe
            this.bis = new BufferedInputStream(this.clientSocket.getInputStream());
            byte[] buffer = new byte[1];
            if (this.bis.read(buffer) != -1)
            {
                this.number = buffer[0];
                this.printLog("Client number: " + this.number);
            }
            else
            {
                this.disconnect();
                this.printLog("Error: can't read from server");
            }
            this.status = Status.ONGOING;
        }
        catch (Exception e)
        {
            this.disconnect();
            this.printLog("Socket error: " + e.getMessage());
        }
    }

    /**
     * Permet d'envoyer une attaque au serveur
     * @param fromX Coordonnée x de la cellule de départ de l'attaque
     * @param fromY Coordonnée y de la cellule de départ de l'attaque
     * @param toX Coordonnée x de la cellule attaquée
     * @param toY Coordonnée y de la cellule attaquée
     * @return Le plateau de jeu après l'attaque
     */
    public InfluenceField attack(int fromX, int fromY, int toX, int toY)
    {
        try
        {
            byte[] buffer = new byte[4];
            buffer[0] = (byte)fromX;
            buffer[1] = (byte)fromY;
            buffer[2] = (byte)toX;
            buffer[3] = (byte)toY;
            this.bos.write(buffer);
            this.bos.flush();
        }
        catch (Exception e)
        {
            this.disconnect();
            this.printLog("Socket error: " + e.getMessage());
        }
        return this.receiveField();
    }

    /**
     * Permet de terminer les attaques et de passer en mode rechargement des cellules
     * @return Le nombre d'unités à placer sur vos cellules
     */
    public int endAttacks()
    {
        int res = 0;
        byte[] buffer;
        try
        {
            this.printLog("End attacks");
            buffer = new byte[]{(byte)255, (byte)255, (byte)255, (byte)255};
            this.bos.write(buffer);
            this.bos.flush();

            buffer = new byte[1];
            if (this.bis.read(buffer) != -1)
            {
                res = buffer[0];
                this.unitsToAdd = res;
            }
            else
            {
                this.disconnect();
                this.printLog("Can't read from server");
            }
        }
        catch (Exception e)
        {
            this.disconnect();
            this.printLog("Socket error: " + e.getMessage());
        }
        return res;
    }

    /**
     * Permet de connaître le nombre restant d'unités à placer au cours du tour
     * @return Le nombre restant d'unités à placer
     */
    public int remainingUnits()
    {
        return this.unitsToAdd - this.cellsToPower.size();
    }

    /**
     * Permet d'ajouter des unités à une cellule (phase de rechargement)
     * @param c La cellule à laquelle on souhaite ajouter des unités
     * @param unitsCount Le nombre d'unités à ajouter sur cette cellule (doit être supérieur à 0)
     */
    public void addUnits(InfluenceCell c, int unitsCount)
    {
        if (unitsCount > 0)
        {
            for (int i = 0; i < unitsCount; i++)
            {
                this.cellsToPower.add(c);
            }
        }
    }

    /**
     * Permet d'ajouter 1 unité à toutes les cellules d'une liste
     * @param lc Liste contenant les cellules auxquelles on souhaite rajouter une unité
     */
    public void addUnitsList(ArrayList<InfluenceCell> lc)
    {
        this.cellsToPower.addAll(lc);
    }

    /**
     * Permet d'envoyer la liste des cellules à recharger
     * @return Le plateau à l'issue du rechargement
     */
    public InfluenceField endAddingUnits()
    {
        try
        {
            this.printLog("Adding units");
            byte[] buffer = new byte[this.unitsToAdd * 2];
            for (int i = 0; i < this.unitsToAdd; i++)
            {
                if (i < this.cellsToPower.size())
                {
                    InfluenceCell c = this.cellsToPower.get(i);
                    buffer[2*i] = (byte)c.getX();
                    buffer[2*i+1] = (byte)c.getY();
                }
                else
                {
                    buffer[2*i] = (byte)255;
                    buffer[2*i+1] = (byte)255;
                }
            }
            this.bos.write(buffer);
            this.bos.flush();
            this.unitsToAdd = 0;
            this.cellsToPower.clear();
        }
        catch (Exception e)
        {
            this.disconnect();
            this.printLog("Socket error:" + e.getMessage());
        }
        return this.receiveField();
    }

    /**
     * Permet de récupérer le plateau de jeu depuis le serveur
     * @return Le plateau de jeu mis à jour
     */
    private InfluenceField receiveField()
    {
        try
        {
            byte[] buffer = new byte[2];
            if (this.bis.read(buffer) != -1)
            {
                this.field = new InfluenceField(buffer[0], buffer[1]);
                buffer = new byte[2 * this.field.getWidth()];
                int x;
                for (int i = 0; i < this.field.getHeight(); i++)
                {
                    if (this.bis.read(buffer) != -1)
                    {
                        x = 0;
                        for (int j = 0; j < 2 * this.field.getWidth(); j+=2)
                        {
                            this.field.getCells().add(new InfluenceCell(x, i , buffer[j], buffer[j+1]));
                            x++;
                        }
                    }
                    else
                    {
                        this.disconnect();
                        this.printLog("Error: can't read from server");
                    }
               }
            }
            else
            {
                this.disconnect();
                this.printLog("Error: can't read from server");
            }

        }
        catch (Exception e)
        {
            this.disconnect();
            this.printLog("Socket error:" + e.getMessage());
        }
        return this.field;
    }

    /**
     * Permet d'attendre le tour suivant : récupère le plateau de jeu et teste si l'on a gagné ou perdu
     * @return Le plateau de jeu actualisé
     */
    public InfluenceField nextRound()
    {
        this.printLog("Waiting for our turn");
        InfluenceField field = this.receiveField();
        if (this.hasWon())
        {
            this.disconnect();
            this.status = Status.VICTORY;
        }
        if (this.hasLost())
        {
            this.disconnect();
            this.status = Status.DEFEAT;
        }
        return field;
    }

    /**
     * Permet de tester si l'on a gagné
     * @return Vrai si l'on a gagné, faux sinon
     */
    private boolean hasWon()
    {
        return this.countOpponentCells() == 0;
    }

    /**
     * Permet de tester si l'on a perdu
     * @return Vrai si l'on a perdu, faux sinon
     */
    private boolean hasLost()
    {
        return this.getMyCells().size() == 0;
    }

    /**
     * Permet de récupérer toutes les cellules appartenant à votre équipe
     * @return Une liste de toutes les cellules qui vous appartiennent
     */
    public ArrayList<InfluenceCell> getMyCells()
    {
        ArrayList<InfluenceCell> res = new ArrayList<>();
        for (InfluenceCell c : this.field.getCells())
        {
            if (c.getOwner() == this.number)
            {
                res.add(c);
            }
        }
        return res;
    }

    /**
     * Permet de compter le nombre de cellules possédées par les adversaires
     * @return Le nombre total de cellules possédées par les adversaires
     */
    private int countOpponentCells()
    {
        int res = 0;
        for (InfluenceCell c : this.field.getCells())
        {
            if (c.getOwner() != this.number && c.getOwner() != 0)
            {
                res++;
            }
        }
        return res;
    }

    /**
     * Permet de déconnecter une équipe (si connexion perdue ou si fin de partie)
     */
    private void disconnect()
    {
        try
        {
            if (this.clientSocket != null && this.clientSocket.isConnected())
            {
                this.clientSocket.close();
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        this.status = Status.CONNECTION_LOST;
    }

    /**
     * Permet d'afficher un message au format suivant : date/heure - message
     * @param message Message à afficher
     */
    public void printLog(String message)
    {
        Calendar c = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
        System.out.println(df.format(c.getTime()) + " - " + message);
    }
}
