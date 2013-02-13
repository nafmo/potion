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
 * Interface for text-based game input.
 * @author peter
 */
public interface GameInputDriver
{
    /**
     * Retrieve a line of input.
     * @param firstTry Set to true if this is the first attempt to get
     *   a string from the user this time. If false, the driver should
     *   prompt something like "I do not understand".
     * @return Input from the user, or null if an error occurred.
     */
    String getLine(boolean firstTry);
}
