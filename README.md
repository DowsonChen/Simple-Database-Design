##[Simple Database Design](https://www.thumbtack.com/challenges/simple-database)

###Commands are input and executed by stdin.###

#### **Data commands:** ####
**Your database should accept the following commands**
- SET **name** **value** – Set the variable name to the value value. Neither variable names nor values will contain spaces.
- GET **name** – Print out the value of the variable name, or NULL if that variable is not set.
- UNSET **name** – Unset the variable name, making it just like that variable was never set.
- NUMEQUALTO **value** – Print out the number of variables that are currently set to value. If no variables equal that value, print 0.
- END – Exit the program. Your program will always receive this as its last command.

**Here are some example command sequences:**
```
SET a 10
GET a
10
UNSET a
GET a
NULL
END
```

```
SET a 10
SET b 10
NUMEQUALTO 10
2
NUMEQUALTO 20
0
UNSET a
NUMEQUALTO 10
1
SET b 30
NUMEQUALTO 10
0
END
```

####**Transaction Command**s####
**In addition to the above data commands, your program should also support database transactions by also implementing these commands:**
- BEGIN – Open a new transaction block. Transaction blocks can be nested; a BEGIN can be issued inside of an existing block.
- ROLLBACK – Undo all of the commands issued in the most recent transaction block, and close the block. Print nothing if successful, or print NO TRANSACTION if no transaction is in progress.
- COMMIT – Close all open transaction blocks, permanently applying the changes made in them. Print nothing if successful, or print NO TRANSACTION if no transaction is in progress.

**Here are some example command sequences:**
```
BEGIN
SET a 10
GET a
10
BEGIN
SET a 20
GET a
20
ROLLBACK
GET a
10
ROLLBACK
GET a
NULL
END
```

```
BEGIN
SET a 30
BEGIN
SET a 40
COMMIT
GET a
40
ROLLBACK
NO TRANSACTION
END
```

```
SET a 50
BEGIN
GET a
50
SET a 60
BEGIN
UNSET a
GET a
NULL
ROLLBACK
GET a
60
COMMIT
GET a
60
END
```

```
SET a 10
BEGIN
NUMEQUALTO 10
1
BEGIN
UNSET a
NUMEQUALTO 10
0
ROLLBACK
NUMEQUALTO 10
1
COMMIT
END
```
