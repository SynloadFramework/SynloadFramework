package com.synload.framework.forms;

import java.util.ArrayList;
import java.util.List;

public class Select extends FormItem {
    public Select() {
        this.setType("select");
    }

    public List<Option> options = new ArrayList<Option>();

    public void addOption(String value, String text, boolean selected) {
        options.add((new Option()).setValue(value).setText(text)
                .setSelected(selected));
    }

    public class Option {
        public String value, text = "";
        public boolean selected = false;

        public boolean isSelected() {
            return selected;
        }

        public Option setSelected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Option setValue(String value) {
            this.value = value;
            return this;
        }

        public String getText() {
            return text;
        }

        public Option setText(String text) {
            this.text = text;
            return this;
        }
    }
}
