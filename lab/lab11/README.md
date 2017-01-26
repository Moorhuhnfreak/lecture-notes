# Solution to Homework Assignment 10 -- Software Design & Programming Techniques (WS 2016)

This folder contains an example solution to the HW 10. It also features some
additional comments and explanations marked with "NOTE".

## Background Info: Pattern Functors
On slide 16 in the deck on [functional patterns](http://ps.informatik.uni-tuebingen.de/teaching/ws16/sdpt/functionalpatterns.pdf) a fixed point operator on types is
mentioned. It is shown that a list of integers can be represented by the
so called ``pattern functor``:

```haskell
data IntListF x = EmptyList | Cons Int x
```

that is all recursive occurrences of the list itself are replaced by the
type parameter `x`. The recursive data type now can be obtained by:

```haskell
type IntList = Fix IntListF
```

In this homework we will first translate the example to Scala, then implement
other pattern functors to work with them.

**... continue reading in the Scala file `Task0.scala`.**

## Task 1: Functors
Follow the instructions in the Scala file `Task1.scala`.

## Task 2. A Generic visitor infrastructure
Follow the instructions in the Scala file `Task2.scala`.

## Task 3. Tupling visitors
Follow the instructions in the Scala file `Task3.scala`.
