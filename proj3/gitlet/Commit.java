package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class Commit implements Serializable {

    /** The default constructor used for init command. */
    public Commit() {
        this._message = "initial commit";
        this._timestamp = "Thu Jan 1 00:00:00 1970 +0000";
        this._parent = null;
        this._blobs = new TreeMap<String, Blob>();
        this._branch = "master";
        this._uid = hashcodeGenerator();
    }

    /**
     * The general commit generator.
     * @param message The commit message
     * @param parent The parent of the commit
     * @param blobs The blobs tracked by the commit
     * @param branch The branch of this commit
     */
    public Commit(String message, ArrayList<String> parent, String branch,
                  TreeMap<String, Blob> blobs) {
        _message = message;
        _parent = parent;
        _blobs = blobs;
        _branch = branch;
        _timestamp = SelfDefinedUtils.createTimestamp();
        _uid = hashcodeGenerator();
    }

    /**
     * Generate a hashcode use sha1 (with object
     * message, blobs, parent, timestamp, branch.
     * @return A unique hashcode
     */
    private String hashcodeGenerator() {
        String contentOfHash = SelfDefinedUtils.createArgumentsForSha1(
                _blobs, _parent, _timestamp, _branch, _message);
        return Utils.sha1(contentOfHash);
    }

    /**
     * Save this commit into .gitlet/commits.
     */
    public void saveCommit() throws IOException {
        File commitToSave = Utils.join(Core.COMMITSFILE, this._uid);
        commitToSave.createNewFile();
        Utils.writeObject(commitToSave, this);
    }

    /**
     * Takes all files in the commit, and puts them in the working directory,
     * overwriting the versions of the files that are already there
     * if they exist.
     */
    public void puts() {
        Set<String> filename = _blobs.keySet();
        for (String file : filename) {
            File temp = Utils.join(Main.getCwd(), file);
            String content = _blobs.get(file).getContentsAsString();
            Utils.writeContents(temp, content);
        }
    }

    public void puts(String remoteName) {
        Set<String> filename = _blobs.keySet();
        File remoteRepo = SelfDefinedUtils.remoteGitRepository(remoteName);
        for (String file : filename) {
            File temp = Utils.join(remoteRepo, file);
            String content = _blobs.get(file).getContentsAsString();
            Utils.writeContents(temp, content);
        }
    }

    /**
     * Find all ancestors of this commit using BFS.
     * (The Commit itself is also considered as an ancestor.)
     * @return: A set contains all ancestors of this commit.
     */
    public Set<String> getAllParents() {
        Set<String> result = new HashSet<>();
        result.add(this.getUid());
        ArrayDeque<Commit> bfsQueue = new ArrayDeque<>();
        bfsQueue.add(this);
        while (!bfsQueue.isEmpty()) {
            Commit temp = bfsQueue.remove();
            if (temp.getParent() != null) {
                for (String commitFile : temp.getParent()) {
                    Commit commit = SelfDefinedUtils.readCommit(commitFile);
                    result.add(commit.getUid());
                    bfsQueue.add(commit);
                }
            }
        }
        return result;
    }


    public Set<String> getAllParentsInRemote(File remoteRepository) {
        Set<String> result = new HashSet<>();
        result.add(this.getUid());
        ArrayDeque<Commit> bfsQueue = new ArrayDeque<>();
        bfsQueue.add(this);
        while (!bfsQueue.isEmpty()) {
            Commit temp = bfsQueue.remove();
            if (temp.getParent() != null) {
                for (String commitFile : temp.getParent()) {
                    File f = Utils.join(remoteRepository,
                            "commits", commitFile);
                    Commit commit = Utils.readObject(f, Commit.class);
                    result.add(commit.getUid());
                    bfsQueue.add(commit);
                }
            }
        }
        return result;
    }

    /**
     * Find the split point of this commit and given commit. Choose
     * the candidate split point that is closest to the head of this
     * commit if there are multiple possible split points.
     * @param given Other commit
     * @return: The split point of this commit and the given commit
     */
    public String getSplitPoint(Commit given) {
        Set<String> ancestors = given.getAllParents();
        ArrayDeque<Commit> bfsQueue = new ArrayDeque<>();
        bfsQueue.add(this);
        while (!bfsQueue.isEmpty()) {
            Commit removalCommit = bfsQueue.remove();
            if (ancestors.contains(removalCommit.getUid())) {
                return removalCommit.getUid();
            } else if (removalCommit.getParent() != null) {
                for (String parent : removalCommit.getParent()) {
                    bfsQueue.add(SelfDefinedUtils.readCommit(parent));
                }
            }
        }
        Utils.message("No common ancestor founded. There should be at"
                + " least one common ancestor(Initial Commit)."
                + " Unexpected error happens");
        throw new GitletException();
    }

    /**
     *  Display information about this commit. Following the below format:
     *  ===
     *  commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     *  Date: Thu Nov 9 20:00:05 2017 -0800
     *  A commit message.
     */
    public void display() {
        Utils.message("===");
        Utils.message("commit %s", _uid);
        Utils.message("Date: %s", _timestamp);
        Utils.message(_message);
        System.out.println();
    }

    static boolean identical(Commit commit1, Commit commit2) {
        if (commit1 != null && commit2 != null) {
            return Objects.equals(commit1.getUid(), commit2.getUid());
        } else {
            Utils.message("Trying to compare a commit with a null object");
            throw new GitletException();
        }
    }

    /** return message. */
    public String getMessage() {
        return _message;
    }

    /** return timestamp. */
    public String getTimestamp() {
        return _timestamp;
    }

    /** return uid. */
    public String getUid() {
        return _uid;
    }

    /**
     * Get the map of this commit.
     * @return The Treemap of this commit.
     */
    public TreeMap<String, Blob> getBlobs() {
        return _blobs;
    }

    /**
     * Get the list of parent of this commit.
     * @return All parents of this commit in an ArrayList.
     */
    public ArrayList<String> getParent() {
        return _parent;
    }

    /**
     * Get method for branch distribution.
     * @return The branch of this commit.
     */
    public String getBranch() {
        return _branch;
    }

    /** The time when create this commit. */
    private String _timestamp;

    /** The message of this commit. */
    private String _message;

    /** The hashcode of the parent commit. */
    private ArrayList<String> _parent;

    /** The hashcode of this commit. */
    private String _uid;

    /** The HashMap tracks blobs for the current commit. */
    private TreeMap<String, Blob> _blobs;

    /** The current branch for this commit. */
    private String _branch;
}
