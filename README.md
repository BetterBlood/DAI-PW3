# DAI-PW3 Tower Defense

## Introduction

This is an application that simulates a tower defense game. Where enemies are multicasting attacks and
a server is listening to the attacks. The server is also listening to unicast messages from the client and 
is responding to them. 

## How to build the application

No need to build the docker image as it is [saved](https://github.com/BetterBlood/DAI-PW3/pkgs/container/tower-defense) 
on the GitHub Container Registry.

If you have done some modifications on the code, build the docker image run the following command:

```
docker build -t ghcr.io/betterblood/tower-defense:latest .
```

## How to run the application

In order to run the server and emitters run the following command:

```
docker compose up tower warrior archer
```

And to start a new client run the following command (you can run multiple clients):

```
docker compose run ally
```

[//]: # (TODO: add examples)

## Protocol

### Section 1 - Overview

The tower defense protocol defines a tower defense game where enemies (emitters) attack a tower (server) which can be 
defended by several allies (clients). The clients can either heal or defend which will respectively add HP to the tower
or make the enemies attacks less effective by adding some defense. The enemies can have different attacks strength and 
cooldown between attacks. The game is finished when the tower is destroyed, you cannot win, but you can try to hold as 
long as possible.

### Section 2 - Transport protocol

The tower defense protocol uses the UDP protocol for each connection. The enemies are using multicast connections 
(fire and forget) to send information to the server. The clients (allies) are communicating with the server (tower) with an 
unicast connection (request / response).

The clients send its requests on the address `localhost` and port `1234`

The server listens for clients on address: `localhost` and port `1234` and 
for enemies on the multicast address `239.1.1.1` and port `9876`

### Section 3 - Messages

#### emitters - MULTICAST communication with server (tower):

- `ATTACK <type> <dmg>`: used to attack the tower
  - `<type>` : name of enemy
  - `<dmg>`  : amount of dmg made to the tower

#### client - UNICAST communication with server (tower):

- `PROTECT <type> <amount>`: used to protect the tower
  - `<type>` : either HEAL of DEFEND 
  - `<amount>` nbr of hp to add to the tower or amount of defense to add to the tower

- `GET_INFO` : asks the server for its related information

#### server - UNICAST communication with client (ally):

- `ANSWER <info>`: send the information concerning its state to the client, triggered by PROTECT and GET_INFO
  - `<info>` amount of hp/hp max, amount of defense/defense max

- `GAME_LOST` : sent when the tower is destroyed

- `ERROR`: the message sent by the client cannot be processed by the server


### Section 4 - Examples

![diagram1](diagrams/Diagram1.png "Diagram")

![diagram2](diagrams/Diagram2.png "Diagram")

![diagram3](diagrams/Diagram3.png "Diagram")