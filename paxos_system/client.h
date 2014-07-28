#ifndef CLIENT_H
#define CLIENT_H

#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <string.h>

#include "types.h"

int send_to_all(message *m);
int send_to(message *m, int);

int send_message(const char *, int, char *, int);


#endif
