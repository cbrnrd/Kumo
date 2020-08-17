# `server`
All classes in this folder make up the listening/command processing server. 

***

Classes:
### `Server`
This class starts all listening threads and ServerSockets.

### `ProcessCommands`
This class is important. This is the class that handles all input from each client. 

### `Listeners`
This class just contains a list of each listening server instance. 

### `KumoSettings`
This class contains all server-side settings for Kumo. There are also project wide constants in there (like the version).

### `ClientObject`
This class holds all information about a single client. The methods in this class are how you communicate with an
individual client. 

### `ClientHandler`
This class listens for new client connections and dispatches a new `ProcessCommands` to it. It also adds
the new client to the client list in the main view. 

### `data/CryptoUtils`
This class contains utility methods for cryptography stuff, like generating AES keys and encryption/decryption of data. 

### `data/FileUtils`
This class contains a couple of methods relating to files that are used throughout the project. 

### `data/PseudoBase`
This class handles reading and writing client and server setting files.

### `data/Repository`
This class is a unified list of current connected clients.