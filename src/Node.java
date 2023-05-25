import java.util.ArrayList;
import java.util.List;

public class Node {
    private String label;
    private String value;
    private List<Node> children;

    public Node(String label) {
        this.label = label;
        this.value = null;
        this.children = new ArrayList<>();
    }

    public Node(String label, String value) {
        this.label = label;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        buildString(builder, 0);
        return builder.toString();
    }

    private void buildString(StringBuilder builder, int level) {
        for (int i = 0; i < level; i++) {
            builder.append("\t");
        }
        builder.append("<").append(label).append(">");
        if (value != null) {
            builder.append(" ").append(value);
        }
        builder.append("\n");
        for (Node child : children) {
            child.buildString(builder, level + 1);
        }
    }
}
