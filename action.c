#include <stdio.h>

#include "action.h"
#include "object.h"
#include "perform.h"
#include "input.h"
#include "game.h"

void cannotdothat(void)
{
	puts("You cannot do that!");
}

void nothing(void)
{
	puts("Nothing.");
}

unsigned char move(unsigned char verb)
{
	unsigned char bitmap;
	signed char offset;

	/* Seen scientist? */
	if (gamedata->seenscientist)
	{
		puts("The scientist captures you.");
		return GAME_LOST;
	}

	/* FIXME: Crossing autobahn? */

	/* Check which movement is requested */
	switch (verb)
	{
		case V_N: bitmap = EXIT_NORTH; offset = -6; break;
		case V_S: bitmap = EXIT_SOUTH; offset =  6; break;
		case V_E: bitmap = EXIT_EAST;  offset =  1; break;
		case V_W: bitmap = EXIT_WEST;  offset = -1; break;
	}

	/* Check if movement is allowed in that direction */
	if (gamedata->map[gamedata->room - 1] & bitmap)
	{
		gamedata->room += offset;
	}
	else
	{
		puts("You cannot go that way.");
	}

	return GAME_CONTINUES;
}

unsigned char inventory(void)
{
	NR unsigned char i;
	NR BOOL hasanything;
	NR signed char *inventory;

	hasanything = FALSE;
	inventory = gamedata->inventory;

	puts("You are carrying:");
	for (i = INVENTORY; i; i --, inventory ++)
		if (*inventory != -1)
		{
			printobject(*inventory);
			hasanything = TRUE;
		}

	if (!hasanything)
		nothing();

	return GAME_CONTINUES;
}

unsigned char take(unsigned char object)
{
	switch (object)
	{
		case O_PILL:
			/* Something special happens when we take the pill */
			gamedata->pilleaten = TRUE;
			gamedata->objects[O_PILL] = -1;
			puts("You understand.");
			break;

		case O_STONE_PILL:
			/* Finds pill, if it's not taken already */
			if (!gamedata->pilleaten && -1 == gamedata->objects[O_PILL])
			{
				puts("You found a small pill beneath it.");
				gamedata->objects[O_PILL] = gamedata->room;
				break;
			}
			/* Else fall through */

		default:
			if (movable(object))
			{
				addinventory(object);
			}
			else
			{
				cannotdothat();
			}
			break;
	}
	return GAME_CONTINUES;
}

unsigned char lift(unsigned char object)
{
	switch (object)
	{
		case O_STONE_PILL:
			if (!gamedata->pilleaten)
			{
				take(object);
				break;
			}
			/* else fall through */

		default:
			nothing();
			break;
	}

	return GAME_CONTINUES;
}

unsigned char read(unsigned char object)
{
	switch (object)
	{
		case O_STONE_MISSION:
			puts("It says:\n"
			     "\"Save us from the evil scientist.\n"
			     " Mix his evil drug with salt water.\n"
			     " That is your mission.\"");
			break;

		default:
			nothing();
	}

	return GAME_CONTINUES;
}

unsigned char talkto(unsigned char object)
{
	switch (object)
	{
		case O_SCIENTIST:
			puts("The scientist says:\n"
			     "\"You will die with the rest!\"\n"
			     "and laughs frantically.\n");
			break;

		case O_SQUIRREL:
			if (gamedata->pilleaten)
			{
				puts("The squirrel says:\n"
				     "\"You can now exit the forest\"");
				gamedata->map[3 - 1] |= EXIT_EAST;
				break;
			}
			/* else fall through */

		default:			
			cannotdothat();
	}

	return GAME_CONTINUES;
}

unsigned char unlock(unsigned char object)
{
	/* Check if we have the key */
	NR BOOL havekey;
	NR unsigned char i;

	havekey = FALSE;

	for (i = 0; i < INVENTORY; i ++)
		if (O_KEY == gamedata->inventory[i])
			havekey = TRUE;

	if (havekey)
	{
		switch (object)
		{
			case O_COTTAGE:
				puts("You find a switch inside.");
				gamedata->objects[O_SWITCH] = gamedata->room;
				return GAME_CONTINUES;
			
			case O_DOOR:
				puts("It opens revealing a laboratory.");
				gamedata->map[20 - 1] |= EXIT_WEST;
				return GAME_CONTINUES;
		}
	}

	cannotdothat();
	return GAME_CONTINUES;
}

unsigned char turn(unsigned char object)
{
	switch (object)
	{
		case O_SWITCH:
			puts("Thud.");
			gamedata->map[22 - 1] |= EXIT_WEST;
			break;

		default:
			cannotdothat();
			break;
	}

	return GAME_CONTINUES;
}

unsigned char hit(unsigned char object)
{
	switch (object)
	{
		case O_SQUIRREL:
			/* If we hit the squirrel, we kill it. */
			puts("You kill the squirrel.");
			return GAME_LOST;

		case O_SCIENTIST:
			/* Clear the seen scientist flag */
			gamedata->seenscientist = FALSE;
			/* Remove scientist from game */
			gamedata->objects[O_SCIENTIST] = -1;

			puts("He will not bother you again.");
			break;

		default:
			cannotdothat();
			break;
	}

	return GAME_CONTINUES;
}

unsigned char kill(unsigned char object)
{
	puts("Please. This is a family game.");
	return GAME_CONTINUES;
}

unsigned char pour(unsigned char object)
{
	switch (object)
	{
		case O_ELIXIR:
			/* The game object is to mix the potion with salt
			 * water. This can only be done by pouring it into
			 * the sea.
			 */
			 switch (gamedata->room)
			 {
			 	case 6:
					puts("The potion has been neutralised.");
					return GAME_WON;

				default:
					puts("The potion poisons the land.");
					return GAME_LOST;
			}

		default:
			cannotdothat();
			return GAME_CONTINUES;
	}
}

unsigned char drink(unsigned char object)
{
	switch (object)
	{
		case O_ELIXIR:
			/* Can only drink the elixir, and we lose if we do it */
			puts("You die instantly.");
			return GAME_LOST;

		default:
			cannotdothat();
			return GAME_CONTINUES;
	}
}

unsigned char throw(unsigned char object)
{
	if (object <= O_LAST_STONE && 9 == gamedata->room)
	{
		return hit(O_SQUIRREL);
	}
	else
	{
		return drop(object);
	}
}

unsigned char look(unsigned char object)
{
	switch (object)
	{
		case O_STONE_MISSION:
			return read(object);

		case O_STONE_PILL:
			if (!gamedata->pilleaten && -1 == gamedata->objects[O_PILL])
				return take(object);
			/* Else fall through */

		default:
			return GAME_CONTINUES;
	}
}

unsigned char drop(unsigned char object)
{
	dropinventory(object);
	return GAME_CONTINUES;
}

