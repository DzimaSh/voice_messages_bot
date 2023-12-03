package by.bsu.voicemessages.util;

import by.bsu.voicemessages.decode.Language;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class ChatMetaInfo {
    private final Long chatId;
    private Language language = Language.RECOGNIZE;
}
