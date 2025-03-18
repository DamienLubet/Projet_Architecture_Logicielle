package draw.core;

import java.util.ArrayList;
import java.util.List;

public class GroupForm implements Form {
    private List<Form> forms = new ArrayList<Form>();

    public GroupForm() {}

    public void setColor(int r, int g, int b) {
        for (Form form : forms) {
            form.setColor(r, g, b);
        }
    }

    public void translate(int dx, int dy) {
        for (Form form : forms) {
            form.translate(dx, dy);
        }
    }

    public void rotate(float angle) {
        for (Form form : forms) {
            form.rotate(angle);
        }
    }

    public void setRoundedEdge(float round) {
        for (Form form : forms) {
            form.setRoundedEdge(round);
        }
    }


    public void add(Form form) {
        forms.add(form);
    }

    public void remove(Form form) {
        forms.remove(form);
    }

    public Form getChild(int index) {
        return forms.get(index);
    }
    
}
