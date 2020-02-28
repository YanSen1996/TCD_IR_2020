# TCD_IR_2020_Indidivual

Use the following procedure to connect to the Ubuntu instance using an SSH client, build and run the source code then get the program's output.

## 1. To connect to the instance using SSH
In a terminal window, use the ssh command to connect to the instance by entering the following command:
```shell
$ ssh -i /"THE PATH"/IR_2020.pem ubuntu@ec2-54-161-86-196.compute-1.amazonaws.com
```
IR_2020.pem could be found in the submitted files from blackboard.
## 2. To build the source code:
Enter the path of the project by entering the following command:
```shell
cd TCD_IR_2020/Individual/
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

  The project writes the results to a file called "results" which is in the folder with path "TCD_IR_2020\trec_eval-9.0.7\assignment\"
