package by.bsu.voicemessages.util;

import by.bsu.voicemessages.bot.command.CommandDetails;
import by.bsu.voicemessages.decode.Language;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static by.bsu.voicemessages.bot.util.BotActionConstants.COMMAND_PREFIX;

@Slf4j
public class TelegramUtil {
    public static SendMessage buildMessage(String text, Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        message.enableMarkdown(true);
        return message;
    }

    public static SendMessage buildReplyMessage(String text, Long chatId, Integer messageId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyToMessageId(messageId)
                .build();
        message.enableMarkdown(true);
        return message;
    }

    public static File voiceToFile(AbsSender sender, Voice voice) throws TelegramApiException {
        return sender.execute(new GetFile(voice.getFileId()));
    }

    public static VoiceMessageInfo buildVoiceMessageInfo(File file, Integer messageId, Long chatId, Language lang) {
        return new VoiceMessageInfo(file, messageId, chatId, lang);
    }

    public static CommandDetails getCommandByIdentifier(String identifier) {
        String[] options = identifier.split(" ");
        if (options.length > 0) {
            return CommandDetails.valueOf(options[0]
                    .toUpperCase(Locale.ROOT)
                    .replace(COMMAND_PREFIX, ""));
        }
        return null;
    }

    public static String decodeUTF8(String encodedString) {
        return new String(encodedString.getBytes(), StandardCharsets.UTF_8);
    }

    public static String decodeUnicodeString(String encodedString) {
        StringBuilder decodedString = new StringBuilder();

        int lastIndex = 0;
        while (lastIndex >= 0) {
            int index = encodedString.indexOf("\\u", lastIndex);
            if (index >= 0) {
                if (index > lastIndex) {
                    decodedString.append(encodedString, lastIndex, index);
                }
                if (index + 6 <= encodedString.length()) {
                    String hexValue = encodedString.substring(index + 2, index + 6);
                    try {
                        int unicodeValue = Integer.parseInt(hexValue, 16);
                        decodedString.append((char) unicodeValue);
                        lastIndex = index + 6;
                    } catch (NumberFormatException e) {
                        decodedString.append("\\u");
                        lastIndex = index + 1;
                    }
                } else {
                    decodedString.append("\\u");
                    lastIndex = index + 1;
                }
            } else {
                if (lastIndex < encodedString.length()) {
                    decodedString.append(encodedString.substring(lastIndex));
                }
                break;
            }
        }

        return decodedString.toString();
    }
}
