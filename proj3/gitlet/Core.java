package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Core class of the gitlet project, implementing all commands.
 * @author Ziyi Xu
 */
public class Core implements Serializable {

    /**
     * The head pointer that corresponds to the branch. Need to write
     * into HEAD file everytime we execute command to track current commit
     */
    private String _head;

    /**
     * Area used to store the blob object which are going to be added,
     * Tree's keys are files' name. Tree's values are the corresponding
     * Blob object, This area help us identify whether the file is changed
     */
    private TreeMap<String, Blob> _stagedForAddition;

    /**
     * Area used to store the blob object which are going to be removed,
     * Tree's keys are files' name. Tree's values are the corresponding
     * Blob object,This area help us identify whether the file is changed
     */
    private TreeMap<String, Blob> _stagedForRemoval;

    /**
     * Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit: a commit that
     * contains no files and has the commit message initial commit
     * (just like that, with no punctuation). It will have a single branch:
     * master, which initially points to this initial commit, and master will
     * be the current branch. The timestamp for this initial commit will be
     * 00:00:00 UTC, Thursday, 1 January 1970 in whatever format you choose
     * for dates (this is called "The (Unix) Epoch", represented internally
     * by the time 0.) Since the initial commit in all repositories created
     * by Gitlet will have exactly the same content, it follows that all
     * repositories will automatically share this commit
     * (they will all have the same UID) and all commits in all
     * repositories will trace back to it.
     * @param operands The input line iterator.
     */
    public void init(Iterator<String> operands) throws IOException {
        if (operands.hasNext()) {
            Utils.message("Too many arguments for init command");
            throw new GitletException();
        }

        if (GITFILE.exists()) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
            throw new GitletException();
        } else {
            SelfDefinedUtils.makeAllNeededDirectories();
            Commit initialCommit = new Commit();
            initialCommit.saveCommit();

            _head = "refs" + File.separator + "heads"
                + File.separator + "master";
            HEAD.createNewFile();
            Utils.writeObject(HEAD, _head);

            File masterCommit = Utils.join(HEADS, "master");
            masterCommit.createNewFile();
            Utils.writeObject(masterCommit, initialCommit.getUid());

            _stagedForAddition = new TreeMap<String, Blob>();
            _stagedForRemoval = new TreeMap<String, Blob>();
            File addition = Utils.join(STAGINGAREA, "stagedForAddition");
            addition.createNewFile();
            Utils.writeObject(addition, _stagedForAddition);
            File removal = Utils.join(STAGINGAREA, "stagedForRemoval");
            removal.createNewFile();
            Utils.writeObject(removal, _stagedForRemoval);
        }
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area
     * (see the description of the commit command). For this reason, adding
     * a file is also called staging the file for addition. Staging an
     * already-staged file overwrites the previous entry in the staging
     * area with the new contents. The staging area should be somewhere
     * in .gitlet. If the current working version of the file is identical
     * to the version in the current commit, do not stage it to be added,
     * and remove it from the staging area if it is already there
     * (as can happen when a file is changed, added, and then changed back).
     * The file will no longer be staged for removal (see gitlet rm),
     * if it was at the time of the command.
     * @param filename The name of the file to be added.
     */
    public void add(String filename) {
        SelfDefinedUtils.check();
        File fileToAdd = new File(filename);
        if (!fileToAdd.exists()) {
            Utils.message("File does not exist.");
            throw new GitletException();
        }

        Commit currentCommit = SelfDefinedUtils.readHEAD();
        _stagedForAddition = SelfDefinedUtils.readStaging();
        _stagedForRemoval = SelfDefinedUtils.readRemoval();
        Blob blob = new Blob(filename);
        TreeMap<String, Blob> commit = currentCommit.getBlobs();

        _stagedForRemoval.remove(filename);

        if (!commit.containsKey(filename)) {
            _stagedForAddition.put(filename, blob);
        } else {
            Blob blobInCommit = commit.get(filename);
            if (Blob.identical(blobInCommit, blob)) {
                _stagedForAddition.remove(filename);
            } else {
                _stagedForAddition.put(filename, blob);
            }
        }

        SelfDefinedUtils.writeStaging(_stagedForAddition);
        SelfDefinedUtils.writeRemoval(_stagedForRemoval);
    }

