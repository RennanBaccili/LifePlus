package org.dasher.speed.taskmanagement.ui.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "test-register", layout = TestRegisterView.EmptyLayout.class)
@PageTitle("Test Register | LifePlus")
@AnonymousAllowed
public class TestRegisterView extends VerticalLayout {

    // Layout vazio para evitar o MainLayout
    public static class EmptyLayout extends VerticalLayout implements RouterLayout {
        public EmptyLayout() {
            setSizeFull();
            setPadding(false);
            setMargin(false);
        }
    }

    public TestRegisterView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        add(new H1("Test Register Page"));
        add(new Div("If you can see this, the routing is working!"));
        add(new Div("This is a test page to verify registration access."));
    }
} 