package com.projetchat.serveur.controleur;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

/**
 * Classe permetant d'écrire l'affichage de la console dans une textArea
 */
@Plugin(name = "TextAreaAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class TextAreaAppender extends AbstractAppender {
    /** La zone de texte */
    private static TextArea textArea;


    /**
     * Le constructeur de textArea
     * @param name le nom
     * @param filter le filtre
     * @param layout le layout
     * @param ignoreExceptions les exceptions à ignorer
     * @param properties les propriétés
     */
    public TextAreaAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions,
            Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    /**
     * Met la zone de texte
     *
     * @param textArea La zone de texte
     */
    public static void setTextArea(TextArea textArea) {
        TextAreaAppender.textArea = textArea;
    }

    // Méthode appelée par Log4j à chaque log
    @Override
    public void append(LogEvent event) {
        if (textArea == null)
            return;

        // Formater le message selon le layout défini dans le XML
        String message = new String(getLayout().toByteArray(event));

        // IMPORTANT : Mise à jour de l'UI dans le thread JavaFX
        Platform.runLater(() -> {
            try {
                textArea.appendText(message);
            } catch (Exception e) {
                // Ignorer les erreurs d'UI pour ne pas boucler
            }
        });
    }

    /**
     * Factory method pour créer une instance de {@link TextAreaAppender}.
     * Cette méthode est utilisée par le système de plugins de Log4j pour instancier
     * un appender configuré via XML ou programme.
     *
     * @param name             le nom de l'appender (doit être non nul)
     * @param layout           le layout utilisé pour formater les messages de log. Si null, 
     *                         un {@link PatternLayout} par défaut sera utilisé.
     * @param filter           le filtre appliqué à cet appender. Peut être null.
     * @param ignoreExceptions indique si les exceptions doivent être ignorées (true) ou propagées (false)
     * @return une instance de {@link TextAreaAppender}, ou null si le nom est null
     */
    @PluginFactory
    public static TextAreaAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions) {

        if (name == null) {
            LOGGER.error("No name provided for JfxAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new TextAreaAppender(name, filter, layout, ignoreExceptions, null);
    }
}