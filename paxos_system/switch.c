
#include "switch.h"


void *serv_switch(void *v){

    message_queue *q = (message_queue *)v;

    message m;

    while( 1 ){

        while(dequeue(q, &m)){
            sleep(1);
        }

        message_type type = m.type;

        switch(type) {

            case DEFAULT:
                process_default(&m);
                break;
            case PREPARE:
                process_prepare(&m);
                break;
            case ACK_PREPARE:
                process_ack_prepare(&m);
                break;
            case NACK_PREPARE:
                break;
            case PROPOSE:
                process_propose(&m);
                break;
            default:
               break; 
        }
     
    }

}
