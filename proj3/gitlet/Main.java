package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Driver class for Gitlet, the tiny stupid version-control system。
 * @author Ziyi Xu
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains <COMMAND> <OPERANDS>.
     */
    public static void main(String... args) {
        try {
            if (args.length == 0) {
                System.out.println("Please enter a command.");
                throw new GitletException();
            }

            _operator = args[0];
            if (!Arrays.asList(ALLVALIDCOMMANDS).contains(_operator)) {
                System.out.println("No command with that name exists.");
                throw new GitletException();
            }
            _operands = Arrays.asList(args).listIterator();
            _operands.next();
            if (Arrays.asList(NOARGUMENTCOMMANDS).contains(_operator)) {
                noArgumentsCommand(_operator, _operands);
            } else if (Arrays.asList(MULTIARGUMENTCOMMANDS).
                    contains(_operator)) {
                multiArgCommand(_operator, _operands);
            } else if (Arrays.asList(REMOTECOMMANDS).contains(_operator)) {
                remoteCommand(_operator, _operands);
            } else {
                Utils.message("THIS SHOULD NOT BE REACHED");
                throw new GitletException();
            }
        } catch (GitletException | IOException exception) {
            System.exit(0);
        }
    }

    /**
     * The class used to implement no arguments command. In fact, all
     * following class can be merged into on class(all use same arguments
     * String operator, Iterator<String>. This class is built to meet style
     * check.
     * @param operator The operator of the commands
     * @param operands The operands of the commands
     * @throws IOException May throw GitletException in class.
     */
    private static void noArgumentsCommand(String operator, Iterator<String>
            operands) throws IOException {
        Core core = new Core();
        switch (operator) {
        case "init":
            core.init(operands);
            break;
        case "log":
            core.log(operands);
            break;
        case "global-log":
            core.globalLog(operands);
            break;
        case "status":
            core.status(operands);
            break;
        default:
            Utils.message("This should not be reached");
            throw new GitletException();
        }
    }

    /**
     * The class used to implement remote arguments command. In fact, all
     * following class can be merged into on class(all use same arguments
     * String operator, Iterator<String>. This class is built to meet style
     * check.
     * @param operator The operator of the commands
     * @param operands The operands of the commands
     * @throws IOException May throw GitletException in class.
     */
    private static void remoteCommand(String operator, Iterator<String>
            operands) throws IOException {
        Core core = new Core();
        switch (operator) {
        case "add-remote":
            core.addRemote(operands);
            break;
        case "rm-remote":
            core.rmRemote(operands);
            break;
        case "push":
            core.push(operands);
            break;
        case "fetch":
            core.fetch(operands);
            break;
        case "pull":
            core.pull(operands);
            break;
        default:
            Utils.message("This should not be reached");
            throw new GitletException();
        }
    }

    /**
     * The class used to implement multi arguments command. In fact, all
     * following class can be merged into on class(all use same arguments
     * String operator, Iterator<String>. This class is built to meet style
     * check.
     * @param operator The operator of the commands
     * @param operands The operands of the commands
     * @throws IOException May throw GitletException in class.
     */
    private static void multiArgCommand(String operator, Iterator<String>
            operands) throws IOException {
        Core core = new Core();
        switch (operator) {
        case "add":
            while (operands.hasNext()) {
                core.add(operands.next());
            }
            break;
        case "rm":
            while (operands.hasNext()) {
                core.rm(operands.next());
            }
            break;
        case "commit":
            core.commit(operands);
            break;
        case "find":
            core.find(operands);
            break;
        case "checkout":
            core.checkout(operands);
            break;
        case "branch":
            core.branch(operands);
            break;
        case "rm-branch":
            core.rmBranch(operands);
            break;
        case "reset":
            core.reset(operands);
            break;
        case "merge":
            core.merge(operands);
            break;
        default:
            Utils.message("This should not be reached");
            throw new GitletException();
        }
    }

    /**
     * The current working directory.
     */
    private static File _cwd = new File(System.getProperty("user.dir"));

    /**
     * All valid commands we will deal with in this project.
     */
    private static final String[] ALLVALIDCOMMANDS = new String[]{"init", "add",
        "commit", "rm", "log", "global-log", "find", "status", "checkout",
        "branch", "rm-branch", "reset", "merge", "add-remote", "rm-remote",
        "push", "fetch", "pull"};

    /**
     * The commands need no argument.
     */
    private static final String[] NOARGUMENTCOMMANDS = new String[]{"init",
        "log", "global-log", "status"};

    /**
     * The commands related to remote.
     */
    private static final String[] REMOTECOMMANDS = new String[]{"add-remote",
        "rm-remote", "push", "fetch", "pull"};

    /**
     * The commands need at least one argument.
     */
    private static final String[] MULTIARGUMENTCOMMANDS = new String[]{"add",
        "commit", "rm", "find", "checkout", "branch", "rm-branch",
        "reset", "merge"};

    /**
     * Get the current working directory as File.
     * @return Current working directory
     */
    static File getCwd() {
        return _cwd;
    }

    /**
     * The operator of the command, should be one of
     * the String in ALLVALIDCOMMANDS.
     */
    private static String _operator;

    /**
     * The Iterator of operands.
     */
    private static Iterator<String> _operands;
}
