package gitlet;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/** Some useful Utils defined by myself.
 * @author Ziyi Xu
 */
public class SelfDefinedUtils {

    /**
     * Create a timestamp in the format of "Thu Nov 9 17:01:33 2017 -0800".
     * @return The String of time after modifying format.
     */
    static String createTimestamp() {
        String pattern = "EEE MMM d HH:mm:ss yyyy Z";
        SimpleDateFormat gitFormat = new SimpleDateFormat(pattern, Locale.US);
        Date now = new Date();
        return gitFormat.format(now);
    }

    /**
     * Return a String that can be used to generate SHA1.
     * @param args arguments for generating String.
     * @return A String consists of args if they are not null.
     */
    static String createArgumentsForSha1(Object... args) {
        StringBuilder result = new StringBuilder();
        for (Object arg: args) {
            if (arg != null) {
                result.append(arg);
            }
        }
        return result.toString();
    }

    /**
     * Create all directories needed for gitlet.
     */
    static void makeAllNeededDirectories() {
        Core.GITFILE.mkdir();
        Core.COMMITSFILE.mkdir();
        Core.STAGINGAREA.mkdir();
        Core.BLOBAREA.mkdir();
        Core.REFS.mkdir();
        Core.HEADS.mkdir();
        Core.REMOTE.mkdir();
        Core.REMOTELOCATION.mkdir();
    }

    /**
     * Generate a iterator which contains args.
     * @param args Arbitrary number of arguments
     * @return An Iterator contains args.
     */
    static Iterator<String> generateIter(String... args) {
        ArrayList<String> result = new ArrayList<>(Arrays.asList(args));
        return result.iterator();
    }

    /**
     * Return the commit according to the unique id. Throw error if
     * there is no such uid in the commitsFile.
     * @param uid The unique id of the commit
     * @return The commit which has the given uid
     */
    static Commit readCommit(String uid) {
        File f = Utils.join(Core.COMMITSFILE, uid);
        if (f.exists()) {
            return Utils.readObject(f, Commit.class);
        } else {
            Utils.message("No commit with that id exists.");
            throw new GitletException();
        }
    }

    static Commit readAbbreviate(String commitId) {
        List<String> commitFiles = Utils.plainFilenamesIn(Core.COMMITSFILE);
        if (commitFiles == null) {
            Utils.message("No commit with that id exists.");
        } else {
            for (String file : commitFiles) {
                if (file.startsWith(commitId)) {
                    return SelfDefinedUtils.readCommit(file);
                }
            }
            Utils.message("No commit with that id exists.");
            throw new GitletException();
        }
        return null;
    }

    static String currentBranch() {
        String headPath = Utils.readObject(Core.HEAD, String.class);
        List<String> path = Arrays.asList(headPath.split("\\\\|\\/"));
        return path.get(path.size() - 1);
    }

    /**
     * Read a commit object according to the HEAD in /.gitlet .
     * @return The commit corresponding to the HEAD
     */
    static Commit readHEAD() {
        File head = Utils.join(Core.GITFILE, Utils.readObject
                (Core.HEAD, String.class));
        String uid = Utils.readObject(head, String.class);
        return readCommit(uid);
    }

    /**
     * Read a previously saved commit according to the given branch name.
     * @param branchName The branch we want to read.
     * @return The corresponding Commit object.
     */
    static Commit readGivenBranch(String branchName) {
        File branch = Utils.join(Core.HEADS, branchName);
        if (branch.exists()) {
            String uid = Utils.readObject(branch, String.class);
            return readCommit(uid);
        } else {
            Utils.message("A branch with that name does not exist.");
            throw new GitletException();
        }

    }

    /**
     * Clear the stagedArea.
     */
    static void clearStagedArea() {
        SelfDefinedUtils.writeStaging(new TreeMap<String, Blob>());
        SelfDefinedUtils.writeRemoval(new TreeMap<String, Blob>());
    }

    /**
     * Used for decrease the line for status. Print a empty line
     * before use Utils.message .
     * @param msg :The format of the msg
     * @param args : The arguments.
     */
    static void title(String msg, Object... args) {
        System.out.println();
        Utils.message(msg, args);
    }

    /**
     * Display all files' name in staged area(Staged for addition).
     */
    static void listAllStagedFile() {
        TreeMap<String, Blob> allStagedFiles = SelfDefinedUtils.readStaging();
        Set<String> allStaged = allStagedFiles.keySet();
        for (String file : allStaged) {
            Utils.message(file);
        }
    }

