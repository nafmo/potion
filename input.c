#include <stdio.h>
#include <string.h>

#include "input.h"
#include "game.h"

#define VERBS 25

static const struct verb_s
{
	const char *s;
	unsigned char verbnum;
} verbs[VERBS] =
{
	{ "n", V_N },
	{ "s", V_S },
	{ "e", V_E },
	{ "w", V_W },
	{ "i", V_I },
	{ "take", V_TAKE },
	{ "pick up", V_TAKE },
	{ "lift", V_LIFT },
	{ "read", V_READ },
	{ "talk to", V_TALKTO },
	{ "unlock", V_UNLOCK },
	{ "open", V_UNLOCK },
	{ "turn", V_TURN },
	{ "hit", V_HIT },
	{ "punch", V_HIT },
	{ "kick", V_HIT },
	{ "kill", V_KILL },
	{ "pour", V_POUR },
	{ "empty", V_POUR },
	{ "drink", V_DRINK },
	{ "throw", V_THROW },
	{ "look at", V_LOOK },
	{ "look", V_LOOK },
	{ "examine", V_LOOK },
	{ "drop", V_DROP },
};

unsigned char getinput(void)
{
	while (1)
	{
		NR unsigned char i;
		NR const struct verb_s *verb;

		fputs("Your turn? ", stdout);
#ifdef __C64__
		gets(input);
#else
		fgets(input, 1024, stdin);
		input[1023] = 0;
		input[strlen(input) - 1] = 0;
#endif
		putchar('\n');

		verb = verbs;
		for (i = VERBS; i; i --, verb ++)
		{
			NR size_t n;
			NR char *p;

			n = strlen(verb->s); /* Verb length */
			p = input + n; /* First character after verb */
			if (!strncmp(input, verb->s, n) &&
			    (!*p || ' ' == *p))
			{
				if (' ' == *p)
				{
					object_string = p + 1;
				}
				else
				{
					object_string = NULL;
				}
				return verb->verbnum;
			}
		}

		puts("I do not understand.");
	}
}
