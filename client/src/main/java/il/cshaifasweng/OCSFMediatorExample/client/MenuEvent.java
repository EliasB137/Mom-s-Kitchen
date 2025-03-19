package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.SendMenu;

public class MenuEvent {
    private SendMenu menu;

    public SendMenu getMenu() {
        return menu;
    }

    public MenuEvent(SendMenu menu) {
        this.menu = menu;
    }
}