    /**
     * Saves a snapshot of tracked files in the current commit and
     * staging area so they can be restored at a later time, creating
     * a new commit. The commit is said to be tracking the saved files.
     * By default, each commit's snapshot of files will be exactly the
     * same as its parent commit's snapshot of files; it will keep versions
     * of files exactly as they are, and not update them. A commit will only
     * update the contents of files it is tracking that have been staged for
     * addition at the time of commit, in which case the commit will now
     * include the version of the file that was staged instead of the
     * version it got from its parent. A commit will save and start
     * tracking any files that were staged for addition but weren't tracked
     * by its parent. Finally, files tracked in the current commit may be
     * untracked in the new commit as a result being staged for
     * removal by the rm command (below).
     * @param operands : The Iterator of the input line.
     */
    public void commit(Iterator<String> operands) throws IOException {
        SelfDefinedUtils.check();
        if (!operands.hasNext()) {
            Utils.message("Please enter a commit message.");
            throw new GitletException();
        }
        String message = operands.next();
        if (Objects.equals(message, "")) {
            Utils.message("Please enter a commit message.");
            throw new GitletException();
        } else if (operands.hasNext()) {
            Utils.message("Can only have one message");
            throw new GitletException();
        }

        Commit currentCommit = SelfDefinedUtils.readHEAD();
        ArrayList<String> parent = new ArrayList<>();
        String branch = SelfDefinedUtils.currentBranch();
        TreeMap<String, Blob> currentCommitBlobs = currentCommit.getBlobs();

        _stagedForAddition = SelfDefinedUtils.readStaging();
        _stagedForRemoval = SelfDefinedUtils.readRemoval();
        if (_stagedForAddition.size() == 0 && _stagedForRemoval.size() == 0) {
            Utils.message("No changes added to the commit.");
            throw new GitletException();
        }
        for (String file : _stagedForAddition.keySet()) {
            currentCommitBlobs.put(file, _stagedForAddition.get(file));
        }
        for (String file : _stagedForRemoval.keySet()) {
            currentCommitBlobs.remove(file);
        }


        Pattern p = Pattern.compile("Merged (\\w+) into (\\w+).");
        Matcher m = p.matcher(message);
        if (m.matches()) {
            parent.add(SelfDefinedUtils.readGivenBranch(m.group(2)).getUid());
            parent.add(SelfDefinedUtils.readGivenBranch(m.group(1)).getUid());
            if (!parent.contains(currentCommit.getUid())) {
                Utils.message("Current commit should be the "
                        + "parent of merge commit. You are giving a "
                        + "wrong commit message");
                throw new GitletException();
            }
        } else {
            parent.add(currentCommit.getUid());
        }


        Commit newCommit = new Commit(message, parent, branch,
                currentCommitBlobs);
        newCommit.saveCommit();

        File currentBranch = Utils.join(Core.HEADS, newCommit.getBranch());
        Utils.writeObject(currentBranch, newCommit.getUid());

        SelfDefinedUtils.writeStaging(new TreeMap<String, Blob>());
        SelfDefinedUtils.writeRemoval(new TreeMap<String, Blob>());
    }

    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it for
     * removal and remove the file from the working directory if the
     * user has not already done so (do not remove it unless it
     * is tracked in the current commit).
     * @param filename The file to unstage.
     */
    public void rm(String filename) {
        SelfDefinedUtils.check();
        Commit headCommit = SelfDefinedUtils.readHEAD();
        TreeMap<String, Blob> blobs = headCommit.getBlobs();
        _stagedForAddition = SelfDefinedUtils.readStaging();
        _stagedForRemoval = SelfDefinedUtils.readRemoval();

        if ((!_stagedForAddition.containsKey(filename))
                && (!blobs.containsKey(filename))) {
            Utils.message("No reason to remove the file.");
            throw new GitletException();
        }

        _stagedForAddition.remove(filename);
        if (blobs.containsKey(filename)) {
            _stagedForRemoval.put(filename, blobs.get(filename));
            Utils.restrictedDelete(filename);
        }

        SelfDefinedUtils.writeStaging(_stagedForAddition);
        SelfDefinedUtils.writeRemoval(_stagedForRemoval);
    }

