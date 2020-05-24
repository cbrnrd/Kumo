# `GUI`
This folder contains everything to do with the JavaFX graphical user interface.

I'll break down what each class/class type does and why it exists.

### `Controller`
This class mostly contains utility methods that other GUI classes rely on

### `Styler`
This class has utility functions in terms of applying style and keeping style and object alignment consistent throughout the project.

### `/Views/*`
Each class in this folder is what I call a View class. Each task/plugin/feature of Kumo that cannot be implemented under the hood or in the
main view has an associated window/popup that is used to interact with that feature. For example, if I wanted to view the clipboard, the `GetClipboardView` class would be initialized and the `#getGetClipboardView(Stage s)` would be called in order to get the actual VBox to show. 

TL;DR, each view class constructs mini windows that associate with features of Kumo.

### `/Components/*`
There aren't that many classes in here and many of them are self explanatory. The classes in this package are each
components of the main window. Eg. the `BottomBar` class creates the bottom bar (client info, etc) of the main window.

Note: There is definitely a simpler more object-oriented way to do this, but I did it this way first and didn't feel like re-doing/abstracting everything. 