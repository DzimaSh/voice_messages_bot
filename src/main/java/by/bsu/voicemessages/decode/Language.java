package by.bsu.voicemessages.decode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    BE("be", "Belarusian", ":by:"),
    EN("en", "English", ":gb:"),
    FR("fr", "French", ":fr:"),
    RU("ru", "Russian", ":ru:"),
    RECOGNIZE("", "Recognize any", ":globe_with_meridians:");

    private final String lang;
    private final String description;
    private final String emojiCode;
}
