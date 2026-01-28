# Dsa-Lab
 Advanced Text Editor
A Java-based CLI editor using a Doubly Linked List for  text operations and a Stack for batch undo/redo

How to Run

1.  Save the code as `AdvancedTextEditor.java`.
2. Compile: javac AdvancedTextEditor.java
3. Run: java AdvancedTextEditor

 Commands
Type  text: Inserts characters at the cursor.
      left/right: Moves cursor position.
      back: Deletes character before cursor.
      undo: Reverts last batch (grouped by 2-second intervals).
      redo: Re-applies last undone action.
      exit: Quits the program.

