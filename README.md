# JFXSortDemo

This is a demo of basic sorting algorithms displayed using the JavaFX scene graph.

![Screenshot of merge sort](https://github.com/PerryHighCS/JFXSortDemo/blob/master/images/SortDemo.png?raw=true)

For the demo, data is represented by SortableBars implementing the Comparable interface so that individual bars can be compared. This allows simple integration into standard sorting algorithms, from Bubble Sort to Quick sort.

The bars being sorted are stored in a special instrumented wrapper class, DemoArray. This class allows getting and setting individual elements, and has built in support for swapping two elements in the array and comparing elements at two indices or an element at a given index with an object of the same type. All such accesses are counted with the counts accessible by accessor methods or via properties.
