Thank you for purchasing Kumo!

Kumo is a modern-looking cross-platform remote administration tool written in Java.

There are two versions of Kumo included in this package: one meant for Java < 9 and one meant for Java 9.
These two versions are identical, except for the GUI library it uses.

========================
Known Issues:
    - Weird GUI alighment or sizing
        - On Windows, this is likely caused by system-wide scaling. To turn it off, go to `Settings > System > Display > Scale and Layout` and change it to 100%.
        - On other operating systems, turn off all scaling programs.
    - Clients are not responding to commands from server.
        - This is likely due to having a different encryption key then the current one on the server.
          Run the server from the command line to see the stacktrace and send it to KumoRAT@protonmail.com.

If you find any other issues, send the stacktrace and operating system to KumoRAT@protonmail.com and we will work with you to do our best to fix it.

========================

Plugins:
Kumo has the ability to run 'plugins.' Plugins are simply runnable JAR files (with a main() method).
There are no plugins included, and the output (stdout) will not be sent back to the server.

========================

Persistence:
Windows persistence is done via the registry.
Linux persistence is done via crontab.
Persistence on Mac is not supported in version 2.0.

========================

Other things that are good to know:
- The key logger is windows only. It will not run on Linux or Mac machines (yet)
- Domain names can be used as the host (including no-ip)
- You can run Lazagne from the command line if you want to do some more password recovery on other operating systems.
  You do not have to use the built in chrome password recovery tool.

========================

Legal:
Copyright 2019 Clova

This software is, under no circumstances, to be:
* Cracked
* Redistributed in any original or modified form
* Decompiled or reverse engineered.

This software product is the intellectual property of
its original creator (Clova) under Copyright Law.

This software is meant for educational
and/or research purposes. No blackhat or
illegal activity is allowed. Be sure you have
explicit permission from the client computers owner.

The original creator is in no way responsible
for any actions the end-user (you)
performs with this product.

========================

Other:
If you have any questions, you can email KumoRAT@protonmail.com or PM Clova on HackForums.net.