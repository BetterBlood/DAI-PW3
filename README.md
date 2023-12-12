# DAI-PW3 Tower Defense

## Protocol

### Section 1 - Overview

The tower defense protocol defines a tower defense game where enemies (emitters) attack a tower (server) which can be 
defended by several allies (clients). The clients can be of 2 types, either healer or defender that will respectively 
add HP to the tower or make the enemies attacks less effective by adding some defense. The enemies can also be of 
different types. The game is finished when the tower is destroyed, you cannot win, but you can try to hold as long as 
possible.

### Section 2 - Transport protocol

The tower defense protocol uses the UDP protocol for each connection. The enemies are using multicast connections 
(fire and forget) to send information to the server. The clients (allies) are communicating with the server (tower) with an 
unicast connection (request / response).

The clients listens on the address `localhost` and port `1234`

The server listens for clients on address: `localhost` and port `1234` and for enemies on address between `239.0.0.0` 
and `239.0.0.4` and port `9876`

### Section 3 - Messages

#### emitters - MULTICAST communication with server (tower):

- `ATTACK <type> <dmg>`: used to attack the tower
  - `<type>` : type of enemy
  - `<dmg>`  : amount of dmg made to the tower

#### client - UNICAST communication with server (tower):

- `PROTECT <type> <amount>`: used to protect the tower
  - `<type>` : either HEAL of DEFEND 
  - `<amount>` nbr of hp to add to the tower or amount of defense to add to the tower

- `GET_INFO` : asks the server for its related information

#### server - UNICAST communication with client (ally):

- `ANSWER <info>`: send the information concerning its state to the client, triggered by PROTECT and GET_INFO
  - `<info>` amount of hp/hp max, amount of defense/defense max

- `ERROR_CD <time_left>`: answer if a client tries to protect when the cooldown is still up
  - `<time_left>`: time left until the client can protect again

- `GAME_LOST`

- `ERROR`: the message sent by the client cannot be processed by the server
      