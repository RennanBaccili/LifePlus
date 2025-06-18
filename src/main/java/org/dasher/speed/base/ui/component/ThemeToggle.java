package org.dasher.speed.base.ui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.theme.lumo.Lumo;

public class ThemeToggle extends Button {
    private boolean isDarkMode = false;

    public ThemeToggle() {
        super();
        addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        updateThemeIcon();
        
        addClickListener(click -> {
            isDarkMode = !isDarkMode;
            updateTheme();
            updateThemeIcon();
        });
    }

    private void updateTheme() {
        getUI().ifPresent(ui -> {
            ThemeList themeList = ui.getElement().getThemeList();
            if (isDarkMode) {
                themeList.add(Lumo.DARK);
                ui.getPage().executeJs("localStorage.setItem('preferredTheme', 'dark');");
            } else {
                themeList.remove(Lumo.DARK);
                ui.getPage().executeJs("localStorage.setItem('preferredTheme', 'light');");
            }
        });
    }

    private void updateThemeIcon() {
        setIcon(new Icon(isDarkMode ? VaadinIcon.SUN_O : VaadinIcon.MOON));
        getElement().setAttribute("title", isDarkMode ? "Modo Claro" : "Modo Escuro");
    }
} 