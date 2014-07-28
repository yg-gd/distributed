
#include "client.h"


int main(int argc, char **argv){

    message m;
    m.type = DEFAULT;
    m.procid = atoi(argv[1]);

    int i = 0;
    while( 1 ){
        m.data.default_message.place_holder = i;

        printf("Calling send_message.\n");
        send_message(LOOP_IP_STRING, PORT, (char *)&m, sizeof(m));

        i++;

        sleep(10);
    }
}


int send_to_all(message *m){
    int i = 0;
    for(i = 0; i < num_procs; ++i){
        send_message(LOOP_IP_STRING,PORT+i, (char *)m, sizeof(message));
    }
}

int send_to(message *m, int who){

    send_message(LOOP_IP_STRING,PORT+who, (char *)m, sizeof(message));
}


//Code from an earlier class. 439H. 

int send_message(const char *ip, int port, char *msg, int size){
    int clientsock;
    struct sockaddr_in server;
    printf("Creating socket!\n");
    if((clientsock = socket(PF_INET, SOCK_STREAM, 0)) < 0){
        printf("Creating the socket failed\n");
        return 0;
    }
    int port_num = port;
    memset(&server, 0, sizeof(server));
    server.sin_family = AF_INET;
    //server.sin_addr.s_addr = htonl(ip); 
    server.sin_port = htons(port_num);

    inet_pton(AF_INET, ip, &server.sin_addr);

    printf("Trying to connect!\n");


    if(connect(clientsock, (struct sockaddr *) &server, sizeof(server)) < 0){
        printf("Client: could not connect to IP: %s PORT: %d\n", ip, port);
        close(clientsock);
        return 0; 
    }
    printf("Connect worked!\n");

    printf("Writing to socket!\n");
    
    write(clientsock, msg, size);

    return clientsock;
}
