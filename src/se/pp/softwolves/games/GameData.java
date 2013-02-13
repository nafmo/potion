/* Copyright © 2002-2003,2013 Peter Krefting <peter@softwolves.pp.se>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package se.pp.softwolves.games;

/**
 * Keeper of game state. This class keeps the game state information.
 * @author peter
 */
public class GameData
{
    // Constants
    static final int INVENTORY = 2; /**< Size of the inventory. */
    static final int OBJECTS = 21; /**< Number of game objects. */
    static final int MAPSIZE = 30; /**< Number of locations to visit. */
    /** Default location of objects */
    static final int[] DEFAULT_ROOMS =
    {
        1, 2, 3, 7, 8, 9, 13, 14, 15, -1, 9,
        18, 16, -1, 24, 28, 19, 25, 26, 20, -1
    };
    static final int EXIT_NORTH = 0x01; /**< Room has north exit. */
    static final int EXIT_SOUTH = 0x02; /**< Room has south exit. */
    static final int EXIT_WEST  = 0x04; /**< Room has west exit. */
    static final int EXIT_EAST  = 0x08; /**< Room has east exit. */
    /** Default map */
    static final int[] DEFAULT_MAP =
    {
                     EXIT_SOUTH |             EXIT_EAST /*  1 */,
                                  EXIT_WEST | EXIT_EAST /*  2 */,
                     EXIT_SOUTH | EXIT_WEST             /*  3 */,
                     EXIT_SOUTH | EXIT_WEST             /*  4 */,
                     EXIT_SOUTH |             EXIT_EAST /*  5 */,
                                  EXIT_WEST             /*  6 */,
        EXIT_NORTH | EXIT_SOUTH                         /*  7 */,
                     EXIT_SOUTH                         /*  8 */,
        EXIT_NORTH | EXIT_SOUTH                         /*  9 */,
        EXIT_NORTH | EXIT_SOUTH                         /* 10 */,
        EXIT_NORTH | EXIT_SOUTH | EXIT_WEST | EXIT_EAST /* 11 */,
                     EXIT_SOUTH | EXIT_WEST             /* 12 */,
        EXIT_NORTH |                          EXIT_EAST /* 13 */,
        EXIT_NORTH |              EXIT_WEST             /* 14 */,
        EXIT_NORTH                                      /* 15 */,
        EXIT_NORTH |                          EXIT_EAST /* 16 */,
        EXIT_NORTH | EXIT_SOUTH | EXIT_WEST | EXIT_EAST /* 17 */,
        EXIT_NORTH | EXIT_SOUTH | EXIT_WEST             /* 18 */,
                     EXIT_SOUTH |             EXIT_EAST /* 19 */,
                     EXIT_SOUTH |             EXIT_EAST /* 20 */,
                     EXIT_SOUTH | EXIT_WEST | EXIT_EAST /* 21 */,
                                              EXIT_EAST /* 22 */,
        EXIT_NORTH | EXIT_SOUTH | EXIT_WEST | EXIT_EAST /* 23 */,
        EXIT_NORTH | EXIT_SOUTH | EXIT_WEST             /* 24 */,
        EXIT_NORTH                                      /* 25 */,
        EXIT_NORTH |                          EXIT_EAST /* 26 */,
        EXIT_NORTH |              EXIT_WEST             /* 27 */,
                                              EXIT_EAST /* 28 */,
        EXIT_NORTH |              EXIT_WEST | EXIT_EAST /* 29 */,
                                  EXIT_WEST             /* 30 */
    };

    // Instance variables
    int room; /**< The current room. 1-based. */
    int[] inventory; /**< The current inventory. -1 for empty slot. */
    int[] objects; /**< Current location of objects. -1 for invisible. */
    boolean pilleaten; /**< Whether the pill has been eaten or not. */
    boolean seenscientist; /**< Whether we have met the scientist or not. */
    int[] map; /**< The current map bit field. */
    int roadvisit; /**< Number of visits to the road. */
    int bothbottles; /**< Number of times both bottles have been held. */

    public GameData()
    {
        // Initialize the game data
        room = 1;
        inventory = new int[INVENTORY];
        for (int i = 0; i < inventory.length; ++ i)
            inventory[i] = -1; // nothing held
        objects = new int[OBJECTS];
        for (int i = 0; i < objects.length; ++ i)
            objects[i] = DEFAULT_ROOMS[i];
        pilleaten = false;
        seenscientist = false;
        map = new int[MAPSIZE];
        for (int i = 0; i < map.length; ++ i)
            map[i] = DEFAULT_MAP[i];
        roadvisit = 0;
        bothbottles = 0;
    }

    /**
     * Find an object, in the room and possibly in the inventory.
     * @param objNum The object to look for.
     * @param checkInventory Whether to check the inventory.
     * @return true if the object is there.
     */
    boolean findObject(int objNum, boolean checkInventory)
    {
        /* Is the object in the room? */
        if (objects[objNum] == room)
        {
            return true;
        }

        /* Is the object in the inventory? */
        if (checkInventory)
        {
            for (int i = 0; i < inventory.length; ++ i)
            {
                if (inventory[i] == objNum)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Remove object from game map and add to inventory.
     * @param objNum The object to add.
     * @param output Where to add any output that need to be displayed.
     * @return true if the object could be added.
     */
    boolean addInventory(int objNum, StringBuilder output)
    {
        for (int i = 0; i < inventory.length; ++ i)
        {
            if (inventory[i] == -1)
            {
                inventory[i] = objNum;
                objects[objNum] = -1;
                return true;
            }
        }

        output.append("It is too heavy.\n");
        return false;
    }

    /**
     * Remove object from inventory and add to game map.
     * @param objNum The object to drop.
     * @param output Where to add any output that need to be displayed.
     * @return true if the object could be dropped.
     */
    boolean dropInventory(int objNum, StringBuilder output)
    {
        for (int i = 0; i < inventory.length; ++ i)
        {
            if (inventory[i] == objNum)
            {
                inventory[i] = -1;
                objects[objNum] = room;
                return true;
            }
        }

        output.append("You are not carrying that.\n");
        return false;
    }
}