    /**
     * Starting at the current head commit, display information about each
     * commit backwards along the commit tree until the initial commit,
     * following the first parent commit links, ignoring any second parents
     * found in merge commits. (In regular Git, this is what you get with
     * git log --first-parent). This set of commit nodes is called the
     * commit's history. For every node in this history, the information it
     * should display is the commit id, the time the commit was made,
     * and the commit message. Here is an example of the exact format
     * it should follow:
     *
     * commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * A commit message.
     * @param operands The Iterator of the input line.
     */
    public void log(Iterator<String> operands) {
        SelfDefinedUtils.check();
        if (operands.hasNext()) {
            Utils.message("Too many arguments for log command");
            throw new GitletException();
        }

        Commit current = SelfDefinedUtils.readHEAD();
        current.display();
        while (current.getParent() != null) {
            String parent = current.getParent().get(0);
            current = SelfDefinedUtils.readCommit(parent);
            current.display();
        }
    }

    /**
     * Checkout is a general command that can do a few different
     * things depending on what its arguments are.
     * There are 3 possible use cases:
     *
     * 1.java gitlet.Main checkout -- [file name]
     * Takes the version of the file as it exists in the head commit,
     * the front of the current branch, and puts it in the working directory,
     * overwriting the version of the file that's already there if there is
     * one. The new version of the file is not staged.
     *
     * 2.java gitlet.Main checkout [commit id] -- [file name]
     * Takes the version of the file as it exists in the commit with the given
     * id, and puts it in the working directory, overwriting the version of
     * the file that's already there if there is one. The new version of
     * the file is not staged.
     *
     * 3.java gitlet.Main checkout [branch name]
     * Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory, overwriting the versions
     * of the files that are already there if they exist. Also, at the
     * end of this command, the given branch will now be considered the
     * current branch (HEAD). Any files that are tracked in the current
     * branch but are not present in the checked-out branch are deleted.
     * The staging area is cleared, unless the checked-out branch is the
     * current branch (see Failure cases below).
     *
     * @param operands The Iterator of arguments
     */
    public void checkout(Iterator<String> operands) {
        SelfDefinedUtils.check();
        String firstArgument = operands.next();
        Commit headCommit = SelfDefinedUtils.readHEAD();
        if (Objects.equals(firstArgument, "--")) {
            String fileName = operands.next();
            if (!headCommit.getBlobs().containsKey(fileName)) {
                Utils.message("File does not exist in that commit.");
                throw new GitletException();
            } else {
                Blob blob = headCommit.getBlobs().get(fileName);
                String contents = blob.getContentsAsString();
                File f = new File(fileName);
                Utils.writeContents(f, contents);
            }
        } else if (firstArgument.matches("[a-f0-9]+") && operands.hasNext()) {
            String next = operands.next();
            SelfDefinedUtils.checkOperand(next);
            String fileName = operands.next();
            Commit commit;
            SelfDefinedUtils.checkCommitIdStyle(firstArgument);
            if (firstArgument.length() == Utils.UID_LENGTH) {
                commit = SelfDefinedUtils.readCommit(firstArgument);
            } else {
                commit = SelfDefinedUtils.readAbbreviate(firstArgument);
            }
            if (!commit.getBlobs().containsKey(fileName)) {
                Utils.message("File does not exist in that commit.");
                throw new GitletException();
            } else {
                Blob blob = commit.getBlobs().get(fileName);
                String contents = blob.getContentsAsString();
                File f = new File(fileName);
                Utils.writeContents(f, contents);
            }
        } else {
            SelfDefinedUtils.checkUntrackedFile();
            File branch = Utils.join(HEADS, firstArgument);
            if (!branch.exists()) {
                Utils.message("No such branch exists.");
                throw new GitletException();
            } else if (Commit.identical(headCommit,
                    SelfDefinedUtils.readGivenBranch(firstArgument))
                && Objects.equals(headCommit.getBranch(), firstArgument)) {
                Utils.message("No need to checkout the current branch.");
                throw new GitletException();
            }
            Commit newCommit = SelfDefinedUtils.readGivenBranch(firstArgument);
            newCommit.puts();
            for (String file : headCommit.getBlobs().keySet()) {
                if (!newCommit.getBlobs().containsKey(file)) {
                    File f = Utils.join(Main.getCwd(), file);
                    Utils.restrictedDelete(f);
                }
            }
            SelfDefinedUtils.switchHeadBranch(firstArgument);
            SelfDefinedUtils.clearStagedArea();
        }
    }

