package com.projetchat.serveur.controleur;

import java.io.Serializable;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

@Plugin(name = "TextAreaAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
/** Classe permetant d'écrire l'affichage de la console dans une textArea */
public class TextAreaAppender extends AbstractAppender {
    /** La zone de texte */
    private static TextArea textArea;

    /**
     * Constructeur de TextAreaAppender
     * 
     * @param textArea
     */
    public TextAreaAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }
    
    public static void setTextArea(TextArea textArea) {
        TextAreaAppender.textArea = textArea;
    }

    // Méthode appelée par Log4j à chaque log
    @Override
    public void append(LogEvent event) {
        if (textArea == null) return;

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

    // Factory method requise par Log4j pour instancier le plugin via le XML
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