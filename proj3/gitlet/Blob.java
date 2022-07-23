package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class Blob implements Serializable {

    /**
     * The constructor for the Blob Class.
     * @param filename :The file tracked by the Blob object
     */
    public Blob(String filename) {
        _filename = filename;
        File file = new File(_filename);
        _contents = Utils.readContents(file);
        _contentsAsString = Utils.readContentsAsString(file);
        _timestamp = SelfDefinedUtils.createTimestamp();
        _hashcode = hashcodeGenerator();
    }

    /**
     * Generate hashcode use SHA1 according to filename, contents, timestamp.
     * @return The hashcode of this blob
     */
    private String hashcodeGenerator() {
        String contentOfHash = SelfDefinedUtils.createArgumentsForSha1
                (_filename, _contentsAsString, _timestamp);
        return Utils.sha1(contentOfHash);
    }


    /**
     * Determine whether two given blobs are identical(in contents).
     * @param blob1 : A blob object.
     * @param blob2 : Another blob object.
     * @return: Whether two blobs have same contents or they are all null.
     */
    static boolean identical(Blob blob1, Blob blob2) {
        if (blob1 != null && blob2 != null) {
            return Objects.equals(blob1.getContentsAsString(),
                    blob2.getContentsAsString());
        } else {
            return blob1 == null && blob2 == null;
        }

    }

    /** Name of the file being tracked. */
    private String _filename;

    /** Hashcode for the blob object. */
    private String _hashcode;

    /** Store the content which read from file. */
    private byte[] _contents;

    /** Store the content which read from file as String. */
    private String _contentsAsString;

    /** The time when blob object is created. */
    private String _timestamp;

    /** Return contents in String type. */
    public String getContentsAsString() {
        return _contentsAsString;
    }
}
