package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Manu John
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
        Machine m;
        if (!_input.hasNextLine()) {
            throw new EnigmaException("empty enigma input file");
        }

        m = readConfig();
        while (_input.hasNextLine()) {
            String f = _input.nextLine();
            f = f.replaceAll("\\s+", " ");
            f = f.trim();
            if (!f.startsWith("*")) {
                if (m == null) {
                    throw new EnigmaException("empty machine!");
                } else if (!m.getCond()) {
                    throw new EnigmaException("empty config file");
                }
                printMessageLine(m.convert(f));
            } else {
                String settingLine = f;
                setUp(m, settingLine);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            if (!_config.hasNext("[^*()]+")) {
                throw new EnigmaException("no alphabet");
            }
            String str = _config.next();
            _alphabet = new Alphabet(str);
            if (!_config.hasNext("\\d")) {
                throw new EnigmaException("no rotors");
            }
            _rotors = Integer.parseInt(_config.next());
            if (!_config.hasNext("\\d")) {
                throw new EnigmaException("no pawls");
            }
            _pawls = Integer.parseInt(_config.next());
            _rotorBag = new ArrayList<>();
            while (_config.hasNext()) {
                _rotorBag.add(readRotor());
            }
            return new Machine(_alphabet, _rotors, _pawls, _rotorBag);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            if (!_config.hasNext()) {
                throw new EnigmaException("does not have next for name");
            }
            String rotorName = _config.next();
            if (!_config.hasNext()) {
                throw new EnigmaException("does not have next for description");
            }
            String rotorDescription = _config.next();
            String rotorCycles = "";

            while (_config.hasNext("(\\([" + _alphabet.getSeq() + "]+\\))+")) {
                String currString = _config.next();
                rotorCycles += currString;
            }

            Permutation p = new Permutation(rotorCycles, _alphabet);
            if (rotorDescription.charAt(0) == 'R') {
                return new Reflector(rotorName, p);
            } else if (rotorDescription.charAt(0) == 'N') {
                return new FixedRotor(rotorName, p);
            } else if (rotorDescription.charAt(0) == 'M') {
                String rotorNotches = rotorDescription.substring(1);
                return new MovingRotor(rotorName,
                        p, rotorNotches);
            } else {
                throw new EnigmaException("wrong formatting");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] arr = settings.split("\\s");
        if (!arr[0].equals("*")) {
            throw new EnigmaException("forgot *");
        }

        int storage = 0;
        for (int i = 1; i < arr.length; i++) {
            if (i == arr.length - 1
                    || arr[i + 1].charAt(0) == '(') {
                storage = i;
                break;
            }
        }

        String[] rotorNames = new String[M.numRotors()];
        System.arraycopy(arr, 1,
                rotorNames, 0, rotorNames.length);
        M.insertRotors(rotorNames);
        if (storage == M.numRotors() + 2) {
            M.setRotors(arr[storage - 1]);
            M.setRing(arr[storage]);
        } else if (storage == M.numRotors() + 1) {
            M.setRotors(arr[storage]);
        } else {
            throw new EnigmaException("wrong arr length");
        }

        String perm = "";
        for (int i = storage; i < arr.length; i++) {
            perm += arr[i];
        }
        M.setPlugboard(new Permutation(perm, _alphabet));
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String str = "";
        if (msg.equals("")) {
            _output.println();
        }


        for (int i = 0; i < msg.length(); i++) {
            if (!Character.isWhitespace(msg.charAt(i))) {
                str += msg.charAt(i);
            }
        }

        msg = str;
        int counter = 0;
        String subStr = "";

        for (int i = 0; i < msg.length(); i++) {
            if (counter < 5) {
                subStr += msg.charAt(i);
                counter++;
            } else {
                _output.print(subStr + " ");
                if (i == msg.length()) {
                    _output.println();
                }
                counter = 1;
                subStr = "";
                subStr += msg.charAt(i);
            }
        }

        if (!subStr.equals("")) {
            _output.println(subStr);
        }
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

    /** rotors. */
    private int _rotors;

    /** pawls. */
    private int _pawls;

    /** collection of rotors. */
    private Collection<Rotor> _rotorBag;

}
