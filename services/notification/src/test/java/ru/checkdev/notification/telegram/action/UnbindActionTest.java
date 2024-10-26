package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.domain.TgUser;
import ru.checkdev.notification.service.TgService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnbindActionTest {

    @Mock
    private TgAuthCallWebClint authCallWebClint;

    @Mock
    private TgService tgService;

    @Mock
    TgConfig tgConfig;

    @InjectMocks
    private UnbindAction unbindAction;
    private final long chatId = 0;
    private final String chatIdString = String.valueOf("0");

    @Test
    void whenUnbindSuccess() {
        var email = "email@m.ru";
        var password = "password";
        var message = new Message();
        message.setText(email + " " + password);
        var chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);
        when(tgConfig.parseMessageLoginAndPassword(message)).thenReturn(Map.of(
                "login", email,
                "password", password
        ));
        when(tgConfig.isEmail(any())).thenReturn(true);
        PersonDTO personDTO = new PersonDTO();
        personDTO.setEmail("email@mail.ru");
        when(authCallWebClint.doGet(any())).thenReturn(Mono.just(personDTO));
        when(tgService.findByChatId(chatIdString))
                .thenReturn(Optional.of(new TgUser(1, email, password, true, chatIdString)));
        var text = "Аккаунт Telegram отвязан от платформы CheckDev.";
        SendMessage rsl = (SendMessage) unbindAction.callback(message);
        assertThat(rsl)
                .isEqualTo(new SendMessage(chatIdString, text));
    }
}