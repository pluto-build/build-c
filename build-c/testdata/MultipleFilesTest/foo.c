#include <stdio.h>
#include "foo.h"
#include "foo1.h"
int foo(int x)    
{
	printf("foo called..\n");
	foo1(x);
    return  x+5;
}