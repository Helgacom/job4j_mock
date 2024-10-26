package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.domain.TgUser;
import ru.checkdev.notification.service.TgService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

@AllArgsConstructor
@Slf4j
public class BindAction implements Action {

    private static final String FIND_PERSON_EMAIL_PASS = "/person/check?email=%s&password=%s";
    private final TgConfig tgConfig;
    private final TgAuthCallWebClint authCallWebClint;
    private final TgService tgService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var sl = System.lineSeparator();
        var chatId = message.getChatId().toString();
        var tg = tgService.findByChatId(chatId);
        if (tg.isPresent()) {
            if (tg.get().getSubscription())
                return new SendMessage(chatId,
                        "Такой аккаунт уже существует." + sl
                                + "Чтобы отписаться пройдите по: " + sl
                                + "/unbind"
                );
        }
        var text = "Введите логин и пароль для добавления аккаунта Telegram:";
        return new SendMessage(chatId, text);
    }
    @Override
    public BotApiMethod<Message> callback(Message message) {
        var text = " ";
        var sl = System.lineSeparator();
        var chatId = message.getChatId().toString();
        var loginAndPass = tgConfig.parseMessageLoginAndPassword(message);
        if (loginAndPass.isEmpty()) {
            text = "Введенные данные не корректны";
            return new SendMessage(chatId, text);
        }
        var login = loginAndPass.get("login");
        var password = loginAndPass.get("password");
        if (!tgConfig.isEmail(login)) {
            text = "Login: " + login + " не зарегистрирован." + sl
                    + "попробуйте снова." + sl
                    + "/bind";
            return new SendMessage(chatId, text);
        }
        PersonDTO result;
        try {
            result = authCallWebClint.doGet(FIND_PERSON_EMAIL_PASS.formatted(login, password)).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен, попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }
        if (result.getEmail() == null) {
            text = "Login или пароль введены не корректно." + sl
                    + "Попробуйте снова." + sl
                    + "/bind";
            return new SendMessage(chatId, text);
        }
        TgUser tgUser = tgService.findByChatId(chatId).get();
        tgUser.setSubscription(true);
        tgService.save(tgUser);
        text = "Аккаунт Telegram привязан к платформе CheckDev.";
        return new SendMessage(chatId, text);
    }
}
