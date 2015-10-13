# Path optimization of 3D printer
### Usage of hybrid algorithms in traveling salesman problem

There is given layer to print which is described as an array of binary points, which  has a size of `a * b`. Value of each point can be either 1, which means that it is a point to print, or 0 otherwise. On each layer the printing tool has to visit exactly once each point which should be printed and then go back to the starting position. The printing tool can also make moves without printing.

In tests where made an assumption that printing tool is using to move two perpendicular axes. The cost of the path can be considered in three different approaches: minimum distance, minimum printing time and minimum energy.

The problem to solve is to find such an order of visiting points, that the cost of printing and time needed for calculation is minimized.

Algorithms used to solve this problem:

1. Edge Follower
2. Greedy
3. Two Opt
4. Greedy Two Opt
5. Harmony Search
6. Greedy Harmony Search
7. Simulated Annealing
8. Greedy Annealing

There were also implemented two simple algorithm to test the environment:

1. Left to Right
2. Snake

The optimal path found by the algorithms depends on the chosen cost function.

Using hybrid algorithms such as Greedy Annealing improved in the same time cost of printing and time needed for path calculation.

Full report can be seen in the `report/` directory

