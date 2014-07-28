
#include "server.h"

int main(){

    message_queue *q = init_message_queue(); 
    
    pthread_t serv_t;
    pthread_t switch_t;
    
    int rc = pthread_create( &serv_t, NULL, server, (void *)q);
    if(rc){
        printf("Error creating server\n");
    }
    rc = pthread_create( &switch_t, NULL, serv_switch, (void *)q);
    if(rc){
        printf("Error creating switch\n");
    }

    while(1){
        ;
    }
}

void *server(void *v){

    message_queue *q = (message_queue *)v;

    int sock_desc;

    struct sockaddr_in client, server;

    memset(&client, 0, sizeof(client));
    memset(&server, 0, sizeof(server));

    sock_desc = socket(AF_INET, SOCK_STREAM, 0);

    if(sock_desc == -1){
        printf("Could not create socket\n");
        exit(-1);
    }

    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons(PORT);

    bind(sock_desc, (struct sockaddr *)&server, sizeof(server));

    printf("Socket is bound!\n");

    listen(sock_desc, MAX_CONN);

    printf("Socket is listening!\n");

    while(1){
        int addrlen = sizeof(struct sockaddr_in);
        int new_sock;
        message m; 
        
        printf("Attempting to accept a message\n");
        new_sock = accept(sock_desc,(struct sockaddr *)&client, &addrlen);

        printf("Got a new connection\n");

        read(new_sock, &m, sizeof(m));

        enqueue(q, &m);

        close(new_sock);

    }

    close(sock_desc);
}
