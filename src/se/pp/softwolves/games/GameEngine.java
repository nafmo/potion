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
 * Game engine. The game is run by calling perform() with the appropritae
 * user input.
 * @author peter
 */
public class GameEngine
{
    // Constants
    public enum GameState
    {
        GAME_CONTINUES,
        GAME_LOST,
        GAME_WON
    };

    // Instance variables
    private GameData data; /**< Game state information. */
    private int objNum; /**< Currently referenced object, or -1 if none. */

    public GameEngine()
    {
        data = new GameData();
        objNum = -1;
    }

    /**
     * Get the game introduction.
     */
    static String intro()
    {
        return
            "The Potion - a simple adventure\n" +
	        "==========\n\n" +
	        "Copyright 2002-2003,2013 Peter Krefting.\n" +
	        "A Softwolves Software Release.\n" +
	        "http://www.softwolves.pp.se/sw/\n\n" +
	        "This program comes with ABSOLUTELY NO WARRANTY. This is free " +
	        "software, and you are welcome to redistribute it under " +
	        "certain conditions; type \"license\" for details.\n";
    }

    /**
     * Get the current room number.
     */
    int currentRoom()
    {
        return data.room;
    }

    /**
     * Perform an action.
     * @param verb The action verb.
     * @param object The object noun.
     * @param output Set to the output string.
     * @return One of GameEngine.GAME_CONTINUES, GameEngine.GAME_LOST or
     *   GameEngine.GAME_WON
     */
    GameState perform(GameInput.GameVerb verb, String object, StringBuilder output)
    {
        objNum = -1;

        /* Verbs that do not require an object */
        switch (verb)
        {
            case N:
            case S:
            case E:
            case W:
                return move(verb, output);

            case I:
                return inventory(output);

            case LICENSE:
                return license(output);

            case QUIT:
                return quit(output);
        }

        /* Other words require an object */
        if (object != null)
        {
            objNum = GameObject.objectFromWord(object,
                    verb != GameInput.GameVerb.TAKE, data);
        }

        if (objNum == -1)
        {
            if (verb == GameInput.GameVerb.LOOK)
            {
                GameRoom.printRoom(data, output);
            }
            else
            {
                output.append("I do not see that here.\n");
            }
            return GameState.GAME_CONTINUES;
        }

        switch (verb)
        {
            case TAKE:
                return take(output);

            case LIFT:
                return lift(output);

            case READ:
                return read(output);

            case TALKTO:
                return talkto(output);

            case UNLOCK:
                return unlock(output);

            case TURN:
                return turn(output);

            case HIT:
                return hit(output);

            case KILL:
                return kill(output);

            case POUR:
                return pour(output);

            case DRINK:
                return drink(output);

            case THROW:
                return throwObject(output);

            case LOOK:
                return look(output);

            case DROP:
                return drop(output);
        }

        /* This should never happen */
        output.append("Say what?");
        return GameState.GAME_CONTINUES;
    }

    /**@{*/
    /** @defgroup Action
     * Action functions; takes the output object as parameter. The current
     * object, if any, is stored in objNum.
     */

    GameState move(GameInput.GameVerb verb, StringBuilder output)
    {
        /* Seen scientist? */
        if (data.seenscientist)
        {
            output.append("The scientist captures you.\n");
            return GameState.GAME_LOST;
        }

        /* FIXME: Crossing autobahn? */

        /* Check which movement is requested */
        int bitmap = 0;
        int offset = 0;
        switch (verb)
        {
            case N: bitmap = GameData.EXIT_NORTH; offset = -6; break;
            case S: bitmap = GameData.EXIT_SOUTH; offset =  6; break;
            case E: bitmap = GameData.EXIT_EAST;  offset =  1; break;
            case W: bitmap = GameData.EXIT_WEST;  offset = -1; break;
        }

        /* Check if movement is allowed in that direction */
        if ((data.map[data.room - 1] & bitmap) != 0)
        {
            data.room += offset;
        }
        else
        {
            output.append("You cannot go that way.\n");
            return GameState.GAME_CONTINUES;
        }

        /* Check if we are carrying both bottles */
        int bottles = 0;
        for (int i = 0; i < GameData.INVENTORY; ++ i)
        {
            if (data.inventory[i] == GameObject.ELIXIR ||
                data.inventory[i] == GameObject.COKE)
            {
                ++ bottles;
            }
        }

        if (bottles == 2)
        {
            switch (++ data.bothbottles)
            {
            case 1:
                output.append("The potion is starting to heat up.\n");
                break;

            case 5:
                output.append("Some kind kind of reaction seems to be happening " +
                              "between the potion and what is left of the Coke.\n");
                break;

            case 10:
                output.append("You better get rid of one of the bottles before " +
                              "the potion explodes!\n");
                break;

            case 12:
                output.append("The potion explodes in your face!\n");
                return GameState.GAME_LOST;
            }
        }
        else
        {
            /* Reset counter */
            data.bothbottles = 0;
        }

        return GameState.GAME_CONTINUES;
    }

