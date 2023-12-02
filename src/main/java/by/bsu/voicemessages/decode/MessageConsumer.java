package by.bsu.voicemessages.decode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.BlockingQueue;

@Slf4j
@RequiredArgsConstructor
public class MessageConsumer implements Runnable {
    private final BlockingQueue<Message> messages;

    @Override
    public void run() {

    }
}
