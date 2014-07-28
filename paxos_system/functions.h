#ifndef FUNC_H
#define FUNC_H

#include "types.h"

int me;
int leader = 0;
int verified;
int num_procs;
int maj_set;

int prepare = -1;
int prepare_maj = 0;

int last_output_slot;
int next_free_slot = 1;

typedef enum {
    PENDING,
    DECIDED,
    ALL_SEEN
} paxos_status;

struct paxos_node{
    char buf[MAX_CHAR_LEN];
    int slot_num;
    int cur_proposal;
    int accept_maj;
    int accpeted;
    int number_known;
    paxos_status status;
    
    struct paxos_node *next;
};

typedef struct paxos_node paxos_node;

paxos_node *paxos_head = NULL;

struct req_node{
    char buf[MAX_CHAR_LEN];

    struct req_node *next;
}

typedef struct req_node req_node;

req_node *req_head = NULL;

void process_default(message*);

void send_prepare();

void process_prepare(message *m);

void propose_all();

void propose(char *, int, int, int);

int paxos_is_free(int);

void process_propose(message *m);


//TODO
/*

0. Values already accepted by an acceptor
1. Accepting values.
2. Learning values.
3. Leader timeout/election
4. Client chat window
*/










#endif
