# TCD_IR_2020_Indidivual

#### Use the following procedure to connect to the Ubuntu instance using an SSH client, build and run the source code then get the program's output.

## 1. To connect to the instance using SSH
In a terminal window, use the ssh command to connect to the instance by entering the following command:
```shell
$ ssh -i /"THE PATH"/IR_2020.pem ubuntu@ec2-54-161-86-196.compute-1.amazonaws.com
```
IR_2020.pem could be found in the submitted files from blackboard.
## 2. To build the source code:
Enter the path of the project by entering the following command:
```shell
$ cd TCD_IR_2020/Individual/
```
Then, use mvn command to build the source code by entering the command below in the terminal window:
```shell
$ mvn package
```
If successful, a response like the following would be shown:

![Build_Success](https://github.com/YanSen1996/TCD_IR_2020/blob/master/images/1.png)

## 3. To run the source code:
To create the index, please enter the following command in a terminal window:
```shell
$ java -cp target/Test1-1.0-SNAPSHOT.jar CreateIndex
```
If successful, a response like the following would be shown:

![Create_Success](https://github.com/YanSen1996/TCD_IR_2020/blob/master/images/2.png)

Then, enter the following command to query the index:
```shell
$ java -cp target/Test1-1.0-SNAPSHOT.jar QueryIndex
```
A resopnse like the following wouldeb shown if successful:

![Create_Results](https://github.com/YanSen1996/TCD_IR_2020/blob/master/images/3.png)
## 4. Instructions on how to get the program's output:
The project writes the results to a file called "results" which is in the folder with path "TCD_IR_2020\trec_eval-9.0.7\assignment\".
Optional: use the following command to enter the folder and open the file:
```shell
$ cd ../trec_eval-9.0.7/assignment/
$ xdg-open results
```
After viewing, press 'w' and 'q' to quit the file.
## 5. View the scores of the output:
Use 'cd' command to enter the folder of trec_eval by type these command in the terminal:
```shell
$ cd
$ cd  TCD_IR_2020/trec_eval-9.0.7/
```
For easier use, the QRelsCorrectedforTRECeval file has been renamed to 'qrels'. So, to view the scores, enter the command below:
```
$ ./trec_eval assignment/qrels assignment/results
```
The scores would be shown like below in the terminal.

![Scores](https://github.com/YanSen1996/TCD_IR_2020/blob/master/images/4.png)
