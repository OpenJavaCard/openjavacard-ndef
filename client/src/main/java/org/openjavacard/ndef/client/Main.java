package org.openjavacard.ndef.client;

import org.openjavacard.util.HexUtil;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;
import java.io.PrintStream;
import java.util.Arrays;

public class Main {

    public static final void main(String[] arguments) {
        PrintStream os = System.out;
        TerminalFactory tf = TerminalFactory.getDefault();
        CardTerminals terminals = tf.terminals();

        try {
            // default command is info
            String command = "info";

            // check if user specified a reader as first argument
            if(arguments.length >= 1) {
                // if yes then use that reader
                String terminalName = arguments[0];
                CardTerminal terminal = terminals.getTerminal(terminalName);
                if(terminal == null) {
                    throw new RuntimeException("Could not find terminal \"" + terminalName + "\"");
                }
                // check if the user also specified a command
                if(arguments.length >= 2) {
                    command = arguments[1];
                }
                // split off the command arguments
                String[] commandArguments = new String[0];
                if(arguments.length > 2) {
                    commandArguments = Arrays.copyOfRange(arguments, 2, arguments.length);
                }
                // run the command
                runCommand(terminal, command, commandArguments);
            } else {
                // else list all readers
                os.println("Available terminals:");
                for (CardTerminal t : terminals.list()) {
                    os.println("  \"" + t.getName() + "\"");
                }
            }
        } catch (CardException e) {
            e.printStackTrace();
        }
    }

    private static void runCommand(CardTerminal reader, String command, String[] arguments) throws CardException {
        PrintStream os = System.out;
        Card card = reader.connect("T=1");
        NdefClient client = new NdefClient(card);
        client.connect();
        if(command.equalsIgnoreCase("read")) {
            short file = NdefProtocol.FILEID_NDEF_DATA;
            if(arguments.length == 1) {
                file = HexUtil.short16(arguments[0]);
            }
            byte[] data = client.readFile(file);
            os.println("Length: " + data.length);
            os.println("Data: " + HexUtil.bytesToHex(data));
        } else if(command.equalsIgnoreCase("info")) {
            NdefCapabilities caps = client.getCapabilities();
            os.println("Available files:");
            for (NdefFile file : caps.files) {
                os.println("  File " + HexUtil.hex16(file.fileId)
                        + " size " + file.fileSize
                        + " read " + HexUtil.hex8(file.readAccess)
                        + " write " + HexUtil.hex8(file.writeAccess));
            }
        } else if(command.equalsIgnoreCase("write")) {
            if(arguments.length != 1) {
                throw new RuntimeException("Need data to write");
            }
            byte[] data = HexUtil.hexToBytes(arguments[0]);
            client.writeData(data);
        } else {
            throw new RuntimeException("Unknown command \"" + command + "\"");
        }
    }

}