    /**
     * Like log, except displays information about all
     * commits ever made. The order of the commits does not matter.
     * @param operands The iterator of arguments
     */
    public void globalLog(Iterator<String> operands) {
        SelfDefinedUtils.check();
        if (operands.hasNext()) {
            Utils.message("Too many arguments for global-log");
            throw new GitletException();
        }
        List<String> allFiles = Utils.plainFilenamesIn(COMMITSFILE);
        if (allFiles != null) {
            for (String allFile : allFiles) {
                Commit temp = SelfDefinedUtils.readCommit(allFile);
                temp.display();
            }
        }
    }

    /**
     * Prints out the ids of all commits that have the given commit message,
     * one per line. If there are multiple such commits, it prints the ids
     * out on separate lines. The commit message is a single operand; to
     * indicate a multiword message, put the operand in quotation marks,
     * as for the commit command above.
     * @param operands The Iterator of arguments.
     */
    public void find(Iterator<String> operands) {
        SelfDefinedUtils.check();
        if (!operands.hasNext()) {
            Utils.message("find command needs exactly one arguments.");
        }
        String message = operands.next();
        List<String> allFiles = Utils.plainFilenamesIn(COMMITSFILE);
        int flag = 0;
        if (allFiles != null) {
            for (String allFile : allFiles) {
                Commit temp = SelfDefinedUtils.readCommit(allFile);
                if (Objects.equals(temp.getMessage(), message)) {
                    Utils.message(temp.getUid());
                    flag = 1;
                }
            }
        }
        if (flag == 0) {
            Utils.message("Found no commit with that message.");
            throw new GitletException();
        }
    }

    /**
     * Displays what branches currently exist, and marks the current
     * branch with a *. Also displays what files have been staged for
     * addition or removal. An example of the exact format it should
     * follow is as follows.
     *
     * === Branches ===
     * *master
     * other-branch
     *
     * === Staged Files ===
     * wug.txt
     * wug2.txt
     *
     * === Removed Files ===
     * goodbye.txt
     *
     * === Modifications Not Staged For Commit ===
     * junk.txt (deleted)
     * wug3.txt (modified)
     *
     * === Untracked Files ===
     * random.stuff
     * @param operands The Iterator of input.
     */
    public void status(Iterator<String> operands) {
        SelfDefinedUtils.check();
        Commit headCommit = SelfDefinedUtils.readHEAD();
        Utils.message("=== Branches ===");
        String currentBranch = SelfDefinedUtils.currentBranch();
        List<String> allBranches = Utils.plainFilenamesIn(HEADS);
        for (String file : allBranches) {
            if (Objects.equals(file, currentBranch)) {
                Utils.message("*%s", file);
            } else {
                Utils.message(file);
            }
        }
        SelfDefinedUtils.title("=== Staged Files ===");
        TreeMap<String, Blob> allStagedFiles = SelfDefinedUtils.readStaging();
        Set<String> allStaged = allStagedFiles.keySet();
        SelfDefinedUtils.listAllStagedFile();
        SelfDefinedUtils.title("=== Removed Files ===");
        TreeMap<String, Blob> allRemovedFiles = SelfDefinedUtils.readRemoval();
        SelfDefinedUtils.listAllRemovedFile();
        SelfDefinedUtils.title("=== Modifications Not Staged For Commit ===");
        TreeMap<String, Blob> currentBlobs = headCommit.getBlobs();
        Set<String> key = currentBlobs.keySet();
        for (String file : key) {
            File temp = Utils.join(Main.getCwd(), file);
            if (temp.exists()) {
                if (!Objects.equals(currentBlobs.get(file).getContentsAsString
                        (), Utils.readContentsAsString(temp))) {
                    if (!allStagedFiles.containsKey(file)) {
                        Utils.message(file + " (modified)");
                    }
                }
            } else {
                if (!allRemovedFiles.containsKey(file)) {
                    Utils.message(file + " (deleted)");
                }
            }
        }
        for (String file : allStaged) {
            File temp = Utils.join(Main.getCwd(), file);
            if (temp.exists() && (!Objects.equals
                    (allStagedFiles.get(file).getContentsAsString(),
                            Utils.readContentsAsString(temp)))) {
                Utils.message(file + " (modified)");
            } else if (!temp.exists()) {
                Utils.message(file + " (deleted)");
            }
        }
        System.out.println();
        Utils.message("=== Untracked Files ===");
        ArrayList<String> allUntracked = SelfDefinedUtils.findAllUntracked();
        for (int i = 0; i < allUntracked.size() - 1; i++) {
            Utils.message(allUntracked.get(i));
        }
        if (allUntracked.size() >= 1) {
            System.out.print(allUntracked.get(allUntracked.size() - 1));
        }
    }

