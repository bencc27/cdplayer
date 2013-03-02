CDPlayer starts creates all the components necesary for the execution of all the tasks, and starts the threads. It uses two arraylist to
save the values of the samples written by simulator and by DacWritting, saving them in the same order as they were written to compare them
in the end and verify that there have been no errors and the program works.
CDPlayer waits to the end of the execution of the thread of DacWritting to start the comparison of the values in the arraylists, and after this,
it finishes. It usually has an error in the execution time of 3%, although sometimes it has  had more than 50% of error. 

Class DiskReading:

 * This class copies the values from buffers to the queue, it's component A.
 * It works as a thread without a period of execution, instead it use an infinite loop. First it tries to get a buffer to read,
 * if there is one it copies the samples to the queue, and if not, it sleeps until there is a buffer filled.
 * To do this, it uses class FullBuffer, which has a lock on the variable selector, that is used  by simulator 
 * to say to diskReading which is the last buffer that have been filled. As simulator and diskReading take over 3 ms
 * to do their job, and as simulator has a period of over 13 ms, DiskReading has the same priority 
 * as simulator, not to provoke any delay on the execution of simulator and because it has 13-3=10ms to copy a buffer,
 * when it usually does it in 3 ms, and with the possibility of using other 10 ms as simulator can use two buffers.
 * 
 * The variable list is just for debugging; lines of code that are just for debugging
 * may be commented to reduce the time of execution.
 * 
 * Another way of implementing this class is with AsynchronousEvents that are fired by Simulator each time
 * it finish reading a sector. I made also this implementation, but I found that this solution has more problems.
 * First, more code is needed to make this implementation, with more changes in Simulator,
 * and also it's easier to make errors with the code:
 * there would be variables common for simulation and DiskReading, the threads should be created with
 * a specified order, etc. Also, making tests, sometimes Simulator and DiskReading took more time than their period
 * to read a would sector, while they usually take over 3 ms to do it; so diskReading could be fired before he
 * finishes reading a sector, and using a lock this can not happen. The only problem now would be if DiskReading took
 * twice the time of simulator to read a buffer,but that is over 26ms.
 

DacWritting:

 * This is component C, used to write the samples to the DAC converter every 22,7us.
 * It is implemented with a periodicThread of period 22,7 us, with  a priority bigger
 * than the rest of threads to ensure it makes its job ontime.
 * The variable num is just for debugging, to verify that all the samples
 * are copied.
 * The line of list.add(entry) may be commented because it introduces a big delay in the execution.
 * To verify that all the samples are well copied it should be uncommented.

Simulator:
     This class is a thread which uses a loop and sleep() to do a work periodically. It works as the class given from the beginning,
the only change is that to calculate delay it uses the value of disk driver's speed register, and when it has finished reading all sectors,
it notifies it to diskReading, which notifies DacWritting to stop the program.

     * I have tried to implement simulator as a periodic thread, using waitForNextPeriod instead of sleep,
     * but each time the period is changed, it's necesary to execute waitForNextPeriod right after, and the time 
     * the thread took for reading a sector varied very much, sometimes it took 3ms and others more than 13.
     * 
     * The period is calculated as the inverse of the number of sectors that should be read, 
     * and this is calculated with the value stored in the speed register.
     * If there is 0 in the speedRegister, the disk would stop,
     * and if its the register had 15, the disk would read 90.



Classes converter, display and JPanelSample don't work very well, I have added their code
   because of the time it has taken  me to write, but they actually don't work. The drawing is not clear,
and its execution makes the execution of the program much slower.They draw
 the value of the samples, with a proportion of 1 sample out of 15. In this case he simulation must last 2 seconds maximum,
   otherwise there will be errors in the threads. 

Class Converter is a thread that waits all the time for DacWritting to write a new sample in the dac registers. Then it saves the samples
in a queue that will be used by display to paint the signal. 

Display is a thread with 80ms period, which creates a panel with 6 parts.
Every period, it moves each part and draws a new part in order to paint the signal moving.

JPanelSample represents one of the parts of the panel, it has a width of 221 pixels, and in average it draws one sample of every 16.
