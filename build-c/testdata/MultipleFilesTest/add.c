#include <stdio.h>
#include "foo.h"
 
int main()
{
   int n;
 
   printf("Enter an integer\n");
   scanf("%d", &n);
   foo(n);
   foo1(1);
   scanf("%d", &n);
   if (n%2 == 0)
      printf("Even\n");
   else
      printf("Odd\n");
 
   return 0;
}

