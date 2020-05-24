## Implementing a new feature

Each of the clientside features have a part in the `IPContextMenu` class in `GUI/Components`.
In short you:
* Create a new `MenuItem`
* Add whatever you want the feature to do in the `.setOnAction(event -> ...)` method.
* Add your menu item object to this line in the file: `mi1.getItems().addAll(sb1, sb2, si4, si5, si6, si7, si10, si8, si9, clip, pwdRecovery, misc, update);`

In the menu item action, you'll have to show a view for that feature. So you need to create one. Views are just `VBox`s, so construct them however you like.

Obviously, you would need to implement the feature clientside as well. You can do this however you want.

Here is the basic communication protocol the client and server use. *Almost all data is encrypted with the generated AES key*.

1. Server sends some command to the client, like "SYINFO".
2. Client will echo that command back and then write whatever data you want it to to the wire.
3. The data will be accessible from the `readFromDis(DataInputStream dis)` method in `ProcessCommands`.
There are plenty of examples in `ProcessCommands`.

**I HIGHLY recommend you read through the code, starting at `IPContextMenu` and following the logic.
This will help you immensely in understanding how the flow of data works.**