    /**
     * Display all files' name in staged area(Staged for removal).
     */
    static void listAllRemovedFile() {
        TreeMap<String, Blob> allRemovedFiles = SelfDefinedUtils.readRemoval();
        Set<String> allRemoved = allRemovedFiles.keySet();
        for (String file : allRemoved) {
            Utils.message(file);
        }
    }

    /**
     * Throw exception if there are untrackedFile.
     */
    static void checkUntrackedFile() {
        if (SelfDefinedUtils.findAllUntracked().size() != 0) {
            Utils.message("There is an untracked file in"
                    + " the way; delete it, or add and commit it first.");
            throw new GitletException();
        }
    }

    /**
     * Check if the given uncheck id can not be commitId.
     * @param unchecked unchecked String
     */
    static void checkCommitIdStyle(String unchecked) {
        if (unchecked.length() > Utils.UID_LENGTH) {
            Utils.message("Wrong format of commit id");
            throw new GitletException();
        }
    }

    /**
     * A method added to meet style check.
     * @param next The String which should equal "--".
     */
    static void checkOperand(String next) {
        if (!Objects.equals(next, "--")) {
            Utils.message("Incorrect operands.");
            throw new GitletException();
        }
    }

    /**
     * The method to decrease the line of merge.
     * @param givenCommit The corresponding commit of given branch.
     * @param splitPoint The split point of two commit.
     * @param headCommit The corresponding commit of current branch.
     */
    static void updateForMerge(Commit givenCommit, Commit splitPoint,
                               Commit headCommit) {
        TreeMap<String, Blob> givenCommitBlobs = givenCommit.getBlobs();
        TreeMap<String, Blob> splitPointBlobs = splitPoint.getBlobs();
        TreeMap<String, Blob> headCommitBlobs = headCommit.getBlobs();
        Set<String> allPossibleFiles = new HashSet<>();
        allPossibleFiles.addAll(givenCommit.getBlobs().keySet());
        allPossibleFiles.addAll(headCommit.getBlobs().keySet());
        allPossibleFiles.addAll(splitPoint.getBlobs().keySet());
        SelfDefinedUtils.checkUntrackedFile();
        TreeMap<String, Blob> stagedForAdd = SelfDefinedUtils.readStaging();
        TreeMap<String, Blob> stagedForRem = SelfDefinedUtils.
                readRemoval();
        boolean conflict = false;
        for (String file : allPossibleFiles) {
            Blob split = splitPointBlobs.get(file);
            Blob given = givenCommitBlobs.get(file);
            Blob current = headCommitBlobs.get(file);
            boolean splitExist = splitPointBlobs.containsKey(file);
            boolean givenExist = givenCommitBlobs.containsKey(file);
            boolean currentExist = headCommitBlobs.containsKey(file);
            boolean flagOfConflict = false;
            String contentForConflict = "";
            if (givenExist && !Blob.identical(given, split)
                    && Blob.identical(current, split)) {
                stagedForAdd.put(file, givenCommitBlobs.get(file));
            } else if (Blob.identical(current, split) && !givenExist) {
                stagedForRem.put(file, headCommitBlobs.get(file));
                Utils.restrictedDelete(file);
            } else if (givenExist && currentExist && !Blob.identical
                    (given, current) && !Blob.identical(given, split)
                    && !Blob.identical(current, split)) {
                flagOfConflict = true;
                contentForConflict = current.getContentsAsString()
                        + "=======" + "\r\n" + given.getContentsAsString();
            } else if (splitExist && !Blob.identical(split, given)
                    && givenExist && !currentExist) {
                flagOfConflict = true;
                contentForConflict = "" + "=======" + "\r\n"
                        + given.getContentsAsString();
            } else if (splitExist && !Blob.identical(split, current)
                    && currentExist && !givenExist) {
                flagOfConflict = true;
                contentForConflict = current.getContentsAsString()
                        + "=======" + "\r\n" + "";
            }
            if (flagOfConflict) {
                conflict = true;
                File f = Utils.join(Main.getCwd(), file);
                contentForConflict = "<<<<<<< HEAD" + "\r\n"
                        + contentForConflict + ">>>>>>>" + "\r\n";
                Utils.writeContents(f, contentForConflict);
                stagedForAdd.put(file, new Blob(file));
            }
        }
        SelfDefinedUtils.writeStaging(stagedForAdd);
        SelfDefinedUtils.writeRemoval(stagedForRem);
        if (conflict) {
            Utils.message("Encountered a merge conflict");
        }
    }

