package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Ziyi Xu
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }
            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        if (args.size() < 1 || args.size() > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }
        _arguments = new ArrayList<>();
        _arguments.addAll(args);

        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machineForThisConfig = readConfig();
        setUp(machineForThisConfig, _input.nextLine());
        while (_input.hasNext()) {
            String nextLine = _input.nextLine();
            if (nextLine.contains("*")) {
                setUp(machineForThisConfig, nextLine);
            } else {
                printMessageLine(machineForThisConfig.convert(nextLine.
                        replaceAll("\\s+", "")));
            }

        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            int numRotors;
            int pawls;
            String firstLine = _config.nextLine();
            if (firstLine.contains("(")
                    || firstLine.contains(")")
                    || firstLine.contains("*")) {
                throw new EnigmaException("Wrong format"
                        + " for the firstLine of configuration");
            }
            _alphabet = new Alphabet(firstLine);

            String seconLine = _config.nextLine();
            if (seconLine.contains("(")
                    || seconLine.contains(")")
                    || seconLine.contains("*")) {
                throw new EnigmaException("Wrong format"
                        + " for the seconLine of configuration");
            } else {
                _config = getInput(_arguments.get(0));
                _config.nextLine();
                numRotors = Integer.parseInt(_config.next());
                pawls = Integer.parseInt(_config.next());
            }

            Collection<Rotor> allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                Rotor rotorToAdd = readRotor();
                if (allRotors.contains(rotorToAdd)) {
                    throw new EnigmaException("A rotor might"
                            + " be repeated in the setting line.");
                }
                allRotors.add(rotorToAdd);
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String type = _config.next();
            String cycles = "";
            while (_config.hasNext("\\(.*\\)")) {
                String str = _config.next();
                cycles += str;
            }
            Permutation perm = new Permutation(cycles, this._alphabet);
            if (type.charAt(0) == 'M') {
                String notches = "";
                for (int i = 1; i < type.length(); i++) {
                    notches += type.charAt(i);
                }
                return new MovingRotor(name, perm, notches);
            }
            if (type.charAt(0) == 'N') {
                return new FixedRotor(name, perm);
            }
            if (type.charAt(0) == 'R') {
                return new Reflector(name, perm);
            }
            throw new EnigmaException("Encountering unexpected"
                    + " input when experimenting readRotor");
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Pattern inputPattern = Pattern.compile(" ");
        String[] settingsInList = inputPattern.split(settings);

        if (!settingsInList[0].equals("*")) {
            throw new EnigmaException("The input "
                    + "might not start with a setting.");
        }
        int countOfRotors = 0;
        for (String s : settingsInList) {
            if (M.containName(s)) {
                countOfRotors += 1;
            }
        }
        if (countOfRotors != M.numRotors()) {
            throw new EnigmaException("Wrong number of rotors in input");
        }

        String[] rotorsForSetUp = new String[M.numRotors()];
        if (M.numRotors() >= 0) {
            System.arraycopy(settingsInList,
                    1, rotorsForSetUp, 0, M.numRotors());
        }
        M.insertRotors(rotorsForSetUp);
        if (settingsInList.length == M.numRotors() + 2
                || settingsInList[M.numRotors() + 2].contains("(")) {
            String settingString = settingsInList[M.numRotors() + 1];
            M.setRotors(settingString);
            StringBuilder cyclesForPlugboard = new StringBuilder();
            for (int i = M.numRotors() + 2; i < settingsInList.length; i++) {
                cyclesForPlugboard.append(settingsInList[i]);
            }
            Permutation plugboard = new Permutation(
                    cyclesForPlugboard.toString(), M.alphabet());
            M.setPlugboard(plugboard);
        } else {
            String settingString = settingsInList[M.numRotors() + 1];
            String ring = settingsInList[M.numRotors() + 2];
            M.setRotors(M.transition(ring, settingString));
            M.notchTransition(ring);
            StringBuilder cyclesForPlugboard = new StringBuilder();
            for (int i = M.numRotors() + 3; i < settingsInList.length; i++) {
                cyclesForPlugboard.append(settingsInList[i]);
            }
            Permutation plugboard = new Permutation(
                    cyclesForPlugboard.toString(), M.alphabet());
            M.setPlugboard(plugboard);
        }
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int count = 0;
        for (int i = 0; i < msg.length(); i++) {
            _output.print(msg.charAt(i));
            count += 1;
            if (count % 5 == 0 && i != msg.length() - 1) {
                _output.print(" ");
            }
        }
        _output.print("\n");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** Enigma machine initialized by the input. */
    private List<String> _arguments;
}
