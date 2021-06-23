package game;

import entities.Terrain;
import java.util.ArrayList;

/**
 * 
 * Sectors store one terrain, and all entities located within it.
 * Sectors should be loaded and keep track of whether or not they have already been loaded.
 * New sectors should be generated, old sectors should be loaded from saved data.
 * Sectors will update their set of entities and terrain.
 * Entities should be able to travel between sectors; however terrain can not.
 * Each sector will load 1 sky light, 1 player light, and 2 outside lights.
 * Sectors may contain buildings that have their own internal lighting systems.
 */

public class Sector {
    //terrain to draw in a given sector
    protected Terrain terrain;
    //list of entities in a given sector
    protected ArrayList Entities;
    //visibility of the Sector
    public boolean visible = false;
    
    public Sector(int x, int z){
    }
    
    
}
