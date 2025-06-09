package org.dasher.speed.base.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.theme.lumo.Lumo;

public class ThemeToggle extends Button {
    private boolean isDarkMode = true;

    public ThemeToggle() {
        super();
        addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);

        // Aplica o tema escuro ao iniciar
        applyInitialDarkMode();

        updateThemeIcon();

        addClickListener(click -> {
            isDarkMode = !isDarkMode;
            updateTheme();
            updateThemeIcon();
        });
    }

    private void applyInitialDarkMode() {
        getUI().ifPresent(ui -> {
            ThemeList themeList = ui.getElement().getThemeList();
            if (!themeList.contains(Lumo.DARK)) {
                themeList.add(Lumo.DARK);
            }
        });
    }

    private void updateTheme() {
        getUI().ifPresent(ui -> {
            ThemeList themeList = ui.getElement().getThemeList();
            if (isDarkMode) {
                themeList.add(Lumo.DARK);
            } else {
                themeList.remove(Lumo.DARK);
            }
        });
    }

    private void updateThemeIcon() {
        setIcon(new Icon(isDarkMode ? VaadinIcon.SUN_O : VaadinIcon.MOON));
        getElement().setAttribute("title", isDarkMode ? "Modo Claro" : "Modo Escuro");
    }
}
