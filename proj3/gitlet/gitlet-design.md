# Gitlet Design Document
author: Ziyi Xu

## Design Document

### File Structure：

.gitlet/commits: Store all commit

.gitlet/HEAD: The location for the head pointer

.gitlet/refs: Some reference files

.gielet/refs/heads: all branches heads.

.gitlet/staging: Contains two files in it, which are two TreeMap representing stagedForAddition and stagedForRemoval



## 1. Classes and Data Structures

Include here any class definitions. For each class list the instance
variables and static variables (if any). Include a ***brief description***
of each variable and its purpose in the class. Your explanations in
this section should be as concise as possible. Leave the full
explanation to the following sections. You may cut this section short
if you find your document is too wordy.

### Commit
The class used to represent a commit object.

#### Variable:
+ String _message: The user-defined commit message
+ Timestamp _timestamp: The timestamp when user commit the message.
+ HashMap<String, Blob> _blobs: A list of strings of hashes of Blobs that are being tracked.
+ String _uid: The hashcode.
+ String _parent:The parent of this commit.

### Blob
The class represent the Blob which is used to record the modification of the file.

#### Variable:
+ String _name: Name of the modified file.
+ String _hashID: hashID of the blob object.
+ byte[] _content: The content which read by Utils.readContents() method
+ String _contentsAsString: Store the content which read from file using the Util.readContentsAsString() method.

### Core
The class implementing Staging Area, Branching, Merging.
+ final File cwd: The current working directory, File type.
+ String _head: The head pointer that corresponds to the branch.
+ HashMap<String, Blob> _stagingArea: Staging area used to store the blob object, key is file name, value is the Blob object, help us identify whether the file is changed.
+ ArrayList _removedFiles: The ArrayList indicating those files we no longer want to track.

### SelfDefinedUtils

Some useful utilities defined by myself.



### Main
The driver of gitlet.

#### Variable:
String _args: The arguments passed by user.


## 2. Algorithms

This is where you tell us how your code works. For each class, include
a high-level description of the methods in that class. That is, do not
include a line-by-line breakdown of your code, but something you would
write in a javadoc comment above a method, ***including any edge cases
you are accounting for***. We have read the project spec too, so make
sure you do not repeat or rephrase what is stated there.  This should
be a description of how your code accomplishes what is stated in the
spec.


The length of this section depends on the complexity of the task and
the complexity of your design. However, simple explanations are
preferred. Here are some formatting tips:

* For complex tasks, like determining merge conflicts, we recommend
  that you split the task into parts. Describe your algorithm for each
  part in a separate section. Start with the simplest component and
  build up your design, one piece at a time. For example, your
  algorithms section for Merge Conflicts could have sections for:

   * Checking if a merge is necessary.
   * Determining which files (if any) have a conflict.
   * Representing the conflict in the file.
  
* Try to clearly mark titles or names of classes with white space or
  some other symbols.

### Commit class:(Some contents may outdated)
+ Commit(): Default Constructor.
+ Commit(String message, String[] parentId, String branch, HashMap<String, Blob> blobs):
The general Commit constructor, called by the commit command.
+ private String generateHash(): Hash generator using the method in Utils.sha1() method.

### Blob class:(Some contents may outdated)
+ Blob(String name): Generate a blob object for the file name
+ private String createHashId(): Create the unique hashID for the Blob object. Using the SHA1() algorithm.
+ public byte[] getContents(): Return the Blob content as a byte array.
+ public String getContentsAsString(): Return the Blob content as String type.

### Core class:(Some contents may outdated)
+ public void add(String filename): The add operation.
+ public void commit(String msg): The commit operation. Take in the message with commit command.
+ public void commit(String msg, String[] parents): The commit operation for merge.
+ public void log(): The log operation.
+ public void globalLog(): The global-log operation.
+ public void status(): The status operation.
+ public void checkout(ArrayList args): The checkout opertion, takes in a Arraylist ARGS.
+ public void checkout(String branchName): This is the third use case for checkout. It takes in a branchName.
+ public void rm(String fileName): The remove opertion. Take in the name of file you want to delete. Using the Utils.restrictedDelete() method.
+ public void branch(String branchName): Create new branch named branchName. But do not change current branch head to the newly created branch.
+ public void rmbranch(String branchName): The remove branch operation.
+ boolean isModified(String fileName, HashMap<String, Blob> h, HashMap<String, Blob> i): Helper
Function for check whether the exact file has been changed from commit H to commit I. Return a boolean
if the file with name F has been modified from commit H to commit I.
## 3. Persistence

Describe your strategy for ensuring that you don’t lose the state of your program
across multiple runs. Here are some tips for writing this section:

* This section should be structured as a list of all the times you
  will need to record the state of the program or files. For each
  case, you must prove that your design ensures correct behavior. For
  example, explain how you intend to make sure that after we call
       `java gitlet.Main add wug.txt`,
  on the next execution of
       `java gitlet.Main commit -m “modify wug.txt”`, 
  the correct commit will be made.
  
* A good strategy for reasoning about persistence is to identify which
  pieces of data are needed across multiple calls to Gitlet. Then,
  prove that the data remains consistent for all future calls.
  
* This section should also include a description of your .gitlet
  directory and any files or subdirectories you intend on including
  there.

### Persistence in GITLET :
As we have implemented in Lab12, we can write the file into the directory .gitlet/repo.
The information we have to store is actually the core object, including the stage
area, untracked file and so on.

Except the core object, for git, we have to 
+ Create a folder to store all commits
+ Create a staging folder to store all blob

## 4. Design Diagram

Attach a picture of your design diagram illustrating the structure of your
classes and data structures. The design diagram should make it easy to 
visualize the structure and workflow of your program.

