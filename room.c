#include <stdio.h>

#include "game.h"
#include "object.h"

void printroom(void)
{
	NR unsigned char i;
	NR BOOL seen;
	NR const char *s;
	NR unsigned char mapdata;
	NR signed char *object;

	seen = FALSE;
	mapdata = gamedata->map[gamedata->room - 1];

	/* Print room description */
	switch (gamedata->room)
	{
		case 1:  case 2:  case 3:
		case 7:  case 8:  case 9:
		case 13: case 14: case 15:
		case 30:
			s = "You are in a forest.\n";
			break;

		case 4: case 10: case 12:
		case 16: case 18:
			s = "You are standing on a big plain. "
			    "You hear the distant hum of motors.";
			break;

		case 5: case 11: case 17:
		case 23: case 24:
			s = "You are standing on an autobahn. "
			    "Several cars are passing passing by.";
			break;

		case 6:
			s = "You are standing on the sea shore.";
			break;

		case 19: case 25:
			s = "You are inside the scientist's lab.";
			break;

		case 20: case 21: case 26: case 27:
			s = "You are standing inside a big fortress.";
			break;

		case 22:
			s = "You are standing beside a big moat.";
			break;

		case 28: case 29:
			s = "You are standing on a dump. It stinks!";
			break;

		default:
			s = "Something is very wrong!";
			break;
	}

	putstring(s);

	/* Print room contents */
	object = gamedata->objects;
	for (i = 0; i < OBJECTS; i ++, object ++)
	{
		if (*object == gamedata->room)
		{
			if (!seen)
			{
				puts("You see:");
				seen = TRUE;
			}
			printobject(i);

			if (O_SCIENTIST == i && !gamedata->seenscientist)
			{
				puts("He says: \"What are you doing here?\"");
				gamedata->seenscientist = TRUE;
			}
		}
	}

	/* Print exits */
	puts("Available exits:");
	if ((unsigned char) (mapdata & EXIT_NORTH) != 0)
		putchar('N');
	if ((unsigned char) (mapdata & EXIT_SOUTH) != 0)
		putchar('S');
	if ((unsigned char) (mapdata & EXIT_EAST) != 0)
		putchar('E');
	if ((unsigned char) (mapdata & EXIT_WEST) != 0)
		putchar('W');
	putchar('\n');
}
