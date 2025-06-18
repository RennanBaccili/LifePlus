package org.dasher.speed.base.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

import org.dasher.speed.base.ui.component.ThemeToggle;
import org.dasher.speed.taskmanagement.security.SecurityService;

@Layout
@AnonymousAllowed
public final class MainLayout extends AppLayout {

    private final SecurityService _securityService;
    private final ThemeToggle themeToggle;

    MainLayout(SecurityService securityService) {
        _securityService = securityService;
        themeToggle = new ThemeToggle();
        setPrimarySection(Section.DRAWER);
        addToDrawer(createHeader(), new Scroller(createSideNav()), createUserMenu());
    }

    private Div createHeader() {
        // Logo e nome da aplicação
        var appLogo = VaadinIcon.CUBES.create();
        appLogo.addClassNames(TextColor.PRIMARY, IconSize.LARGE);

        var appName = new Span("Life Plus");
        appName.addClassNames(FontWeight.SEMIBOLD, FontSize.LARGE);

        var leftSide = new HorizontalLayout(appLogo, appName);
        leftSide.addClassNames(Gap.MEDIUM, AlignItems.CENTER);
        leftSide.setFlexGrow(1);

        // Toggle de tema no lado direito
        var rightSide = new HorizontalLayout(themeToggle);
        rightSide.addClassNames(AlignItems.CENTER);

        var header = new HorizontalLayout(leftSide, rightSide);
        header.addClassNames(Display.FLEX, Padding.MEDIUM, Width.FULL);
        header.setWidthFull();

        return new Div(header);
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.addClassNames(Margin.Horizontal.MEDIUM);
        MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            return new SideNavItem(menuEntry.title(), menuEntry.path(), new Icon(menuEntry.icon()));
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.path());
        }
    }

    private Component createUserMenu() {
        var avatar = new Avatar("John Smith");
        avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
        avatar.addClassNames(Margin.Right.SMALL);
        avatar.setColorIndex(5);

        var userMenu = new MenuBar();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        userMenu.addClassNames(Margin.MEDIUM);

        var userMenuItem = userMenu.addItem(avatar);
        userMenuItem.add("User");
        userMenuItem.getSubMenu().addItem("View Profile", event -> {
            com.vaadin.flow.component.UI.getCurrent().navigate("person");
        });
   
        userMenuItem.getSubMenu().addItem("Manage Settings").setEnabled(false);
        
        userMenuItem.getSubMenu().addItem("Logout", event -> {
            _securityService.lougout();
        });

        return userMenu;
    }
}