    /**
     * Creates a new branch with the given name, and points it at the
     * current head node. A branch is nothing more than a name for a
     * reference (a SHA-1 identifier) to a commit node. This command
     * does NOT immediately switch to the newly created branch
     * (just as in real Git). Before you ever call branch, your code
     * should be running with a default branch called "master".
     * @param operands The Iterator of input line.
     */
    public void branch(Iterator<String> operands) {
        SelfDefinedUtils.check();
        if (!operands.hasNext()) {
            Utils.message("Branch command requires"
                    + " the specification of branch name");
            throw new GitletException();
        }
        String branchName = operands.next();
        if (operands.hasNext()) {
            Utils.message("Too many arguments for Branch command");
            throw new GitletException();
        }

        List<String> allBranches = Utils.plainFilenamesIn(HEADS);
        assert allBranches != null;
        if (allBranches.contains(branchName)) {
            Utils.message("A branch with that name already exists.");
            throw new GitletException();
        } else {
            String uid = SelfDefinedUtils.readHEAD().getUid();
            File f = Utils.join(HEADS, branchName);
            Utils.writeObject(f, uid);
        }
    }

    /**
     * Deletes the branch with the given name. This only means
     * to delete the pointer associated with the branch; it does
     * not mean to delete all commits that were created under the
     * branch, or anything like that.
     * @param operands The Iterator of operands.
     */
    public void rmBranch(Iterator<String> operands) {
        SelfDefinedUtils.check();
        if (!operands.hasNext()) {
            Utils.message("rm-branch command requires"
                    + " the specification of branch name");
            throw new GitletException();
        }
        String branchName = operands.next();
        if (operands.hasNext()) {
            Utils.message("Too many arguments for rm-branch command");
            throw new GitletException();
        }
        List<String> allBranches = Utils.plainFilenamesIn(HEADS);
        assert allBranches != null;
        if (!allBranches.contains(branchName)) {
            Utils.message("A branch with that name does not exists.");
            throw new GitletException();
        } else if (Objects.equals(SelfDefinedUtils.currentBranch(),
                branchName)) {
            Utils.message("Cannot remove the current branch.");
            throw new GitletException();
        } else {
            File f = Utils.join(HEADS, branchName);
            f.delete();
        }
    }

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch's head to that commit node.
     * See the intro for an example of what happens to the head
     * pointer after using reset. The [commit id] may be abbreviated
     * as for checkout. The staging area is cleared. The command is
     * essentially checkout of an arbitrary commit that also changes
     * the current branch head.
     * @param operands The Iterator of arguments.
     */
    public void reset(Iterator<String> operands) {
        SelfDefinedUtils.check();
        if (!operands.hasNext()) {
            Utils.message("reset command requires"
                    + " the specification of commit id");
            throw new GitletException();
        }
        String commitId = operands.next();
        if (operands.hasNext()) {
            Utils.message("Too many arguments for reset command");
            throw new GitletException();
        }

        Commit givenCommit = SelfDefinedUtils.readCommit(commitId);
        Set<String> headKey = SelfDefinedUtils.readHEAD().getBlobs().keySet();
        Set<String> stagedKey = SelfDefinedUtils.readStaging().keySet();
        ArrayList<String> untracked = SelfDefinedUtils.findAllUntracked();
        Set<String> fileNames = givenCommit.getBlobs().keySet();
        for (String file : fileNames) {
            if (untracked.contains(file)) {
                Utils.message("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                throw new GitletException();
            }
        }
        Core core = new Core();

        for (String file : fileNames) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(commitId);
            temp.add("--");
            temp.add(file);
            core.checkout(temp.iterator());
        }

        for (String file : headKey) {
            if (!fileNames.contains(file)) {
                Utils.restrictedDelete(Utils.join(Main.getCwd(), file));
            }
        }

        for (String file : stagedKey) {
            if (!fileNames.contains(file)) {
                Utils.restrictedDelete(Utils.join(Main.getCwd(), file));
            }
        }

        SelfDefinedUtils.switchHeadBranch(givenCommit.getBranch());
        File f = Utils.join(HEADS, givenCommit.getBranch());
        Utils.writeObject(f, givenCommit.getUid());
        _stagedForRemoval = new TreeMap<>();
        _stagedForAddition = new TreeMap<>();
        SelfDefinedUtils.writeRemoval(_stagedForRemoval);
        SelfDefinedUtils.writeStaging(_stagedForAddition);
    }

