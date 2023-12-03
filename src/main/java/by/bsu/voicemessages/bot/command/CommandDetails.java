package by.bsu.voicemessages.bot.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommandDetails {
    SET_LANG("/lang", "Set default language to decode messages");

    private final String commandIdentifier;
    private final String commandDescription;
}