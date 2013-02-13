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
 * Actions about rooms. The class methods of this class handle actions
 * pertaining to rooms in the game.
 * @author peter
 */
public class GameRoom
{
    /**
     * Create room description.
     * @param data The current game state.
     * @param output Where to create output.
     */
    static void printRoom(GameData data, StringBuilder output)
    {
        boolean seen = false;
        int mapdata = data.map[data.room - 1];
        String s = null;

        /* Print room description */
        switch (data.room)
        {
            case 1:  case 2:  case 3:
            case 7:  case 8:  case 9:
            case 13: case 14: case 15:
            case 30:
                s = "You are in a forest. There are trees in every direction " +
                    "as far as the eye can see.";
                break;

            case 4: case 10:
            case 16: case 18:
                s = "You are standing on a big plain. " +
                    "You hear the distant hum of motors from somewhere.";
                break;

            case 12:
                s = "You are standing on a big plain, next to a recycling " +
                    "station.";
                break;

            case 5: case 11: case 17:
            case 23: case 24:
                switch (data.roadvisit)
                {
                case 0:
                case 1:
                    ++ data.roadvisit;
                case 3:
                    s = "You are standing on an autobahn. " +
                        "Several cars are passing passing by.";
                    break;

                case 2:
                    s = "You are standing on an autobahn. " +
                        "Suddenly you see a skateboarder pass by, grabbing a " +
                        "bottle of Coca Cola from a nearby car.";
                    ++ data.roadvisit;
                    data.objects[GameObject.COKE] = data.room;
                    break;
                }
                break;

            case 6:
                s = "You are standing on the sea shore. The sea stretches out " +
                    "as far as the eye can see to the north and to the east.";
                break;

            case 19: case 25:
                s = "You are inside the scientist's lab. You notice that he " +
                    "doesn't seem to put much energy in keeping it tidy.";
                break;

            case 20: case 21: case 26: case 27:
                s = "You are standing inside a big fortress. There are some " +
                    "spider webs in the corners, and a layer of dust on " +
                    "pretty much everything.";
                break;

            case 22:
                if ((mapdata & GameData.EXIT_WEST) == 0)
                {
                    s = "You are standing beside a big moat. There is no way " +
                        "to pass over it without lowering the drawbridge.";
                }
                else
                {
                    s = "You are standing beside a drawbridge leading to " +
                        "a big fortress.";
                }
                break;

            case 28: case 29:
                s = "You are standing on a dump. It stinks!";
                break;

            default:
                s = "Something is very wrong!";
                break;
        }

        output.append(s);
        output.append('\n');

        /* Print room contents */
        for (int i = 0; i < GameData.OBJECTS; ++ i)
        {
            if (data.objects[i] == data.room)
            {
                if (!seen)
                {
                    output.append("You see:\n");
                    seen = true;
                }
                output.append(GameObject.printObject(i));

                if (i == GameObject.SCIENTIST && !data.seenscientist)
                {
                    output.append(" He says: \"What are you doing here?\"");
                    data.seenscientist = true;
                }
                output.append('\n');
            }
        }

        /* Print exits */
        output.append("Available exits: ");
        if ((mapdata & GameData.EXIT_NORTH) != 0)
            output.append('N');
        if ((mapdata & GameData.EXIT_SOUTH) != 0)
            output.append('S');
        if ((mapdata & GameData.EXIT_EAST) != 0)
            output.append('E');
        if ((mapdata & GameData.EXIT_WEST) != 0)
            output.append('W');
        output.append('\n');
    }

}