    /**
     * Get the corresponding commit of the given branch in given remote repo.
     * @param remoteName The remote repository name.
     * @param branchName The remote branch name.
     * @return The corresponding commit.
     */
    static Commit readRemoteBranch(String remoteName, String branchName) {
        File remoteRepository = SelfDefinedUtils.
                remoteGitRepository(remoteName);
        File remoteCommitFile = Utils.join(remoteRepository, "commits");
        if (!remoteRepository.exists()) {
            Utils.message("Remote directory not found.");
            throw new GitletException();
        }

        File branch = Utils.join(remoteRepository, "refs", "heads", branchName);
        if (!branch.exists()) {
            Utils.message("That remote does not have that branch.");
            throw new GitletException();
        }

        String headCommitUid = Utils.readObject(branch, String.class);
        return Utils.readObject(Utils.join(remoteCommitFile, headCommitUid),
                Commit.class);
    }

    static File remoteGitRepository(String remoteName) {
        File pathFile = Utils.join(Core.REMOTELOCATION, remoteName);
        if (!pathFile.exists()) {
            Utils.message("A remote with that name does not exist.");
            throw new GitletException();
        }

        String remoteGit = Utils.readContentsAsString(pathFile);
        return new File(remoteGit);
    }

    /**
     * Read from .gitlet/staging/stagedForAddition, the previous saved file.
     * @return A TreeMap representing stagingArea
     */
    @SuppressWarnings("unchecked")
    static TreeMap<String, Blob> readStaging() {
        File staging = Utils.join(Core.STAGINGAREA, "stagedForAddition");
        return (TreeMap<String, Blob>)
                (Utils.readObject(staging, TreeMap.class));
    }

    /**
     * Write _stagingArea into .gitlet/staging/stagedForAddition .
     * @param stagedForAddition The TreeMap which needs to be saved.
     */
    static void writeStaging(TreeMap<String, Blob> stagedForAddition) {
        Utils.writeObject(Utils.join(Core.STAGINGAREA, "stagedForAddition"),
                stagedForAddition);
    }

    /**
     * Read from .gitlet/staging/stagedForRemoval, the previous saved file.
     * @return A TreeMap representing area of removal.
     */
    @SuppressWarnings("unchecked")
    static TreeMap<String, Blob> readRemoval() {
        File staging = Utils.join(Core.STAGINGAREA, "stagedForRemoval");
        return (TreeMap<String, Blob>)
                (Utils.readObject(staging, TreeMap.class));
    }

    /**
     * Write _stagingArea into .gitlet/staging/stagedForRemoval .
     * @param stagedForRemoval The TreeMap which needs to be saved.
     */
    static void writeRemoval(TreeMap<String, Blob> stagedForRemoval) {
        Utils.writeObject(Utils.join(Core.STAGINGAREA, "stagedForRemoval"),
                stagedForRemoval);
    }

    /**
     * Change head pointer to the given branchName.
     * @param branchName The branch to switch to.
     */
    static void switchHeadBranch(String branchName) {
        String head = "refs" + File.separator + "heads"
                + File.separator + branchName;
        Utils.writeObject(Core.HEAD, head);
    }

    /**
     * The method to find all untracked files.
     * @return An arraylist contains all untracked files' name.
     */
    static ArrayList<String> findAllUntracked() {
        ArrayList<String> result = new ArrayList<>();
        TreeMap<String, Blob> allRemovedFiles = SelfDefinedUtils.readRemoval();
        Set<String> allRemoved = allRemovedFiles.keySet();
        TreeMap<String, Blob> allStagedFiles = SelfDefinedUtils.readStaging();
        Commit headCommit = SelfDefinedUtils.readHEAD();
        TreeMap<String, Blob> currentBlobs = headCommit.getBlobs();
        List<String> allFilesInCwd = Utils.plainFilenamesIn(Main.getCwd());

        if (allFilesInCwd != null) {
            for (String file : allFilesInCwd) {
                if ((!allStagedFiles.containsKey(file))
                        && (!currentBlobs.containsKey(file))) {
                    result.add(file);
                }
            }

            for (String file : allRemoved) {
                if (allFilesInCwd.contains(file)) {
                    result.add(file);
                }
            }
        }
        return result;
    }

    /**
     * Check whether it is in an initialized git directory.
     * If it is not, throw a Gieletexception.
     */
    static void check() {
        if (!Core.GITFILE.exists()) {
            Utils.message("Not in an initialized Gitlet directory.");
            throw new GitletException();
        }
    }
}



