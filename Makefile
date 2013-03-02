all: 
	jamaicac *.java
  
run: all
	jamaicavm CDPlayer
  
paint: all
	jamaicavm CDPlayer 2 2
  
clean:
	rm *.class

help:
	@echo Type:
	@echo  - "make all"     to compile files
	@echo  - "make run" to compile and run for 10 seconds
	@echo  - "make paint" to compile and run for 2 seconds painting the samples
	@echo  - "make clean" to delete .class files
