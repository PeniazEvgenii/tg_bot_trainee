package com.example.secondTelegramBot.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class SenderService {

    private TelegramLongPollingBot bot;
    private final TelegramMessageSplitter messageSplitter;

    public SenderService(TelegramMessageSplitter messageSplitter) {
        this.messageSplitter = messageSplitter;
    }

    public void setBot(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendMessage(Long chatId, String text) {
        if (!isBotReady("sendText")) return;

        try {
//            SendMessage msg = SendMessage.builder()
//                    .chatId(chatId)
//                    .text(text)
//                    .build();
//
//            executeSendMessage(msg);
            sendMessage(chatId, text, null);
        } catch (Exception e) {
            log.error("sendMessage error", e);
        }
    }

    public void sendMessage(Long chatId, String text, ReplyKeyboard replyMarkup) {
        try {

            List<String> messages = messageSplitter.splitMessage(text);

            for (String message : messages) {
                SendMessage msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(message)
                        .replyMarkup(replyMarkup)
                        .build();

                executeSendMessage(msg);
            }


        } catch (Exception e) {
            log.error("sendMessage error", e);
        }
    }

    public void editMessage(Long chatId, Integer messageId, String newText, InlineKeyboardMarkup inlineKeyboard) {
        try {
            EditMessageText editMessageText = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(newText)
                    .replyMarkup(inlineKeyboard)
                    .build();

            executeGeneric(editMessageText);
        } catch (Exception e) {
            log.error("editMessage error", e);
        }
    }

    public void editMessage(Long chatId, Integer messageId, String newText) {
        try {
            EditMessageText editMessageText = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(newText)
                    .build();

            executeGeneric(editMessageText);
        } catch (Exception e) {
            log.error("editMessage error", e);
        }
    }

    /**
     * Ответ на callback query.
     */
    public void answerCallback(String callbackQueryId, String text, boolean showAlert) {
        if (!isBotReady("answerCallback")) return;
        try {
            AnswerCallbackQuery ack = AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQueryId)
                    .text(text)
                    .showAlert(showAlert)
                    .build();

            executeGeneric(ack);
        } catch (Exception e) {
            log.error("answerCallback error", e);
        }
    }

    /**
     * Отправить фото по fileId (когда файл уже загружен в Telegram).
     */
    public void sendPhotoById(String chatId, String fileId, String caption) {
        if (!isBotReady("sendPhotoById")) return;

        SendPhoto photo = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(fileId)) // fileId (String) — проще, не требует загрузки файла
                .caption(caption)
                .build();

        executeSendPhoto(photo);
    }

    public void sendPhotoFromFile(String chatId, File file, String caption) {
        SendPhoto photo = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(file))    // ← InputFile из File
                .caption(caption)
                .build();
        executeSendPhoto(photo);
    }

    public void sendPhotoFromStream(String chatId, InputStream inputStream, String fileName, String caption) {
        InputFile inputFile = new InputFile(inputStream, fileName); // (inputStream, filename)
        SendPhoto photo = SendPhoto.builder()
                .chatId(chatId)
                .photo(inputFile)
                .caption(caption)
                .build();
        executeSendPhoto(photo);
    }

    /**
     * Удалить сообщение (если бот имеет права).
     */
    public void deleteMessage(String chatId, Integer messageId) {
        if (!isBotReady("deleteMessage")) return;

        DeleteMessage del = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();

        executeGeneric(del);
    }

    /* ====== SendDocument ====== */

    /**
     * Отправить документ по fileId (когда файл уже загружен в Telegram).
     */
    public void sendDocumentById(String chatId, String fileId, String caption) {
        if (!isBotReady("sendDocumentById")) return;

        SendDocument doc = SendDocument.builder()
                .chatId(chatId)
                .document(new InputFile(fileId))
                .caption(caption)
                .build();

        executeSendDocument(doc);
    }

    /**
     * Отправить документ из локального файла.
     */
    public void sendDocumentFromFile(String chatId, File file, String caption) {
        if (!isBotReady("sendDocumentFromFile")) return;

        SendDocument doc = SendDocument.builder()
                .chatId(chatId)
                .document(new InputFile(file))
                .caption(caption)
                .build();

        executeSendDocument(doc);
    }

    /* -----------------------
       Низкоуровневые execute-обёртки
       ----------------------- */

    private void executeSendMessage(SendMessage msg) {
        try {
            bot.execute(msg); // SendMessage implements BotApiMethod<?>
        } catch (TelegramApiException e) {
            log.error("Failed to send message to {}: {}", msg.getChatId(), e.getMessage(), e);
        } catch (Throwable t) {
            log.error("Unexpected error sending message to {}: {}", msg.getChatId(), t.getMessage(), t);
        }
    }

    private void executeSendPhoto(SendPhoto photo) {
        try {
            bot.execute(photo); // конкретная перегрузка для SendPhoto
        } catch (TelegramApiException e) {
            log.error("Failed to send photo to {}: {}", photo.getChatId(), e.getMessage(), e);
        } catch (Throwable t) {
            log.error("Unexpected error sending photo to {}: {}", photo.getChatId(), t.getMessage(), t);
        }
    }

    /**
     * Универсальный executor для простых видов запросов,
     * поддерживающих BotApiMethod (EditMessageText, AnswerCallbackQuery, DeleteMessage и др.).
     */
    public void executeGeneric(BotApiMethod<?> method) {
        try {
            bot.execute(method);
        } catch (TelegramApiException e) {
            log.error("Failed to execute method {}: {}", method.getMethod(), e.getMessage(), e);
        } catch (Throwable t) {
            log.error("Unexpected error executing method {}: {}", method.getMethod(), t.getMessage(), t);
        }
    }


    private void executeSendDocument(SendDocument doc) {
        try {
            bot.execute(doc); // конкретная перегрузка для SendDocument
        } catch (TelegramApiException e) {
            log.error("Failed to send document to {}: {}", doc.getChatId(), e.getMessage(), e);
        } catch (Throwable t) {
            log.error("Unexpected error sending document to {}: {}", doc.getChatId(), t.getMessage(), t);
        }
    }

    /* -----------------------
       Вспомогательное
       ----------------------- */

    private boolean isBotReady(String action) {
        if (bot == null) {
            log.warn("Bot not set yet. Can't perform action: {}", action);
            return false;
        }
        return true;
    }
