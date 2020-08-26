# Kumo

Kumo is a lightweight and portable remote administrative tool written in Java.
It can be used on Windows, macOS, or Linux as long as they have the JRE installed.

I was selling this software for a while but it got cracked and I'm not maintaining it anymore, so here's the source :).

## Features

* AES encrypted C2 communication
* Send commands to client
* Cross platform (more or less)
* Clean, simple, and modern UI
* Easy client building process
* Customizable server and client settings
* Persistence
* System information
* Clipboard control
* Visit website
* Show messagebox
* Windows keylogger
* Plugin support

## Compilation instructions

Windows:
```
.\gradlew.bat fatJar
```
This will produce a server jar in `build/`.

Linux:
Same as above but `./gradlew fatJar`. Produces the same output jar.


## Legal
Kumo is not to be used without permission or ownership of the systems Kumo is installed on. Kumo is a remote administrative tool to aid in the control and management of systems alongside IT setups. Any other use is prohibited. Kumo is not liable for system damage or unauthorised use.

## Notes
* This is a very very very very hard fork of Ghosts/Maus, so props to Ghosts for a great starting point.
* Originally this software was licensed, but it's been commented out (not fully removed) in `kumo.Kumo`.

## TODO
* Create basic obfuscator of final jar so the client class name isn't always `Client.class`
* Organize the client context menu and add icons to those sections.

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/cbrnrd/Kumo. This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

<p align="center">
  <img height="100" width="300" src="https://i.imgur.com/obHmDnX.png">
</p>