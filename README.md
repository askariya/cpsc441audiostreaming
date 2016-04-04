# cpsc441audiostreaming
Repository for our CPSC 441 Audiostreaming Final Project

Steps for Set Up:
1. Move MTServer.java, Playlist.java and User.java into Server directory
2. Move MTClient.java and a copy of Playlist.java into Client directory


Steps for Compiling:
1. First terminal: javac MTServer.java
2. Second terminal: javac MTClient.java


Steps for Running:

1. First terminal: java MTServer <Port Number>

2. Second terminal: java MTClient <Server IP> <Port Number>

3. Log In as User -> username: user
			 	     password: pass

4. OR Log In as Admin -> username: admin
			 	     	 password: pass

5. Enter Commands


	--------------COMMAND LIST-----------------
	
	ADMIN SPECIFIC:
	-----------------------
	add_song <song name>
	remove_song <song name>
	create_user <username> <pass>
	remove_user <username>

	
	ALL USERS
	-----------------------
	play <song name>
	pause
	resume
	stop

	list 
	
	play_playlist <playlist name>
	add_playlist <playlist name>
	remove_playlist <playlist name>
	view_playlist <playlist name>

	add_to_playlist <song name> <playlist name>
	remove_from_playlist <song name> <playlist name>




