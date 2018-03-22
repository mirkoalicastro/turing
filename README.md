# Turing

>A Non-Deterministic Multi-Tape Turing machine
>is like a Turing machine but it has several tapes
>and its set of rules may prescribe more than one
>action to be performed for any given situation.

### What is it?
It's just another Turing machine simulator. It can handle non-deterministic events and there is no limit to tapes number.

### Development
I wrote the Java code in 2 hours, more or less. So don't consider it perfect, but it could be a good starting point for a more complete and complex Turing machine simulator.

If you find a bug or just want to contribute to the project, feel free to help or contact me by mail: mirkoalicastro@gmail.com. It would be very appreciate!

### Syntax
The first line of the program file must be the input (without starting symbol).
Then, each line consists of:
```
state; configuration; relation
```
Where,
- **state** is a string which represents the name of the state;
- **configuration** is a sequence enclosed between two brackets where each elements is separated by a comma. The i-th element represents the character on the i-th tape;
- **relation** is a relation (not necessarily a function). It is represented by a sequence enclosed between two brackets where each elements is separated by a comma. The first element is the *state* where the Turing machine will go if it is in the state *state* and has the configuration *configuration*. Then, there are a two elements of each tape. The first one, for each tape, represents the character which will be written by the head of this tape. The second one, for each tape, represents the direction where the head of this tape will go, in particular:
  - **r** for right;
  - **l** for left;
  - **-** don't move; 

#### Reserved Character
| Char | Meaning |
| ------ | ------- |
| > | Starting symbol|
| _ | Blank symbol |
| s | Initial state |
| H | Halt: final state |
| Y | Yes: final state |
| N | No: final state |

#### An example
Check if a binary string is a palindrome one.
```
001100
s; (>, >); (s, >, r, >, r)
s; (1, _); (s, 1, r, _, -)
s; (1, _); (s, 1, r, 1, r)
s; (0, _); (s, 0, r, _, -)
s; (0, _); (s, 0, r, 1, r)
s; (_, _); (b, _, l, _, l)
b; (1,1); (b, 1, l, 1, l)
b; (0,1); (b, 0, l, 1, l)
b; (1, >); (z1, 1, l, >, -)
b; (0, >); (z0, 0, l, >, -)
b; (>, >); (N, >, -, >, -)
z0; (1, >); (z0, 1, l, >, -)
z0; (0, >); (z0, 0, l, >, -)
z0; (>, >); (q0, >, r, >, r)
z1; (1, >); (z1, 1, l, >, -)
z1; (0, >); (z1, 0, l, >, -)
z1; (>, >); (q1, >, r, >, r)
q0; (1,1); (q0, 1, r, 1, r)
q0; (0,1); (q0, 0, r, 1, r)
q1; (1,1); (q1, 1, r, 1, r)
q1; (0,1); (q1, 0, r, 1, r)
q1; (1, _); (N, 1, -, _, -)
q1; (0, _); (Y, 0, -, _, -)
q0; (0, _); (N, 0, -, _, -)
q0; (1, _); (Y, 1, -, _, -)
```


License
----

MIT