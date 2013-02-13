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

import java.util.*;

/**
 * Input driver. This class retrieves the user input and parses it.
 * @author peter
 */
public class GameInput
{
    // Constants
    public enum GameVerb
    {
        NOT_A_WORD,
        N,
        S,
        E,
        W,
        I,
        TAKE,
        LIFT,
        READ,
        TALKTO,
        UNLOCK,
        TURN,
        HIT,
        KILL,
        POUR,
        DRINK,
        THROW,
        LOOK,
        DROP,
        LICENSE,
        QUIT,
    }
    final static String[] VERBS =
    {
        "s", "n", "e", "w", "i",
        "take", "pick up", "lift", "read",
        "talk to", "unlock", "open", "turn",
        "hit", "punch", "kick", "kill",
        "pour", "empty", "drink", "throw",
        "look at", "look", "examine", "drop",
        "license", "licence", "quit", "exit"
    };
    final static GameVerb[] VERBNUMS =
    {
        GameVerb.S, GameVerb.N, GameVerb.E, GameVerb.W, GameVerb.I,
        GameVerb.TAKE, GameVerb.TAKE, GameVerb.LIFT, GameVerb.READ,
        GameVerb.TALKTO, GameVerb.UNLOCK, GameVerb.UNLOCK, GameVerb.TURN,
        GameVerb.HIT, GameVerb.HIT, GameVerb.HIT, GameVerb.KILL,
        GameVerb.POUR, GameVerb.POUR, GameVerb.DRINK, GameVerb.THROW,
        GameVerb.LOOK, GameVerb.LOOK, GameVerb.LOOK, GameVerb.DROP,
        GameVerb.LICENSE, GameVerb.LICENSE, GameVerb.QUIT, GameVerb.QUIT
    };

    // Instance variables
    GameInputDriver driver; /**< Where to retrieve input strings. */
    GameVerb verb; /**< Current verb. */
    String object; /**< Current object (if any). */

    public GameInput(GameInputDriver driver)
    {
        this.driver = driver;
    }

    /**
     * Retrieve user input. Gets one line of user input, parses it and sets
     * the appropriate variables for later use. When this returns true, we
     * have valid data from the user.
     *
     * @return true if all went fine, false on fatal error.
     */
    boolean getInput()
    {
        verb = GameVerb.NOT_A_WORD;
        boolean firstTry = true;
        while (verb == GameVerb.NOT_A_WORD)
        {
            // Get the text from the input driver
            String input = driver.getLine(firstTry);
            if (input == null)
            {
                return false;
            }
            firstTry = false;

            // Find the verb
            for (int i = 0; i < VERBS.length; ++ i)
            {
                if (input.startsWith(VERBS[i]) &&
                    (input.length() == VERBS[i].length() ||
                    input.charAt(VERBS[i].length()) == ' '))
                {
                    verb = VERBNUMS[i];

                    // Find the noun, if any
                    if (input.length() > VERBS[i].length())
                    {
                        object = input.substring(VERBS[i].length() + 1);
                        if (object.startsWith("the "))
                        {
                            object = object.substring(4);
                        }
                    }
                    else
                    {
                        object = null;
                    }
                    break;
                }
            }
        }

        // Successfully parsed a verb from the user input
        return true;
    }
}