    /**
     * Merges files from the given branch into the current branch.
     * @param operands The Iterator of the operands.
     */
    public void merge(Iterator<String> operands) throws IOException {
        SelfDefinedUtils.check();
        String givenBranch = operands.next();
        if (Objects.equals(givenBranch, SelfDefinedUtils.currentBranch())) {
            Utils.message("Cannot merge a branch with itself.");
            throw new GitletException();
        }
        Commit givenCommit = SelfDefinedUtils.readGivenBranch(givenBranch);
        Commit headCommit = SelfDefinedUtils.readHEAD();
        Commit splitPoint = SelfDefinedUtils.readCommit(headCommit.
                getSplitPoint(givenCommit));
        _stagedForAddition = SelfDefinedUtils.readStaging();
        _stagedForRemoval = SelfDefinedUtils.readRemoval();
        if (_stagedForAddition.size() + _stagedForRemoval.size() > 0) {
            Utils.message("You have uncommitted changes.");
            throw new GitletException();
        }
        if (Commit.identical(givenCommit, splitPoint)) {
            Utils.message("Given branch is an ancestor of the current branch.");
        } else if (Commit.identical(headCommit, splitPoint)) {
            new Core().checkout(SelfDefinedUtils.generateIter(givenBranch));
            Utils.message("Current branch fast-forwarded.");
        } else {
            SelfDefinedUtils.updateForMerge(givenCommit,
                    splitPoint, headCommit);
            String message = String.format("Merged %s into %s.",
                    givenBranch, SelfDefinedUtils.currentBranch());
            new Core().commit(SelfDefinedUtils.generateIter(message));
            SelfDefinedUtils.readHEAD().puts();
        }
    }

    /**
     *Saves the given login information under the given remote name.
     * Attempts to push or pull from the given remote name will then
     * attempt to use this .gitlet directory.
     * @param operands The iterator of input line.
     * @throws IOException:Could happen when creating new directory
     */
    public void addRemote(Iterator<String> operands) throws IOException {
        if (!operands.hasNext()) {
            Utils.message("add-remote command requires 2 arguments.");
            throw new GitletException();
        }
        String remoteName = operands.next();
        if (!operands.hasNext()) {
            Utils.message("add-remote command requires 2 arguments.");
            throw new GitletException();
        }
        String path = operands.next();
        if (operands.hasNext()) {
            Utils.message("Too many arguments for add-remote command.");
            throw new GitletException();
        }

        File f = Utils.join(Core.REMOTELOCATION, remoteName);
        if (f.exists()) {
            Utils.message("A remote with that name already exists.");
            throw new GitletException();
        } else {
            f.createNewFile();
            Utils.writeContents(f, path);
        }
        File remoteBranches = Utils.join(Core.HEADS, remoteName);
        remoteBranches.mkdir();
    }

