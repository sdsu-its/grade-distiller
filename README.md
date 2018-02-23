Blackboard Grade Distiller
==========================

Grade Distiller allows for the aggregation of an arbitrary set of Grade Center Columns from an arbitrary set of courses
from within Blackboard Learn. This is done via Blackboard's REST WEB API, which requires Version 3000.3.0 or higher of
Blackboard Learn. Using the provided RegEx expressions to select both the desired courses and columns from within those
courses, Grade Distiller generates a CSV File containing the user's username, the course that they are enrolled in, and
their grade in each of the columns (which are matched between courses by name).

## Usage
_Prerequisite:_ Blackboard Configuration has been completed.

To start the Grade Distiller using the easy-start script, enter the following command in the command line, while within
the directory containing thew script and the distiller jar file.

```bash
./extract.sh
```

If you receive an error that the file is not executable, you may need to make it executable via the following command:
```bash
chmod +x extract.sh
```

For selecting both Courses and Columns from Blackboard, a Regular Expression is used to select the desired courses and
columns. A few examples are included below, and expressions can be tested on a sample course name using online tools,
like [RegExr](https://regexr.com).

If this is the first time running the distiller, the script will need to generate a list of all of the courses in your
Blackboard Learn instance. Depending on the number of courses, this can take a while (about 30 minutes for an
installation with over 100,000 courses). This generation will not need to be done again, unless the course list is
updated and does not currently contain a course which you would like to pull grades from.

Once the distillation is complete, the script will exit, having saved the results in the specified output file
(`grades.csv` by default).

### Selector Examples
#### Courses
| Expression     | Use Case                                               |
| :------------- | :----------------------------------------------------- |
| `.*`           | All Columns                                            |
| `Test.*`       | All Columns that begin with "Test" (Case Sensitive)    |
| `[Tt]est.*`    | All Columns that begin with "Test" (Case Insensitive)  |

#### Columns
| Expression     | Use Case                                               |
| :------------- | :----------------------------------------------------- |
| `.*`           | All Columns                                            |
| `Test.*`       | All Columns that begin with "Test" (Case Sensitive)    |
| `[Tt]est.*`    | All Columns that begin with "Test" (Case Insensitive)  |


## Blackboard Configuration
Once the API Application has been whitelisted with your Blackboard Installation, which is described on Blackboard's
Developer Community Site [Managing REST Integrations in Learn](https://community.blackboard.com/docs/DOC-1580-managing-rest-integrations-in-learn-the-rest-integrations-tool-for-system-administrators), you will need to configure the execution script with the correct configuration variables, listed below.

| Variable Name  | Usage                       | Example Value                        |
| :------------- | :-------------------------- | :----------------------------------- |
| BB-URL         | Blackboard Learn Root URL   | https://my-site.blackboard.com       |
| BB-KEY         | REST API Application Key    | 68ecd4c9-3e8b-4e27-9527-5d646285ac30 |
| BB-SECRET      | REST API Application Secret | hpeWLBEX32Bzcyr8USEJ                 |

The user to which the API will be bound in Blackboard must have at least the following entitlements to function
correctly:

| Entitlement                         | Display Name in Blackboard                                     |
| :---------------------------------- | :------------------------------------------------------------- |
| course.user.VIEW                    | Course/Organization Control Panel (Users and Groups) > Users |
| course.gradebook.MODIFY             | Course/Organization Control Panel (Grade Center) > All Grading |
| system.user.VIEW                    | Administrator Panel (Users) > Users                            |
