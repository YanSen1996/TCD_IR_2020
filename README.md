# TCD_IR_2020_Indidivual

Use the following procedure to connect to the Ubuntu instance using an SSH client, build and run the source code then get the program's output.

## 1. To connect to the instance using SSH
In a terminal window, use the ssh command to connect to the instance by entering the following command:
```shell
ssh -i /path/my-key-pair.pem ubuntu@ec2-54-161-86-196.compute-1.amazonaws.com
```
## 2. To build the source code:
Enter the path of the project by entering the following command:
```shell
cd TCD_IR_2020/Individual/
```
Then, build the source code by entering the command below:
```shell
mvn package
```
If successful, a response, like the following, would be shown:
![image](https://github.com/YanSen1996/TCD_IR_2020/tree/master/images/build_success.jpg)


## 3. Instructions on how to run the source code:



## 4. Instructions on how to get the program's output:

  The project writes the results to a file called "results" which is in the folder with path "\IR\Individual\trec_eval-9.0.7\assignment\"
