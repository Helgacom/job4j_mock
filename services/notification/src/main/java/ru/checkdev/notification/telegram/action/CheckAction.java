package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.service.TgService;

@AllArgsConstructor
@Slf4j
public class CheckAction implements Action {

    private final TgService tgService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var sl = System.lineSeparator();
        var chatId = message.getChatId().toString();
        var tg = tgService.findByChatId(chatId);
        if (tg.isEmpty()) {
            return new SendMessage(chatId,
                    "Аккаунт Telegram не зарегистрирован." + sl
                            + "Для регистрации передите по: " + sl
                            + "/new"
            );
        }
        var text = "ФИО: " + tg.get().getUsername() + sl
                + "Email: " + tg.get().getEmail();
        return new SendMessage(chatId, text);
    }
    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}

