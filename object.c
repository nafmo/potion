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

		case O_COKE:
			s = "Empty Coke bottle.";
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

#define NOUNS 22

static struct objinfo_s
{
	const char *noun;
	signed char objnum;
} objinfo[NOUNS] =
{
	{ "stone",			-1 },
	{ "rock",			-1 },
	{ "pill",			O_PILL },
	{ "squirrel",		O_SQUIRREL },
	{ "coin",			O_COIN },
	{ "gold coin",		O_COIN },
	{ "money",			O_COIN },
	{ "cottage",		O_COTTAGE },
	{ "house",			O_COTTAGE },
	{ "door",			O_DOOR },
	{ "switch",			O_SWITCH },
	{ "hub cap",		O_HUBCAP },
	{ "key",			O_KEY },
	{ "door key",		O_KEY },
	{ "scientist",		O_SCIENTIST },
	{ "madman",			O_SCIENTIST },
	{ "potion",			O_ELIXIR },
	{ "book",			O_BOOK },
	{ "bottle",			O_COKE },
	{ "empty bottle",	O_COKE },
	{ "coke",			O_COKE },
	{ "coca cola",		O_COKE }
};

signed char objectfromword(const char *s, BOOL checkinventory)
{
	NR signed char i, j;
	NR struct objinfo_s *obj;
	NR signed char objnum;

	j = -1;

	obj = objinfo;
	for (i = 0; i < NOUNS; i ++, obj ++)
	{
		if (!strcmp(s, obj->noun))
		{
			j = i;
			break;
		}
	}

	switch (j)
	{
	case -1:
		/* Not found in word list */
		return -1;

	case 0:
	case 1:
		/* Stones (obj 0-8) */
		for (i = 0; i < 9; i ++)
		{
			if (findobject(i, checkinventory))
				return i;
		}
		break;

	default:
		objnum = obj->objnum;
		if (findobject(objnum, checkinventory))
			return objnum;
		break;
	}

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
		case O_COKE:
			return TRUE;

		default:
			return FALSE;
	}
}
