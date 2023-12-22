package com.example.web.telegram;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class AlgaSamgaBot extends AbilityBot {
    private final ResponseHandler responseHandler;

    public AlgaSamgaBot(Environment environment) {
        super(environment.getProperty("BOT_TOKEN"), "algasamga_system_information_bot");
        responseHandler = new ResponseHandler(silent, db);
    }

    public Ability startBot() {
        return Ability.builder()
                .name("start")
                .info(Constants.START_DESCRIPTION)
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    public Reply replyToButtons() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, update) -> responseHandler.replyToButtons(getChatId(update), update.getMessage());
        return Reply.of(action, Flag.TEXT, update -> responseHandler.userIsActive(getChatId(update)));
    }

    @Override
    public long creatorId() {
        return 1L;
    }
}