    GameState inventory(StringBuilder output)
    {
        boolean hasanything = false;

        output.append("You are carrying:\n");
        for (int i = 0; i < GameData.INVENTORY; ++ i)
        {
            if (data.inventory[i] != -1)
            {
                output.append(GameObject.printObject(data.inventory[i])).append("\n");
                hasanything = true;
            }
        }

        if (!hasanything)
        {
            output.append("Nothing.\n");
        }

        return GameState.GAME_CONTINUES;
    }

    GameState take(StringBuilder output)
    {
        switch (objNum)
        {
            case GameObject.PILL:
                /* Something special happens when we take the pill */
                data.pilleaten = true;
                data.objects[GameObject.PILL] = -1;
                output.append("You understand.\n");
                break;

            case GameObject.STONE_PILL:
                /* Finds pill, if it's not taken already */
                if (!data.pilleaten && -1 == data.objects[GameObject.PILL])
                {
                    output.append("You found a small pill beneath it.\n");
                    data.objects[GameObject.PILL] = data.room;
                    break;
                }
                /* Else fall through */

            default:
                if (GameObject.movable(objNum))
                {
                    if (data.addInventory(objNum, output))
                        output.append("Taken.\n");
                }
                else
                {
                    output.append("You cannot do that!\n");
                }
                break;
        }
        return GameState.GAME_CONTINUES;
    }

    GameState lift(StringBuilder output)
    {
        switch (objNum)
        {
            case GameObject.STONE_PILL:
                if (!data.pilleaten)
                {
                    return take(output);
                }
                /* else fall through */

            default:
                output.append("Nothing.\n");
                break;
        }

        return GameState.GAME_CONTINUES;
    }

    GameState read(StringBuilder output)
    {
        switch (objNum)
        {
            case GameObject.STONE_MISSION:
                output.append("It says:\n")
                      .append("\"Save us from the evil scientist. ")
                      .append("Mix his evil drug with salt water. ")
                      .append("That is your mission.\"");
                break;

            default:
                output.append("Nothing.\n");
        }

        return GameState.GAME_CONTINUES;
    }

    GameState talkto(StringBuilder output)
    {
        switch (objNum)
        {
            case GameObject.SCIENTIST:
                output.append("The scientist says:\n")
                      .append("\"You will die with the rest!\" ")
                      .append("and laughs frantically.\n");
                break;

            case GameObject.SQUIRREL:
                if (data.pilleaten)
                {
                    output.append("The squirrel says:\n")
                          .append("\"You can now exit the forest\"\n");
                    data.map[3 - 1] |= GameData.EXIT_EAST;
                    break;
                }
                /* else fall through */

            default:
                output.append("You cannot do that!\n");
        }

        return GameState.GAME_CONTINUES;
    }

    GameState unlock(StringBuilder output)
    {
        /* Check if we have the key */
        boolean havekey = false;
        for (int i = 0; i < GameData.INVENTORY; ++ i)
            if (GameObject.KEY == data.inventory[i])
                havekey = true;

        if (havekey)
        {
            switch (objNum)
            {
                case GameObject.COTTAGE:
                    output.append("You find a switch inside.\n");
                    data.objects[GameObject.SWITCH] = data.room;
                    return GameState.GAME_CONTINUES;

                case GameObject.DOOR:
                    output.append("It opens revealing a laboratory.\n");
                    data.map[20 - 1] |= GameData.EXIT_WEST;
                    return GameState.GAME_CONTINUES;
            }
        }

        output.append("You cannot do that!\n");
        return GameState.GAME_CONTINUES;
    }

    GameState turn(StringBuilder output)
    {
        switch (objNum)
        {
            case GameObject.SWITCH:
                output.append("Thud.\n");
                data.map[22 - 1] |= GameData.EXIT_WEST;
                break;

            default:
                output.append("You cannot do that!\n");
                break;
        }

        return GameState.GAME_CONTINUES;
    }

    GameState hit(StringBuilder output)
    {
        switch (objNum)
        {
            case GameObject.SQUIRREL:
                /* If we hit the squirrel, we kill it. */
                output.append("You kill the squirrel.\n");
                return GameState.GAME_LOST;

            case GameObject.SCIENTIST:
                /* Clear the seen scientist flag */
                data.seenscientist = false;
                /* Remove scientist from game */
                data.objects[GameObject.SCIENTIST] = -1;

                output.append("He will not bother you again.\n");
                break;

            default:
                output.append("You cannot do that!\n");
                break;
        }

        return GameState.GAME_CONTINUES;
    }

