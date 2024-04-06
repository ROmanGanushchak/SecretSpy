This is a board game secret spy, it is still in development so not all parts of the user interface are done. (Sorry for not using Slovak, it would be painful to read. Thanks for understanding.)

---------Beginning------------------------
The voting system is already made, but still requires some work with its initialization and BotAnswers. 
Initially, the Class Voting from Model/Voting is responsible for collecting the votes. 
First, the Game creates a voting and then asks a controller to fulfill it, then the voting component is sent to each player, where they are voting. 
The Voting class automatically takes care of voting duties, like storing the votes and determining the result, and through the observer, inform the creator about the result.

-----------ProgramRun---------------------
First, after running the program, the player can check its secret role, afterward press in the top right corner an exit button, to start the game. 
To start voting use a command line in the activated program in the center-upper part and write command "chooseChancellor", then press the button execute. 
Then open a terminal and write a number 1. Afterwards, the vote component will appear on the screen. You can vote for or against a chancellor election. 
Then the result menu will appear where all votes are seen (for a while players' names are just numbers), press the big black circle to exit. 
The further functionality is supported in model but not in graphical interface. 
If more tests are needed then the command field can be used, more detailed commands can be found in GameController/GameController.executeCommand

-----------WhatWasImplemented-------------
The required extends can be found in Model/ChangeableRole that has a subclass Political which has subclasses President and Chancellor. 
Moreover, in the PlayerGameManager with Human and Bot players, and more recent from test_ui/Components/Component and test_ui/PopupLayerManager.PopupComponent

The MVC model was used to separate logic from the interface. The interface where used in Observers/ActionObservers.

The common pattern that project consists of is ObserverPattern, there is the whole folder Model/Observers that takes provides a flexible observers and array of observers classes to work with, 
the custom observers are used both in logic (For example in Model/ChangeableRole/Political) and in interface. 
Another one is proxy pattern used to give a provide a secure communication between GameController and Game. 
The strategy pattern in Model/Cards/CardShuffle to separate main Shuffle logic from CardArray to use it in Game separately.

The project consists of inner classes for instance Model/Cards/CardsArray/Card, and in test_ui/PopupLayerManager.

The static polymorphism can be found in test_ui/Components/Component/Component.java, with method initialize being overloaded multiple times, and dynamic in test_ui/PopupLayerManager.
PopupComponent, where the setComponent method was overloaded to ensure that at the start of the program all popup components are hidden and deactivated.
