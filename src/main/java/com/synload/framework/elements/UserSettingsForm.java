package com.synload.framework.elements;

import com.synload.framework.forms.Form;
import com.synload.framework.forms.Text;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.WSHandler;

public class UserSettingsForm {
    public static Form get(WSHandler user) {
        Form f = new Form();
        f.setHeader("Edit user settings");
        Request r = new Request();
        r.setRequest("action");
        r.setPage("userSettings");
        f.setParent(".content[page='wrapper']");
        f.setParentTemplate("wrapper");
        Text t = new Text();
        t.setEnabled(true);
        t.setLabel("Email");
        t.setIdentifier("email");
        t.setValue(user.getUser().getEmail());
        f.addFormItem(t);
        f.addJavascript(f.getFileData("./elements/js/menu_change.js"));
        f.setRequest(r);
        f.setPageId("userSettings");
        f.setRequest(new Request("get", "usersettings"));
        f.data.put("uname", "usersettings");
        f.setPageTitle("AnimeCap .::. User Settings");
        return f;
    }
}
