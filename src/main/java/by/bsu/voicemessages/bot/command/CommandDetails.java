package by.bsu.voicemessages.bot.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommandDetails {
    START("/start", "Start the bot"),
    SET_LANG("/lang", "Set default language to decode messages"),
    HELP("/help", "Get help info");

    private final String commandIdentifier;
    private final String commandDescription;

    public static CommandDetails fromString(String commandIdentifier) {
        for (CommandDetails cmd : CommandDetails.values()) {
            if (cmd.commandIdentifier.equalsIgnoreCase(commandIdentifier)) {
                return cmd;
            }
        }
        throw new IllegalArgumentException("No constant with commandIdentifier " + commandIdentifier + " found");
    }
}