#ifndef MSGQ_H
#define MSGQ_H

#include "types.h"
#include <pthread.h>

struct node{

    message msg;
    struct node *next;

};

typedef struct node node;


typedef struct {

    node *head;
    node *tail;

    pthread_mutex_t lock;

} message_queue;


message_queue *init_message_queue();

int dequeue(message_queue *q, message *m);

int enqueue(message_queue *q, message *m);

#endif