    /**
     * Remove information associated with the given remote name.
     * @param operands The iterator of input line.
     */
    public void rmRemote(Iterator<String> operands) {
        if (!operands.hasNext()) {
            Utils.message("rm-remote command requires 1 argument.");
            throw new GitletException();
        }
        String remoteName = operands.next();
        if (operands.hasNext()) {
            Utils.message("Too many arguments for add-remote command.");
            throw new GitletException();
        }

        File f = Utils.join(Core.REMOTELOCATION, remoteName);
        if (!f.exists()) {
            Utils.message("A remote with that name does not exist.");
            throw new GitletException();
        } else {
            f.delete();
        }

        File branchOfRemote = Utils.join(Core.HEADS, remoteName);
        branchOfRemote.delete();
    }

    /**
     * Attempts to append the current branch's commits to the end of the
     * given branch at the given remote.
     * This command only works if the remote branch's head is in the history
     * of the current local head, which means that the local branch contains
     * some commits in the future of the remote branch. In this case, append
     * the future commits to the remote branch. Then, the remote should reset
     * to the front of the appended commits (so its head will be the same as
     * the local head). This is called fast-forwarding.
     * If the Gitlet system on the remote machine exists but does not have
     * the input branch, then simply add the branch to the remote Gitlet.
     * @param operands :The Iterator of operands.
     * @throws IOException: Possible exception when creating new file
     */
    public void push(Iterator<String> operands) throws IOException {
        String remoteName = operands.next();
        if (!operands.hasNext()) {
            Utils.message("push command requires 2 arguments.");
            throw new GitletException();
        }
        String branchName = operands.next();
        File remoteRepo = SelfDefinedUtils.remoteGitRepository(remoteName);
        File remoteHeads = Utils.join(remoteRepo, "refs", "heads");
        File remoteCommits = Utils.join(remoteRepo, "commits");
        File thisRemoteBranch = Utils.join(remoteHeads, branchName);
        Commit headCommit = SelfDefinedUtils.readHEAD();
        String currentHead = SelfDefinedUtils.currentBranch();
        Commit remoteBranch = SelfDefinedUtils.readRemoteBranch(remoteName,
                branchName);
        String remoteBranchUid = remoteBranch.getUid();
        Set<String> ancestors = headCommit.getAllParents();
        List<String> allRemoteBranch = Utils.plainFilenamesIn(remoteHeads);
        List<String> allRemoteCommits = Utils.plainFilenamesIn(remoteCommits);
        List<String> allFilesInRemote = Utils.plainFilenamesIn(remoteRepo);
        if (allRemoteBranch == null
                || !allRemoteBranch.contains(currentHead)) {
            for (String file : ancestors) {
                if (allRemoteCommits == null || !allRemoteCommits.
                        contains(file)) {
                    File f = Utils.join(remoteCommits, file);
                    f.createNewFile();
                    Commit commitToWrite = Utils.readObject(Utils.join
                            (COMMITSFILE, file), Commit.class);
                    Utils.writeObject(f, commitToWrite);
                }
            }
            File thisBranch = Utils.join(remoteHeads, currentHead);
            thisBranch.createNewFile();
            Utils.writeObject(thisBranch, headCommit.getUid());
        } else if (!ancestors.contains(remoteBranchUid)) {
            Utils.message("Please pull down remote changes before pushing.");
            throw new GitletException();
        } else {
            for (String fileId : ancestors) {
                File f = Utils.join(remoteCommits, fileId);
                if (!f.exists()) {
                    Commit commitToAdd = SelfDefinedUtils.readCommit(fileId);
                    f.createNewFile();
                    Utils.writeObject(f, commitToAdd);
                }
            }
            Utils.writeObject(thisRemoteBranch, headCommit.getUid());
            File remoteHead = Utils.join(remoteRepo, "HEAD");
            Utils.writeObject(remoteHead, "refs" + File.separator
                    + "heads" + File.separator + branchName);
            for (String fileName : allFilesInRemote) {
                File f = Utils.join(REMOTE, fileName);
                f.delete();
            }
            headCommit.puts(remoteName);
        }
    }

