#ifndef SERV_H
#define SERV_H

#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "switch.h"
#include "msg_queue.h"

int main();

void *server(void *v);

#endif
