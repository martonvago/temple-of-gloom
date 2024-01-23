Temple of Gloom
==========

This was a group project for my final year Software and Programming III module at Birkbeck.

My contributions were mainly:
- `dijkstra` package: implementation of Dijkstra's algorithm to find the shortest path in a weighted graph between a starting node and one or more target nodes. Can be used both in the explore and the escape phase.
- `escape` package: implementation of the algorithm used by the explorer to find their way out of the maze while trying to collect as much gold as possible.


Exercise
==========
This is a maze escape problem set in a cave with 2 stages:
### 1. Explore: you descend into a cave in search of an orb
- Maze layout unknown
- Available information: current tile, neighbouring tiles and their distance from the orb
- <u>Objective:</u> find the orb in as few steps as you can. The more steps you take, the lower your score for this stage.

### 2. Escape: you loot the cave as you flee to the surface
- The maze changes: the layout is different, the edges between the tiles are now weighted, some tiles now contain (different amounts of) gold
- There is also a time limit, measured in the maximum number of steps you can take. The shortest path to the exit is guaranteed to be walkable in this number of steps.
- Available information: current tile, complete graph of the maze, maximum number of steps you can take
- <u>Objective:</u> find the exit while collecting as much gold as you can on the way

Final score = exploration score * amount of gold collected while escaping

Coding Instructions
==========
- Your solution to the Explore stage should be contained in the `explore()` method of the `Explorer` class.
- Your solution to the Escape stage should be contained in the `escape()` method of the `Explorer` class.
- In both cases, return from the method when you're on the correct finishing tile for the stage.
- Do not modify code outside the `student` package.
- Do not use reflection.
- You should use full Javadoc comments in your classes and follow the coding guidelines set out earlier during the course.
- When running in headless mode, your code should not take longer than roughly 10 seconds to complete any single map.

Running the Program
==========
- `TXTmain`: runs the Explore and Escape stages without showing the animation in the GUI. Can run `n` number of times consecutively. Lists scores for the two stages, combined score, and average score over multiple runs. Run with: 
```
java main.TXTmain -n 100
```
- `GUImain`: runs the Explore and Escape stages with the animation.
- Both modes can run with a user-defined seed to generate a particular maze layout. Run with: 
```
java main.TXTmain -s 1234
```

Credits
=======
Thank you to Eric Perdew, Ryan Pindulic, and Ethan Cecchetti from the Department of Computer Science at Cornell for the basis of this coursework.