
#include "functions.h"
#include <stdio.h>
#include <stdlib.c>

void process_default(message *m){

    printf("Heard from process %d with num: %d\n", m->procid, m->data.default_message.place_holder);
}

void send_prepare(){
    message m;
    m.type = PREPARE;
    m.procid = me;
    
    prepare_maj = 0;
    verified = 0;
    prepare = prepare + (me) - (prepare % num_procs);
    m.data.prepare_message.prepare_num = prepare;
    
    send_to_all(&m); 
}

void process_prepare(message *m){

    message msg;
    msg.procid = me;
    
    if(m->data.prepare_message.prepare_num > prepare){
        
        prepare = m->data.prepare_message.prepare_num;
        leader = m->procid;
        verified = 0;

        msg.type = ACK_PREPARE;
        msg.data.ack_prepare.next_free_slot = next_free_slot; 
        msg.data.ack_prepare.prepare_num = prepare; 
    } else {
        
        msg.type = NACK_PREPARE;

    }

    send_to(&msg,m->procid);

}

void process_ack_prepare(message *m){
    
    if(m->data.ack_prepare.prepare_num == prepare){
        prepare_maj++;

        if(prepare_maj >= maj_set){
            verified = 1;
            propose_all();
        }
    }

}

void propose_all(){

    paxos_node *cur = paxos_head;
    while(cur != NULL){
        if(cur->status == PENDING){
            cur->accept_maj = 0;
            propose(cur->buf, MAX_CHAR_LEN, cur->slot_num, prepare);
        }
        cur = cur->next;
    }

    int i = last_output_slot;
    while(i < next_free_slot){
        if(paxos_is_free(i)){
            propose("", 0, i, prepare);
        }
        ++i;
    }

    while(req_head != NULL){
        propose(req_head->buf, MAX_CHAR_LEN, next_free_slot, prepare);

        req_node *temp = req_head->next;
        free(req_head);
        req_head = temp;

        next_free_slot++;
    }
}

void propose(char *str, int n, int slot, int psn){

    //probably need to create an entry in the paxos list for this.}

   message m;
   m.type = PROPOSE;
   
   memcpy(m.data.propose.buf, str, n);
   m.data.propose.psn = psn; 
   m.data.propose.slot = slot;
   
   send_to_all(&m); 

}

int paxos_is_free(int i){
    paxos_node *cur = paxos_head;
    while(cur != NULL){
        if(cur->slot_num == i){
            return 0;
        }
    }
    return 1;
}

int process_propose(message *m){

    message msg;
    msg.procid = me;

    paxos_node *pax = find_paxos_node(m->data.propose.slot);

    if(m->data.propose.psn < prepare){
        //do not accept.
    } else {
        if(pax != NULL){
            if(pax->accepted){
                if(strcmp(pax->buf, m->data.propose.buf, MAX_CHAR_LEN) == 0){
                    pax->cur_proposal = m->data.propose.psn;
                    msg.type = ACCEPT;
                    msg.psn = m->data.propose.psn;
                    msg.slot = m->data.propose.slot;
                } else {
                    //proposed for an accepted but the values were not the same
                }
            } else {
                memcpy(pax->buf, m->data.propose.buf, MAX_CHAR_LEN);
                pax->accepted = 1;
                pax->cur_proposal = m->data.propose.psn;

                msg.type = ACCEPT;
                msg.psn = m->data.propose.psn;
                msg.slot = m->data.propose.slot;
            }
        } else {
            pax = (paxos_node *)malloc(sizeof(paxos_node));
            memcpy(pax->buf, m->data.propose.buf, MAX_CHAR_LEN);
            pax->slot_num = m->data.propose.slot;
            pax->cur_proposal = m->data.propose.psn;
            pax->accepted = 1;
            pax->next = paxos_head;
            paxos_head = pax;

            msg.type = ACCEPT;
            msg.psn = m->data.propose.psn;
            msg.slot = m->data.propose.slot;
        }
    }

    send_to(&msg, m->procid);
}

paxos_node *find_paxos_node(int slot){
    paxos_node *cur = paxos_head;
    while(cur != NULL){
        if(cur->slot_num == slot){
            return cur;
        }
    }
    return NULL;
}










