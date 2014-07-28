#ifndef TYPES_H
#define TYPES_H

typedef enum {
    DEFAULT,
    PREPARE,
    DECIDED_VALUE,
    CONGRATS_LEADER,
    ACK_PREPARE,
    NACK_PREPARE,
    PROPOSE_REQ,
    PROPOSE,
    ACCEPT,
    DENY,
    OTHER
} message_type;

typedef struct {
    message_type type;
    int procid;

    union {
        struct{
            int place_holder;
        } default_message;
        struct{
            int prepare_num;
        } prepare_message;
        struct{
            char buf[MAX_CHAR_LEN];
            int slot_num;
        } decided_value;
        struct{
            int next_free_slot;
            int prepare_num;
        } ack_prepare;
        struct{
            char buf[MAX_CHAR_LEN];
            int psn;
            int slot;
        } propose;
        struct{
            char buf[MAX_CHAR_LEN];
        } propose_req;
        struct{
            int slot;
            int psn;
        } accept;
    }data;

} message;


#define MAX_CHAR_LEN 512

#define PORT 4562
#define NUM_PROCS 3
#define MAX_CONN 30
#define BUF_SIZE 512

#define LOOP_IP_STRING "127.0.0.1"

#endif
