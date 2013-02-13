/*
 * The Potion
 * ==========
 *
 * A Java port of my entry to the 2003 Minigame Competion.
 * Copyright © 2002-2003,2013 Peter Krefting <peter@softwolves.pp.se>
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
 * Main game class. This is the class that runs the game.
 */
public class Potion
{
    /**
     * Entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        if (args.length > 0)
        {
            System.out.println("The Potion, a miniature adventure game.");
            System.out.println("Run without any arguments to start.");
            System.exit(1);
        }

        // Run the game loop
        run();
    }

    /**
     * Game engine driver. This method runs the game loop.
     */
    static private void run()
    {
        // Initialize the game
        GameEngine engine = new GameEngine();
        System.out.println(GameEngine.intro());

        ConsoleInputDriver inputdriver = new ConsoleInputDriver();
        GameInput input = new GameInput(inputdriver);

        // Run the game loop
        int oldroom = 0;
        GameEngine.GameState exit = GameEngine.GameState.GAME_CONTINUES;
        do
        {
            // Display the room description, if different from last time
            if (oldroom != engine.currentRoom())
            {
                oldroom = engine.currentRoom();
                String description = engine.describeRoom();
                // FIXME: Wordwrap
                System.out.println(description);
            }

            // Get user input and act on it.
            if (input.getInput())
            {
                StringBuilder output = new StringBuilder();
                exit = engine.perform(input.verb, input.object, output);
                System.out.print(output.toString());
            }
            else
            {
                System.out.println("An error occurred, terminating.");
                System.exit(1);
            }
        } while (exit == GameEngine.GameState.GAME_CONTINUES);

        // Display the appropriate exit message
        switch (exit)
        {
            case GAME_LOST:
                System.out.println("GAME OVER!");
                break;

            case GAME_WON:
                System.out.println("Congratulations!");
                break;
        }

        // Clean up
        inputdriver.close();
    }
}
