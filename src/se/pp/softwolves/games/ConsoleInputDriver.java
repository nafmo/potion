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

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Console input driver. Gets user input from the console.
 * @author peter
 */
public class ConsoleInputDriver implements GameInputDriver
{
    BufferedInputStream buff;

    /**
     * Create the console input driver.
     */
    public ConsoleInputDriver()
    {
        buff = new BufferedInputStream(System.in);
    }

    @Override
    public String getLine(boolean firstTry)
    {
        // Display a prompt
        System.out.print((firstTry ? "" : "I do not understand. ") +
                "Your turn? ");

        // Loop to retrieve a line of user input
        StringBuilder response = new StringBuilder();
        while (true)
        {
            char in;
            try
            {
                in = (char) buff.read();
            }
            catch (IOException e)
            {
                // Return null to indicate error
                return null;
            }

            // Terminate on EOF, CR or LF
            if (in == (char) -1 || in == '\n' || in == '\r')
            {
                return response.toString();
            }

            response.append(in);
        }
    }

    /**
     * Cleanup. Closes any open handles.
     */
    public void close()
    {
        try
        {
            buff.close();
        }
        catch (IOException e)
        {
            // Ignore the exception, nothing we can do here
        }
    }
}
