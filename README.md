# FaceSpace
## CS 1555 final project
### Zach Ward, Gangzheng Tong, Xiaokai Wang


Milestore 1

We have implemented the first 3 methods, createUser, initiateFriendship, establishFriendship and use them in the driver.

For createUser, we simply insert an entry to the userTable with email(Primary key), firstName, lastName and birthday. 

For initiateFriendship, we insert an entry to the frindship table, with person1(the sender email) and person2(the receiver email) and the time the request sent specified. At this point, we leave the time_esatbleshed null and update only if person2 conform the friend request (by sending a friend request back to person 1). 

For establishFriendship, we update the time_esatbleshed of the corresponding entry to the time when person1 and person2 build a mutual friendship. 


How to test:
1. You can create a user by register a new one. 
2. You can initiateFriendship after you login and choose "Send Friend Request" in the Menu, and input an email(person 2) to which you want to send a request. 
3. For establishFriendship, login in as person2 and send a request back to person 1. 


