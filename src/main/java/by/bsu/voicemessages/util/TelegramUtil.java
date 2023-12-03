package by.bsu.voicemessages.util;

import by.bsu.voicemessages.bot.command.CommandDetails;
import by.bsu.voicemessages.decode.Language;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class TelegramUtil {
    private final static String ANSWER_SENT = "Answer sent to user";

    public static void updateMessage(AbsSender sender, EditMessageText editedMessage) throws TelegramApiException {
        sender.execute(editedMessage);
        log.debug(ANSWER_SENT);
    }

    public static void sendMessage(AbsSender sender, SendMessage message) throws TelegramApiException {
        sender.execute(message);
        log.debug(ANSWER_SENT);
    }

    public static EditMessageText buildEditMessage(String newText, Integer messageId, Long chatId) {
        EditMessageText editedMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(newText)
                .build();

        editedMessage.enableMarkdown(true);
        return editedMessage;
    }

    public static SendMessage buildMessage(String text, Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        message.enableMarkdown(true);
        return message;
    }

    public static SendMessage buildReplyMessage(String text, Long chatId, Integer messageId) {
        SendMessage message = buildMessage(text, chatId);
        message.setReplyToMessageId(messageId);
        return message;
    }

    public static SendMessage buildReplyMarkupMessage(String text, Long chatId, ReplyKeyboard markup) {
        SendMessage message = buildMessage(text, chatId);
        message.setReplyMarkup(markup);
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
            return CommandDetails.fromString(options[0]);
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

    @SafeVarargs
    public static List<List<InlineKeyboardButton>> buildKeyboard(List<InlineKeyboardButton>... rows) {
        return List.of(rows);
    }

    public static List<InlineKeyboardButton> buildKeyboardRow(InlineKeyboardButton... buttons) {
        return List.of(buttons);
    }

    public static InlineKeyboardButton buildKeyboardButton(String buttonText, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(EmojiParser.parseToUnicode(buttonText))
                .callbackData(callbackData)
                .build();
    }

    public static String concatStringsWithSeparator(String separator, String... strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            if (i < strings.length - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

}
