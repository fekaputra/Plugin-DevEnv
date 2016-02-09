/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.unifiedviews.helpers.dpu.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.exec.ExecContext;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;

/**
 * Utilities for context.
 * Limitations: It is not possible to send message which has arguments in short and also long part of the message.
 * 
 * @author Škoda Petr
 */
public class ContextUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ContextUtils.class);

    private ContextUtils() {

    }

    /**
     * Translates and sends given formated message consisting of shortMessage and longMessage. sDoes not support message with exception.
     * Only body (long message) may have arguments.
     * 
     * @param context
     *            If null only log about message is stored.
     * @param type
     * @param shortMessage
     * @param fullMessage
     * @param args
     */
    public static void sendMessage(UserContext context, DPUContext.MessageType type, String shortMessage,
            String fullMessage, Object... args) {
        // Localization.
        String shortMessageTranslated = context.tr(shortMessage);
        final String fullMessageTranslated = context.tr(fullMessage, args);
        // Send message.
        if (context.getMasterContext() instanceof ExecContext) {
            final DPUContext dpuContext = ((ExecContext) context.getMasterContext()).getDpuContext();
            if (dpuContext != null) {
                dpuContext.sendMessage(type, shortMessageTranslated, fullMessageTranslated);
                return;
            }
        }
        // Else context has not yet been initialized.
        LOG.info("Message ignored:\ntype:{}\ncaption:{}\ntext:{}\n", type, shortMessageTranslated, fullMessageTranslated);
    }

    /**
     * Translates and sends given formated message consisting of shortMessage. Does not support messages with exception.
     * Given arguments are used in the localization of a shortMessage.
     *
     * @param context
     *            If null only log about message is stored.
     * @param type
     * @param shortMessage
     * @param args
     */
    public static void sendShortMessage(UserContext context, DPUContext.MessageType type, String shortMessage,
            Object... args) {
        // Localization.
        final String shortMessageTranslated = context.tr(shortMessage, args);
        // Send message.
        if (context.getMasterContext() instanceof ExecContext) {
            final DPUContext dpuContext = ((ExecContext) context.getMasterContext()).getDpuContext();
            if (dpuContext != null) {
                dpuContext.sendMessage(type, shortMessageTranslated, null);
                return;
            }
        }
        // Else context has not yet been initialized.
        LOG.info("Message ignored:\ntype:{}\ncaption:{}\n", type, shortMessageTranslated);
    }

    /**
     * Translates and sends given formated message consisting of shortMessage and longMessage.
     * Only body (long message) may have arguments.
     * 
     * @param context
     *            If null only log about message is stored.
     * @param type
     * @param shortMessage
     * @param exception
     * @param fullMessage
     * @param args
     */
    public static void sendMessage(UserContext context, DPUContext.MessageType type, String shortMessage,
            Exception exception, String fullMessage, Object... args) {
        // Localization.
        String shortMessageTranslated = context.tr(shortMessage);
        final String fullMessageTranslated = context.tr(fullMessage, args);
        // Send message.
        if (context.getMasterContext() instanceof ExecContext) {
            final DPUContext dpuContext = ((ExecContext) context.getMasterContext()).getDpuContext();
            if (dpuContext != null) {
                dpuContext.sendMessage(type, shortMessageTranslated, fullMessageTranslated, exception);
                return;
            }
        }
        // Else context has not yet been initialized.
        LOG.info("Message ignored:\ntype:{}\ncaption:{}\ntext:{}\n", type, shortMessageTranslated, fullMessageTranslated);
    }

    /**
     * Sends formated {@link DPUContext.MessageType#INFO} message. Localization is applied before the
     * message is send.
     * 
     * @param context
     * @param shortMessage
     *            Caption ie. short message.
     * @param fullMessage
     * @param args
     */
    public static void sendInfo(UserContext context, String shortMessage, String fullMessage, Object... args) {
        sendMessage(context, DPUContext.MessageType.INFO, shortMessage, fullMessage, args);
    }

    /**
     * Send formated {@link DPUContext.MessageType#WARNING} message. Localization is applied before the
     * message is send.
     * 
     * @param context
     * @param shortMessage
     *            Caption ie. short message.
     * @param fullMessage
     * @param args
     */
    public static void sendWarn(UserContext context, String shortMessage, String fullMessage, Object... args) {
        sendMessage(context, DPUContext.MessageType.WARNING, shortMessage, fullMessage, args);
    }

    /**
     * Send formated {@link DPUContext.MessageType#WARNING} message. Localization is applied before the
     * message is send.
     * 
     * @param context
     * @param shortMessage
     * @param exception
     * @param fullMessage
     * @param args
     */
    public static void sendWarn(UserContext context, String shortMessage, Exception exception,
            String fullMessage, Object... args) {
        sendMessage(context, DPUContext.MessageType.WARNING, shortMessage, exception, fullMessage, args);
    }

    /**
     * Send formated {@link DPUContext.MessageType#ERROR} message. Localization is applied before the
     * message is send.
     * 
     * @param context
     * @param shortMessage
     * @param exception
     * @param fullMessage
     * @param args
     */
    public static void sendError(UserContext context, String shortMessage, Exception exception,
            String fullMessage, Object... args) {
        sendMessage(context, DPUContext.MessageType.ERROR, shortMessage, exception, fullMessage, args);
    }

    /**
     * Send formated {@link DPUContext.MessageType#ERROR} message. Localization is applied before the
     * message is send.
     * 
     * @param context
     * @param shortMessage
     * @param fullMessage
     * @param args
     */
    public static void sendError(UserContext context, String shortMessage, String fullMessage,
            Object... args) {
        sendMessage(context, DPUContext.MessageType.ERROR, shortMessage, fullMessage, args);
    }

