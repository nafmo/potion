#include <stdio.h>

#include "perform.h"
#include "input.h"
#include "action.h"
#include "object.h"

unsigned char perform(unsigned char verb)
{
	signed char object;

	/* Verbs that do not require an object */
	switch (verb)
	{
		case V_N:
		case V_S:
		case V_E:
		case V_W:
			return move(verb);

		case V_I:
			return inventory();
	}

	/* Other words require an object */
	object = object_string
		? objectfromword(object_string, verb != V_TAKE)
		: -1;

	if (-1 == object)
	{
		puts("I do not see that here.");
		return GAME_CONTINUES;
	}

	switch (verb)
	{
		case V_TAKE:
			return take((unsigned char) object);

		case V_LIFT:
			return lift((unsigned char) object);

		case V_READ:
			return read((unsigned char) object);

		case V_TALKTO:
			return talkto((unsigned char) object);

		case V_UNLOCK:
			return unlock((unsigned char) object);

		case V_TURN:
			return turn((unsigned char) object);

		case V_HIT:
			return hit((unsigned char) object);

		case V_KILL:
			return kill((unsigned char) object);

		case V_POUR:
			return pour((unsigned char) object);

		case V_DRINK:
			return drink((unsigned char) object);

		case V_THROW:
			return throw((unsigned char) object);

		case V_LOOK:
			return look((unsigned char) object);

		case V_DROP:
			return drop((unsigned char) object);
	}
}