//
//    // Ленивая ссылка на MyBot — избегаем cycle dependency
//    private final ObjectProvider<MyBot2> botProvider;
//
//    /* ===========================
//       СИНХРОННЫЕ МЕТОДЫ (execute)
//       =========================== */
//
//    public void sendText(String chatId, String text) {
//        SendMessage msg = SendMessage.builder()
//                .chatId(chatId)
//                .text(text)
//                .build();
//        executeSafe(msg, chatId);
//    }
//
//    public void sendText(String chatId, String text, boolean enableMarkdownV2, boolean disableWebPagePreview) {
//        SendMessage message = SendMessage.builder()
//                .chatId(chatId)
//                .text(text)
//                .parseMode(enableMarkdownV2 ? "MarkdownV2" : null)
//                .disableWebPagePreview(disableWebPagePreview)
//                .build();
//
//        executeSafe(message, chatId);
//    }
//
//    public void answerCallback(String callbackQueryId, String text, boolean showAlert) {
//        AnswerCallbackQuery ack = AnswerCallbackQuery.builder()
//                .callbackQueryId(callbackQueryId)
//                .text(text)
//                .showAlert(showAlert)
//                .build();
//        executeSafe(ack, callbackQueryId);
//    }
//
//    public void sendChatActionTyping(String chatId) {
//        SendChatAction action = SendChatAction.builder()
//                .chatId(chatId)
//                .action("typing")
//                .build();
//        executeSafe(action, chatId);
//    }
//
//    public void sendPhoto(String chatId, File file, String caption) {
//        SendPhoto photo = SendPhoto.builder()
//                .chatId(chatId)
//                .photo(new InputFile(file))
//                .caption(caption)
//                .build();
//        executeSafe(photo, chatId);
//    }
//
//    public void sendPhotoById(String chatId, String fileId, String caption) {
//        SendPhoto photo = SendPhoto.builder()
//                .chatId(chatId)
//                .photo(new InputFile(fileId))
//                .caption(caption)
//                .build();
//        executeSafe(photo, chatId);
//    }
//
//    public void sendDocument(String chatId, File file, String caption) {
//        SendDocument doc = SendDocument.builder()
//                .chatId(chatId)
//                .document(new InputFile(file))
//                .caption(caption)
//                .build();
//        executeSafe(doc, chatId);
//    }
//
//    public void forwardMessage(String targetChatId, String fromChatId, Integer messageId) {
//        ForwardMessage forward = ForwardMessage.builder()
//                .chatId(targetChatId)
//                .fromChatId(fromChatId)
//                .messageId(messageId)
//                .build();
//        executeSafe(forward, targetChatId);
//    }
//
//
//    /* ============================
//       АСИНХРОННЫЕ ВЕРСИИ (executeAsync)
//       ============================ */
//
//    /**
//     * Отправляет текст асинхронно (не блокирует текущий поток).
//     */
//    public CompletableFuture<Void> sendTextAsync(String chatId, String text) {
//        return CompletableFuture.runAsync(() -> sendText(chatId, text));
//    }
//
//    public <T extends Serializable> CompletableFuture<Void> executeAsync(BotApiMethod<T> method) {
//        return CompletableFuture.runAsync(() -> executeSafe(method, "async"));
//
//    }
//
//
//
//    private void executeSafe(Object method, Object context) {
//        try {
//            MyBot2 bot = botProvider.getIfAvailable();
//            if (bot == null) {
//                log.error("Bot bean is not available, cannot send message. context={}", context);
//                return;
//            }
//            if (method instanceof BotApiMethod<?>) {
//                @SuppressWarnings("unchecked")
//                BotApiMethod<?> apiMethod = (BotApiMethod<?>) method;
//                bot.execute(apiMethod); // SendMessage и т.д.
//            } else if (method instanceof PartialBotApiMethod<?>) {
//                @SuppressWarnings("unchecked")
//                PartialBotApiMethod<?> partial = (PartialBotApiMethod<?>) method;
//                bot.execute(partial);   // SendPhoto, SendDocument и т.д.
//            } else {
//                log.error("Unsupported telegram method type: {}. context={}", method.getClass().getName(), context);
//            }
//        } catch (TelegramApiException e) {
//            log.error("Failed to execute Telegram method. context={}, error={}", context, e.getMessage(), e);
//            // Тут можно добавить retry/backoff, запись в DLQ или оповещение в Sentry/Prometheus
//        } catch (Exception e) {
//            log.error("Unexpected error executing Telegram method. context={}", context, e);
//        }
//    }
}

