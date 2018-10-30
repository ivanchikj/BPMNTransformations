<!---I diagrammi su come funzionano YPNR NRNP eccetera, includili nel readme del programma.-->
<!---spiega come fare per creare una nuova regola. Non è complicato, si tratta di modificare 2 o 3 classi al massimo e poche righe di codice.-->
[![Usi](Images/logoUsi.png)](https://www.usi.ch/)


# Tool for semantics-preserving BPMN transformations

* This program will create semantically equivalent version of BPMN2.0 Models that are given as input
* The user can select which rules to apply among 8 different semantically equivalent transformations
* Reverse versions of the transformation rules are also available.

- - - -

## Introduction

This project was created in the scope of my master thesis at USI, in partial fullfillment of...
Both the thesis and the project were supervised by
**You will find the pdf of the thesis in the folder..**

## The transformation rules
```
TODO
```
```
TODO
```
```
TODO
```
```
TODO
```
<!---Mettere le immagini qua-->

- - - -

## Getting Started

#### Prerequisites

* Before running the program you need to install JAVA 1.8:

```
TODO
```

### Running the program

You can run them simply by :
```
TODO
```

- - - -

## Using the program

The software is managed through console commands that are composed of 3 parts:
* In the first part you can select the input file(s)
* In the middle part you can select the behavior of the program
* In the final part you can select the transformation rule(s) to apply.

#### Selecting the input models:
In the first part simply enter the path of the folder containing the files that you want to transform: all '.bpmn' or '.bpmn.xml' files will be taken as input.

```
\Example\bpmnModels\
```
It is also possible to enter the path of a single file:
```
\Example\bpmnModels\Model.bpmn.xml
```

#### Selecting the program behavior:
For the second part you have the choice among turning on/off two behaviors:
* Repetition (i.e. the program will try to apply the same transformations multiple times on the same model until it yelds no results)
* Permutations (i.e. the program will apply the rules you have chose in different orders, not just in the order you provided).

To turn on the desired behavior, you have the choice among 4 combinations:

* **YPYR** || **YRYP** (Yes Perm Yes Rec)
* **NPNR** || **NRNP** (No Perm No Rec)
* **YPNR** || **NRYP** (Yes Perm No Rec)
* **NPYR** || **YRNP** (No Perm Yes Rec)

Now our input line looks something like this:

```
\Example\bpmnModels\ YPNR
```

#### Selecting the permutation rules:

Finally, we select the rules that will be applied from this list:
* Note that rules 3 and 4 can be divided in 3 different transformations: a, b and c and it is possible to choose exactly which ones to apply independently.
* Note that it is not suggested <!---nor possible ---> to apply a rule and the reverse version of the same rule.


Rule | Reversed rule:
---- | -------------
-1   | -r1
-2   | -r2
-3   | -r3
-3a  | -r3a
-3b  | -r3b
-3c  | -r3c
-4   | -r4
-4a  | -r4a
-4b  | -r4b
-4c  | -r4c

*Some rules <!---insert appropriate rules--> accept an optional input parameter <!---explain what it does-->. It can be added to rules by adding an asterisk followed by the desired number.*

### Example inputs:
Some example inputs to get you started:

```
\Example\bpmnModels\Model.bpmn.xml NPNR (-1-2-3)
```

```
\Example\bpmnModels\ YPYR (-1-2-r3*2)
```

```
\Example\bpmnModels\ yryp (-R3*2-2)
```

```
\Example\bpmnModels\ yrnp (-3 -4a -4b)
```

**You can also write 'help' in the console to display a list of example inputs.**

### Expected Outputs

A new folder called 'output' will be created inside the input folder, 
<!---Far vedere sia i cambiamenti nei modelli, che il comportamento della console, che il report-->


### Extending the program
<!---spiega come fare per creare una nuova regola. Non è complicato, si tratta di modificare 2 o 3 classi al massimo e poche righe di codice.-->
<!---includi un disegno dell'architettura.-->

## Built With Libraries:

* [Camunda](www.google.com) - The API that...
* [Xpath](www.google.com) - The tool that..


## Author

* [**Ruben**](https://github.com/realityhas)

## Supervised by

* [**A**](www.google.com)
* [**C**](www.google.com)

## License

todo

## Acknowledgments

* todo
