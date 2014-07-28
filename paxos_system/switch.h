#ifndef SERV_SWITCH_H
#define SERV_SWITCH_H

#include "error.h"
#include "functions.h"
#include "types.h"
#include "msg_queue.h"


void *serv_switch(void *v);
int send_error(int, errors error);

#endif
