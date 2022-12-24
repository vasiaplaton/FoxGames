## connectToExistingRoom
* sends c + roomId + "\n"
* @param roomId room to connect
* @return connected or not to given server
## createNewRoom 
* sends h + F/G + "\n"
* force server to create new room
* @param side which player start on
* @return room id
## getBoardSize --Y
* sends b\n,
* wait to get b + X + \n, where X is number
## whichMoveNow --Y
* sends n\n,
* wait to get n + X + \n, where X is F/G
## getWinner --Y
* sends w\n
* wait to get w + X + \n, where X is F/G/N
## getMovesToAllCloseNeighbours --Y
* sends aX,Y\n
* wait to get a + list + \n, where list
## getCellState --Y
* sends sX,Y\n
* wait to get s + X + \n, where X is F/G/O/N
## move ***TODO
* sends mX,Y:A,B\n
* wait to get m + X + \n, where X is T/F
## getPossibleMoves ***TODO
* sends pX,Y\n
* wait to get p + list + \n, where list