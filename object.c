#include <stdio.h>
#include <string.h>

#include "game.h"
#include "object.h"

void printobject(unsigned char objnum)
{
	const char *s;

	switch (objnum)
	{
		case O_STONE_PILL:
		case O_STONE_1:
		case O_STONE_2:
		case O_STONE_3:
		case O_STONE_MISSION:
		case O_STONE_5:
		case O_STONE_6:
		case O_STONE_7:
		case O_STONE_8:
			s = "A stone.";
			break;

		case O_PILL:
			s = "A pill.";
			break;

		case O_SQUIRREL:
			s = "A squirrel.";
			break;

		case O_COIN:
			s = "A gold coin.";
			break;

		case O_COTTAGE:
			s = "A cottage.";
			break;

		case O_SWITCH:
			s = "A switch.";
			break;

		case O_HUBCAP:
			s = "A hub cap.";
			break;

		case O_KEY:
			s = "A key.";
			break;

		case O_SCIENTIST:
			s = "A scientist.";
			break;

		case O_ELIXIR:
			s = "A potion.";
			break;

		case O_BOOK:
			s = "A book.";
			break;

		case O_DOOR:
			s = "A door.";
			break;
	}

	puts(s);
}

BOOL findobject(unsigned char object, BOOL checkinventory)
{
	NR unsigned char i;
	NR signed char *inventory;

	/* Is the object in the room? */
	if (gamedata->objects[object] == gamedata->room)
		return TRUE;	

	/* Is the object in the inventory? */
	if (checkinventory)
	{
		inventory = gamedata->inventory;
		for (i = INVENTORY; i; i --, inventory ++)
			if (*inventory == object)
				return TRUE;
	}

	return FALSE;
}

#define NOUNS 12

static const char *nouns[NOUNS] =
{
	"stone", "pill", "squirrel", "coin",
	"cottage", "door", "switch", "hub cap",
	"key", "scientist", "potion", "book"
};

static const signed char objnums[NOUNS] =
{
	-1, O_PILL, O_SQUIRREL, O_COIN,
	O_COTTAGE, O_DOOR, O_SWITCH, O_HUBCAP,
	O_KEY, O_SCIENTIST, O_ELIXIR, O_BOOK
};

signed char objectfromword(const char *s, BOOL checkinventory)
{
	NR signed char i, j;
	NR const char **noun;
	NR const signed char *objnum;

	j = -1;

	noun = nouns;
	for (i = 0; i < NOUNS; i ++, noun ++)
	{
		if (!strcmp(s, *noun))
		{
			j = i;
			break;
		}
	}

	if (-1 == j) return -1;

	if (!j)
	{
		/* Stones (obj 0-8) */
		for (i = 0; i < 9; i ++)
		{
			if (findobject(i, checkinventory))
				return i;
		}
	}

	objnum = &objnums[j];
	if (findobject(*objnum, checkinventory))
		return *objnum;

	return -1;
}

BOOL addinventory(unsigned char object)
{
	NR unsigned char i;
	NR signed char *inventory;

	inventory = gamedata->inventory;

	for (i = INVENTORY; i; i --, inventory ++)
		switch (*inventory)
		{
			case -1:
				*inventory = object;
				gamedata->objects[object] = -1;
				return TRUE;
		}

	puts("It is too heavy.");
	return FALSE;
}

BOOL dropinventory(unsigned char object)
{
	NR unsigned char i;
	NR signed char *inventory;

	inventory = gamedata->inventory;

	for (i = INVENTORY; i; i --, inventory ++)
		if ((signed char) object == *inventory)
		{
			*inventory = -1;
			gamedata->objects[object] = gamedata->room;
			return TRUE;
		}

	puts("You are not carrying that.");
	return FALSE;
}

BOOL movable(unsigned char object)
{
	switch (object)
	{
		case O_PILL:
		case O_COIN:
		case O_HUBCAP:
		case O_KEY:
		case O_SCIENTIST:
		case O_ELIXIR:
		case O_BOOK:
			return TRUE;

		default:
			return FALSE;
	}
}
