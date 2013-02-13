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
 * Resolve object strings. This is the keeper of the strings representing
 * the game objects, able to parse their strings.
 * @author peter
 */
public class GameObject
{
    // Constants
    static final int NOT_A_WORD = -1;
    static final int STONE_PILL = 0;
    static final int STONE_1 = 1;
    static final int STONE_2 = 2;
    static final int STONE_3 = 3;
    static final int STONE_MISSION = 4;
    static final int STONE_5 = 5;
    static final int STONE_6 = 6;
    static final int STONE_7 = 7;
    static final int STONE_8 = 8;
    static final int PILL = 9;
    static final int SQUIRREL = 10;
    static final int COIN = 11;
    static final int COTTAGE = 12;
    static final int SWITCH = 13;
    static final int HUBCAP = 14;
    static final int KEY = 15;
    static final int SCIENTIST = 16;
    static final int ELIXIR = 17;
    static final int BOOK = 18;
    static final int DOOR = 19;
    static final int COKE = 20;
    static final int LAST_STONE = STONE_8;
    final static String[] NOUNS =
    {
        "stone", "rock", "pill", "squirrel", "coin", "gold coin", "money",
        "cottage", "house", "door", "switch", "hub cap", "key", "door key",
        "scientist", "madman", "potion", "book", "bottle", "empty bottle",
        "coke", "coca cola"
    };
    final static int[] NOUNNUMS =
    {
        NOT_A_WORD, NOT_A_WORD, PILL, SQUIRREL, COIN, COIN, COIN,
        COTTAGE, COTTAGE, DOOR, SWITCH, HUBCAP, KEY, KEY,
        SCIENTIST, SCIENTIST, ELIXIR, BOOK, COKE, COKE,
        COKE, COKE
    };

    /**
     * Retrieve object number from a word. Checks the current room and
     * (optionally) inventory.
     * @param noun The object string from the user.
     * @param checkInventory Set to true to check inventory as well.
     * @param state The current game state.
     * @return NOT_A_WORD (-1) if not found, else a valid object index.
     */
    public static int objectFromWord(String noun, boolean checkInventory, GameData state)
    {
        int j = -1;

        for (int i = 0; i < NOUNS.length; ++ i)
        {
            if (noun.equals(NOUNS[i]))
            {
                j = i;
            }
        }

        switch (j)
        {
            case -1:
                /* Not found in word list */
                return NOT_A_WORD;

            case 0:
            case 1:
                /* Stones (obj 0-8) */
                for (int i = 0; i < 9; ++ i)
                {
                    if (state.findObject(i, checkInventory))
                    {
                        return i;
                    }
                }
                break;

            default:
                if (state.findObject(NOUNNUMS[j], checkInventory))
                {
                    return NOUNNUMS[j];
                }
                break;
        }

    	return NOT_A_WORD;
    }

    /**
     * Returns a strings corresponding to the object.
     * @param objNum The object to describe.
     * @return A string describing the object.
     */
    public static String printObject(int objNum)
    {
        String s = null;

        switch (objNum)
        {
            case STONE_PILL:
            case STONE_1:
            case STONE_2:
            case STONE_3:
            case STONE_MISSION:
            case STONE_5:
            case STONE_6:
            case STONE_7:
            case STONE_8:
                s = "A stone.";
                break;

            case PILL:
                s = "A pill.";
                break;

            case SQUIRREL:
                s = "A squirrel.";
                break;

            case COIN:
                s = "A gold coin.";
                break;

            case COTTAGE:
                s = "A cottage.";
                break;

            case SWITCH:
                s = "A switch.";
                break;

            case HUBCAP:
                s = "A hub cap.";
                break;

            case KEY:
                s = "A key.";
                break;

            case SCIENTIST:
                s = "A scientist.";
                break;

            case ELIXIR:
                s = "A potion.";
                break;

            case BOOK:
                s = "A book.";
                break;

            case DOOR:
                s = "A door.";
                break;

            case COKE:
                s = "Empty Coke bottle.";
                break;
        }

        return s;
    }

    /**
     * Check if an object is movable.
     * @param objNum The object to check.
     * @return true if the object can be moved.
     */
    public static boolean movable(int objNum)
    {
        switch (objNum)
        {
            case PILL:
            case COIN:
            case HUBCAP:
            case KEY:
            case SCIENTIST:
            case ELIXIR:
            case BOOK:
            case COKE:
                return true;

            default:
                return false;
        }
    }
}
