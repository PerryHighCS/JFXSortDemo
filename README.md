# JFXSortDemo

This is a demo of basic sorting algorithms displayed using the JavaFX scene
graph.

![Screenshot of merge sort](https://github.com/PerryHighCS/JFXSortDemo/blob/master/images/SortDemo.png?raw=true)

For the demo, data is represented by SortableBars implementing the Comparable 
interface so that individual bars can be compared. This allows simple 
integration into standard sorting algorithms, from Bubble Sort to Quick sort.

The bars being sorted are stored in a special instrumented wrapper class, 
DemoArray. This class allows getting and setting individual elements, and has 
built in support for swapping two elements in the array and comparing elements 
at two indices or an element at a given index with an object of the same type. 
All such accesses are counted with the counts accessible by accessor methods or 
via properties.

## Adding Sorting Algorithms
Sorting algorithms need to extend the abstract class SteppableSorter in order to 
be run by the demo. Sorting algorithms can be added to the package 
run.mycode.sortdemo.sort . Classes added to the package that extend 
SteppableSorter will be automatically added to the Sorting Method ChoiceBox when
the program starts.

Sorting algorithms should use the semaphore named step to pause between "steps".
Ideally for this demo, one "step" of an algorithm corresponds to one access to
the data being sorted, but may also represent a complicated step in the 
algorithm, such as preparing to sort or changing state in the algorithm. The
step semaphore is released each time step() is called, so before accessing the
array, use step.acquire() to wait.