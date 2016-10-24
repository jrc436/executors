# executors
a relatively generic system for analyzing big data

I open-sourced this more because I couldn't think of a valid reason to maintain a private repository for what's essentially
utility code. It's not really necessarily ready for public use, in that the documentation probably makes more sense to me
than to anyone else.

The important point to remember, if you want to try to use the code, is that the program considers all big data processing
as data conversion. Sometimes, that's turning a corpus into a language model, other times it's extracting a feature set. Due
primarily to limitations in the java generic system, there's also a fair amount of boilerplate that's required. To some, having to
recreate this boilerplate might make the whole endeavor feel less worthwhile, but the advantage of the system is that it reduces
the places you can make errors (which can be costly in a big data setup) to only a few. It's easy to see what the expected 
runtime of things are. Beyond that, you can think of it as a somewhat generic system for creating and using threads, with
relatively little expectation that the user understands thread-safety (obviously, any attempt to use static but not final variables
violates thread-safety).

The basic things you have to define (with several examples provided):

Your input and output DataTypes: 
This is an interface you have to implement that gives you several functions that have to be defined.

A FileProcessor:
This specifies how to convert one DataType into another, more or less. It has two functions:
map: a function with no thread-safety requirments (beyond obvious), where a thread takes new data and aggregates it
reduce: For the majority of cases, this function is just taking the results of various maps and adding them together. It thus 
needs a lock on the 'processAggregate', which can be seen in the examples.

An Executor:
This is just boilerplate code to make use of the Executor abstract class. By convention, an Executor supertype should specify 
a main method.

Some important notes:
Both DataTypes and FileProcssors should have default constructors, even if those default constructors are unusable. 
FileProcessors generally have to specify a constructor that takes a String representing the input directory, a String representing
the output directory, and a DataType that can serve as the initial value for the output DataType

If you are actually interested in using this, but feel like you're missing something, email jrcole@psu.edu. If you feel it's too
confusing at this time but have some advice, please also email. 
