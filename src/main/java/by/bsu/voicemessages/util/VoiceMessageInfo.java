package by.bsu.voicemessages.util;

import by.bsu.voicemessages.decode.Language;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.File;

/**
 * Represents information about a voice message in Telegram.
 * Includes the file, message ID, and chat ID.
 */
@Getter
@RequiredArgsConstructor
public class VoiceMessageInfo {
    private final File file;
    private final int messageId;
    private final long chatId;
    private final Language lang;
}