    /**
     * Brings down commits from the remote Gitlet repository
     * into the local Gitlet repository.
     * @param operands The iterator of input line.
     * @throws IOException Could happen when creating new directory
     */
    public void fetch(Iterator<String> operands) throws IOException {
        if (!operands.hasNext()) {
            Utils.message("fetch command requires 2 arguments.");
            throw new GitletException();
        }
        String remoteName = operands.next();
        if (!operands.hasNext()) {
            Utils.message("fetch command requires 2 arguments.");
            throw new GitletException();
        }
        String branchName = operands.next();
        if (operands.hasNext()) {
            Utils.message("Too many arguments for fetch command.");
            throw new GitletException();
        }

        File remoteRepository = SelfDefinedUtils.
                remoteGitRepository(remoteName);
        File remoteCommitFile = Utils.join(remoteRepository, "commits");

        Commit headCommit = SelfDefinedUtils.readRemoteBranch
                (remoteName, branchName);
        Set<String> ancestors = headCommit.
                getAllParentsInRemote(remoteRepository);

        for (String fileName : ancestors) {
            Commit temp = Utils.readObject
                    (Utils.join(remoteCommitFile, fileName), Commit.class);
            File file = Utils.join(COMMITSFILE, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            Utils.writeObject(file, temp);
        }

        File remoteBranches = Utils.join(Core.HEADS, remoteName);

        File thisBranch = Utils.join(remoteBranches, branchName);
        if (!thisBranch.exists()) {
            thisBranch.createNewFile();
        }
        Utils.writeObject(thisBranch, headCommit.getUid());
    }

    /**
     * Fetches branch [remote name]/[remote branch name] as for the
     * fetch command, and then merges that fetch into the current branch.
     * @param operands The iterator of input line.
     * @throws IOException Could happen when creating new directory.
     */
    public void pull(Iterator<String> operands) throws IOException {
        if (!operands.hasNext()) {
            Utils.message("pull command requires 2 arguments.");
            throw new GitletException();
        }
        String remoteName = operands.next();
        if (!operands.hasNext()) {
            Utils.message("pull command requires 2 arguments.");
            throw new GitletException();
        }
        String branchName = operands.next();
        if (operands.hasNext()) {
            Utils.message("Too many arguments for pull command.");
            throw new GitletException();
        }
        Core core = new Core();
        core.fetch(SelfDefinedUtils.generateIter(remoteName, branchName));
        String remoteBranch = remoteName + File.separator + branchName;
        core.merge(SelfDefinedUtils.generateIter(remoteBranch));
    }


    /** The gitlet file, similar to the real .git/ file in git.*/
    static final File GITFILE = Utils.join(Main.getCwd(), ".gitlet");

    /** The file where we store all commit objects.*/
    static final File COMMITSFILE = Utils.join(GITFILE, "commits");

    /** The file where we store all staging objects.*/
    static final File STAGINGAREA = Utils.join(GITFILE, "staging");

    /** The file where we store all staging objects.*/
    static final File BLOBAREA = Utils.join(GITFILE, "blobs");

    /** The file where we store all staging objects.*/
    static final File REFS = Utils.join(GITFILE, "refs");

    /** The file where we store all branches' heads. */
    static final File HEADS = Utils.join(GITFILE, "refs", "heads");

    /** The file where we store the reference to the real head.*/
    static final File HEAD = Utils.join(GITFILE, "HEAD");

    /** The File store information about remote command. */
    static final File REMOTE = Utils.join(GITFILE, "refs", "remotes");

    /** The File which stores all remote directories. */
    static final File REMOTELOCATION = Utils.join(REMOTE, "locations");
}
