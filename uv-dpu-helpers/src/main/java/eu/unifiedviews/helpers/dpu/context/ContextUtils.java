package eu.unifiedviews.helpers.dpu.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.exec.ExecContext;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;

/**
 * Utilities for context.
 *
 * @author Škoda Petr
 */
public class ContextUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ContextUtils.class);

    private ContextUtils() {

    }

    /**
     * Translate and send given formated message.
     *
     * @param context    If null only log about message is stored.
     * @param type
     * @param caption
     * @param bodyFormat
     * @param args
     */
    public static void sendMessage(UserContext context, DPUContext.MessageType type, String caption,
            String bodyFormat, Object... args) {
        final String body = context.tr(bodyFormat, args);
        if (context.getMasterContext() instanceof ExecContext) {
            final DPUContext dpuContext = ((ExecContext) context.getMasterContext()).getDpuContext();
            if (dpuContext != null) {
                dpuContext.sendMessage(type, caption, body);
                return;
            }
        }
        // Else context has not yet been initialized.
        LOG.info("Message ignored:\ntype:{}\ncaption:{}\ntext:{}\n", type, caption, body);
    }

    /**
     * Translate and send given formated message. Does not support message with exception.
     *
     * @param context    If null only log about message is stored.
     * @param type
     * @param caption
     * @param exception
     * @param bodyFormat
     * @param args
     */
    public static void sendMessage(UserContext context, DPUContext.MessageType type, String caption,
            Exception exception, String bodyFormat, Object... args) {
        final String body = context.tr(bodyFormat, args);
        if (context.getMasterContext() instanceof ExecContext) {
            final DPUContext dpuContext = ((ExecContext) context.getMasterContext()).getDpuContext();
            if (dpuContext != null) {
                dpuContext.sendMessage(type, caption, body, exception);
                return;
            }
        }
        // Else context has not yet been initialized.
        LOG.info("Message ignored:\ntype:{}\ncaption:{}\ntext:{}\n", type, caption, body);
    }

    /**
     * Send formated {@link DPUContext.MessageType#INFO} message. Localization is applied before the
     * message is send.
     *
     * @param context
     * @param caption    Caption ie. short message.
     * @param bodyFormat
     * @param args
     */
    public static void sendInfo(UserContext context, String caption, String bodyFormat, Object... args) {
        sendMessage(context, DPUContext.MessageType.INFO, caption, bodyFormat, args);
    }

    /**
     * Send formated {@link DPUContext.MessageType#WARNING} message. Localization is applied before the
     * message is send.
     *
     * @param context
     * @param caption    Caption ie. short message.
     * @param bodyFormat
     * @param args
     */
    public static void sendWarn(UserContext context, String caption, String bodyFormat, Object... args) {
        sendMessage(context, DPUContext.MessageType.WARNING, caption, bodyFormat, args);
    }

    /**
     * Send formated {@link DPUContext.MessageType#WARNING} message. Localization is applied before the
     * message is send.
     *
     * @param context
     * @param caption
     * @param exception
     * @param bodyFormat
     * @param args
     */
    public static void sendWarn(UserContext context, String caption, Exception exception,
            String bodyFormat, Object... args) {
        sendMessage(context, DPUContext.MessageType.WARNING, caption, exception, bodyFormat, args);
    }

    /**
     * Send formated {@link DPUContext.MessageType#ERROR} message. Localization is applied before the
     * message is send.
     *
     * @param context
     * @param caption
     * @param exception
     * @param bodyFormat
     * @param args
     */
    public static void sendError(UserContext context, String caption, Exception exception,
            String bodyFormat, Object... args) {
        sendMessage(context, DPUContext.MessageType.ERROR, caption, exception, bodyFormat, args);
    }

    /**
     * Send formated {@link DPUContext.MessageType#ERROR} message. Localization is applied before the
     * message is send.
     *
     * @param context
     * @param caption
     * @param exception
     * @param bodyFormat
     * @param args
     */
    public static void sendError(UserContext context, String caption, String bodyFormat,
            Object... args) {
        sendMessage(context, DPUContext.MessageType.ERROR, caption, bodyFormat, args);
    }

    /**
     * Send short {@link DPUContext.MessageType#INFO message (caption only). The caption is formated.
     * Localization is applied before the message is send.
     *
     * @param context
     * @param captionFormat
     * @param args
     */
    public static void sendShortInfo(UserContext context, String captionFormat, Object... args) {
        final String caption = String.format(captionFormat, args);
        sendMessage(context, DPUContext.MessageType.INFO, caption, "");
    }

    /**
     * Send short {@link DPUContext.MessageType#WARNING} message (caption only). The caption is formated.
     * Localization is applied before the message is send.
     *
     * @param context
     * @param captionFormat
     * @param args
     */
    public static void sendShortWarn(UserContext context, String captionFormat, Object... args) {
        final String caption = String.format(captionFormat, args);
        sendMessage(context, DPUContext.MessageType.WARNING, caption, "");
    }

    /**
     * Return DPU exception of given text. Before throw given text is localized based on current locale
     * setting.
     *
     * @param context
     * @param message Exception message.
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
     * @param message Exception message.
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
     */
    public static  DPUException dpuException(UserContext context, String message, Object... args) {
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
     */
    public static  DPUException dpuException(UserContext context, Exception cause, String message, Object... args) {
        return new DPUException(context.tr(message, args), cause);
    }

    /**
     * Return DPU exception that informs that current execution has been cancelled.
     *
     * @param context
     */
    public static DPUException dpuExceptionCancelled(UserContext context) {
        return new DPUException(context.tr("lib.boost.execution.cancelled"));
    }

}
