#include <stdio.h>

#include "game.h"
#include "object.h"
#include "output.h"

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
			s = "You are in a forest. There are trees in every direction "
			    "as far as the eye can see.";
			break;

		case 4: case 10:
		case 16: case 18:
			s = "You are standing on a big plain. "
			    "You hear the distant hum of motors from somewhere.";
			break;

		case 12:
			s = "You are standing on a big plain, next to a recycling "
			    "station.";
			break;

		case 5: case 11: case 17:
		case 23: case 24:
			switch (gamedata->roadvisit)
			{
			case 0:
			case 1:
				++ gamedata->roadvisit;
			case 3:
				s = "You are standing on an autobahn. "
				    "Several cars are passing passing by.";
				break;

			case 2:
				s = "You are standing on an autobahn. "
				    "Suddenly you see a skateboarder pass by, grabbing a "
				    "bottle of Coca Cola from a nearby car.";
				++ gamedata->roadvisit;
				gamedata->objects[O_COKE] = gamedata->room;
				break;
			}
			break;

		case 6:
			s = "You are standing on the sea shore. The sea stretches out "
			    "as far as the eye can see to the north and to the east.";
			break;

		case 19: case 25:
			s = "You are inside the scientist's lab. You notice that he "
			    "doesn't seem to put much energy in keeping it tidy.";
			break;

		case 20: case 21: case 26: case 27:
			s = "You are standing inside a big fortress. There are some "
			    "spider webs in the corners, and a layer of dust on "
			    "pretty much everything.";
			break;

		case 22:
			if (!(unsigned char) (mapdata & EXIT_WEST))
			{
				s = "You are standing beside a big moat. There is no way "
				    "to pass over it without lowering the drawbridge.";
			}
			else
			{
				s = "You are standing beside a drawbridge leading to "
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

	putstring(s);

	/* Print room contents */
	object = gamedata->objects;
	for (i = 0; i < OBJECTS; ++ i, ++ object)
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
