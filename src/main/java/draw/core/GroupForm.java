package draw.core;

import java.util.ArrayList;
import java.util.List;

public class GroupForm implements Form {
    private List<Form> forms = new ArrayList<Form>();

    public GroupForm() {}

    @Override
    public void setColor(int r, int g, int b) {
        for (Form form : forms) {
            form.setColor(r, g, b);
        }
    }

    @Override
    public void translate(int dx, int dy) {
        for (Form form : forms) {
            form.translate(dx, dy);
        }
    }

    @Override
    public void rotate(float angle) {
        for (Form form : forms) {
            form.rotate(angle);
        }
    }

    @Override
    public void setRoundedEdge(float round) {
        for (Form form : forms) {
            form.setRoundedEdge(round);
        }
    }

    @Override
    public void add(Form form) {
        forms.add(form);
    }

    @Override
    public void remove(Form form) {
        forms.remove(form);
    }

    @Override
    public Form getChild(int index) {
        return forms.get(index);
    }
    
}