    GameState kill(StringBuilder output)
    {
        output.append("No strong violence please. This is a family game.\n");
        return GameState.GAME_CONTINUES;
    }

    GameState pour(StringBuilder output)
    {
        switch (objNum)
        {
            case GameObject.ELIXIR:
                /* The game object is to mix the potion with salt
                * water. This can only be done by pouring it into
                * the sea.
                */
                switch (data.room)
                {
                    case 6:
                        output.append("The potion has been neutralised.\n");
                        return GameState.GAME_WON;

                    default:
                        output.append("The potion poisons the land.\n");
                        return GameState.GAME_LOST;
                }

            case GameObject.COKE:
                output.append("The bottle is empty.\n");
                break;

            default:
                output.append("You cannot do that!\n");
                break;
        }

        return GameState.GAME_CONTINUES;
    }

    GameState drink(StringBuilder output)
    {
        switch (objNum)
        {
            case GameObject.ELIXIR:
                /* Can only drink the elixir, and we lose if we do it */
                output.append("You die instantly.");
                return GameState.GAME_LOST;

            case GameObject.COKE:
                /* It's empty */
                return pour(output);

            default:
                output.append("You cannot do that!\n");
                return GameState.GAME_CONTINUES;
        }
    }

    GameState throwObject(StringBuilder output)
    {
        if ((objNum <= GameObject.LAST_STONE || objNum == GameObject.COKE) &&
            data.room == 9)
        {
            objNum = GameObject.SQUIRREL;
            return hit(output);
        }
        else
        {
            return drop(output);
        }
    }

    GameState look(StringBuilder output)
    {
        switch (objNum)
        {
            case GameObject.STONE_MISSION:
                return read(output);

            case GameObject.STONE_PILL:
                if (!data.pilleaten && data.objects[GameObject.PILL] == -1)
                    return take(output);
                /* Else fall through */

            default:
                output.append("Nothing.\n");
                return GameState.GAME_CONTINUES;
        }
    }

    GameState drop(StringBuilder output)
    {
        switch (objNum)
        {
            case GameObject.COKE:
                /* Can only recycle the Cola bottle once we've taken it */
                switch (data.room)
                {
                case 12:
                    if (data.dropInventory(GameObject.COKE, output))
                    {
                        output.append("You recycle the bottle. ")
                              .append("Thank you for being kind to the nature!\n");
                        data.objects[GameObject.COKE] = -1; /* Remove from game */
                        break;
                    }
                    /* else fall through */

                case 28: case 29: /* Dump */
                    output.append("Throwing away a perfectly recyclable bottle? ")
                          .append("No way!\n");
                    break;

                case 1:  case 2:  case 3:
                case 7:  case 8:  case 9:
                case 13: case 14: case 15:
                case 30: /* Forest */
                    output.append("Someone might get hurt!\n");
                    break;

                default:
                    output.append("Please recycle the bottle.\n");
                    break;
                }
                break;

            case GameObject.ELIXIR:
                if (data.bothbottles > 0)
                {
                    output.append("You dare not drop the potion in its current state!\n");
                    break;
                }
                /* else fall through */

            default:
                if (data.dropInventory(objNum, output))
                    output.append("Dropped.\n");
        }

        return GameState.GAME_CONTINUES;
    }

    GameState license(StringBuilder output)
    {
        output.append("This program is free software; you can redistribute it ")
              .append("and/or modify it under the terms of the GNU General Public ")
              .append("License as published by the Free Software Foundation, ")
              .append("version 2.\n\n")
              .append("This program is distributed in the hope that it will be ")
              .append("useful, but WITHOUT ANY WARRANTY; without even the implied ")
              .append("warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR ")
              .append("PURPOSE.  See the GNU General Public License for more ")
              .append("details.\n\n")
              .append("You should have received a copy of the GNU General Public ")
              .append("License along with this program; if not, write to the Free ")
              .append("Software Foundation, Inc., 59 Temple Place, Suite 330, ")
              .append("Boston, MA  02111-1307  USA\n");

        return GameState.GAME_CONTINUES;
    }

    GameState quit(StringBuilder output)
    {
        output.append("You leave for your home country. In the distance, you ")
              .append("can hear the evil scientist laughing mercilessly as he ")
              .append("unleashes his horrible magical potion on the land.\n");
        return GameState.GAME_LOST;
    }
    /**@}*/

    String describeRoom()
    {
        StringBuilder output = new StringBuilder();
        GameRoom.printRoom(data, output);
        return output.toString();
    }
}
