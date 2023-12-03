package by.bsu.voicemessages.decode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    EN("en"),
    RU("ru"),
    BE("be"),
    RECOGNIZE("");

    private final String lang;
}
