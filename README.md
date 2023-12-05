# DAI-PW3 Tower Defense

##Protocol

Section 1 - overview
The tower defense protocol defines a tower defense game where ennemies(emitter) attack a tower (server) which can be defended by serveral clients. The clients can be of 2 types, either healer or defender that will respectively add HP to the tower or make the ennemies attacks less effective by adding some defense. The ennemies can also be of different types. The game is finished when the tower is destroyed, you cannot win but you can try to hold as long as possible.

Section 2 - Transport protocol
The tower defense protocol uses the UDP protocol for each connection. The ennemies are using multicast connections (fire and forget) to send information to the server. The clients (allies) are communicating with the server with an unicast connection (request / response). 

- ENNEMIES - MULTICAST:


- ALLIES - UNICAST:


- TOWER - SERVER

Section 3 - Messages

- ENNEMIES - MULTICAST communication with server (tower):
ATTACK <type> <dmg>: used to attack the tower
  <type> type of ennemi
  <dmg>  amount of dmg made to the tower

- ALLIES - UNICAST communication with server (tower):

CLIENT:

PROTECT <type> <amount>: used to protect the tower
  <type> either heal of defend 
  <amount> nbr of hp to add OR 

GET_INFO : asks the server for its related information

SERVER: 

ANSWER <info>: send the information concerning its state to the client, triggered by PROTECT and GET_INFO
  <info> amount of hp/hp max, amount of defense/defense max

ERROR_CD <time_left>: answer if a client tries to protect when the cooldown is still up
  <time_left>: time left until the client can protect again

GAME_LOST

ERROR 404: the message sent by the client cannot be processed by the server
      