package org.dasher.speed.taskmanagement.ui.view;

import org.dasher.speed.base.ui.component.ViewToolbar;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login | LifePlus")
public class LoginView extends VerticalLayout implements BeforeEnterListener{

    private LoginForm login = new LoginForm();

    public LoginView() {
        // Layout principal da página (equivalente ao <body>)
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addClassName("login-view");
        mainLayout.setSizeFull();
        mainLayout.setHeight("100vh");
        mainLayout.setPadding(false); // Remove padding interno do layout principal
        mainLayout.setSpacing(false); // Remove espaço entre os elementos
        mainLayout.setAlignItems(Alignment.STRETCH);
    
        setPadding(false);
        // "Div" 1 - Toolbar no topo (alinhada ao início)
        VerticalLayout toolbarLayout = new VerticalLayout();
        toolbarLayout.setWidthFull();
        toolbarLayout.setPadding(true);
        toolbarLayout.setSpacing(false);
        toolbarLayout.setAlignItems(Alignment.START);
        toolbarLayout.getStyle().setDisplay(Display.BLOCK);
    
        var toolbar = new ViewToolbar("Login");
        toolbarLayout.add(toolbar); // Adiciona a toolbar nesse layout específico
    
        // "Div" 2 - Área central com login (centralizado)
        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.setAlignItems(Alignment.CENTER);
        loginLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        loginLayout.setSizeFull(); // Ocupa o restante da tela
    

        LoginForm login = new LoginForm();
        login.setI18n(createCustomI18n());
        login.setAction("login");
        loginLayout.add(login);
    
        // Agora adicionamos essas duas "divs" no layout principal
        mainLayout.add(toolbarLayout, loginLayout);
    
        // Adiciona o layout principal à view
        add(mainLayout);
    }
    

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
        .getQueryParameters()
        .getParameters()
        .containsKey("error")){
            login.setError(true);
        }
    }

    private LoginI18n createCustomI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setUsername("Email");
        i18n.setForm(i18nForm);
        return i18n;
    }
}