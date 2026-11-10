# M6 Submission

## 1. Who decides, who shows

`MoveGenerator.status()` decides whether the current position is in progress, check, checkmate, or stalemate. This belongs in `MoveGenerator` because determining game status is a chess rule. `GameController.announceResult()` tells the player when the game ends, because the controller takes the model's result and tells the view what should be shown.

## 2. The dependency rule

`GameController` imports and uses classes from `edu.sfsu.csc413.chess.view` because it connects the model to a view. Classes in `engine`, such as `Game` and `MoveGenerator`, must not import the view package because the chess rules should not depend on how the game is displayed.

## 3. Test 2 on your engine

For M7, I expect to add or change the Swing view files and possibly `Main.java` so the new view can be selected or launched. I should not need to change `GameController` or anything in `engine`, because the controller already communicates through the `BoardView` interface and the engine has no dependency on a specific user interface.