/**
     * Send short {@link DPUContext.MessageType#INFO message (caption only). The caption is formated.
     * Localization is applied before the message is send.
     * 
     * @param context
     * @param shortMessage
     * @param args
     */
    public static void sendShortInfo(UserContext context, String shortMessage, Object... args) {
        sendShortMessage(context, DPUContext.MessageType.INFO, shortMessage, args);
    }

    /**
     * Send short {@link DPUContext.MessageType#WARNING} message (caption only). The caption is formated.
     * Localization is applied before the message is send.
     * 
     * @param context
     * @param shortMessage
     * @param args
     */
    public static void sendShortWarn(UserContext context, String shortMessage, Object... args) {
        sendShortMessage(context, DPUContext.MessageType.WARNING, shortMessage, args);
    }

    /**
     * Return DPU exception of given text. Before throw given text is localized based on current locale
     * setting.
     * 
     * @param context
     * @param message
     *            Exception message.
     * @return newly created exception
     */
    public static DPUException dpuException(UserContext context, String message) {
        return new DPUException(context.tr(message));
    }

    /**
     * Return DPU exception of given text. Before throw given text is localized based on current locale
     * setting.
     * 
     * @param context
     * @param cause
     * @param message
     *            Exception message.
     * @return newly created exception
     */
    public static DPUException dpuException(UserContext context, Exception cause, String message) {
        return new DPUException(context.tr(message), cause);
    }

    /**
     * Return DPU exception of given text. Before throw given text is localized based on current locale
     * setting.
     * 
     * @param context
     * @param message
     * @param args
     * @return newly created exception
     */
    public static DPUException dpuException(UserContext context, String message, Object... args) {
        return new DPUException(context.tr(message, args));
    }

    /**
     * Return DPU exception of given text. Before throw given text is localized based on current locale
     * setting.
     * 
     * @param context
     * @param cause
     * @param message
     * @param args
     * @return newly created exception
     */
    public static DPUException dpuException(UserContext context, Exception cause, String message, Object... args) {
        return new DPUException(context.tr(message, args), cause);
    }

    /**
     * Return DPU exception that informs that current execution has been cancelled.
     * 
     * @param context
     * @return newly created exception
     */
    public static DPUException dpuExceptionCancelled(UserContext context) {
        return new DPUException(context.tr("lib.boost.execution.cancelled"));
    }

}
